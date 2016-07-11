package eu.esdihumboldt.hale.io.jdbc.msaccess.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

/**
 * 
 * Test for MS Access database reader
 * 
 * @author Arun
 *
 */
public class MSAcessDatabaseTest {

	/**
	 * Testing connection and data reading from access database
	 */
	@Test
	public void testgetData() {

		MsAccessDataReaderTestSuit obj = new MsAccessDataReader();
		try {
			obj.createSourceTempFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String rs = obj.getFirstData();

		assertTrue(rs != null);
		assertTrue(rs.length() > 0);

		obj.deleteSourceTempFile();

	}

}
