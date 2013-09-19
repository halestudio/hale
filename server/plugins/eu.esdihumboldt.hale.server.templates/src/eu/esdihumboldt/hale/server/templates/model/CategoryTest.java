/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.templates.model;

import org.junit.Assert;
import org.junit.Test;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class CategoryTest {

	@Test
	public void xxx() {
		ODatabaseDocumentTx db = new ODatabaseDocumentTx("memory:Test").create();

		Category cat = new Category();
		ODocument doc = cat.getDocument();
		Assert.assertEquals("category", doc.getClassName());

		cat.setName("Test");
		Assert.assertEquals("Test", cat.getName());
		Assert.assertEquals("Test", cat.getDocument().field("name"));

		db.close();
	}
}
