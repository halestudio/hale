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

package eu.esdihumboldt.hale.io.json;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.schema.io.SchemaWriter;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.common.test.TestUtil;

/**
 * A comprehensive test of the {@link JsonSchemaWriter} using XSDs.
 * 
 * @author Thorsten Reitz
 */
public class JsonSchemafromXsdTest {

	@Test
	public void testFeatureData() {
		try {
			Schema schema = TestUtil.loadSchema(new URI(this.getClass()
					.getResource("xsd/FeatureData.xsd").toString()));

			assertNotNull(schema);

			Path tempFile = Files.createTempFile("hale-json-schema-test", ".json");
			SchemaWriter writer = new JsonSchemaWriter();
			writer.setSchemas(new DefaultSchemaSpace().addSchema(schema));
			writer.setTarget(new FileIOSupplier(tempFile.toFile()));
			IOReport report = writer.execute(null);

		} catch (IOProviderConfigurationException | IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
