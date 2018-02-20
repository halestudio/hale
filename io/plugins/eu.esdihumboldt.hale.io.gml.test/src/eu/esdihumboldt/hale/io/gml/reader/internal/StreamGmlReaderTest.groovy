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

package eu.esdihumboldt.hale.io.gml.reader.internal

import static org.junit.Assert.*;

import org.junit.Test
import org.opengis.referencing.crs.CoordinateReferenceSystem

import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import eu.esdihumboldt.hale.common.instance.geometry.GeometryUtil
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader
import groovy.transform.CompileStatic

/**
 * Tests for {@link StreamGmlReader}
 * 
 * @author Florian Esser
 */
@SuppressWarnings("restriction")
@CompileStatic
class StreamGmlReaderTest {
	/**
	 * Test whether the reader detects the CRS from the GML srsName attribute 
	 * correctly.
	 *  
	 * @throws Exception
	 */
	@Test
	public void testSrs() throws Exception {
		def schemaLocation = getClass().getResource("/data/hydro/hydroEx.xsd").toURI();
		def gmlLocation = getClass().getResource("/data/hydro/hydro.gml").toURI();

		def sourceSchema = loadSchema(schemaLocation)
		def instances = loadGml(gmlLocation, sourceSchema, null)

		instances.iterator().withCloseable { it ->
			def inst = ((ResourceIterator<Instance>)it).next();
			assertNotNull(inst)

			def geoms = GeometryUtil.getAllGeometries(inst)
			assertEquals(1, geoms.size())
			GeometryProperty<?> geom = geoms.iterator().next();
			assertNotNull(geom);
			assertNotNull(geom.getCRSDefinition());

			CoordinateReferenceSystem crs = geom.getCRSDefinition().getCRS();
			assertNotNull(crs);
			assertEquals("27700", crs.getIdentifiers().iterator().next().getCode());
		}
	}

	/**
	 * Test whether the reader honours the "defaultSrs" attribute when there is 
	 * no srsName in the data.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDefaultSrs() throws Exception {
		def schemaLocation = getClass().getResource("/data/hydro/hydroEx.xsd").toURI();
		def gmlLocation = getClass().getResource("/data/hydro/hydro-nosrs.gml").toURI();

		def sourceSchema = loadSchema(schemaLocation)
		def instances = loadGml(gmlLocation, sourceSchema, ["defaultSrs":"code:EPSG:4326"])

		instances.iterator().withCloseable { it ->
			def inst = ((ResourceIterator<Instance>)it).next();
			assertNotNull(inst)

			def geoms = GeometryUtil.getAllGeometries(inst)
			assertEquals(1, geoms.size())
			GeometryProperty<?> geom = geoms.iterator().next();
			assertNotNull(geom);
			assertNotNull(geom.getCRSDefinition());

			CoordinateReferenceSystem crs = geom.getCRSDefinition().getCRS();
			assertNotNull(crs);
			assertEquals("4326", crs.getIdentifiers().iterator().next().getCode());
		}
	}

	Schema loadSchema(URI schemaLocation) throws Exception {
		def schemaReader = new XmlSchemaReader()
		schemaReader.sharedTypes = null
		schemaReader.source = new DefaultInputSupplier(schemaLocation)
		def schemaReport = schemaReader.execute(null)
		assertTrue(schemaReport.isSuccess())

		schemaReader.getSchema()
	}

	InstanceCollection loadGml(URI gmlLocation, Schema schema, Map<String, String> readerParams) {
		def gmlReader = new StreamGmlReader(true)
		gmlReader.source = new DefaultInputSupplier(gmlLocation)
		gmlReader.sourceSchema = schema
		if (readerParams) {
			readerParams.each { k, v ->
				gmlReader.setParameter(k, Value.of(v))
			}
		}
		def gmlReport = gmlReader.execute(null)
		assertTrue(gmlReport.isSuccess())

		gmlReader.getInstances();
	}
}
