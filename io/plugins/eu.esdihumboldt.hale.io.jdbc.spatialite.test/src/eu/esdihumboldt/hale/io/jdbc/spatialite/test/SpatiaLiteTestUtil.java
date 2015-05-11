package eu.esdihumboldt.hale.io.jdbc.spatialite.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

public class SpatiaLiteTestUtil {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteTestUtil.class);

	private static final String WKT_POLYGON = "POLYGON((146.232727 -42.157501, 146.238007 -42.16111, "
			+ "146.24411 -42.169724, 146.257202 -42.193329, 146.272217 -42.209442, "
			+ "146.274689 -42.214165, 146.27832 -42.21833, 146.282471 -42.228882, "
			+ "146.282745 -42.241943, 146.291351 -42.255836, 146.290253 -42.261948, "
			+ "146.288025 -42.267502, 146.282471 -42.269997, 146.274994 -42.271111, "
			+ "146.266663 -42.270279, 146.251373 -42.262505, 146.246918 -42.258057, "
			+ "146.241333 -42.256111, 146.23468 -42.257782, 146.221344 -42.269165, "
			+ "146.210785 -42.274445, 146.20163 -42.27417, 146.196075 -42.271385, "
			+ "146.186646 -42.258057, 146.188568 -42.252785, 146.193298 -42.249443, "
			+ "146.200806 -42.248055, 146.209137 -42.249168, 146.217468 -42.248611, "
			+ "146.222473 -42.245277, 146.22525 -42.240555, 146.224121 -42.22805, "
			+ "146.224396 -42.221382, 146.228302 -42.217216, 146.231354 -42.212502, "
			+ "146.231628 -42.205559, 146.219421 -42.186943, 146.21637 -42.17028, "
			+ "146.216644 -42.16333, 146.219696 -42.158607, 146.225525 -42.156105, "
			+ "146.232727 -42.157501))";
	private static Geometry POLYGON = null;

	static {
		try {
			POLYGON = new WKTReader().read(WKT_POLYGON);
		} catch (ParseException e) {
			// should never happen
		}
	}

	public static final String SOURCE_DB_NAME = "tasmania_water_bodies.sqlite";
	public static final String SOURCE_DB_LOCATION = "/data/" + SOURCE_DB_NAME;
	public static final String TARGET_DB_NAME = "tasmania_water_bodies_target.sqlite";
	public static final String TARGET_DB_LOCATION = "/data/" + TARGET_DB_NAME;

	public static final String SOUURCE_TYPE_LOCAL_NAME = "tasmania_water_bodies";
	public static final String TARGET_TYPE_LOCAL_NAME = "tasmania_water_bodies_target";
	public static final String[] SOUURCE_TYPE_PROPERTY_NAMES = new String[] { "AREA", "CNTRY_NAME",
			"CONTINENT", "Geometry", "PERIMETER", "PK_UID", "WATER_TYPE" };
	public static final Object[] SOUURCE_TYPE_PROPERTY_VALUES = new Object[] { 1064866676,
			"Australia", "Australia", POLYGON, 1071221047, 1, "Lake" };

	private static Long RANDOM_NUMBER = null;

	public static void createSourceTempFile() throws IOException {
		ByteSource source = Resources.asByteSource(SpatiaLiteTestUtil.class
				.getResource(SOURCE_DB_LOCATION));
		ByteSink dest = Files.asByteSink(new File(getSourceTempFilePath()));

		source.copyTo(dest);
	}

	public static void createTargetTempFile() throws IOException {
		ByteSource source = Resources.asByteSource(SpatiaLiteTestUtil.class
				.getResource(TARGET_DB_LOCATION));
		ByteSink dest = Files.asByteSink(new File(getTargetTempFilePath()));

		source.copyTo(dest);
	}

	public static void deleteSourceTempFile() {
		deleteTempFile(getSourceTempFilePath());
	}

	public static void deleteTargetTempFile() {
		deleteTempFile(getTargetTempFilePath());
	}

	public static String getSourceTempFilePath() {
		return getTempDir() + File.separator + getRandomNumber() + "_" + SOURCE_DB_NAME;
	}

	public static String getTargetTempFilePath() {
		return getTempDir() + File.separator + getRandomNumber() + "_" + TARGET_DB_NAME;
	}

	private static String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}

	private static long getRandomNumber() {
		if (RANDOM_NUMBER == null) {
			RANDOM_NUMBER = System.currentTimeMillis();
		}
		return RANDOM_NUMBER;
	}

	private static void deleteTempFile(String tempFilePath) {
		File toBeDeleted = new File(tempFilePath);
		if (toBeDeleted.exists()) {
			toBeDeleted.delete();
		}
	}

	public static boolean isSpatiaLiteExtensionAvailable() {
		Connection conn = null;

		try {
			// enabling dynamic extension loading
			// absolutely required by SpatiaLite
			SQLiteConfig config = new SQLiteConfig();
			config.enableLoadExtension(true);

			String dbPath = getSourceTempFilePath();
			if (!new File(dbPath).exists()) {
				createSourceTempFile();
			}

			// create a database connection
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath, config.toProperties());
			Statement stmt = conn.createStatement();
			stmt.setQueryTimeout(30); // set timeout to 30 sec.

			// loading SpatiaLite
			stmt.execute("SELECT load_extension('mod_spatialite')");
		} catch (Exception e) {
			log.warn("Could not load SpatiaLite extension", e);

			return false;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// ignore
			}
		}

		return true;
	}

}
