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

package eu.esdihumboldt.hale.orient.embedded;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.query.nativ.ONativeSynchQuery;
import com.orientechnologies.orient.core.query.nativ.OQueryContextNativeSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * Basic tests on the document based DB
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractDocumentTest {
	
	/**
	 * Get the database
	 * 
	 * @return the database
	 */
	protected abstract ODatabaseDocumentTx getDb();
	
	/**
	 * Create the test database
	 */
	@Before
	public abstract void init();
	
	/**
	 * Dispose the test database
	 */
	@After
	public abstract void dispose();

	/**
	 * Test writing a document to the database
	 */
	@Test
	public void testWriteDocument() {
		createLuke(getDb());
		
		//XXX what is the difference between class and cluster?
		assertEquals(1, getDb().countClass("Person")); // schema?
		assertEquals(1, getDb().countClusterElements("Person")); // schema-less?
	}

	/**
	 * Create a Person named Luke in the given DB
	 * 
	 * @param db the database
	 */
	protected static void createLuke(ODatabaseDocumentTx db) {
		ODocument doc = new ODocument(db, "Person");
		doc.field("name", "Luke");
		doc.field("surname", "Skywalker");
		doc.field("city", new ODocument(db, "City").field("name","Rome").field("country", "Italy") );
		              
		// save the document
		doc.save();
	}

	/**
	 * Test a native query
	 */
	@SuppressWarnings("serial")
	@Test
	public void testNativeQuery() {
		createMiaAndTim();
		
		// query
		List<ODocument> result = getDb()
				.query(new ONativeSynchQuery<ODocument, OQueryContextNativeSchema<ODocument>>(
						getDb(), "Person",
						new OQueryContextNativeSchema<ODocument>()) {
					@Override
					public boolean filter(
							OQueryContextNativeSchema<ODocument> iRecord) {
						return iRecord.field("city").field("name").eq("Tokio").go();
					}
				});
		
		assertEquals(1, result.size());
		assertEquals("Mia", result.get(0).field("name"));
	}

	/**
	 * Test a SQL query
	 */
	@Test
	public void testSqlQuery() {
		createMiaAndTim();
		
		// query
		List<ODocument> result = getDb().query(
			  new OSQLSynchQuery<ODocument>("select * from Person where city.name = 'Tokio'"));
		
		
		assertEquals(1, result.size());
		assertEquals("Mia", result.get(0).field("name"));
	}

	/**
	 * Create persons Mia and Tim and store them in the database
	 */
	protected void createMiaAndTim() {
			ODocument docMia = new ODocument(getDb(), "Person");
			docMia.field("name", "Mia");
			docMia.field("surname", "Serenade");
			docMia.field("city", new ODocument(getDb(), "City").field("name","Tokio").field("country", "Japan") );
			docMia.save();
			
			ODocument docTim = new ODocument(getDb(), "Person");
			docTim.field("name", "Tim");
			docTim.field("surname", "Takati");
			docTim.field("city", new ODocument(getDb(), "City").field("name","Los Angeles").field("country", "USA") );
			docTim.save();
			
			//XXX what is the difference between class and cluster?
			assertEquals(2, getDb().countClass("Person")); // schema?
			assertEquals(2, getDb().countClusterElements("Person")); // schema-less?
		}

}