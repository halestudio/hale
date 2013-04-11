/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.citygml;

import javax.xml.namespace.NamespaceContext;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.CityGMLConstants;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xslt.SourceContextProvider;

/**
 * Provides specific source contexts for city objects and appearances. Assumes
 * the source root element is a city model.
 * 
 * @author Simon Templer
 */
public class CityGMLSourceContext implements SourceContextProvider, CityGMLConstants {

	private final XmlElement cityModel;

	/**
	 * Create a CityGML source context.
	 * 
	 * @param cityModel the city model element
	 */
	public CityGMLSourceContext(XmlElement cityModel) {
		this.cityModel = cityModel;
	}

	@Override
	public String getSourceContext(TypeDefinition type, NamespaceContext namespaceContext) {
		String memberName = null;

		if (isCityGMLType(type, "AbstractCityObjectType")) {
			memberName = CITY_OBJECT_MEMBER_ELEMENT;
		}
		else if (isCityGMLType(type, "AppearanceType")) {
			memberName = "appearanceMember";
		}

		if (memberName != null) {
			/*
			 * TODO some kind of check that the member actually is defined in
			 * the type?
			 */
			StringBuilder builder = new StringBuilder();
			builder.append('/');

			// city model
			builder.append(namespaceContext.getPrefix(cityModel.getName().getNamespaceURI()));
			builder.append(':');
			builder.append(cityModel.getName().getLocalPart());

			builder.append('/');

			// member
			// XXX not specifying the exact namespace
			builder.append('*');
			builder.append(':');
			builder.append(memberName);

			return builder.toString();
		}

		// default is anywhere in the document
		return "/";
	}

	/**
	 * Determine if a given type is or inherits from a specific CityGML base
	 * type.
	 * 
	 * @param type the type definition
	 * @param baseTypeName the local name of the base type
	 * @return if the type is or inherits from the given CityGML type
	 */
	public static boolean isCityGMLType(TypeDefinition type, String baseTypeName) {
		if (baseTypeName.equals(type.getName().getLocalPart())
				&& type.getName().getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)) {
			return true;
		}

		if (type.getSuperType() != null) {
			return isCityGMLType(type.getSuperType(), baseTypeName);
		}

		return false;
	}

}
