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

import java.net.URI;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryMetadata;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.attributes.AttributesDao;
import mil.nga.geopackage.core.srs.SpatialReferenceSystem;
import mil.nga.geopackage.db.GeoPackageDataType;
import mil.nga.geopackage.features.columns.GeometryColumns;
import mil.nga.geopackage.features.user.FeatureColumn;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.user.UserColumn;
import mil.nga.geopackage.user.UserDao;
import mil.nga.geopackage.user.UserTable;

/**
 * Class that derives a hale schema from a {@link GeoPackage}.
 * 
 * @author Simon Templer
 */
public class GeopackageSchemaBuilder {

	/**
	 * Default namespace for GeoPackage schemas and types.
	 */
	public static final String DEFAULT_NAMESPACE = "http://www.esdi-humboldt.eu/hale/gpkg";

	private final String defaultNamespace;

	/**
	 * Create a new schema builder.
	 */
	public GeopackageSchemaBuilder() {
		this(DEFAULT_NAMESPACE);
	}

	/**
	 * Create a new schema builder.
	 * 
	 * @param defaultNamespace the default namespace for schema and types
	 */
	protected GeopackageSchemaBuilder(String defaultNamespace) {
		super();
		this.defaultNamespace = defaultNamespace;
	}

	/**
	 * Build a schema.
	 * 
	 * @param gpkg the geopackage
	 * @param location the schema location or <code>null</code>
	 * @return the hale schema
	 */
	public Schema buildSchema(GeoPackage gpkg, URI location) {
		List<String> tables = gpkg.getFeatureTables();

		DefaultSchema schema = new DefaultSchema(defaultNamespace, location);

		tables.stream().forEach(table -> {
			try {
				FeatureDao features = gpkg.getFeatureDao(table);
				GeometryColumns geomColumns = gpkg.getGeometryColumnsDao().queryForTableName(table);
				schema.addType(buildType(features, geomColumns, schema));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

		tables = gpkg.getAttributesTables();

		tables.stream().forEach(table -> {
			AttributesDao attributes = gpkg.getAttributesDao(table);
			schema.addType(buildType(attributes, null, schema));
		});

		return schema;
	}

	private TypeDefinition buildType(UserDao<?, ?, ?, ?> dao, GeometryColumns geomColumns,
			DefaultSchema schema) {
		QName name = new QName(defaultNamespace, dao.getTableName());
		DefaultTypeDefinition type = new DefaultTypeDefinition(name);

		type.setConstraint(MappableFlag.ENABLED);
		type.setConstraint(MappingRelevantFlag.ENABLED);
		type.setConstraint(HasValueFlag.DISABLED);
		type.setConstraint(AbstractFlag.DISABLED);
		type.setConstraint(Binding.get(Instance.class));

		UserTable<? extends UserColumn> table = dao.getTable();

		// set primary key constraint
		UserColumn pkColumn = table.getPkColumn();
		if (pkColumn != null) {
			type.setConstraint(
					new PrimaryKey(Collections.singletonList(new QName(pkColumn.getName()))));
		}

		for (String columnName : table.getColumnNames()) {
			UserColumn column = table.getColumn(columnName);

			QName propertyName = new QName(columnName);

			// determine property type
			TypeDefinition propertyType = getOrCreatePropertyType(column, geomColumns, schema);

			DefaultPropertyDefinition property = new DefaultPropertyDefinition(propertyName, type,
					propertyType);
			property.setConstraint(Cardinality.CC_EXACTLY_ONCE);
			if (column.isNotNull()) {
				property.setConstraint(NillableFlag.DISABLED);
			}
			else {
				property.setConstraint(NillableFlag.ENABLED);
			}
		}

		return type;
	}

	private TypeDefinition getOrCreatePropertyType(UserColumn column, GeometryColumns geomColumns,
			DefaultSchema schema) {
		String localName;

		if (column instanceof FeatureColumn && ((FeatureColumn) column).isGeometry()) {
			localName = ((FeatureColumn) column).getGeometryType().getName();
		}
		else {
			localName = column.getDataType().name();
		}

		QName typeName = new QName(defaultNamespace, localName);

		TypeDefinition type = schema.getType(typeName);
		if (type != null) {
			// use existing type
			return type;
		}
		else {
			DefaultTypeDefinition typeDef = new DefaultTypeDefinition(typeName);

			typeDef.setConstraint(MappingRelevantFlag.DISABLED);
			typeDef.setConstraint(MappableFlag.DISABLED);
			typeDef.setConstraint(HasValueFlag.ENABLED); // simple type

			if (column instanceof FeatureColumn && ((FeatureColumn) column).isGeometry()) {
				mil.nga.sf.GeometryType geomType = ((FeatureColumn) column).getGeometryType();

				typeDef.setConstraint(Binding.get(GeometryProperty.class));

				Class<? extends Geometry> geomClass = Geometry.class;
				switch (geomType) {
				case LINESTRING:
					geomClass = LineString.class;
					break;
				case MULTIPOINT:
					geomClass = MultiPoint.class;
					break;
				case MULTILINESTRING:
					geomClass = MultiLineString.class;
					break;
				case MULTIPOLYGON:
					geomClass = MultiPolygon.class;
					break;
				case POINT:
					geomClass = Point.class;
					break;
				case POLYGON:
					geomClass = Polygon.class;
					break;
				default:
					break;
				}

				typeDef.setConstraint(GeometryType.get(geomClass));

				SpatialReferenceSystem srs = geomColumns.getSrs();
				if (srs != null) {
					String code = String.valueOf(srs.getOrganizationCoordsysId());
					int dimension = GeometryMetadata.UNKNOWN_DIMENSION;
					String srsText = srs.getDefinition();
					String auth_name = srs.getOrganization();
					typeDef.setConstraint(
							new GeometryMetadata(code, dimension, srsText, auth_name));
				}
			}
			else {
				GeoPackageDataType dataType = column.getDataType();

				Class<?> binding;
				switch (dataType) {
				case DATETIME:
					binding = Instant.class;
					break;
				case DATE:
					binding = LocalDate.class;
					break;
				default:
					binding = dataType.getClassType();
				}
				typeDef.setConstraint(Binding.get(binding));
			}

			return typeDef;
		}
	}
}
