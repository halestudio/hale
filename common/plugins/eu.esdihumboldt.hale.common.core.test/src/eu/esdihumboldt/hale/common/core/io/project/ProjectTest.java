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

package eu.esdihumboldt.hale.common.core.io.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.Version;

import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;

/**
 * Test saving and loading a project
 * 
 * @author Simon Templer
 */
public class ProjectTest {

	/**
	 * Temporary folder for tests
	 */
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	/**
	 * Test saving and loading an example project
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSaveLoad() throws Exception {
		// populate project
		Project project = new Project();

		String author;
		project.setAuthor(author = "Simon");
		String name;
		project.setName(name = "Testprojekt");
		Date created;
		project.setCreated(created = new Date(0));
		Date modified;
		project.setModified(modified = new Date());
		Version haleVersion;
		project.setHaleVersion(haleVersion = new Version("2.2.0.alpha"));
		String desc;
		project.setDescription(desc = "Hallo Welt!\nBist Du auch hier?\nÖhm.");

		IOConfiguration conf1;
		project.getResources().add(conf1 = new IOConfiguration());

		String advisorId1;
		conf1.setActionId(advisorId1 = "some advisor");
		String providerId1;
		conf1.setProviderId(providerId1 = "some provider");
		String key1;
		String value1;
		conf1.getProviderConfiguration().put(key1 = "some key", value1 = "some value");
		String value2;
		String key2;
		conf1.getProviderConfiguration().put(key2 = "some other key", value2 = "some other value");

		IOConfiguration conf2;
		project.getResources().add(conf2 = new IOConfiguration());
		String advisorId2;
		conf2.setActionId(advisorId2 = "a certain advisor");
		String providerId2;
		conf2.setProviderId(providerId2 = "a special provider");

		// write project
		File projectFile = tmp.newFile("project.xml");
		System.out.println(projectFile.getAbsolutePath());

		Project.save(project, new FileOutputStream(projectFile));

		// load project
		Project p2 = Project.load(new FileInputStream(projectFile));

		// test project
		assertEquals(author, p2.getAuthor());
		assertEquals(name, p2.getName());
		assertEquals(created, p2.getCreated());
		assertEquals(modified, p2.getModified());
		assertEquals(haleVersion, p2.getHaleVersion());
		assertEquals(desc, p2.getDescription());

		assertEquals(2, p2.getResources().size());

		Iterator<IOConfiguration> it = p2.getResources().iterator();
		IOConfiguration c1 = it.next();
		assertNotNull(c1);

		assertEquals(advisorId1, c1.getActionId());
		assertEquals(providerId1, c1.getProviderId());
		assertEquals(2, c1.getProviderConfiguration().size());
		assertTrue(c1.getProviderConfiguration().get(key1).equals(value1));
		assertTrue(c1.getProviderConfiguration().get(key2).equals(value2));

		IOConfiguration c2 = it.next();
		assertNotNull(c2);

		assertEquals(advisorId2, c2.getActionId());
		assertEquals(providerId2, c2.getProviderId());
	}

}
