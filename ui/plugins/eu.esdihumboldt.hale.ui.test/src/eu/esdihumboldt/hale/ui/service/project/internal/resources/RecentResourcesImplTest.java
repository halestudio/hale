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

package eu.esdihumboldt.hale.ui.service.project.internal.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfigurationResource;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;

/**
 * Tests related to {@link RecentResourcesImpl}.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class RecentResourcesImplTest {

	/**
	 * Test saving and loading recent resources configuration to/from a file.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testSaveLoad() throws Exception {
		RecentResourcesImpl res1 = new RecentResourcesImpl();

		IOConfiguration conf = new IOConfiguration();
		conf.setActionId("action");
		conf.setProviderId("xxx");
		String source = "http://www.example.com/resource";
		conf.getProviderConfiguration().put(ImportProvider.PARAM_SOURCE, Value.of(source));
		conf.getProviderConfiguration().put(ImportProvider.PARAM_CONTENT_TYPE,
				Value.of("org.eclipse.core.runtime.xml"));

		Resource resource = new IOConfigurationResource(conf);

		// add resource
		res1.addResource(resource);

		// add location
		res1.addResource("some-content-type", URI.create("file:///opt"));

		Path tmp = Files.createTempFile("resres", ".xml");
		try {

			// save
			try (OutputStream out = Files.newOutputStream(tmp)) {
				res1.save(out);
			}

			// load
			RecentResourcesImpl res2 = new RecentResourcesImpl();
			try (InputStream in = Files.newInputStream(tmp)) {
				res2.load(in);
			}

			// check loaded result
			assertEquals(2, res2.getNumberOfLocations());

			List<Resource> resources = res2.getRecent("action");
			assertNotNull(resources);
			assertEquals(1, resources.size());

			Resource res = resources.get(0);
			assertEquals("action", res.getActionId());
			assertEquals(source, res.getSource().toString());

		} finally {
			Files.delete(tmp);
		}
	}
}
