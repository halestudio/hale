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

package eu.esdihumboldt.hale.io.wfs.file

import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.io.gml.geometry.GMLConstants
import eu.esdihumboldt.hale.io.wfs.WFSVersion
import eu.esdihumboldt.hale.io.xsd.model.XmlElement


/**
 * Build WFS schema structure manually.
 * 
 * @author Simon Templer
 */
class WFSSchemaHelper {

	public static TypeDefinition createFeatureCollectionType(WFSVersion version, List<XmlElement> members) {
		SchemaBuilder b = new SchemaBuilder()
		switch (version) {
			case WFSVersion.V1_1_0:
			// WFS 1.1.0
				return b.FeatureCollectionType(namespace: version.wfsNamespace) {
					featureMember(namespace: GMLConstants.NS_GML, cardinality: '0..n') { createChildren(b, members) }
				}
			case WFSVersion.V2_0_0:
			// WFS 2.0
				return b.FeatureCollectionType(namespace: version.wfsNamespace) {
					member(namespace: version.wfsNamespace, cardinality: '0..n') { createChildren(b, members) }
				}
			//FIXME in WFS 2.0 required: timeStamp, numberReturned, numberMatched!
		}
		return null;
	}

	private static void createChildren(SchemaBuilder b, List<XmlElement> members) {
		b._(choice: true) {
			members.each { element ->
				b.createNode(element.name.localPart, [
					[namespace: element.name.namespaceURI, cardinality: 1],
					element.type
				])
			}
		}
	}
}
