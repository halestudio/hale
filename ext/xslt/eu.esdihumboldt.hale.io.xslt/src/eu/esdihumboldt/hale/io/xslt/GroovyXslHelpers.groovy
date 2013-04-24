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

package eu.esdihumboldt.hale.io.xslt

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag


/**
 * Groovy based helper class for XSL transformations.
 * 
 * @author Simon Templer
 */
class GroovyXslHelpers {

	/**
	 * Convert a QName to a map that can be used with a markup builder to
	 * specify an XSL attribute or element.
	 *   
	 * @param name the name
	 * @return the map containing name and if specified namespace
	 */
	static Map<String, String> asMap(QName name) {
		if (name.namespaceURI) {
			[name: name.localPart, namespace: name.namespaceURI]
		}
		else {
			[name: name.localPart]
		}
	}

	/**
	 * Convert a QName to a map that can be used with a markup builder to
	 * specify an XSL attribute or element. It includes the namespace prefix
	 * if applicable.
	 *
	 * @param name the name
	 * @param xsltContext the XSLT generation context 
	 * @return the map containing name and if specified namespace
	 */
	static Map<String, String> asMap(QName name, XsltGenerationContext xsltContext) {
		if (name.namespaceURI) {
			def prefix = xsltContext.namespaceContext.getPrefix(name.namespaceURI)
			if (prefix) {
				return [name: "${prefix}:${name.localPart}", namespace: name.namespaceURI]
			}
		}

		asMap(name)
	}

	/**
	 * Convert the given QName to the notation with prefix.
	 * 
	 * @param name the name
	 * @param xsltContext the XSLT generation context for looking up the prefixes
	 * @return prefix if applicable and local name separated by a colon
	 */
	static String asPrefixedName(QName name, XsltGenerationContext xsltContext) {
		if (name.namespaceURI) {
			def prefix = xsltContext.namespaceContext.getPrefix(name.namespaceURI)
			if (prefix) {
				return "${prefix}:${name.localPart}"
			}
		}

		name.localPart
	}

	/**
	 * Create a XPath fragment for selecting the given child definition.
	 * 
	 * @param child the child definition
	 * @param xsltContext the XSLT context to determine the namespace prefix
	 * @return the XPath fragment to select the child 
	 */
	static String asXPath(ChildDefinition<?> child, XsltGenerationContext xsltContext) {
		StringBuilder str = new StringBuilder()

		if (isAttribute(child)) {
			str << '@'
		}

		String prefix = xsltContext.namespaceContext.getPrefix(child.name.namespaceURI)
		if (prefix) {
			str << prefix
			str <<':'
		}

		str << child.name.localPart

		str.toString()
	}

	/**
	 * Determines if a child definition represents a XML attribute.
	 * 
	 * @param child the definition
	 * @return if the definition represents a XSL attribute
	 */
	static boolean isAttribute(ChildDefinition<?> child) {
		child.asProperty() && child.asProperty().getConstraint(XmlAttributeFlag).enabled
	}

	/**
	 * Determines if a child definition may have an associated value.
	 *
	 * @param child the definition
	 * @return if the definition may have a value (that is not an augmented value)
	 */
	static boolean hasValue(ChildDefinition<?> child) {
		child.asProperty() && child.asProperty().getPropertyType().getConstraint(HasValueFlag).enabled
	}

	/**
	 * Determines if a definition group may have an associated value.
	 *
	 * @param group the definition group
	 * @return if the group may have a value (i.e. it represents a type
	 *   definition that may have a value)
	 */
	static boolean hasValue(DefinitionGroup group) {
		if (group instanceof TypeDefinition) {
			group.getConstraint(HasValueFlag).enabled
		}
		else {
			false
		}
	}
}
