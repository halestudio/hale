/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.persist.hsd.test

import java.nio.file.Files
import java.nio.file.Path

import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.io.SchemaWriter
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace
import eu.esdihumboldt.hale.common.schema.persist.hsd.HaleSchemaWriter
import groovy.transform.CompileStatic


/**
 * Tests for {@link HaleSchemaWriter}.
 * 
 * @author Simon Templer
 */
class HaleSchemaWriterTest extends GroovyTestCase {

	/**
	 * Test with a simple schema with the type Person.
	 */
	void testPerson() {
		Schema schema = new SchemaBuilder().schema {
			Person {
				name()
				age(Integer)
				address(cardinality: '0..n') {
					street()
					postcode()
					city()
				}
			}
		}

		Path schemaFile = writeToTempFile(schema)

		//TODO do some real testing

		assertTrue 'Nothing was actually written to the schema file', Files.size(schemaFile) > 0

		Files.delete(schemaFile)
	}

	@CompileStatic
	private Path writeToTempFile(Schema schema) {
		Path tempFile = Files.createTempFile('hale-schema-test', '.xml')

		SchemaWriter writer = new HaleSchemaWriter()
		writer.schemas = new DefaultSchemaSpace().addSchema(schema)
		writer.target = new FileIOSupplier(tempFile.toFile())
		IOReport report = writer.execute(null)

		assertTrue 'Writer not successful', report.isSuccess()
		assertTrue 'Errors reported by the writer', report.errors.isEmpty()

		tempFile
	}
}

