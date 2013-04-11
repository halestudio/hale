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

package eu.esdihumboldt.hale.io.xslt;

import javax.xml.namespace.NamespaceContext;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Interface for custom source context providers for the XSLT export.
 * 
 * @author Simon Templer
 */
public interface SourceContextProvider {

	/**
	 * Get the context from which source instances of the given type should be
	 * retrieved.
	 * 
	 * @param type the type definition
	 * @param namespaceContext the namespace context for determining namespace
	 *            prefixes
	 * @return the source context XPath expression, e.g. &quot;/&quot; for the
	 *         whole document
	 */
	public String getSourceContext(TypeDefinition type, NamespaceContext namespaceContext);

}
