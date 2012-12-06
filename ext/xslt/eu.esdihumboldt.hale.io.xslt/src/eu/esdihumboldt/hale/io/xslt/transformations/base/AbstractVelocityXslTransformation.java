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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.io.xslt.XslTransformation;

/**
 * Base class for XSL transformations based on a velocity template.
 * 
 * @author Simon Templer
 */
public abstract class AbstractVelocityXslTransformation extends AbstractXslTransformation {

	/**
	 * Load the main transformation velocity template.
	 * 
	 * @return the loaded template
	 * @throws TransformationException if loading the template fails
	 */
	protected Template loadTemplate() throws TransformationException {
		try {
			return context().loadTemplate(getTemplateClass());
		} catch (Exception e) {
			throw new TransformationException("Could not load transformation template", e);
		}

	}

	/**
	 * Get the transformation class that alongside it has the template to be
	 * loaded. By default returns this instance's class. Used in the default
	 * implementation of {@link #loadTemplate()}.
	 * 
	 * @return the template class
	 */
	protected Class<? extends XslTransformation> getTemplateClass() {
		return getClass();
	}

	/**
	 * Merge the velocity template with the context and write it to the given
	 * output supplier.
	 * 
	 * @param template the velocity template
	 * @param context the velocity context
	 * @param out the output supplier
	 * @throws TransformationException if merging or writing the template fails
	 */
	protected void writeTemplate(Template template, VelocityContext context,
			LocatableOutputSupplier<? extends OutputStream> out) throws TransformationException {
		try {
			OutputStream outStream = out.getOutput();
			Writer writer = new OutputStreamWriter(outStream, "UTF-8");
			try {
				template.merge(context, writer);
				writer.flush();
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			throw new TransformationException("Merging the transformation template failed", e);
		}
	}

}
