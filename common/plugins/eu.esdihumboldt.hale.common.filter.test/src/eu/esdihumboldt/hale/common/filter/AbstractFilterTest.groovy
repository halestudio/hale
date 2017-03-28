/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.filter;

import static org.junit.Assert.*

import java.text.SimpleDateFormat

import org.junit.Before

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory

import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema

/**
 * Base class for filter tests providing test instances.
 * 
 * @author Simon Templer
 */
abstract class AbstractFilterTest {

	private static final String defaultNs = "http://www.my.namespace"

	protected Instance maxNoSchema

	protected Schema schema
	protected Instance max

	@Before
	void setup() {
		GeometryFactory gf = new GeometryFactory()

		def lineString = gf.createLineString([
			new Coordinate(0, 0),
			new Coordinate(1, 1),
			new Coordinate(1, 0)] as Coordinate[])
		def lineGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), lineString)

		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)

		def dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		def createMax = {
			name 'Max Mustermann'
			age 31
			address {
				street 'Musterstrasse'
				number 12
				city 'Musterstadt'
			}
			address {
				street 'Taubengasse'
				number 13
			}
			relative('father') {
				name 'Markus Mustermann'
				age 56
			}
			friend { name 'Lotte Laura' }
			legalStatus null
			nulls(null)
			nulls(null)
			area(polyGeom)
			joinDate(dateFormat.parse('2012-12-01T12:00:00+0000'))
		}

		// build instance
		maxNoSchema = new InstanceBuilder().instance(createMax)

		// build schema
		schema = new SchemaBuilder().schema(defaultNs) {
			Person {
				name()
				age(Integer)
				address(cardinality: '0..n') {
					street()
					number()
					city()
				}
				relative(cardinality: '0..n', String) {
					name()
					age(Integer)
				}
				friend(cardinality: '0..n', nullable: true, String) { name() }
				legalStatus(String)
				nulls(cardinality: '0..n', String)
				area(DefaultGeometryProperty)
				joinDate(Date)
			}
		}

		// build instance
		max = new InstanceBuilder(types: schema).Person(createMax)
	}

}
