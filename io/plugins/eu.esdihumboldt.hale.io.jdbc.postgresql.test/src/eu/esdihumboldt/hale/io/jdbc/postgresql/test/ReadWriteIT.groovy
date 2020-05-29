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
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.test.TestUtil
import eu.esdihumboldt.hale.io.jdbc.test.AbstractDBTest
import eu.esdihumboldt.hale.io.jdbc.test.DBConfigInstance
import groovy.sql.Sql


/**
 * Database tests reading and writing data.
 *
 * @author Simon Templer
 */
@Features("Databases")
@Stories("PostgreSQL")
class ReadWriteIT extends AbstractDBTest{

	private static final TABLE_LINES = '''CREATE TABLE lines
		(
		  ps integer NOT NULL,
		  name character varying,
		  geom geometry(LineString, 4326),
		  dat bytea,
		  CONSTRAINT lines_pkey PRIMARY KEY (ps)
		);'''

	public ReadWriteIT(){
		super(new DBConfigInstance("postgis", ReadWriteIT.class.getClassLoader()))
	}


	@Test
	void writeRead() {

		TestUtil.startConversionService()

		// setup

		Sql sql = new Sql(waitForConnection())
		try {
			// create table
			sql.execute TABLE_LINES
		}
		finally {
			sql.close()
		}

		// read schema
		Schema schema = readSchema()

		// create objects to write
		GeometryFactory gf = new GeometryFactory()
		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			for (i in 1..20) {
				lines {
					ps i
					name "Some feature $i"
					geom new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), gf.createLineString([
						new Coordinate(0, 0),
						new Coordinate(i, i)] as Coordinate[]))
					dat([0, 1, i, 1, 0] as byte[])
				}
			}
		}
		instances.iterator()

		// write
		writeInstances(instances, schema)

		// read & test
		TypeDefinition linesType = schema.getTypes().find { TypeDefinition type ->
			type.name.localPart == 'lines'
		}

		int count = readAndCountInstances(instances,schema,linesType)

		assertEquals(20, count)
	}

}
