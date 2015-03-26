package eu.esdihumboldt.hale.io.wfs.test.capabilities

import static org.junit.Assert.*

import javax.xml.namespace.QName

import org.junit.Test

import eu.esdihumboldt.hale.io.wfs.WFSVersion
import eu.esdihumboldt.hale.io.wfs.capabilities.CapabilitiesHelper
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities
import groovy.transform.CompileStatic

@CompileStatic
class CapabilitiesHelperTest {

	private static final String PLUGIN_NAME = 'eu.esdihumboldt.hale.io.wfs.test'

	private static final URL WFS_CAPABILITIES_2 = new URL("platform:/plugin/$PLUGIN_NAME/resources/wfsCapabilities2.xml")
	private static final URL WFS_CAPABILITIES_11 = new URL("platform:/plugin/$PLUGIN_NAME/resources/wfsCapabilities11.xml")

	private static final QName FEATURE_TYPE_NAME = new QName('urn:x-inspire:specification:gmlas:Addresses:3.0', 'Address')

	private static final String POST_URL  = 'http://localhost:8070/services/wfs'

	@Test
	void testLoadWFS2Capabilities() {
		WFSCapabilities caps
		WFS_CAPABILITIES_2.withInputStream {
			caps = CapabilitiesHelper.loadCapabilities(it)
		}

		assertNotNull(caps)

		// check version
		assertEquals(WFSVersion.V2_0_0, caps.version)

		// check transaction URL
		assertNotNull('Transaction operation missing', caps.transactionOp)
		assertEquals('Wrong transaction POST URL', POST_URL, caps.transactionOp.httpPostUrl)

		// check feature types
		assertEquals(1, caps.featureTypes.size())
		QName ft = caps.featureTypes.keySet().iterator().next()
		assertEquals(FEATURE_TYPE_NAME, ft)

		def ftInfo = caps.featureTypes[ft]
		assertNotNull(ftInfo)
		assertEquals('urn:ogc:def:crs:EPSG::4258', ftInfo.defaultCrs)
		assertNotNull(ftInfo.wgs84BBox)
	}

	@Test
	void testLoadWFS11Capabilities() {
		WFSCapabilities caps
		WFS_CAPABILITIES_11.withInputStream {
			caps = CapabilitiesHelper.loadCapabilities(it)
		}

		assertNotNull(caps)

		// check version
		assertEquals(WFSVersion.V1_1_0, caps.version)

		// check transaction URL
		assertNotNull('Transaction operation missing', caps.transactionOp)
		assertEquals('Wrong transaction POST URL', POST_URL, caps.transactionOp.httpPostUrl)

		// check feature types
		assertEquals(1, caps.featureTypes.size())
		QName ft = caps.featureTypes.keySet().iterator().next()
		assertEquals(FEATURE_TYPE_NAME, ft)

		def ftInfo = caps.featureTypes[ft]
		assertNotNull(ftInfo)
		assertEquals('urn:ogc:def:crs:EPSG::4258', ftInfo.defaultCrs)
		assertNotNull(ftInfo.wgs84BBox)
	}
}
