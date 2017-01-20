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

import static org.junit.Assert.*

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
 * Unit test to check the sorting of the complex referencing types.
 * 
 * @author Sameer Sheikh
 */
@Features("Databases")
@Stories("PostgreSQL")
public class CheckComplexTypeOrderIT extends AbstractDBTest{
	private static final FIRST_TABLE_LINES = '''CREATE TABLE FIRST_TABLE
		(
	first_id VARCHAR(10) REFERENCES SECOND_TABLE(second_id),
	name VARCHAR(254)
	

		);'''
	private static final SECOND_TABLE_LINES = '''create table SECOND_TABLE(
	second_id VARCHAR(10) primary key,
	third_id VARCHAR(10) REFERENCES THIRD_TABLE(test_id),
	fourth_id VARCHAR(10) REFERENCES FOURTH_TABLE(fourth_id),
	name VARCHAR(254)

	);'''
	private static final THIRD_TABLE_LINES = '''CREATE TABLE THIRD_TABLE (
	
	test_id VARCHAR(10) primary key,
	third_id VARCHAR(10),
	year INTEGER,
	physicians INTEGER,
	unique(third_id)
	);'''

	private static final FOURTH_TABLE_LINES = '''CREATE TABLE FOURTH_TABLE (
	fourth_id VARCHAR(10) primary key,
	test_id VARCHAR(10) REFERENCES THIRD_TABLE(third_id),
	description VARCHAR(254)
	);'''

	public CheckComplexTypeOrderIT(){
		super(new DBConfigInstance("postgis",CheckComplexTypeOrderIT.class.getClassLoader()));
	}

	@Test
	public void checkComplexOrder(){
		Sql s = new Sql(waitForConnection());
		try{

			s.execute THIRD_TABLE_LINES;
			s.execute FOURTH_TABLE_LINES;
			s.execute SECOND_TABLE_LINES;
			s.execute FIRST_TABLE_LINES;
		}
		finally{
			s.close();
		}
		Schema schema = readSchema()

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			for (i in 1..1) {
				first_table{
					first_id i
					name "test $i"
				}
				second_table{
					second_id i
					third_id i
					fourth_id i
					name "test $i"
				}
				third_table{
					test_id i
					third_id i
					year 2010 + i
					physicians 10+ i
				}
				fourth_table{
					fourth_id i
					test_id i
					description "description $i"
				}
			}
		}
		writeInstances(instances, schema);
	}
}
