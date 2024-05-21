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

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.opengis.referencing.crs.CoordinateReferenceSystem

import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import eu.esdihumboldt.hale.common.instance.geometry.GeometryUtil
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator
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

	@Before
	public void clearResolverCache() {
		PropertyResolver.clearCache();
	}

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

	@Ignore
	@Test
	public void testSkipWfs() {
		/*
		 * FIXME relies on external resources that are not guaranteed to exist and is thus not enabled for automated testing.
		 * Better would be a test that could mock the WFS responses (e.g. a mock service running w/ testcontainers)
		 */
		def schemaUrl = 'https://test.haleconnect.de/ows/services/org.325.bf74352b-36e7-4711-9aca-bdec2658ef68_wfs?SERVICE=WFS&VERSION=2.0.0&REQUEST=DescribeFeatureType'
		def dataUrl = 'https://test.haleconnect.de/ows/services/org.325.bf74352b-36e7-4711-9aca-bdec2658ef68_wfs?SERVICE=WFS&VERSION=2.0.0&REQUEST=GetFeature&NAMESPACES=xmlns%28ex%2Ceu%3Aesdihumboldt%3Ahale%3Aexample%29&TYPENAMES=ex%3ARiver'
		def paging = 100
		def expected = 982

		def schema = loadSchema(URI.create(schemaUrl))

		Map<String, String> params = [
			(StreamGmlReader.PARAM_FEATURES_PER_WFS_REQUEST): paging as String,
			(StreamGmlReader.PARAM_PAGINATE_REQUEST): 'true'
		]

		def instances = loadGml(URI.create(dataUrl), schema, params)

		int count = 0
		instances.iterator().withCloseable { it ->
			while (it.hasNext()) {
				((InstanceIterator) it).skip()
				count++
				if (count % 100 == 0) {
					println("$count instances skipped")
				}
			}
		}

		println("$count instances skipped")
		assertEquals(expected, count)
	}

	//	this test might not work anymore in the future. At that moment please ignore it
	//	@Ignore
	@Test
	public void testWfsPagination() {
		/*
		 * FIXME relies on external resources that are not guaranteed to exist and is thus not enabled for automated testing.
		 * Better would be a test that could mock the WFS responses (e.g. a mock service running w/ testcontainers)
		 */
		def schemaUrl = 'https://geodienste.komm.one/ows/services/org.107.7e499bca-5e63-4595-b3c4-eaece8b68608_wfs?SERVICE=WFS&VERSION=2.0.0&REQUEST=DescribeFeatureType'
		def dataUrl = 'https://geodienste.komm.one/ows/services/org.107.7e499bca-5e63-4595-b3c4-eaece8b68608_wfs?SERVICE=WFS&VERSION=2.0.0&REQUEST=GetFeature&typenames=xplan:BP_Plan&resolvedepth=*'
		def paging = 100
		def expected = 2262

		def schema = loadSchema(URI.create(schemaUrl))

		Map<String, String> params = [
			(StreamGmlReader.PARAM_FEATURES_PER_WFS_REQUEST): paging as String,
			(StreamGmlReader.PARAM_PAGINATE_REQUEST): 'true'
		]

		def instances = loadGml(URI.create(dataUrl), schema, params)

		int count = 0
		instances.iterator().withCloseable { it ->
			while (it.hasNext()) {
				((InstanceIterator) it).skip()
				count++
			}
		}

		assertEquals(expected, count)
	}

	// helpers

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
