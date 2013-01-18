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
import java.io.OutputStream;

import javax.xml.namespace.NamespaceContext;

import org.apache.velocity.Template;

import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

import eu.esdihumboldt.hale.common.align.model.Alignment;

/**
 * Context for a XSLT generation process.
 * 
 * @author Simon Templer
 */
public interface XsltGenerationContext extends XsltConstants {

	/**
	 * Get the namespace context available for the XSLT.
	 * 
	 * @return the namespace context holding the association of prefixes to
	 *         namespaces
	 */
	public NamespaceContext getNamespaceContext();

	/**
	 * Get the alignment the XSLT is generated from.
	 * 
	 * @return the alignment
	 */
	public Alignment getAlignment();

	/**
	 * Load a velocity template associated to a XSL transformation or function.
	 * The template encoding is assumed to be UTF-8.
	 * 
	 * @param transformation the transformation or function class
	 * @param resource the resource the template can be retrieved from
	 * @param id the identifier of the template, must be unique for this
	 *            template in context of the XSL transformation
	 * @return the loaded template
	 * @throws Exception if loading the template failed
	 */
	public Template loadTemplate(Class<?> transformation,
			InputSupplier<? extends InputStream> resource, String id) throws Exception;

	/**
	 * Load a velocity template associated to a XSL transformation or function
	 * placed in a default location. The default location is right next to the
	 * class with the same name as the class but with <code>xsl</code> as file
	 * extension. Please note that as <code>id</code> for the template
	 * <code>null</code> will be used. The template encoding is assumed to be
	 * UTF-8.
	 * 
	 * @param transformation the transformation or function class
	 * @return the loaded template
	 * @throws Exception if loading the template failed
	 */
	public Template loadTemplate(Class<?> transformation) throws Exception;

	/**
	 * Add an include to the XSL transformation. Output written to the returned
	 * output supplier will be included as child to the <code>transform</code>
	 * element of the XSL file. The encoding of the output should be UTF-8.<br>
	 * <br>
	 * This can be used to add top-level declarations to the transformation,
	 * such as XSL template definitions.
	 * 
	 * @return the output supplier to be used to write the XSL fragment to
	 *         include
	 */
	public OutputSupplier<? extends OutputStream> addInclude();

	/**
	 * Reserve a name for an XSL template if possible.
	 * 
	 * @param desiredName the desired template name to reserve
	 * @return the reserved template name, this is the desired name if it was
	 *         not already taken
	 */
	public String reserveTemplateName(String desiredName);

	/**
	 * Get the property transformation for the given function identifier.
	 * 
	 * @param functionId the function identifier
	 * @return the property transformation instance or <code>null</code>
	 */
	public XslPropertyTransformation getPropertyTransformation(String functionId);

}
