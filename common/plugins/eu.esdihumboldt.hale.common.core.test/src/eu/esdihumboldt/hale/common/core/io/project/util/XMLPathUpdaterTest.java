/*
 * Copyright (c) 2019 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.core.io.project.util;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.hale.common.cache.Request;

/**
 * Tests for {@link XMLPathUpdater} / {@link XMLSchemaUpdater}.
 * 
 * @author Simon Templer
 */
public class XMLPathUpdaterTest {

	/**
	 * Test for retrieving and updating this schema was added because this lead
	 * to a deadlock due to HTTP connections that were not released.
	 * 
	 * @throws Exception if any error occurs during the test
	 */
	@Ignore("Depends on remote resources")
	@Test
	public void testCopyISOTC211() throws Exception {
		Path tmpDir = Files.createTempDirectory("xml-updater");
		System.out.println(tmpDir.toAbsolutePath());
		File schemaFile = new File(tmpDir.toFile(), "schema.xsd");
		URI uri = URI.create("https://www.isotc211.org/2005/gmd/gmd.xsd");
		try (InputStream input = Request.getInstance().get(uri)) {
			Files.copy(input, schemaFile.toPath());
		}
		XMLSchemaUpdater.update(schemaFile, uri, true, null);
		// TODO check result
		// TODO delete files?
	}

}
