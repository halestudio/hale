/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.util.orient.embedded;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Test the iterator provided through browseClass.
 * 
 * @author Simon Templer
 */
public class BrowseClassIterateTest {

	private static final String TEST_DB = "local:"
			+ new File(new File(System.getProperty("java.io.tmpdir")),
					"testDB_" + UUID.randomUUID().toString()).getAbsolutePath();

	private ODatabaseDocumentTx db;

//	private static OServer server;

	/**
	 * Test if the correct number of persons is retrieved through the iterator
	 */
	@Test
	public void testCount() {
		createMiaAndTim();

		ORecordIteratorClass<ODocument> it = getDb().browseClass("Person");

		// first person
		assertTrue(it.hasNext());
		assertNotNull(it.next());

		// second person
		assertTrue(it.hasNext());
		assertNotNull(it.next());

		assertFalse(it.hasNext());
	}

	/**
	 * Test if multiple hasNext calls before next change the behavior
	 */
	@Ignore
	// Bug in OrientDB 1.0rc8!
	@Test
	public void testHasNext() {
		createMiaAndTim();

		ORecordIteratorClass<ODocument> it = getDb().browseClass("Person");

		// additional hasNext call
		assertTrue(it.hasNext());

		// first person
		assertTrue(it.hasNext());
		assertNotNull(it.next());

		// second person
		assertTrue(it.hasNext());
		assertNotNull(it.next());

		assertFalse(it.hasNext());
	}

	/**
	 * Test if multiple hasNext calls before next change the behavior
	 */
	@Ignore
	// Bug in OrientDB 1.0rc8!
	@Test
	public void testHasNext2() {
		createMiaAndTim();

		ORecordIteratorClass<ODocument> it = getDb().browseClass("Person");

		// call hasNext only once
		it.hasNext();
		ODocument var1 = it.next();

		it = getDb().browseClass("Person");

		// call hasNext twice
		it.hasNext();
		it.hasNext();
		ODocument var2 = it.next();

		assertEquals(var1, var2);
	}

	/**
	 * Create persons Mia and Tim and store them in the database
	 */
	protected void createMiaAndTim() {
		ODocument docMia = new ODocument("Person");
		docMia.field("name", "Mia");
		docMia.field("surname", "Serenade");
		docMia.field("city",
				new ODocument("City").field("name", "Tokio").field("country", "Japan"));
		docMia.save();

		ODocument docTim = new ODocument("Person");
		docTim.field("name", "Tim");
		docTim.field("surname", "Takati");
		docTim.field("city",
				new ODocument("City").field("name", "Los Angeles").field("country", "USA"));
		docTim.save();

		// XXX what is the difference between class and cluster?
		assertEquals(2, getDb().countClass("Person")); // schema?
		assertEquals(2, getDb().countClusterElements("Person")); // schema-less?
	}

	/**
	 * Get the database
	 * 
	 * @return the database
	 */
	protected ODatabaseDocumentTx getDb() {
		return db;
	}

	/**
	 * Initialize the server
	 * 
	 * @throws Exception if creating the DB server fails
	 */
	@BeforeClass
	public static void initServer() throws Exception {
//		server = EmbeddedOrientDB.getServer();

//		server = OServerMain.create();
//		server.startup("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
//		   + "<orient-server>"
//		   + "<network>"
//		   + "<protocols>"
//		   + "<protocol name=\"binary\" implementation=\"com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary\"/>"
//		   + "<protocol name=\"http\" implementation=\"com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb\"/>"
//		   + "</protocols>"
//		   + "<listeners>"
////		   + "<listener ip-address=\"0.0.0.0\" port-range=\"2424-2430\" protocol=\"binary\"/>"
////		   + "<listener ip-address=\"0.0.0.0\" port-range=\"2480-2490\" protocol=\"http\"/>"
//		   + "</listeners>"
//		   + "</network>"
//		   + "<users>"
//		   + "<user name=\"root\" password=\"root\" resources=\"*\"/>"
//		   + "</users>"
//		   + "<properties>"
//		   //+ "<entry name=\"orientdb.www.path\" value=\"C:/work/dev/orientechnologies/orientdb/releases/1.0rc1-SNAPSHOT/www/\"/>"
//		   //+ "<entry name=\"orientdb.config.file\" value=\"C:/work/dev/orientechnologies/orientdb/releases/1.0rc1-SNAPSHOT/config/orientdb-server-config.xml\"/>"
//		   + "<entry name=\"server.cache.staticResources\" value=\"false\"/>"
//		   + "<entry name=\"log.console.level\" value=\"info\"/>" + "<entry name=\"log.file.level\" value=\"info\"/>"
//		   + "</properties>" 
//		   + "</orient-server>");
	}

	/**
	 * Create the test database
	 */
	@SuppressWarnings("resource")
	@Before
	public void init() {
//		assertNotNull(server);
		db = new ODatabaseDocumentTx(TEST_DB).create();
	}

	/**
	 * Dispose the test database
	 */
	@After
	public void dispose() {
		db.drop();
		db.close();
	}

}
