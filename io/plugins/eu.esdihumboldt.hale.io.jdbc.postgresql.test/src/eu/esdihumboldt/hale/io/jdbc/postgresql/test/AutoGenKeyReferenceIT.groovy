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

package eu.esdihumboldt.hale.io.jdbc.postgresql.test;

import org.junit.Test

import ru.yandex.qatools.allure.annotations.Features
import ru.yandex.qatools.allure.annotations.Stories
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.io.jdbc.test.AbstractDBTest
import eu.esdihumboldt.hale.io.jdbc.test.DBConfigInstance
import groovy.sql.Sql

/**
 * Unit test for checking writing og the instances which has reference and
 * primary key is automatically generated.
 * 
 * @author Sameer Sheikh
 */
@Features("Databases")
@Stories("PostgreSQL")
public class AutoGenKeyReferenceIT extends AbstractDBTest {

	private static final String STATISTICAL_UNIT = '''create table statisticalunits
	(
			id serial primary key,
	        name varchar(20) )'''
	private static final String INDICATORS = '''create table indicators (
			f_id bigint not null references statisticalunits(id),
			id serial primary key,
			name varchar (20))'''
	private static final String THIRD = '''create table third_table (
			f_id bigint not null references indicators(id),
			name varchar (20))'''

	/**
	 * Constructor
	 */
	public AutoGenKeyReferenceIT() {
		super(new DBConfigInstance("postgis", AutoGenKeyReferenceIT.class.getClassLoader()));
	}

	/**
	 * unit test
	 */
	@Test
	public void testReferenceWriting() {
		Sql s = new Sql(waitForConnection());
		try{

			s.execute STATISTICAL_UNIT;
			s.execute INDICATORS;
			s.execute(THIRD)
		}
		finally{
			s.close();
		}
		Schema schema = readSchema()

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			for (i in 1..5) {
				statisticalunits{
					id i+10
					name "test $i"
				}
				indicators{
					f_id i+10
					id i+5
					name "test $i"
				}
				third_table{
					f_id i+5
					name "test $i"
				}
			}
		}
		writeInstances(instances, schema);
		InstanceCollection coll = readInstances(schema);
	}
}

