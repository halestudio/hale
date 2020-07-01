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

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory

import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.io.jdbc.test.AbstractDBTest
import eu.esdihumboldt.hale.io.jdbc.test.DBConfigInstance
import groovy.sql.Sql

/**
 * test the order of the instances to be written to the database, as unorder
 * writing may throw integrity constraint exception
 * 
 * @author Sameer Sheikh
 */
@Features("Databases")
@Stories("PostgreSQL")
public class WriteInstanceOrderIT extends AbstractDBTest {
	private static final FIRST_TABLE_LINES = '''CREATE TABLE statisticalunits
		(
	ID VARCHAR(10) PRIMARY KEY,
	name VARCHAR(254),
	geom geometry(LineString, 4326)

		);'''
	private static final SECOND_TABLE_LINES = '''create table testtable(
	test_id varchar(10) primary key,
	name varchar(254)

	)'''
	private static final THIRD_TABLE_LINES = '''CREATE TABLE indicators (
	
	SU_ID VARCHAR(10) REFERENCES statisticalunits (ID),
	test_id varchar(10) references testtable (test_id),
	year INTEGER,
	physicians INTEGER
	);'''

	/**
	 * constructor
	 */
	public WriteInstanceOrderIT() {
		super(new DBConfigInstance("postgis", WriteInstanceOrderIT.class.getClassLoader()));
	}

	/**
	 * a test
	 */
	@Test
	public void testInstanceWriteOrder() {
		Sql s = new Sql(waitForConnection());
		try{
			s.execute FIRST_TABLE_LINES;
			s.execute SECOND_TABLE_LINES;
			s.execute THIRD_TABLE_LINES;
		}
		finally{
			s.close();
		}
		Schema schema = readSchema()
		GeometryFactory gf = new GeometryFactory()
		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			for (i in 1..1) {
				indicators{
					su_id i
					test_id i
					year 2009+i
					physicians 1+i
				}
				statisticalunits{
					id i
					name "test $i"
					geom  new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), gf.createLineString([
						new Coordinate(0, 0),
						new Coordinate(i, i)] as Coordinate[]))
				}
				testtable{
					test_id i
					name "test row $i"
				}
			}
		}
		writeInstances(instances, schema);
	}
}
