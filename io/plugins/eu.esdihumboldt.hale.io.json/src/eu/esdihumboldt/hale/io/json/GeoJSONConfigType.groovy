/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.json;

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.align.io.impl.DOMEntityDefinitionHelper
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import eu.esdihumboldt.util.xml.XmlUtil
import groovy.transform.CompileStatic
import groovy.xml.dom.DOMCategory

/**
 * Complex value type for {@link GeoJSONConfig}.
 * 
 * @author Simon Templer
 * @deprecated as of release 4.2.0 this class is deprecated because
 *             {@link InstanceToJson} is used to export the data into GeoJson
 *             format.
 */
@Deprecated
@CompileStatic
public class GeoJSONConfigType implements ComplexValueType<GeoJSONConfig, Object> {

	public static final String NAMESPACE = 'http://www.esdi-humboldt.eu/hale/geojson'

	@Override
	public GeoJSONConfig fromDOM(Element fragment, Object context) {
		GeoJSONConfig config = new GeoJSONConfig()

		def entries = NSDOMCategory.children(fragment, NAMESPACE, 'default-geometry')
		for (Element entry in entries) {
			// property from first element
			PropertyEntityDefinition property = DOMEntityDefinitionHelper.propertyFromDOM(
					(Element) DOMCategory.getAt(entry, 0), null, SchemaSpaceID.TARGET)
			// add to configuration
			config.addDefaultGeometry(property.getType(), property)
		}

		return config
	}

	@Override
	public Element toDOM(GeoJSONConfig config) {
		def b = NSDOMBuilder.newBuilder(gj: NAMESPACE)

		Element fragment = b 'gj:config', {
			config.defaultGeometries.each { TypeDefinition type, PropertyEntityDefinition property ->
				if (type != null && property != null) {
					Element association = b 'gj:default-geometry'
					// append property
					XmlUtil.append(association, DOMEntityDefinitionHelper.propertyToDOM(property))
				}
			}
		}

		return fragment
	}

	@Override
	public Class<? extends Object> getContextType() {
		return Object.class;
	}
}
