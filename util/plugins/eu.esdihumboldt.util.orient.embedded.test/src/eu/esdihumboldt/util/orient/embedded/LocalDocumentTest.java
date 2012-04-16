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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Tests basic functions in the document based DB in the file system
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class LocalDocumentTest extends AbstractDocumentTest {
	
	private static final String TEST_DB = "local:" + new File(new File(System.getProperty("java.io.tmpdir")), "testDB_"
			+ UUID.randomUUID().toString()).getAbsolutePath();
	
	private ODatabaseDocumentTx db;
	
	/**
	 * @see AbstractDocumentTest#init()
	 */
	@Override
	public void init() {
//		assertNotNull(EmbeddedOrientDB.getServer()); // to activate the embedded DB
		
		db = new ODatabaseDocumentTx(TEST_DB).create();
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
	 * Test connecting to the database from another thread
	 * @throws Throwable if any exception occurs
	 */
	@Test
	public void testThread() throws Throwable {
		createMiaAndTim();
		
		ExecutorService xs = Executors.newSingleThreadExecutor();
		Future<Throwable> future = xs.submit(new Callable<Throwable>() {
			
			@Override
			public Throwable call() throws Exception {
				try {
					ODatabaseDocumentTx db2 = new ODatabaseDocumentTx(TEST_DB).open("admin", "admin");
//					ODatabaseDocumentTx db2 = ODatabaseDocumentPool.global().acquire(TEST_DB, "admin", "admin");
					assertEquals(2, db2.countClass("Person"));
					
					createLuke(db2);
					assertEquals(3, db2.countClass("Person"));
					
					db2.close();
					
					return null;
				} catch (Throwable e) {
					return e;
				}
			}

		});
		Throwable error = future.get();
		if (error != null) {
			throw error;
		}
	}

	/**
	 * @see AbstractDocumentTest#getDb()
	 */
	@Override
	protected ODatabaseDocumentTx getDb() {
		return db;
	}
	
}
