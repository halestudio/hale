/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.gml;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Inspire GML related utilities.
 * 
 * @author Simon Templer
 */
public class InspireUtil implements InspireConstants {

	/**
	 * Find the INSPIRE spatial data set element in the XML schema.
	 * 
	 * @param index the XML index representing the schema
	 * @return the spatial data set element or <code>null</code> if not found
	 */
	public static XmlElement findSpatialDataSet(XmlIndex index) {
		// first try with default namespace
		XmlElement result = index.getElements().get(
				new QName(DEFAULT_INSPIRE_NAMESPACE_BASETYPES, ELEMENT_SPATIAL_DATASET));

		if (result == null) {
			// then try with namespace prefix
			for (XmlElement element : index.getElements().values()) {
				if (ELEMENT_SPATIAL_DATASET.equals(element.getName().getLocalPart())) {
					if (element.getName().getNamespaceURI()
							.startsWith(PREFIX_1_INSPIRE_NAMESPACE_BASETYPES)
							|| element.getName().getNamespaceURI()
									.startsWith(PREFIX_2_INSPIRE_NAMESPACE_BASETYPES)) {
						result = element;
						break;
					}
				}
			}
		}

		return result;
	}

}
