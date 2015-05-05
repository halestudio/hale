



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

import java.sql.Date
import java.sql.Timestamp

import org.junit.Test

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory

import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.io.jdbc.test.AbstractDBTest
import eu.esdihumboldt.hale.io.jdbc.test.DBConfigInstance
import groovy.sql.Sql


/**
 * SQL type and binding test for postgresql database
 * @author sameer sheikh
 */
public class PostDataTypesIT extends AbstractDBTest {

	public static final String query = ''' CREATE TABLE employees

    ( employee_id    INTEGER 
    , first_name    VARCHAR
	, handicaped     bool
    , decimal_test   DECIMAL
    , dec_test       NUMERIC   
	, b_array		 BYTEA
	, salary		 MONEY
    , geometry_test  geometry(LineString, 4326)
    , hire_date      DATE  NOT NULL
    , last_login     TIMESTAMP
	, share_price    FLOAT
	, pers_msg       TEXT
    ,  CONSTRAINT lines_pkey PRIMARY KEY (EMPLOYEE_ID)
    ) ;'''
	private final Map<String, Class<?>> map = createMap();
	public PostDataTypesIT(){
		super(new DBConfigInstance("postgis"))
	}
	/**
	 * Test for checking sql type and binding
	 */
	@Test
	void testDataTypes(){
		Sql s = new Sql(waitForConnection());
		try{
			s.execute query;
		}
		finally{
			s.close();
		}
		Schema schema = readSchema()
		checkBindingAndSqlType(schema,map);
		GeometryFactory gf = new GeometryFactory()

		//creating instances
		Calendar c = Calendar.getInstance();
		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			for (i in 1..1) {
				c.set(2015, 4, i, 13, 5,0)
				employees {
					employee_id    i
					first_name     "Employee $i"
					handicaped 	   false
					decimal_test   i
					dec_test       i
					salary         new BigDecimal("$i")
					b_array 	   ([0, 1, i, 1, 0] as byte[])
					geometry_test  new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), gf.createLineString([new Coordinate(0, 0), new Coordinate(i, i)] as Coordinate[]))
					hire_date      Date.valueOf("2015-04-$i")
					last_login     new Timestamp(c.getTimeInMillis())
					share_price    new Double(i)
					pers_msg       "msg for employee $i"
				}
			}
		}

		writeInstances(instances, schema)
		TypeDefinition gType = schema.getTypes().find { TypeDefinition type ->
			type.name.localPart == 'employees'
		}

		int count = readAndCountInstances(instances, schema, gType)
		assertEquals(1, count)

	}

	private static Map<String, Class<?>> createMap() {
		Map<String, Class<?>> m = new HashMap<String, Class<?>>();
		m.put("INT", Integer.class);
		m.put("MONEY", BigDecimal.class);
		m.put("FLOAT", Float.class);
		m.put("INT4", Integer.class);
		m.put("INT8", Long.class);
		m.put("INT8", Long.class);
		m.put("FLOAT8", Double.class);
		m.put("BYTEA", byte[].class);
		m.put("BOOLEAN", Boolean.class);
		m.put("TIMESTAMP", java.sql.Timestamp.class);
		m.put("DATE", java.sql.Date.class);
		m.put("VARCHAR", java.lang.String.class);
		m.put("NUMERIC", BigDecimal.class);
		m.put("DECIMAL", BigDecimal.class);
		return Collections.unmodifiableMap(m);

	}
}
