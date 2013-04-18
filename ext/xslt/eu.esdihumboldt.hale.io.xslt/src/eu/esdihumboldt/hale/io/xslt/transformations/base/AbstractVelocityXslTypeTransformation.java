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

package eu.esdihumboldt.hale.io.xslt.transformations.base;

import java.io.OutputStream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xslt.XslTransformationUtil;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;
import eu.esdihumboldt.hale.io.xslt.internal.CellXslInfo;

/**
 * {@link XslTypeTransformation} based on a velocity template.
 * 
 * @author Simon Templer
 */
public abstract class AbstractVelocityXslTypeTransformation extends
		AbstractVelocityXslTransformation implements XslTypeTransformation {

	/**
	 * The name of the velocity context parameter that is populated with the
	 * cell information.
	 */
	public static final String CONTEXT_PARAM_COMMENT = "comment";

	/**
	 * The name of the velocity context parameter that is populated with the XSL
	 * template name.
	 */
	public static final String CONTEXT_PARAM_TEMPLATE_NAME = "name";

	/**
	 * The name of the velocity context parameter that is populated with the
	 * target element namespace prefix and name.
	 */
	public static final String CONTEXT_PARAM_TARGET_ELEMENT = "targetElement";

	@Override
	public void generateTemplate(String templateName, XmlElement targetElement, Cell typeCell,
			LocatableOutputSupplier<? extends OutputStream> out) throws TransformationException {
		// load default template
		Template template = loadTemplate();

		// create velocity context
		VelocityContext context = XslTransformationUtil.createStrictVelocityContext();

		// set default parameters
		context.put(CONTEXT_PARAM_TEMPLATE_NAME, templateName);
		context.put(CONTEXT_PARAM_TARGET_ELEMENT,
				context().getNamespaceContext()
						.getPrefix(targetElement.getName().getNamespaceURI())
						+ ":"
						+ targetElement.getName().getLocalPart());
		context.put(CONTEXT_PARAM_COMMENT, CellXslInfo.getInfo(typeCell));

		// custom context configuration
		configureTemplate(context, typeCell);

		// write template file
		writeTemplate(template, context, out);
	}

	/**
	 * Configure the velocity template.
	 * 
	 * @param context the velocity context only filled with the default
	 *            parameters yet
	 * @param typeCell the type cell
	 * @throws TransformationException if the template cannot be configured
	 *             completely
	 */
	protected abstract void configureTemplate(VelocityContext context, Cell typeCell)
			throws TransformationException;

}
