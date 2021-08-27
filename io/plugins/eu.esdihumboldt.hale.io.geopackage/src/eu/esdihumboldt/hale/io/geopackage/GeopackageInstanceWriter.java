/*
 * Copyright (c) 2020 wetransform GmbH
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.geopackage;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKBWriter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.springframework.core.convert.ConversionException;

import eu.esdihumboldt.hale.common.convert.ConversionUtil;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceAccessor;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractGeoInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryMetadata;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey;
import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.attributes.AttributesColumn;
import mil.nga.geopackage.attributes.AttributesDao;
import mil.nga.geopackage.attributes.AttributesRow;
import mil.nga.geopackage.core.srs.SpatialReferenceSystem;
import mil.nga.geopackage.core.srs.SpatialReferenceSystemDao;
import mil.nga.geopackage.db.GeoPackageDataType;
import mil.nga.geopackage.extension.index.FeatureTableIndex;
import mil.nga.geopackage.features.columns.GeometryColumns;
import mil.nga.geopackage.features.index.FeatureIndexManager;
import mil.nga.geopackage.features.index.FeatureIndexType;
import mil.nga.geopackage.features.user.FeatureColumn;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.manager.GeoPackageManager;
import mil.nga.geopackage.schema.TableColumnKey;
import mil.nga.geopackage.user.UserColumn;
import mil.nga.geopackage.user.UserCoreRow;
import mil.nga.sf.util.ByteReader;
import mil.nga.sf.wkb.GeometryReader;

/**
 * Writes features to a GeoPackage file.
 * 
 * @author Simon Templer
 */
public class GeopackageInstanceWriter extends AbstractGeoInstanceWriter {

	/**
	 * The identifier of the writer as registered to the I/O provider extension.
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.geopackage.instance.writer";

	/**
	 * Parameter to define the type of spatial index to create for new tables
	 */
	public static final String PARAM_SPATIAL_INDEX_TYPE = "spatialindex.type";

	/**
	 * Default value assumed when spatial index type is not specified
	 */
	public static final String DEFAULT_SPATIAL_INDEX_TYPE = "rtree";

	/**
	 * Set the type of spatial index to create for new tables
	 * 
	 * @param spatialIndexType Spatial index type to use
	 */
	public void setSpatialIndexType(String spatialIndexType) {
		setParameter(PARAM_SPATIAL_INDEX_TYPE, Value.of(spatialIndexType));
	}

	@Override
	public boolean isPassthrough() {
		return isWriteUnordered();
	}

	/**
	 * @return if instances are written unordered
	 */
	protected boolean isWriteUnordered() {
		// FIXME support properly handling foreign key constraints (similar to
		// for JDBC)?
		// XXX for now just a simple unordered write
		return true;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generating Geopackage", ProgressIndicator.UNKNOWN);
		InstanceCollection instances = getInstances();

		GeoPackage geoPackage = null;
//		Connection connection = null;
		try {
			URI loc = getTarget().getLocation();
			File file;
			try {
				file = new File(loc);
			} catch (Exception e) {
				throw new IllegalArgumentException("Only files are supported as data source", e);
			}

			if (file.exists() && file.length() == 0L) {
				// convenience for overwriting empty existing file
				file.delete();
			}
			if (!file.exists()) {
				GeoPackageManager.create(file);
			}
			// open existing file
			geoPackage = GeoPackageManager.open(file, true);

			/*
			 * XXX Not sure how to make a transaction work (running into
			 * different kinds of errors in the tests with this) Might be that
			 * the tables would have to be created first (with auto-commit on)
			 * and the transaction only used for inserting the data.
			 */
//			connection = geoPackage.getConnection().getConnection();
//			connection.setAutoCommit(false);

			if (true /* isWriteUnordered() */) {
				// write instances as they come in
				writeInstances(geoPackage, instances, progress, reporter);
			}
			else {
				// write instances based on type order needed for insert
				// (to avoid violating constraints)
				/*
				 * Set<TypeDefinition> sortedSet = getSortedSchemas(
				 * getTargetSchema().getMappingRelevantTypes());
				 * 
				 * for (TypeDefinition td : sortedSet) {
				 * writeInstances(geoPackage, instances.select(new
				 * TypeFilter(td)), progress, reporter); }
				 */
			}

//			connection.commit();

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
			reporter.setSuccess(false);
			reporter.setSummary("Saving instances to GeoPackage failed.");
//			if (connection != null) {
//				try {
//					connection.rollback();
//				} catch (SQLException e1) {
//					reporter.error("Error rolling back transaction", e1);
//				}
//			}
		} finally {
			if (geoPackage != null) {
				geoPackage.close();
			}
			progress.end();
		}

		return reporter;
	}

	/**
	 * Write instances to the GeoPackage.
	 * 
	 * @param geoPackage the GeoPackage
	 * @param instances the instances to write
	 * @param progress the progress indicator
	 * @param reporter the reporter
	 * @throws SQLException if an error occurs creating a database table
	 */
	protected void writeInstances(GeoPackage geoPackage, InstanceCollection instances,
			ProgressIndicator progress, IOReporter reporter) throws SQLException {
		try (ResourceIterator<Instance> it = instances.iterator()) {
			while (it.hasNext() && !progress.isCanceled()) {
				Instance instance = it.next();
				TypeDefinition type = instance.getDefinition();

				// TODO only specific types? (e.g. ignore some kinds of types?)
				String tableName = type.getName().getLocalPart();

				// determine table for type (and create if necessary)
				GeopackageTableType tableType = createTableIfNecessary(geoPackage, tableName, type,
						instance, reporter);
				switch (tableType) {
				case ATTRIBUTE:
					AttributesDao attributes = geoPackage.getAttributesDao(tableName);
					AttributesRow arow = attributes.newRow();

					populateRow(arow, instance, reporter);

					attributes.insert(arow);
					break;
				case FEATURE:
					FeatureDao features = geoPackage.getFeatureDao(tableName);
					FeatureRow frow = features.newRow();

					populateRow(frow, instance, reporter);

					// set geometry
					String geometryColumn = features.getGeometryColumnName();
					// XXX instead of using value traverse (GeometryFinder) for
					// geometry?
					Object geom = new InstanceAccessor(instance).findChildren(geometryColumn)
							.value();
					GeoPackageGeometryData geomData = convertGeometry(geom,
							features.getGeometryColumns(), reporter);
					frow.setGeometry(geomData);

					features.insert(frow);
					break;
				}
			}
		}
	}

	private GeoPackageGeometryData convertGeometry(Object someGeom, GeometryColumns geomColumns,
			SimpleLog log) {
		Geometry geom = null;
		CRSDefinition sourceCrs = null;

		if (someGeom instanceof GeometryProperty<?>) {
			GeometryProperty<?> prop = (GeometryProperty<?>) someGeom;
			geom = prop.getGeometry();
			sourceCrs = prop.getCRSDefinition();
		}
		else if (someGeom instanceof Geometry) {
			geom = (Geometry) someGeom;
		}

		GeoPackageGeometryData geometryData = new GeoPackageGeometryData(geomColumns.getSrsId());

		if (geom != null) {
			SpatialReferenceSystem targetSrs = geomColumns.getSrs();
			CRSDefinition targetCrs = toCRSDefinition(targetSrs);

			// do conversion to target CRS (if possible)
			Geometry targetGeometry = geom;
			try {
				if (sourceCrs != null && targetCrs != null) {
					MathTransform transform = CRS.findMathTransform(sourceCrs.getCRS(),
							targetCrs.getCRS());
					targetGeometry = JTS.transform(geom, transform);
				}
			} catch (Exception e) {
				log.error("Failed to convert geometry to target SRS " + targetSrs.getSrsName());
			}

			// XXX also an option to only use a SrsId or use a SrsId that
			// differs from the column SrsId?

			byte[] wkb = new WKBWriter().write(targetGeometry);
			mil.nga.sf.Geometry geometry = GeometryReader.readGeometry(new ByteReader(wkb));

			geometryData.setGeometry(geometry);
		}

		return geometryData;
	}

	private CRSDefinition toCRSDefinition(SpatialReferenceSystem srs) {
		String org = srs.getOrganization();
		long code = srs.getOrganizationCoordsysId();

		if (org != null && org.equals("EPSG")) {
			// we expect this to be available in Geotools
			// axis order is always x,y (longitude first)
			return new CodeDefinition(org + ":" + code, true);
		}

		return new WKTDefinition(srs.getDefinition(), null);
	}

	private void populateRow(UserCoreRow<?, ?> row, Instance instance, SimpleLog log) {
		for (String column : row.getColumnNames()) {
			Object value = new InstanceAccessor(instance).findChildren(column).value();
			if (value != null) {
				UserColumn col = row.getColumn(column);
				if (col.isPrimaryKey()) {
					// it seems for primary key we can't set a value
					continue;
				}

				// any value processing
				try {
					switch (col.getDataType()) {
					case DATE:
						// convert to LocalDate, then to String
						LocalDate ld = ConversionUtil.getAs(value, LocalDate.class);
						value = ld.toString();
						break;
					case DATETIME:
						// convert to Instant, then to String
						Instant inst = ConversionUtil.getAs(value, Instant.class);
						value = inst.toString();
						break;
					case INT:
					case INTEGER:
					case MEDIUMINT:
					case SMALLINT:
					case TINYINT:
					case REAL:
					case FLOAT:
					case DOUBLE:
					case TEXT:
						// generic conversion to data type
						value = ConversionUtil.getAs(value, col.getDataType().getClassType());
						break;
					default:
						// use as-is
						break;
					}
				} catch (ConversionException e) {
					log.error("Could not convert value to column type", e);
				}

				row.setValue(column, value);
			}
		}
	}

	private GeopackageTableType createTableIfNecessary(GeoPackage geoPackage, String tableName,
			TypeDefinition type, Instance instance, SimpleLog log) throws SQLException {
		if (geoPackage.getFeatureTables().contains(tableName)) {
			// table already exists
			return GeopackageTableType.FEATURE;
		}
		if (geoPackage.getAttributesTables().contains(tableName)) {
			// table already exists
			return GeopackageTableType.ATTRIBUTE;
		}

		// determine if we need a feature or attribute table
		Collection<? extends PropertyDefinition> allProperties = DefinitionUtil
				.getAllProperties(type).stream().filter(p -> {
					// filter out complex properties w/o HasValue or
					// AugmentedValue
					return p.getPropertyType().getConstraint(HasValueFlag.class).isEnabled() || p
							.getPropertyType().getConstraint(AugmentedValueFlag.class).isEnabled();
				}).collect(Collectors.toList());

		Optional<? extends PropertyDefinition> geometryProperty = allProperties.stream()
				.filter(property -> {
					return property.getPropertyType().getConstraint(GeometryType.class)
							.isGeometry();
				}).findFirst();

		List<QName> primaryPath = type.getConstraint(PrimaryKey.class).getPrimaryKeyPath();
		String primaryKeyColumn;
		if (primaryPath != null && !primaryPath.isEmpty()) {
			primaryKeyColumn = primaryPath.get(0).getLocalPart();
		}
		else {
			primaryKeyColumn = null;
		}

		GeopackageTableType tableType;
		if (geometryProperty.isPresent()) {
			// create feature table
			tableType = GeopackageTableType.FEATURE;

			PropertyDefinition geomProp = geometryProperty.get();

			// XXX generally only one geometry column supported?
			GeometryColumns geometryColumns = new GeometryColumns();
			geometryColumns.setId(new TableColumnKey(tableName, geomProp.getName().getLocalPart()));
			geometryColumns.setGeometryType(convertGeometryType(
					geomProp.getPropertyType().getConstraint(GeometryType.class).getBinding()));

			BoundingBox boundingBox = null; // not known at that point
			// determine SRS from GeometryMetadata or sample instance
			long srsId = determineSrsId(geoPackage, geomProp, instance, log);
			List<FeatureColumn> columns = allProperties.stream()
					.map(p -> createFeatureColumn(p, primaryKeyColumn))
					.collect(Collectors.toList());

			if (primaryKeyColumn == null) {
				// primary key column seems to be always needed (without there
				// were errors reading the data)
				columns.add(FeatureColumn.createPrimaryKeyColumn(findIdName(columns)));
			}

			geoPackage.createFeatureTableWithMetadata(geometryColumns, boundingBox, srsId, columns);

			FeatureDao featureDao = geoPackage.getFeatureDao(tableName);
			String spatialIndexType = getParameter(PARAM_SPATIAL_INDEX_TYPE).as(String.class,
					DEFAULT_SPATIAL_INDEX_TYPE);
			switch (spatialIndexType.toLowerCase()) {
			case "nga":
				createFeatureTableIndex(geoPackage, featureDao);
				break;
			case "rtree":
				createRTreeIndex(geoPackage, featureDao);
				break;
			case "none":
				// Do nothing
				break;
			default:
				throw new IllegalArgumentException(MessageFormat.format(
						"Unknown or unsupported spatial index type \"{0}\"", spatialIndexType));
			}
		}
		else {
			// create attributes table
			tableType = GeopackageTableType.ATTRIBUTE;

			List<AttributesColumn> columns = allProperties.stream()
					.map(p -> createAttributeColumn(p, primaryKeyColumn))
					.collect(Collectors.toList());

			if (primaryKeyColumn == null) {
				// primary key column seems to be always needed (without there
				// were errors reading the data)
				columns.add(AttributesColumn.createPrimaryKeyColumn(findIdName(columns)));
			}

			geoPackage.createAttributesTable(tableName, columns);
		}

		return tableType;
	}

	private void createRTreeIndex(GeoPackage geoPackage, FeatureDao featureDao) {
		FeatureIndexManager indexer = new FeatureIndexManager(geoPackage, featureDao);
		indexer.setIndexLocation(FeatureIndexType.RTREE);
		indexer.index();
	}

	private void createFeatureTableIndex(GeoPackage geoPackage, FeatureDao featureDao) {
		FeatureTableIndex idx = new FeatureTableIndex(geoPackage, featureDao);
		idx.index();
	}

	private long determineSrsId(GeoPackage geoPackage, PropertyDefinition geomProp,
			Instance instance, SimpleLog log) throws SQLException {
		GeometryMetadata geomMetadata = geomProp.getPropertyType()
				.getConstraint(GeometryMetadata.class);

		// determine from geometry metadata
		SpatialReferenceSystem srs = findSrs(geoPackage, geomMetadata.getAuthName(),
				geomMetadata.getSrs(), geomMetadata.getSrsText(), log);

		if (srs == null && geomMetadata.getSrs() != null && geomMetadata.getAuthName() != null) {
			// try to create CRS from geometry metadata
			CRSDefinition crs = new CodeDefinition(
					geomMetadata.getAuthName() + ":" + geomMetadata.getSrs());
			srs = findOrCreateSrs(geoPackage, crs, log);
		}

		if (srs == null) {
			// determine from target CRS parameter
			CRSDefinition crs = getTargetCRS();
			srs = findOrCreateSrs(geoPackage, crs, log);
		}

		if (srs == null) {
			// try to determine from example instance
			Object geom = new InstanceAccessor(instance)
					.findChildren(geomProp.getName().getLocalPart()).value();
			if (geom instanceof GeometryProperty<?>) {
				CRSDefinition crs = ((GeometryProperty<?>) geom).getCRSDefinition();
				srs = findOrCreateSrs(geoPackage, crs, log);
			}
		}

		if (srs != null) {
			return srs.getSrsId();
		}
		return 0;
	}

	private SpatialReferenceSystem findOrCreateSrs(GeoPackage geoPackage, CRSDefinition crs,
			SimpleLog log) throws SQLException {
		SpatialReferenceSystem srs = null;
		if (crs instanceof CodeDefinition) {
			String code = ((CodeDefinition) crs).getCode();
			srs = findSrs(geoPackage, null, code, null, log);
			if (srs == null) {
				// XXX creating a CRS currently only supported for EPSG codes
				String epsg = CodeDefinition.extractEPSGCode(code);
				if (epsg != null) {
					srs = createEpsgSrs(geoPackage, (CodeDefinition) crs, epsg);
					return srs;
				}
			}
		}
		else if (crs instanceof WKTDefinition) {
			String wkt = ((WKTDefinition) crs).getWkt();
			srs = findSrs(geoPackage, null, null, wkt, log);
		}
		return srs;
	}

	private SpatialReferenceSystem createEpsgSrs(GeoPackage geoPackage, CodeDefinition crs,
			String epsg) throws SQLException {
		SpatialReferenceSystemDao srsDao = geoPackage.getSpatialReferenceSystemDao();
		SpatialReferenceSystem srs = new SpatialReferenceSystem();
		CoordinateReferenceSystem geoCrs = crs.getCRS();
		srs.setSrsName(geoCrs.getName().toString());
		int codeId = Integer.parseInt(epsg);
		String wkt = geoCrs.toWKT();
		srs.setSrsId(codeId); // XXX how to avoid clashes?
		srs.setOrganization("EPSG");
		srs.setOrganizationCoordsysId(codeId);
		// XXX not sure what the difference between the definition types is
		srs.setDefinition(wkt);
		srs.setDefinition_12_063(wkt);
		if (geoCrs.getRemarks() != null) {
			srs.setDescription(geoCrs.getRemarks().toString());
		}
		srsDao.create(srs);
		return srs;
	}

	private SpatialReferenceSystem findSrs(GeoPackage geoPackage, String org, String code,
			String wkt, SimpleLog log) {
		SpatialReferenceSystemDao srsDao = geoPackage.getSpatialReferenceSystemDao();
		SpatialReferenceSystem srs = null;

		if (org == null && code != null) {
			// try to split auth and code from code

			// extract EPSG code
			String epsg = CodeDefinition.extractEPSGCode(code);
			if (epsg != null) {
				org = "EPSG";
				code = epsg;
			}
		}

		// TODO process org name to match name in database? (e.g. EPSG)

		if (org != null && code != null) {
			try {
				long codeNum = Long.parseLong(code);
				srs = srsDao.queryForOrganizationCoordsysId(org, codeNum);
			} catch (Exception e) {
				log.warn("Failed to use SRS code for geometry column", e);
			}
		}

		if (srs == null && wkt != null) {
			try {
				List<SpatialReferenceSystem> candidates = srsDao
						.queryForEq(SpatialReferenceSystem.COLUMN_DEFINITION, wkt);
				if (!candidates.isEmpty()) {
					srs = candidates.get(0);
				}
			} catch (SQLException e) {
				log.warn("Failed to retrieve SRS based on WKT definition", e);
			}

			if (srs == null) {
				// TODO create new SRS entry with definition?
			}
		}

		return srs;
	}

	private String findIdName(List<? extends UserColumn> columns) {
		Set<String> names = columns.stream().map(col -> col.getName()).collect(Collectors.toSet());

		// try some names (avoid conflicts)
		if (!names.contains("id")) {
			return "id";
		}
		if (!names.contains("fid")) {
			return "fid";
		}
		if (!names.contains("oid")) {
			return "oid";
		}
		if (!names.contains("pk")) {
			return "pk";
		}

		throw new IllegalStateException("Could not find a free name for a primary key column");
	}

	private FeatureColumn createFeatureColumn(PropertyDefinition property,
			String primaryKeyColumn) {
		String columnName = property.getName().getLocalPart();
		if (columnName.equals(primaryKeyColumn)) {
			// primary key
			return FeatureColumn.createPrimaryKeyColumn(primaryKeyColumn);
		}
		else if (property.getPropertyType().getConstraint(GeometryType.class).isGeometry()) {
			// geometry column
			Class<? extends Geometry> jtsBinding = property.getPropertyType()
					.getConstraint(GeometryType.class).getBinding();
			return FeatureColumn.createGeometryColumn(columnName, convertGeometryType(jtsBinding));
		}
		else {
			// normal column
			Class<?> binding = property.getPropertyType().getConstraint(Binding.class).getBinding();
			return FeatureColumn.createColumn(columnName, convertDataType(binding));
		}
	}

	private AttributesColumn createAttributeColumn(PropertyDefinition property,
			String primaryKeyColumn) {
		String columnName = property.getName().getLocalPart();
		if (columnName.equals(primaryKeyColumn)) {
			// primary key
			return AttributesColumn.createPrimaryKeyColumn(primaryKeyColumn);
		}
		else {
			// normal column
			Class<?> binding = property.getPropertyType().getConstraint(Binding.class).getBinding();
			return AttributesColumn.createColumn(columnName, convertDataType(binding));
		}
	}

	private GeoPackageDataType convertDataType(Class<?> binding) {
		if (binding == null) {
			// default to text
			return GeoPackageDataType.TEXT;
		}

		if (String.class.equals(binding)) {
			return GeoPackageDataType.TEXT;
		}
		if (Byte.class.equals(binding)) {
			return GeoPackageDataType.TINYINT;
		}
		if (Short.class.equals(binding)) {
			return GeoPackageDataType.SMALLINT;
		}
		if (Integer.class.equals(binding)) {
			return GeoPackageDataType.MEDIUMINT;
		}
		if (Long.class.equals(binding)) {
			return GeoPackageDataType.INTEGER;
		}
		if (Float.class.equals(binding)) {
			return GeoPackageDataType.FLOAT;
		}
		if (Double.class.equals(binding)) {
			return GeoPackageDataType.DOUBLE;
		}
		if (Boolean.class.equals(binding)) {
			return GeoPackageDataType.BOOLEAN;
		}
		if (byte[].class.equals(binding)) {
			return GeoPackageDataType.BLOB;
		}
		if (LocalDate.class.equals(binding)) {
			return GeoPackageDataType.DATE;
		}
		if (Instant.class.equals(binding) || Date.class.isAssignableFrom(binding)) {
			return GeoPackageDataType.DATETIME;
		}
		if (BigInteger.class.equals(binding)) {
			// XXX would better be represented by text, but is used as binding
			// in XML schemas for any integer
			return GeoPackageDataType.INTEGER;
		}

		// default to text
		return GeoPackageDataType.TEXT;
	}

	private mil.nga.sf.GeometryType convertGeometryType(Class<? extends Geometry> binding) {
		if (Point.class.equals(binding)) {
			return mil.nga.sf.GeometryType.POINT;
		}
		if (MultiPoint.class.equals(binding)) {
			return mil.nga.sf.GeometryType.MULTIPOINT;
		}
		if (Polygon.class.equals(binding)) {
			return mil.nga.sf.GeometryType.POLYGON;
		}
		if (MultiPolygon.class.equals(binding)) {
			return mil.nga.sf.GeometryType.MULTIPOLYGON;
		}
		if (LineString.class.equals(binding)) {
			return mil.nga.sf.GeometryType.LINESTRING;
		}
		if (MultiLineString.class.equals(binding)) {
			return mil.nga.sf.GeometryType.MULTILINESTRING;
		}

		// TODO more types

		return mil.nga.sf.GeometryType.GEOMETRY;
	}

	@Override
	protected String getDefaultTypeName() {
		return "GeoPackage";
	}

}
