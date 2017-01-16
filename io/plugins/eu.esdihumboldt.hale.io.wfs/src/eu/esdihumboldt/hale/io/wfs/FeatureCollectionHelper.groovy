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

package eu.esdihumboldt.hale.io.wfs


/**
 * Helper class for parsing WFS GetFeature requests
 * 
 * @author Florian Esser
 */
class FeatureCollectionHelper {

	static int getNumberOfFeatures(InputStream input, WFSVersion version) throws WFSException {
		def xml
		try {
			xml = new XmlSlurper().parse(input)
		} catch (Exception e) {
			throw new WFSException('Could not parse WFS response', e)
		}

		// verify that the root element has the correct name
		switch (xml.name()) {
			case 'FeatureCollection':
				break;
			default:
				throw new WFSException("Not a FeatureCollection: Invalid root element ${xml.name()}")
		}

		String nof = xml.@numberOfFeatures.text();
		if (!nof.isInteger() || nof.toInteger() < 0) {
			throw new WFSException("Invalid value in numberOfFeatures attribute: ${xml.@numberOfFeatures.text()}")
		}

		return nof.toInteger();
	}
}
