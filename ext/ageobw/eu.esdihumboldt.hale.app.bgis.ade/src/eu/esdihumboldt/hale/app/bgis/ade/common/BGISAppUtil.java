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

package eu.esdihumboldt.hale.app.bgis.ade.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Common utility methods.
 * 
 * @author Simon Templer
 */
public class BGISAppUtil implements BGISAppConstants {

	/**
	 * Determine if a given type is a feature type.
	 * 
	 * @param type the type definition
	 * @return if the type represents a feature type
	 */
	public static boolean isFeatureType(TypeDefinition type) {
		if ("AbstractFeatureType".equals(type.getName().getLocalPart())
				&& type.getName().getNamespaceURI().startsWith("http://www.opengis.net/gml")) {
			return true;
		}

		if (type.getSuperType() != null) {
			return isFeatureType(type.getSuperType());
		}

		return false;
	}

	/**
	 * Determines if the given type represents a XML ID.
	 * 
	 * @param type the type definition
	 * @return if the type represents an ID
	 */
	public static boolean isID(TypeDefinition type) {
		if (type.getName().equals(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ID"))) {
			return true;
		}

		if (type.getSuperType() != null) {
			return isID(type.getSuperType());
		}

		return false;
	}

	/**
	 * Get all ADE feature types from a given schema.
	 * 
	 * @param schema the schema
	 * @return the list of ADE feature types
	 */
	public static List<TypeDefinition> getADEFeatureTypes(TypeIndex schema) {
		List<TypeDefinition> featureTypes = new ArrayList<TypeDefinition>();
		for (TypeDefinition type : schema.getTypes()) {
			if (ADE_NS.equals(type.getName().getNamespaceURI()) && BGISAppUtil.isFeatureType(type)) {
				featureTypes.add(type);
			}
		}

		return featureTypes;
	}

}
