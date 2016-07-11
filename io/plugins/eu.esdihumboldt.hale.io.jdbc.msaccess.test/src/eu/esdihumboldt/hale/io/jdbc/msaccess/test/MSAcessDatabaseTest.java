package eu.esdihumboldt.hale.io.jdbc.msaccess.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.esdihumboldt.hale.io.jdbc.msaccess.MsAccessDataReader;

public class MSAcessDatabaseTest {

	@Test
	public void testgetData() {

		String rs = MsAccessDataReader.getFirstData();

		assertTrue(rs != null);

		assertTrue(rs.length() > 0);
	}

}
