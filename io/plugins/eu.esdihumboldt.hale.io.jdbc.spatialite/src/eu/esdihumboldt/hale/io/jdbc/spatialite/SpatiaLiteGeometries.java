package eu.esdihumboldt.hale.io.jdbc.spatialite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.sqlite.SQLiteConnection;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTWriter;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.GeometryAdvisor;
import eu.esdihumboldt.hale.io.jdbc.constraints.GeometryMetadata;
import eu.esdihumboldt.hale.io.jdbc.spatialite.internal.GeometryTypeMetadata;
import eu.esdihumboldt.hale.io.jdbc.spatialite.internal.SpatiaLiteSupport;
import eu.esdihumboldt.hale.io.jdbc.spatialite.internal.SpatiaLiteSupportFactory;
import eu.esdihumboldt.hale.io.jdbc.spatialite.internal.SrsMetadata;

/**
 * Geometry advisor for SpatiaLite.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteGeometries implements GeometryAdvisor<SQLiteConnection> {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteGeometries.class);

	@Override
	public boolean isFixedType(ColumnDataType columnType) {
		/*
		 * Concrete information on geometry type and SRS is not stored in the
		 * column but as metadata in the database. Therefore every column has to
		 * be configured on its own.
		 */
		return false;
	}

	@Override
	public Class<? extends Geometry> configureGeometryColumnType(SQLiteConnection connection,
			Column column, DefaultTypeDefinition type) {
		String colName = column.getName();
		String tabName = column.getParent().getName();
		SpatiaLiteSupport slSupport = SpatiaLiteSupportFactory.getInstance()
				.createSpatiaLiteSupport(connection);

		GeometryTypeMetadata geomTypeMeta = slSupport.getGeometryTypeMetadata(connection, tabName,
				colName);

		if (geomTypeMeta != null) {
			SrsMetadata srsMeta = slSupport.getSrsMetadata(connection, geomTypeMeta.getSrid());

			if (srsMeta != null) {
				// Create constraint to save the informations
				GeometryMetadata columnTypeConstraint = new GeometryMetadata(
						Integer.toString(srsMeta.getAuthSrid()), geomTypeMeta.getCoordDimension(),
						srsMeta.getSrText(), srsMeta.getAuthName());

				type.setConstraint(columnTypeConstraint);
			}

			return geomTypeMeta.getGeomType();
		}
		else {
			// no geometry column could be found
			return null;
		}
	}

	@Override
	public Object convertGeometry(GeometryProperty<?> geom, TypeDefinition columnType,
			SQLiteConnection connection) throws Exception {
		// Transform from sourceCRS to targetCRS
		GeometryMetadata columnTypeMetadata = columnType.getConstraint(GeometryMetadata.class);

		// transform
		CoordinateReferenceSystem targetCRS = null;
		if (columnTypeMetadata.getAuthName().equalsIgnoreCase("EPSG")) {
			targetCRS = CRS.decode(columnTypeMetadata.getAuthName() + ":"
					+ columnTypeMetadata.getSrs());
		}
		else {
			String wkt = columnTypeMetadata.getSrsText();
			if (wkt != null && !wkt.isEmpty()) {
				targetCRS = CRS.parseWKT(columnTypeMetadata.getSrsText());
			}
		}

		Geometry targetGeometry;
		if (targetCRS != null) {
			MathTransform transform = CRS.findMathTransform(geom.getCRSDefinition().getCRS(),
					targetCRS);
			targetGeometry = JTS.transform(geom.getGeometry(), transform);
		}
		else {
			targetGeometry = geom.getGeometry();
		}

		// encode JTS Geometry
		return encodeGeometryValue(targetGeometry, columnTypeMetadata, connection);
	}

	private Object encodeGeometryValue(Geometry value, GeometryMetadata metadata,
			SQLiteConnection connection) throws SQLException {
		// convert JTS geometry to SpatiaLite's internal BLOB format
		WKTWriter wktWriter = new WKTWriter(metadata.getDimension());
		String sqlGeomFromText = "SELECT GeomFromText(?, ?)";

		PreparedStatement stmt = connection.prepareStatement(sqlGeomFromText);
		stmt.setString(1, wktWriter.write(value));
		stmt.setInt(2, Integer.valueOf(metadata.getSrs()));

		ResultSet rs = stmt.executeQuery();

		Object encodedValue = null;
		if (rs.next()) {
			encodedValue = rs.getObject(1);
		}

		return encodedValue;
	}

	@Override
	public GeometryProperty<?> convertToInstanceGeometry(Object geom, TypeDefinition columnType,
			SQLiteConnection connection) throws Exception {
		// decode geometry read from DB
		GeometryMetadata columnTypeMetadata = columnType.getConstraint(GeometryMetadata.class);
		Geometry jtsGeom = decodeGeometryValue(geom, columnTypeMetadata, connection);

		// determine CRS
		CRSDefinition crsDef = null;
		if (columnTypeMetadata.getAuthName().equalsIgnoreCase("EPSG")) {
			String epsgCode = columnTypeMetadata.getAuthName() + ":" + columnTypeMetadata.getSrs();
			crsDef = new CodeDefinition(epsgCode, null);
		}
		else {
			crsDef = new WKTDefinition(columnTypeMetadata.getSrsText(), null);
		}

		return new DefaultGeometryProperty<Geometry>(crsDef, jtsGeom);
	}

	private Geometry decodeGeometryValue(Object geom, GeometryMetadata metadata,
			SQLiteConnection connection) throws ParseException, SQLException {
		// geom parameter is a byte[] in SpatiaLite's internal BLOB format;
		// for easy parsing with JTS, I must re-read geometry from DB in WKB
		// format
		String sqlGeomAsWKB = "SELECT AsBinary(?)";

		PreparedStatement stmt = connection.prepareStatement(sqlGeomAsWKB);
		stmt.setObject(1, geom);

		ResultSet rs = stmt.executeQuery();

		Geometry jtsGeom = null;
		if (rs.next()) {
			byte[] geomAsByteArray = rs.getBytes(1);

			// conversion to JTS via WKB
			GeometryFactory factory = new GeometryFactory();
			WKBReader wkbReader = new WKBReader(factory);

			jtsGeom = wkbReader.read(geomAsByteArray);
		}

		return jtsGeom;
	}
}
