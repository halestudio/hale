package eu.esdihumboldt.hale.io.jdbc.spatialite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.sqlite.SQLiteConnection;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
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

public class SpatiaLiteGeometries implements GeometryAdvisor<SQLiteConnection> {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteGeometries.class);

	/*
	 * see: https://www.gaia-gis.it/fossil/libspatialite/wiki?name=switching-to-4.0
	 */
	private static final int[] TYPE_GEOMETRY = new int[] {0, 1000, 2000, 3000};
	private static final int[] TYPE_POINT = new int[] {1, 1001, 2001, 3001};
	private static final int[] TYPE_LINESTRING = new int[] {2, 1002, 2002, 3002};
	private static final int[] TYPE_POLYGON = new int[] {3, 1003, 2003, 3003};
	private static final int[] TYPE_MULTIPOINT = new int[] {4, 1004, 2004, 3004};
	private static final int[] TYPE_MULTILINESTRING = new int[] {5, 1005, 2005, 3005};
	private static final int[] TYPE_MULTIPOLYGON = new int[] {6, 1006, 2006, 3006};
	private static final int[] TYPE_GEOMETRYCOLLECTION = new int[] {7, 1007, 2007, 3007};
	
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
	public Class<? extends Geometry> configureGeometryColumnType(
			SQLiteConnection connection, Column column,
			DefaultTypeDefinition type) {
		
		String columnName = column.getName();
		String tableName = column.getParent().getName();
		int geometryType = -1;
		PreparedStatement stmt = null;
		PreparedStatement stmtMeta = null;
		ResultSet rs = null;
		ResultSet rsMeta = null;
		try {
			String sql = "SELECT "
					+ "		srid,geometry_type,coord_dimension "
					+ "	FROM "
					+ "		geometry_columns "
					+ "	WHERE "
					+ "		f_table_name = ? AND f_geometry_column = ?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, tableName.toLowerCase());
			stmt.setString(2, columnName.toLowerCase());
			
			// Get the srid, dimension and geometry type
			rs = stmt.executeQuery();
			if (rs.next()) {
				geometryType = rs.getInt("geometry_type");
				Integer dimension = rs.getInt("coord_dimension");

				// Get the epsg code for the srid
				Integer srid = rs.getInt("srid");
				String sqlMeta = "SELECT "
							+ "		auth_srid, auth_name, srtext "
							+ "	FROM "
							+ "		spatial_ref_sys "
							+ "	WHERE "
							+ "		srid = ?";
				stmtMeta = connection.prepareStatement(sqlMeta);
				stmtMeta.setInt(1, srid);
				rsMeta = stmtMeta.executeQuery();
				if (rsMeta.next()) {
					// Create Constraint to save the informations
					GeometryMetadata columnTypeConstraint = new GeometryMetadata(
							rsMeta.getString("auth_srid"), dimension,
							rsMeta.getString("srtext"), rsMeta.getString("auth_name"));
					type.setConstraint(columnTypeConstraint);
				}
			}
		} catch (SQLException e) {
			String errMsg = String.format(
					"Error configuring geometry column \"%s\" in table \"%s\"",
					columnName, tableName);
			log.error(errMsg, e);
		} finally {
			closeFinally(stmt, rs);
			closeFinally(stmtMeta, rsMeta);
		}

		// In this case we have no geometry column
		if (geometryType == -1) {
			return null;
		}
		// return the geometryType
		if (Arrays.binarySearch(TYPE_MULTIPOLYGON, geometryType) >= 0) {
			return MultiPolygon.class;
		}
		else if (Arrays.binarySearch(TYPE_MULTIPOINT, geometryType) >= 0) {
			return MultiPoint.class;
		}
		else if (Arrays.binarySearch(TYPE_MULTILINESTRING, geometryType) >= 0) {
			return MultiLineString.class;
		}
		else if (Arrays.binarySearch(TYPE_LINESTRING, geometryType) >= 0) {
			return LineString.class;
		}
		else if (Arrays.binarySearch(TYPE_POINT, geometryType) >= 0) {
			return Point.class;
		}
		else if (Arrays.binarySearch(TYPE_POLYGON, geometryType) >= 0) {
			return Polygon.class;
		}
		else if (Arrays.binarySearch(TYPE_GEOMETRYCOLLECTION, geometryType) >= 0) {
			return GeometryCollection.class;
		}
		else if (Arrays.binarySearch(TYPE_GEOMETRY, geometryType) >= 0) {
			return Geometry.class;
		} else {
			throw new IllegalArgumentException("Unsupported geometry type: " + geometryType);
		}
	}

	private void closeFinally(Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// ignore
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// ignore
			}
		}
	}

	@Override
	public Object convertGeometry(GeometryProperty<?> geom,
			TypeDefinition columnType, SQLiteConnection connection)
			throws Exception {
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

	private Object encodeGeometryValue(Geometry value, GeometryMetadata metadata, SQLiteConnection connection) throws SQLException {
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
	public GeometryProperty<?> convertToInstanceGeometry(Object geom,
			TypeDefinition columnType, SQLiteConnection connection)
			throws Exception {
		// decode geometry read from DB
		GeometryMetadata columnTypeMetadata = columnType.getConstraint(GeometryMetadata.class);
		Geometry jtsGeom = decodeGeometryValue(geom, columnTypeMetadata, connection);

		// determine CRS
		CRSDefinition crsDef = null;
		if (columnTypeMetadata.getAuthName().equals("EPSG")) {
			crsDef = new CodeDefinition(columnTypeMetadata.getAuthName() + ":"
					+ columnTypeMetadata.getSrs(), null);
		}
		else {
			crsDef = new WKTDefinition(columnTypeMetadata.getSrsText(), null);
		}

		return new DefaultGeometryProperty<Geometry>(crsDef, jtsGeom);
	}

	private Geometry decodeGeometryValue(Object geom,
			GeometryMetadata metadata, SQLiteConnection connection)
			throws ParseException, SQLException {
		// geom parameter is a byte[] in SpatiaLite's internal BLOB format;
		// for easy parsing with JTS, I must re-read geometry from DB in WKB format
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
