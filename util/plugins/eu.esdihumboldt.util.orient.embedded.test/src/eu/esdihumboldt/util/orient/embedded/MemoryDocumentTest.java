/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.util.orient.embedded;

import java.util.UUID;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Tests basic functions in the document based DB in memory
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class MemoryDocumentTest extends AbstractDocumentTest {

	/**
	 * The URI of the test DB
	 */
	private static final String TEST_DB = "memory:DocumentTest";

	private ODatabaseDocumentTx db;

	/**
	 * @see AbstractDocumentTest#init()
	 */
	@Override
	public void init() {
//		assertNotNull(EmbeddedOrientDB.getServer()); // to activate the embedded DB

		// use random db name as deletion doesn't seem to be supported (in
		// 1.0rc1)
		db = new ODatabaseDocumentTx(TEST_DB + "_" + UUID.randomUUID().toString()).create();
	}

	/**
	 * @see AbstractDocumentTest#dispose()
	 */
	@Override
	public void dispose() {
		db.delete();
		db.close();
	}

	/**
	 * @see AbstractDocumentTest#getDb()
	 */
	@Override
	protected ODatabaseDocumentTx getDb() {
		return db;
	}

}
