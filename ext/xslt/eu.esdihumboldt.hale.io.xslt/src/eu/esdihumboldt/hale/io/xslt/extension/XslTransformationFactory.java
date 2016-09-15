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

package eu.esdihumboldt.hale.io.xslt.extension;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.io.xslt.XslTransformation;

/**
 * Factory interface for the {@link XslTransformation} extensions.
 * 
 * @author Simon Templer
 * @param <T> the concrete type of XSLT transformation
 * @param <X> the concrete function type associated to the XSLT transformation
 *            type
 */
public interface XslTransformationFactory<T extends XslTransformation, X extends FunctionDefinition<?>>
		extends ExtensionObjectFactory<T> {

	/**
	 * Get the identifier of the function the {@link XslTransformation} is
	 * linked to.
	 * 
	 * @return the function identifier
	 */
	public String getFunctionId();

	/**
	 * Get the function the {@link XslTransformation} is linked to.
	 * 
	 * @return the function
	 */
	public X getFunction();

}
