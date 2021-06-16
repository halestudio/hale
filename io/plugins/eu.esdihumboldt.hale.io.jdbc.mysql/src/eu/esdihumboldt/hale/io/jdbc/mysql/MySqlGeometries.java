package eu.esdihumboldt.hale.io.jdbc.mysql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Supplier;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.InputStreamInStream;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.mysql.cj.jdbc.JdbcConnection;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryMetadata;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.GeometryAdvisor;
import schemacrawler.schema.BaseColumn;
import schemacrawler.schema.ColumnDataType;

//Übersetzer MySQL-Welt <> hale-Welt
//In PostGIS in separater Tabelle z.B.
//docker compose-Setup mit MySQL-Supereinfach-Beispiel, um Struktur einzusehen - dann Funktionen für Geometrien testen/Tabellenstruktur analysieren
//docker-compose für MySQL liegt in JOhanna/docker/MySQL - macht Admin-Anwendung auf 8080 und für sptäeren ZUgriff DB auf 3306
//dort noch Daten eintragen (check Hilfe für Definition Geometrien) -> in DB (hs_dev) checken, ob zusätzliche Geometrie-Tabelle auftreten oder ob z.B. in MySQL-DB eine entsprechende Tabelle mit den Geometrie-Informationen entsteht
// --> WIE KANN MAN DIE TABELLEN ÜBER EIN STATEMENT ERZEUGEN IN ADMINER? AN DER STELLE MUSS DAS SRID GESETZT WERDEN https://dev.mysql.com/doc/refman/8.0/en/spatial-type-overview.html
//angeblich "Spatial columns with no SRID attribute are not SRID-restricted and accept values with any SRID.", aber die, die ich mti SRID eingefüllt habe, haben die Info jetzt nicht mehr
//https://dev.mysql.com/doc/refman/8.0/en/spatial-reference-systems.html: MySQL maintains information about available spatial reference systems for spatial data in the data dictionary mysql.st_spatial_reference_systems table, which can store entries for projected and geographic SRSs. This data dictionary table is invisible, but SRS entry contents are available through the INFORMATION_SCHEMA ST_SPATIAL_REFERENCE_SYSTEMS table, implemented as a view on mysql.st_spatial_reference_systems
//orientierung evtl. eher an mssql als an postresql?
//https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html SRID kann aus Geometrie ausgelesen werden

/**
 * Geometry advisor for MySQL.
 * 
 * @author Florian Esser, Johanna Ott
 */

public class MySqlGeometries implements GeometryAdvisor<JdbcConnection> {

	@Override
	public boolean isFixedType(ColumnDataType columnType) {
		/*ARBEITSHYPOTHESE IN DER ANNAHME DASS DAS VON POSTGIS AUCH FÜR MYSQL GILT
		 * Concrete information on geometry type and SRS is not stored in the
		 * column but as metadata in the database. Therefore every column has to
		 * be configured on its own.
		 */
		return false;
	}

	@Override
	public Class<? extends Geometry> configureGeometryColumnType(JdbcConnection connection, BaseColumn<?> column,
			DefaultTypeDefinition type, SimpleLog log) {
		// TODO Auto-generated method stub
		Connection con = (Connection) connection;
		
		String tableName = column.getParent().getName();
		String geometryType = null;
		try {
			Statement stmt = con.createStatement();
			// Get the srid, dimension and geometry type
			/*
			 * FIXME this query should also take into account the schema and
			 * possibly the column name.
			 */
			ResultSet rs = stmt.executeQuery(
					"SELECT SRS_ID, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = "
							+ "'" + tableName + "'");
			//In MySQL, the SRID information can be added either to the geometry column or to the geometry value itself. Only the former is supported
			if (rs.next()) {
				geometryType = rs.getString("DATA_TYPE");

				// Get the epsg code for the srid
				String srid = rs.getString("SRS_ID");
				ResultSet r = stmt.executeQuery(
						"SELECT ORGANIZATION_COORDSYS_ID, ORGANIZATION, DEFINITION FROM spatial_ref_sys WHERE SRS_ID = "
								+ srid);
				if (r.next()) {
					//TODO
					// Create Constraint to save the informations
					GeometryMetadata columnTypeConstraint = new GeometryMetadata(
							r.getString("ORGANIZATION_COORDSYS_ID"), GeometryMetadata.UNKNOWN_DIMENSION,
							r.getString("DEFINITION"), r.getString("ORGANIZATION"));
					type.setConstraint(columnTypeConstraint);

					log.info("Determined geometry metadata for table {0} with SRID {1}: {2}",
							tableName, srid, columnTypeConstraint);
				}
				else {
					log.warn("Could not determine SRS information for SRID " + srid);
				}
			}
			else {
				// XXX what if no SRID information is present? is that a case
				log.warn("Could not determine SRS information for table {0}", tableName);
			}
		} catch (SQLException e) {
			log.error("Error trying to retrieve geometry information for table " + tableName, e);
		}

		// In this case we have no geometry column information
		if (geometryType == null) {
			// use geometry even if no geometry column is present describing it
			return Geometry.class;
		}
		// return the geometryType
		if (geometryType.equalsIgnoreCase("MultiPolygon")) {
			return MultiPolygon.class;
		}
		else if (geometryType.equalsIgnoreCase("MultiPoint")) {
			return MultiPoint.class;
		}
		else if (geometryType.equalsIgnoreCase("MultiLineString")) {
			return MultiLineString.class;
		}
		else if (geometryType.equalsIgnoreCase("LinearRing")) {
			return LinearRing.class;
		}
		else if (geometryType.equalsIgnoreCase("LineString")) {
			return LineString.class;
		}
		else if (geometryType.equalsIgnoreCase("Point")) {
			return Point.class;
		}
		else if (geometryType.equalsIgnoreCase("Polygon")) {
			return Polygon.class;
		}
		else {
			return Geometry.class;
		}
	}

	@Override
	public Object convertGeometry(GeometryProperty<?> geom, TypeDefinition columnType, JdbcConnection connection,
			SimpleLog log) throws Exception {
		// TODO Auto-generated method stub
		//AUS DER JTS MUSS EIN WKB/WKT (?) ZURÜCKGEGEBEN WERDEN ODER IRGENDWAS; DAS DER JDBC TREIBER IN DIE DATENBANK SCHREIBEN KANN - FUNKTION GIBT AN ABSTRAKTEN JDBC INSTANCE WRITER; DER MIT DEM INPUT UMGEHEN KÖNNEN MUSS
		//INTERN WIRD DANN SETSTATEMENTPARAMETER VOM JDBCINSTANCEWRITER GENUTZT - NICHT VON MIR ABER DAS MUSS MIT DEM INPUT UMGEHEN KÖNNEN
		//MUSS HIER Geometry GENUTZT WERDEN (IST DANN LOCATIONTECH GEOMETRIE) ODER ZUSÄTZLICH DIE GEOMETRIE AUS OPENGIS IMPORTIERT UND GENUTZT WERDEN WEIL https://dev.mysql.com/doc/refman/8.0/en/spatial-type-overview.html
		//WIESO HAT ER HIER KEINE PROBLEME DIE ABSTRAKTE KLASSE ZU INSTANZIIEREN SO WIE UNTEN?
		byte[] mGeometry = null;
		// Transform from sourceCRS to targetCRS
		GeometryMetadata columnTypeMetadata = columnType.getConstraint(GeometryMetadata.class);

		// transform
		CoordinateReferenceSystem targetCRS = null;
		String authName = columnTypeMetadata.getAuthName();
		if (authName != null && authName.equals("EPSG")) {
			// PostGIS assumes lon/lat (use CodeDefinition because of cache) - IS THIS TRUE FOR MYSQL AS WELL?
			targetCRS = new CodeDefinition(authName + ":" + columnTypeMetadata.getSrs(), true)
					.getCRS();
		}
		else {
			String wkt = columnTypeMetadata.getSrsText();
			if (wkt != null && !wkt.isEmpty()) {
				targetCRS = CRS.parseWKT(wkt);
			}
		}

		//IST DAS HIER NUR NOTWENDIG, WENN DAS ZIEL IN EINEM BESTIMMTEN CRS ERWARTET WIRD? targetCRS WIRD AUS ÜBERGABEPARAMETER colomnType AUSGELESEN, IST DER HIER ÜBERHAUPT NOTWENDIG?
		//columnType enthält Infos aus DB und damit Infos dazu, wie die Geometrie erwartet wird
		Geometry targetGeometry;
		if (targetCRS != null) {
			MathTransform transform = CRS.findMathTransform(geom.getCRSDefinition().getCRS(),
					targetCRS);
			targetGeometry = JTS.transform(geom.getGeometry(), transform);
		}
		else {
			targetGeometry = geom.getGeometry();
		}

		// Convert the jts Geometry to WKB
		WKBWriter writer = new WKBWriter();
		//TYP VON MGEOEMTRY IST JETZT BYTE. IST DAS ETWAS; WOMIT DER JDBC TREIBER ETWAS ANFANGEN KANN? -> ja
		mGeometry = writer.write(targetGeometry);
		
		return mGeometry;
	}

	@Override
	public GeometryProperty<?> convertToInstanceGeometry(Object geom, TypeDefinition columnType,
			JdbcConnection connection, Supplier<CRSDefinition> crsProvider, SimpleLog log) throws Exception {
		// TODO Auto-generated method stub
		// geom object, das rein kommt, hängt von der verwendeten Quelle ab, wird aber in einer generichen Funktion eingelesen. Wir gehen davon aus, dass es ein InputStream ist.
		//ggf. breakpoint machen um diese Hypothese zu prüfen
		if (geom instanceof InputStream) {
			InputStream geominput = (InputStream) geom;

			// conversion to JTS via WKT
			// TODO use better conversion (p4b?)
			//aus MySQL kommt evtl. ein WKB (keine eigene Geometrie-Klasse), WKB reader gibt es aber auch
			WKBReader reader = new WKBReader();
			InputStreamInStream isis = new InputStreamInStream(geominput);
	         
			Geometry jtsGeom = reader.read(isis);

			// determine CRS
			GeometryMetadata columnTypeMetadata = columnType.getConstraint(GeometryMetadata.class);
			CRSDefinition crsDef = null;
			String authName = columnTypeMetadata.getAuthName();
			if (authName != null && authName.equals("EPSG")) {
				// PostGIS assumes lon/lat order - SPÄTER FÜR MY SQL PRÜFEN
				crsDef = new CodeDefinition(authName + ":" + columnTypeMetadata.getSrs(), true);
			}
			else {
				String wkt = columnTypeMetadata.getSrsText();
				if (wkt != null) {
					crsDef = new WKTDefinition(wkt, null);
				}
			}

			return new DefaultGeometryProperty<Geometry>(crsDef, jtsGeom);
		}

		throw new IllegalArgumentException("Only conversion of InputStream supported");
	}
}
