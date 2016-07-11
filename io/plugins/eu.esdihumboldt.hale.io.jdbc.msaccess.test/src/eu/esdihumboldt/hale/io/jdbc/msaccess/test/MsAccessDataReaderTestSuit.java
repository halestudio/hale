package eu.esdihumboldt.hale.io.jdbc.msaccess.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

/**
 * To test Access database
 * 
 * @author Arun
 *
 */
public abstract class MsAccessDataReaderTestSuit {

	/**
	 * Source Database name
	 */
	protected String SOURCE_DB_NAME;

	/**
	 * Source Database path
	 */
	protected String SOURCE_DB_PATH;

	/**
	 * User name to connect to database
	 */
	protected String USER_NAME;

	/**
	 * password to connect to database
	 */
	protected String PASSWORD;

	/**
	 * Query to execute
	 */
	protected String SQL_QUERY;

	private static Long RANDOM_NUMBER;

	/**
	 * Copies the source database to a temporary file.
	 * 
	 * @throws IOException if temp file can't be created
	 */
	public void createSourceTempFile() throws IOException {
		ByteSource source = Resources.asByteSource(
				MsAccessDataReaderTestSuit.class.getClassLoader().getResource(SOURCE_DB_PATH));
		ByteSink dest = Files.asByteSink(new File(getSourceTempFilePath()));

		source.copyTo(dest);
	}

	/**
	 * Generates a random path (within the system's temporary folder) for the
	 * source database. The random number used to construct the path is saved in
	 * a static variable and thus the path will remain constant for the whole
	 * run.
	 * 
	 * @return the absolute path of the source temp file
	 */
	public String getSourceTempFilePath() {
		return getTempDir() + File.separator + getRandomNumber() + "_" + SOURCE_DB_NAME;
	}

	private static String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}

	/**
	 * Deletes the source temp file.
	 */
	public void deleteSourceTempFile() {
		deleteTempFile(getSourceTempFilePath());
	}

	private void deleteTempFile(String tempFilePath) {
		File toBeDeleted = new File(tempFilePath);
		if (toBeDeleted.exists()) {
			toBeDeleted.delete();
		}
	}

	private static long getRandomNumber() {
		if (RANDOM_NUMBER == null) {
			RANDOM_NUMBER = System.currentTimeMillis();
		}
		return RANDOM_NUMBER;
	}

	/**
	 * To get {@link Connection} object by giving database location, user name
	 * and password.
	 * 
	 * @return Connection object
	 */
	public Connection getConnection() {
		Connection con = null;

		try {
			con = DriverManager.getConnection("jdbc:ucanaccess://" + getSourceTempFilePath(),
					USER_NAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return con;

	}

	/**
	 * Connection to MS Access database and getting first row data.
	 * 
	 * @return String value, First row data joined by delimiter \t
	 */
	public String getFirstData() {
		Connection con;
		Statement st;
		ResultSet rs;

		try {
			con = getConnection();

			st = con.createStatement();
			rs = st.executeQuery(SQL_QUERY);

			StringBuilder row = new StringBuilder();
			if (rs != null) {
				rs.next();

				row.append(String.valueOf(rs.getInt(1)));
				row.append("\t");
				row.append(rs.getString(2));
				row.append("\t");
				row.append(String.valueOf(rs.getInt(3)));
				row.append("\t");
				row.append(String.valueOf(rs.getInt(4)));

			}

			return row.toString();

		} catch (SQLException e) {
			System.out.println(e.toString());
		}

		return null;
	}

}
//'eu.esdihumboldt.hale.io.jdbc.msaccess',