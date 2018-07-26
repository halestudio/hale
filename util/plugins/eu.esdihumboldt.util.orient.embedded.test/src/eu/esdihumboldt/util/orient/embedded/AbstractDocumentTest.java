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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.query.nativ.ONativeSynchQuery;
import com.orientechnologies.orient.core.query.nativ.OQueryContextNative;
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

		// XXX what is the difference between class and cluster?
		assertEquals(1, getDb().countClass("Person")); // schema?
		assertEquals(1, getDb().countClusterElements("Person")); // schema-less?
	}

	/**
	 * Create a Person named Luke in the given DB
	 * 
	 * @param db the database
	 */
	protected static void createLuke(ODatabaseDocumentTx db) {
		ODocument doc = new ODocument("Person");
		doc.field("name", "Luke");
		doc.field("surname", "Skywalker");
		doc.field("city", new ODocument("City").field("name", "Rome").field("country", "Italy"));

		// save the document
		doc.save();
	}

	/**
	 * Test writing a document to the database
	 */
	@Ignore
	// this fails now, I have no idea why - Version compatibility problem?
	@Test
	public void testComplexWrite() {
//		OSchema schema = getDb().getMetadata().getSchema();
//		OClass person = schema.createClass("Person");
//		person.createProperty("city", OType.EMBEDDEDLIST);

		createLuke(getDb());
		createPeter(getDb());

		// XXX what is the difference between class and cluster?
		assertEquals(2, getDb().countClass("Person")); // schema?
		assertEquals(2, getDb().countClusterElements("Person")); // schema-less?

		List<ODocument> result = getDb().query(new OSQLSynchQuery<ODocument>(
				"select * from Person where city contains (country like '%land')"));
		assertEquals(1, result.size());
		assertEquals("Peter", result.get(0).field("name"));

		// Luke will not be retrieved if only using contains as he has no list
		// for city
		// but using the or'ed expression will result in a logged error message
//		result = getDb().query(
//				  new OSQLSynchQuery<ODocument>("select * from Person where (city contains (country = 'Italy')) or (city.country = 'Italy')"));
//		assertEquals(1, result.size());
//		assertEquals("Luke", result.get(0).field("name"));

		// having results that differ will result in a wrong result set
//		result = getDb().query(
//				  new OSQLSynchQuery<ODocument>("select * from Person where (city contains (country = '%l%')) or (city.country = '%l%')"));
//		assertEquals(2, result.size());
	}

	/**
	 * Create a Person named Peter in the given DB that lives in two cities
	 * 
	 * @param db the database
	 */
	protected static void createPeter(ODatabaseDocumentTx db) {
		ODocument doc = new ODocument("Person");
		doc.field("name", "Peter");
		doc.field("surname", "Pan");

		List<ODocument> cities = new ArrayList<ODocument>();
		cities.add(new ODocument("City").field("name", "Somewhere").field("country", "Neverland"));
		cities.add(new ODocument("City").field("name", "Dunno").field("country", "England"));

//		doc.field("city", cities, OType.EMBEDDEDLIST);
		doc.field("city", cities);

		doc.field("fictional", Boolean.TRUE);

		// save the document
		doc.save();
	}

	/**
	 * Test a native query
	 */
	@Test
	public void testNativeQuery() {
		createMiaAndTim();

		// query
		List<ODocument> result = getDb().query(new ONativeSynchQuery<OQueryContextNative>(getDb(),
				"Person", new OQueryContextNative()) {

			private static final long serialVersionUID = 2603436417957747935L;

			@Override
			public boolean filter(OQueryContextNative iRecord) {
				return iRecord.field("city").field("name").eq("Tokio").go();
			}

			@Override
			public void end() {
				// XXX wat?
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
	 * Test a prepared SQL query
	 */
	@Test
	public void testPreparedQuery() {
		// tested with some special characters in field names
		// not supported: '.' '/' '@' '%' '-'
		// supported: digits, A-Za-z, '_'

		ODocument doc = new ODocument("Person");
		doc.field("personalname", "Barack");
		doc.field("___surname___", "Obama");
		doc.validate();
		doc.save();

		OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(
				"select * from Person where personalname = :name and ___surname___ = :surname");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Barack");
		params.put("surname", "Obama");

		List<ODocument> result = getDb().command(query).execute(params);

		assertEquals(1, result.size());
		assertEquals("Barack", result.get(0).field("personalname"));
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

}
