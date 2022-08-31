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

package eu.esdihumboldt.hale.io.wfs.capabilities

import java.util.regex.Pattern

import javax.xml.XMLConstants
import javax.xml.namespace.QName

import com.google.common.base.Splitter

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.io.wfs.WFSVersion
import groovy.xml.XmlSlurper


/**
 * Helper for parsing WFS capabilities.
 * @author Simon Templer
 */
class CapabilitiesHelper {

	private static final ALogger log = ALoggerFactory.getLogger(CapabilitiesHelper)

	static WFSCapabilities loadCapabilities(InputStream input) throws WFSCapabilitiesException {
		def xml
		try {
			xml = new XmlSlurper().parse(input)
		} catch (Exception e) {
			throw new WFSCapabilitiesException('Could not parse XML document', e)
		}

		// verify that the root element has the correct name
		switch (xml.name()) {
			case 'WFS_Capabilities':
				break; // for both versions 1.1.0 and 2.0.0
			default:
				throw new WFSCapabilitiesException("Invalid capabilities root element ${xml.name()}")
		}

		// WFS version
		WFSVersion version = WFSVersion.fromString(xml.@version.text(), null)
		if (!version) {
			throw new WFSCapabilitiesException("Unsupported WFS version ${xml.@version.text()}")
		}

		// operations
		Map<String, WFSOperation> operations = [:]
		xml.OperationsMetadata.Operation.each {
			String name = it.@name.text()
			if (name) {
				def getAttrs = it.DCP.HTTP.Get[0].nodeIterator().next()?.attributes() ?: [:]
				def postAttrs = it.DCP.HTTP.Post[0].nodeIterator().next()?.attributes() ?: [:]

				def getUrl = getAttrs['{http://www.w3.org/1999/xlink}href']
				def postUrl = postAttrs['{http://www.w3.org/1999/xlink}href']

				WFSOperation op = new WFSOperation(name: name, httpGetUrl: getUrl, httpPostUrl: postUrl)
				operations[name] = op
			}
		}

		// feature types
		Map<QName, FeatureTypeInfo> featureTypes = [:]
		xml.FeatureTypeList.FeatureType.each {
			String name = it.Name.text()
			String lp = name
			String ns = XMLConstants.NULL_NS_URI
			String prefix = XMLConstants.DEFAULT_NS_PREFIX
			def nameExtract = /^([^:]+):(.+)$/
			def matcher = (name =~ nameExtract)
			if (matcher.matches()) {
				prefix = matcher[0][1]
				ns = it.Name.lookupNamespace(prefix)
				lp = matcher[0][2]
			}
			QName qn = new QName(ns, lp, prefix)

			// determine additional information
			String defaultCrs = it.DefaultSRS.text() // WFS 1.1
			if (!defaultCrs) {
				defaultCrs = it.DefaultCRS.text() // WFS 2.0
			}
			BBox wgs84BBox = null
			def bb = it.WGS84BoundingBox
			if (!bb.isEmpty()) {
				Splitter wsSplitter = Splitter.on(Pattern.compile(/\s+/)).omitEmptyStrings().trimResults()
				List<String> lowerCorner = wsSplitter.split(bb[0].LowerCorner.text()).toList()
				List<String> upperCorner = wsSplitter.split(bb[0].UpperCorner.text()).toList()
				if (lowerCorner.size() >= 2 && upperCorner.size() >= 2) {
					try {
						wgs84BBox = new BBox(x1: lowerCorner[0] as double, y1: lowerCorner[1] as double, x2: upperCorner[0] as double, y2: upperCorner[1] as double)
					} catch (Exception e) {
						log.error("Could not read WGS84 bounding box for feature type $qn", e)
					}
				}
			}

			FeatureTypeInfo info = new FeatureTypeInfo(qn, defaultCrs, wgs84BBox)

			featureTypes.put(qn, info)
		}

		// create result
		new WFSCapabilities(version: version, operations: operations, featureTypes: featureTypes)
	}
}
