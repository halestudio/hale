/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.postgresql.test

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import eu.esdihumboldt.hale.io.jdbc.test.AbstractDBTest
import groovy.sql.Sql

/**
 * Tests for multi-dimensional arrays. Also documents the current state of support.
 * 
 * As it is not possible to determine array dimensions from the database schema,
 * for now we treat multi-dimensional arrays the same as one dimensionals.
 * 
 * @author Simon Templer
 */
class MultiDimensionalArraysIT extends AbstractDBTest {

	public static final String query = '''
	CREATE TABLE test (
		matrix		INTEGER[][],
		puzzle		CHARACTER VARYING[][]
    );'''

	MultiDimensionalArraysIT(){
		super("postgis", MultiDimensionalArraysIT.class)
	}

	@Before
	void setupTable() {
		Sql s = new Sql(waitForConnection());
		try {
			s.execute query;
		}
		finally{
			s.close();
		}
	}

	@Test
	void testArrayTypes(){
		Schema schema = readSchema()

		TypeDefinition type = schema.getTypes().find { TypeDefinition type ->
			type.name.localPart == 'test'
		}
		assertNotNull('Schema type not found', type)

		// check binding
		checkBindingAndSqlType(schema, [
			INT: Integer.class, //
			SERIAL: Integer.class, //
			INT4: Integer.class, //
			_INT4: Integer.class, // XXX currently array as multi-occurrence property
			BOOL: Boolean.class, //
			DECIMAL: BigDecimal.class, //
			NUMERIC: BigDecimal.class, //
			VARCHAR: String.class, //
			_VARCHAR: String.class, // XXX currently array as multi-occurrence property
			FLOAT8: Double.class, //
		]);

		// check cardinalities
		def expectedCardinalities = [
			// XXX currently array as multi-occurrence property
			matrix: [0, -1],
			puzzle: [0, -1]]
		for (def p : DefinitionUtil.getAllProperties(type)) {
			def name = p.name.localPart
			Cardinality card = p.getConstraint(Cardinality)
			def expected = expectedCardinalities[name]
			assertNotNull("No expected cardinalities given for property $name", expected)

			assertEquals("Max cardinality not matched for property $name - ", expected[1], card.maxOccurs)
			assertEquals("Min cardinality not matched for property $name - ", expected[0], card.minOccurs)
		}
	}

	@Test
	void testWriteRead() {
		Schema schema = readSchema()

		// create instances
		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			test {
				// XXX currently array as multi-occurrence property
				matrix ([1, 0, 0] as int[])
				matrix ([0, 1, 0] as int[])
				matrix ([0, 0, 1] as int[])

				puzzle ([
					['A', 'B', 'C'] as String[],
					['X', 'Y', 'Z'] as String[]] as String[][])
				puzzle ([
					['a', 'b', 'c'] as String[],
					['x', 'y', 'z'] as String[]] as String[][])
			}
		}

		writeInstances(instances, schema)
		TypeDefinition gType = schema.getTypes().find { TypeDefinition type ->
			type.name.localPart == 'test'
		}

		int count = readAndCountInstances(instances, schema, gType)
		assertEquals(1, count)
	}

}
