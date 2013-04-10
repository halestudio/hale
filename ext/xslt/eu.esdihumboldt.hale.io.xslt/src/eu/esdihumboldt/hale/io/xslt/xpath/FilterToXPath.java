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

package eu.esdihumboldt.hale.io.xslt.xpath;

import javax.xml.namespace.NamespaceContext;

import eu.esdihumboldt.hale.common.filter.AbstractGeotoolsFilter;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Convert from {@link Filter}s to XPath expressions.
 * 
 * @author Simon Templer
 */
public class FilterToXPath {

	/**
	 * Transforms the given filter of the given type to a XPath query.
	 * Namespaces are transformed with the given mapping.
	 * 
	 * @param definition the type
	 * @param namespaceContext the namespace context
	 * @param filter the filter to transform
	 * @return the XPath query representing the given filter
	 * @throws IllegalStateException if the type of filter is not supported to
	 *             be converted to XPath
	 */
	public static String toXPath(TypeDefinition definition, NamespaceContext namespaceContext,
			Filter filter) {
		if (filter instanceof AbstractGeotoolsFilter) {
			return GeotoolsFilterToXPath.toXPath(definition, namespaceContext,
					((AbstractGeotoolsFilter) filter).getInternFilter());
		}
		if (filter instanceof XPathFilter) {
			return processXPath((XPathFilter) filter, namespaceContext);
		}

		throw new IllegalStateException("Unsupported filter");
	}

	/**
	 * Transforms the given filter of the given type to a XPath query.
	 * Namespaces are transformed with the given mapping.
	 * 
	 * @param definition the property definition
	 * @param namespaceContext the namespace context
	 * @param filter the property filter to transform
	 * @return the XPath query representing the given filter
	 * @throws IllegalStateException if the type of filter is not supported to
	 *             be converted to XPath
	 */
	public static String toXPath(PropertyDefinition definition, NamespaceContext namespaceContext,
			Filter filter) {
		if (filter instanceof AbstractGeotoolsFilter) {
			return GeotoolsFilterToXPath.toXPath(definition, namespaceContext,
					((AbstractGeotoolsFilter) filter).getInternFilter());
		}
		if (filter instanceof XPathFilter) {
			return processXPath((XPathFilter) filter, namespaceContext);
		}

		throw new IllegalStateException("Unsupported filter");
	}

	/**
	 * Process an XPath filter expression for use in context of the XSLT.
	 * 
	 * @param filter the XPath filter
	 * @param namespaceContext the namespace context
	 * @return the XPath expression to use in XSLT
	 */
	private static String processXPath(XPathFilter filter, NamespaceContext namespaceContext) {
		String xpath = filter.getExpression();

		/*
		 * TODO replace XPath 3.0 like notations of namespaces with prefixes?
		 */

		return xpath;
	}

}
