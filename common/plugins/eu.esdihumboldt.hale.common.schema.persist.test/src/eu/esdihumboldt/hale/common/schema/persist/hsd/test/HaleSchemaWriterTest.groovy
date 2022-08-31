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

import javax.xml.XMLConstants
import javax.xml.namespace.QName
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.io.SchemaReader
import eu.esdihumboldt.hale.common.schema.io.SchemaWriter
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace
import eu.esdihumboldt.hale.common.schema.persist.hsd.HaleSchemaReader
import eu.esdihumboldt.hale.common.schema.persist.hsd.HaleSchemaUtil
import eu.esdihumboldt.hale.common.schema.persist.hsd.HaleSchemaWriter
import eu.esdihumboldt.hale.common.test.TestUtil
import eu.esdihumboldt.util.validator.EnumerationValidator
import eu.esdihumboldt.util.validator.Validator
import groovy.test.GroovyTestCase
import groovy.transform.CompileStatic


/**
 * Tests for {@link HaleSchemaWriter}.
 * 
 * @author Simon Templer
 */
class HaleSchemaWriterTest extends GroovyTestCase {

	/**
	 * Test with a simple schema with the type Person, result is validated based on the HSD XSD.
	 */
	void testPersonWriteValidate() {
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

		Path schemaFile = writeToTempFileAndValidate(schema)

		assertTrue 'Nothing was actually written to the schema file', Files.size(schemaFile) > 0

		Files.delete(schemaFile)
	}

	/**
	 * Test writing a schema, reading it again and comparing definitions and constraints.
	 */
	void testWriteRead() {
		// conversion service needed for value conversion
		TestUtil.startConversionService()

		Schema schema = new SchemaBuilder().schema {
			def goodType = GoodType(enumeration: ['saint', 'angel'], binding: String, [
				HasValueFlag.ENABLED,
				MappingRelevantFlag.DISABLED,
				MappableFlag.DISABLED
			])
			def evilType = EvilType(enumeration: ['devil', 'grinch'], binding: String, [
				HasValueFlag.ENABLED,
				MappingRelevantFlag.DISABLED,
				MappableFlag.DISABLED,
			])

			evilType.setConstraint(new ValidationConstraint(new EnumerationValidator(['devil', 'grinch']), evilType))

			Person(display: 'Persona') {
				name()
				age(Integer)
				address(cardinality: '0..n') {
					street()
					postcode()
					city()
				}
				_(cardinality: 0..1, choice: true) {
					good(goodType)
					evil(evilType)
				}
			}
		}

		Path schemaFile = writeToTempFileAndValidate(schema)

		Schema schema2 = readFromFile(schemaFile)

		// compare type count
		assertEquals schema.types.size(), schema2.types.size()
		assertEquals 3, schema2.types.size()
		assertEquals schema.mappingRelevantTypes.size(), schema2.mappingRelevantTypes.size()
		assertEquals 1, schema2.mappingRelevantTypes.size()

		// good type
		TypeDefinition goodType2 = schema2.getType(new QName('GoodType'))
		assertNotNull goodType2
		// binding
		assertEquals String, goodType2.getConstraint(Binding).binding
		// has-value
		assertTrue goodType2.getConstraint(HasValueFlag).enabled
		// mapping relevant
		assertFalse goodType2.getConstraint(MappingRelevantFlag).enabled
		// mappable
		assertFalse goodType2.getConstraint(MappableFlag).enabled
		// enum
		Enumeration en = goodType2.getConstraint(Enumeration)
		assertFalse en.allowOthers
		assertEquals(['saint', 'angel'], en.values)

		// evil type
		TypeDefinition evilType2 = schema2.getType(new QName('EvilType'))
		assertNotNull evilType2
		// validation constraint
		ValidationConstraint valEvil2 = evilType2.getConstraint(ValidationConstraint)
		Validator val = valEvil2.validator
		assertTrue val instanceof EnumerationValidator
		assertEquals(['devil', 'grinch'], val.values.toList())

		// person type
		TypeDefinition personType = schema.getType(new QName('Person'))
		TypeDefinition personType2 = schema2.getType(new QName('Person'))
		assertNotNull personType2
		// qualified name
		assertEquals personType.name, personType2.name
		// display name
		assertEquals 'Persona', personType2.displayName

		// address property
		PropertyDefinition address2 = personType2.getChild(new QName('address'))
		assertNotNull address2
		// cardinality
		assertEquals 0, address2.getConstraint(Cardinality).minOccurs
		assertEquals Cardinality.UNBOUNDED, address2.getConstraint(Cardinality).maxOccurs

		// age property
		PropertyDefinition age2 = personType2.accessor().age as PropertyDefinition
		assertNotNull age2
		// type binding
		assertEquals Integer, age2.propertyType.getConstraint(Binding).binding

		// choice
		GroupPropertyDefinition choice2 = personType2.children.find { it.asGroup() }
		assertNotNull choice2
		// children
		assertNotNull choice2.getChild(new QName('good'))
		assertNotNull choice2.getChild(new QName('evil'))
		// is choice
		assertTrue choice2.getConstraint(ChoiceFlag).enabled
		// cardinality
		assertEquals 0, choice2.getConstraint(Cardinality).minOccurs
		assertEquals 1, choice2.getConstraint(Cardinality).maxOccurs

		Files.delete(schemaFile)
	}

	@CompileStatic
	protected Schema readFromFile(Path file) {
		SchemaReader reader = new HaleSchemaReader()
		reader.source = new FileIOSupplier(file.toFile())
		IOReport report = reader.execute(null)

		assertTrue 'Reader not successful', report.isSuccess()
		assertTrue 'Errors reported by the reader', report.errors.isEmpty()

		reader.schema
	}

	@CompileStatic
	protected Path writeToTempFileAndValidate(Schema schema) {
		Path tempFile = Files.createTempFile('hale-schema-test', '.xml')

		SchemaWriter writer = new HaleSchemaWriter()
		writer.schemas = new DefaultSchemaSpace().addSchema(schema)
		writer.target = new FileIOSupplier(tempFile.toFile())
		IOReport report = writer.execute(null)

		assertTrue 'Writer not successful', report.isSuccess()
		assertTrue 'Errors reported by the writer', report.errors.isEmpty()

		// XML validation
		def factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
		def xsdschema = factory.newSchema(new StreamSource(HaleSchemaUtil.getHaleSchemaXSD()))
		def validator = xsdschema.newValidator()
		tempFile.toFile().withReader { Reader r ->
			validator.validate(new StreamSource(r))
		}

		tempFile
	}
}

