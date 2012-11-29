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

import java.io.OutputStream;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Translates a type transformation function to a XSLT template. It is designed
 * to be configured and used only once for the template generation. As such an
 * instance may hold state regarding the generation process. Implementing
 * classes must have a default constructor for them to be used in the extension
 * point for XSLT type transformations.
 * 
 * @author Simon Templer
 */
public interface XslTypeTransformation extends XslTransformation {

	/**
	 * Generate a XSLT fragment with a template for the type transformation
	 * represented by the given cell. The template should generate a list of
	 * nodes of the given target element.
	 * 
	 * @param templateName the name the XSLT template should have
	 * @param targetElement the target element that holds a transformed instance
	 * @param typeCell the type cell representing the type transformation
	 * @param out the output supplier for writing the template to
	 * @throws TransformationException if an unrecoverable error occurs during
	 *             the XSLT transformation generation
	 */
	public void generateTemplate(String templateName, XmlElement targetElement, Cell typeCell,
			LocatableOutputSupplier<? extends OutputStream> out) throws TransformationException;

}
