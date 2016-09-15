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
	@SuppressWarnings("resource")
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
		db.drop();
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
