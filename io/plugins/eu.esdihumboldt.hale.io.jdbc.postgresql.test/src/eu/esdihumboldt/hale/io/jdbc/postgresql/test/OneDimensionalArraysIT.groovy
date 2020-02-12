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

import ru.yandex.qatools.allure.annotations.Features
import ru.yandex.qatools.allure.annotations.Stories
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import eu.esdihumboldt.hale.io.jdbc.test.AbstractDBTest
import groovy.sql.Sql

/**
 * Tests for one dimensional arrays.
 * 
 * @author Simon Templer
 */
@Features("Databases")
@Stories("PostgreSQL")
class OneDimensionalArraysIT extends AbstractDBTest {

	public static final String query = '''
	CREATE TABLE test (
		id			INTEGER,
		friends		INTEGER[],
		settings	BOOL[3],
		values		DECIMAL[],
		notes		VARCHAR ARRAY,

    	CONSTRAINT test_id PRIMARY KEY (id)
    );'''

	OneDimensionalArraysIT(){
		super("postgis", OneDimensionalArraysIT.class)
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
			BIGSERIAL: Long.class, //
			INT4: Integer.class, //
			_INT4: Integer.class, // Array as multi-occurrence property
			BOOL: Boolean.class, //
			_BOOL: Boolean.class, // Array as multi-occurrence property
			DECIMAL: BigDecimal.class, //
			_DECIMAL: BigDecimal.class, // Array as multi-occurrence property
			NUMERIC: BigDecimal.class, //
			_NUMERIC: BigDecimal.class, // Array as multi-occurrence property
			VARCHAR: String.class, //
			_VARCHAR: String.class, // Array as multi-occurrence property
			FLOAT8: Double.class, //
		]);

		// check cardinalities
		def expectedCardinalities = [
			id: [1, 1],
			friends: [0, -1],
			settings: [0, -1], // XXX size cannot be determined
			values: [0, -1],
			notes: [0, -1]]
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
				id 1
				friends 2
				friends 4
				friends 9
				settings true
				settings false
				settings true
				values 1.2
				values 3.14
				notes 'Mimimi'
				notes 'Wohoo'
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
