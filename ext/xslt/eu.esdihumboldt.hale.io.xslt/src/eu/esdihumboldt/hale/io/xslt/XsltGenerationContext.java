/*
 * Copyright (c) 2012 Fraunhofer IGD
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

import java.io.InputStream;

import javax.xml.namespace.NamespaceContext;

import org.apache.velocity.Template;

import com.google.common.io.InputSupplier;

/**
 * Context for a XSLT generation process.
 * 
 * @author Simon Templer
 */
public interface XsltGenerationContext {

	/**
	 * Namespace URI for XSLT.
	 */
	public static final String NS_URI_XSL = "http://www.w3.org/1999/XSL/Transform";

	/**
	 * Get the namespace context available for the XSLT.
	 * 
	 * @return the namespace context holding the association of prefixes to
	 *         namespaces
	 */
	public NamespaceContext getNamespaceContext();

	/**
	 * Load a velocity template associated to a XSL transformation. The template
	 * encoding is assumed to be UTF-8.
	 * 
	 * @param transformation the transformation class
	 * @param resource the resource the template can be retrieved from
	 * @param id the identifier of the template, must be unique for this
	 *            template in context of the XSL transformation
	 * @return the loaded template
	 * @throws Exception if loading the template failed
	 */
	public Template loadTemplate(Class<? extends XslTransformation> transformation,
			InputSupplier<? extends InputStream> resource, String id) throws Exception;

	/**
	 * Load a velocity template associated to a XSL transformation placed in a
	 * default location. The default location is right next to the
	 * transformation class with the same name as the class but with
	 * <code>xsl</code> as file extension. Please note that as <code>id</code>
	 * for the template <code>null</code> will be used. The template encoding is
	 * assumed to be UTF-8.
	 * 
	 * @param transformation the transformation class
	 * @return the loaded template
	 * @throws Exception if loading the template failed
	 */
	public Template loadTemplate(Class<? extends XslTransformation> transformation)
			throws Exception;

}
