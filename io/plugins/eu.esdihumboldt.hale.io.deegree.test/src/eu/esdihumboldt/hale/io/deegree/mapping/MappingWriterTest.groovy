/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.deegree.mapping

import org.junit.Test

import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema
import eu.esdihumboldt.hale.io.deegree.mapping.config.GenericMappingConfiguration
import eu.esdihumboldt.util.config.Config

/**
 * Tests for MappingWriter class.
 * 
 * @author Simon Templer
 */
class MappingWriterTest {

	/**
	 * Simple test that checks if a configuration can be successfully written.
	 */
	@Test
	void testSuccessSaveConfig() {
		Schema targetSchema = new DefaultSchema(
				'http://inspire.ec.europa.eu/schemas/ps/4.0',
				URI.create('https://inspire.ec.europa.eu/schemas/ps/4.0/ProtectedSites.xsd'))
		Alignment alignment = null
		GenericMappingConfiguration config = new GenericMappingConfiguration(new Config())
		config.fillDefaults()
		def writer = new MappingWriter(targetSchema, alignment, config)

		File tempFile = File.createTempFile('deegree-sql-config', '.xml')
		tempFile.deleteOnExit()

		tempFile.withOutputStream { writer.saveConfig(it) }

		//TODO check content?
	}

	/**
	 * Simple test that checks if a DDL can be successfully written.
	 */
	@Test
	void testSuccessSaveDDL() {
		Schema targetSchema = new DefaultSchema(
				'http://inspire.ec.europa.eu/schemas/ps/4.0',
				URI.create('https://inspire.ec.europa.eu/schemas/ps/4.0/ProtectedSites.xsd'))
		Alignment alignment = null
		GenericMappingConfiguration config = new GenericMappingConfiguration(new Config())
		config.fillDefaults()
		def writer = new MappingWriter(targetSchema, alignment, config)

		File tempFile = File.createTempFile('deegree-sql-config', '.xml')
		tempFile.deleteOnExit()

		tempFile.withOutputStream { writer.saveDDL(it) }

		//TODO check content?
	}
}
