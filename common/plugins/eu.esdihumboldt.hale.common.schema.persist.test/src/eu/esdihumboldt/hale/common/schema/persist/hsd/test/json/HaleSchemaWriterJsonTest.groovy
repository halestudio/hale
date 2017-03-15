/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.schema.persist.hsd.test.json

import java.nio.file.Files
import java.nio.file.Path

import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.schema.io.SchemaWriter
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace
import eu.esdihumboldt.hale.common.schema.persist.hsd.json.HaleSchemaWriterJson
import eu.esdihumboldt.hale.common.schema.persist.hsd.test.HaleSchemaWriterTest
import groovy.transform.CompileStatic


/**
 * Tests for {@link HaleSchemaWriterJson}.
 * 
 * @author Simon Templer
 */
class HaleSchemaWriterJsonTest extends HaleSchemaWriterTest {

	@CompileStatic
	protected Schema readFromFile(Path file) {
		// SchemaReader reader = new HaleSchemaReader()
		// reader.source = new FileIOSupplier(file.toFile())
		// IOReport report = reader.execute(null)
		//
		// assertTrue 'Reader not successful', report.isSuccess()
		// assertTrue 'Errors reported by the reader', report.errors.isEmpty()
		//
		// reader.schema

		null
	}

	@CompileStatic
	protected Path writeToTempFileAndValidate(Schema schema) {
		Path tempFile = Files.createTempFile('hale-schema-test', '.json')

		SchemaWriter writer = new HaleSchemaWriterJson()
		writer.schemas = new DefaultSchemaSpace().addSchema(schema)
		writer.target = new FileIOSupplier(tempFile.toFile())
		IOReport report = writer.execute(null)

		assertTrue 'Writer not successful', report.isSuccess()
		assertTrue 'Errors reported by the writer', report.errors.isEmpty()

		//TODO JSON validation

		tempFile
	}
}
