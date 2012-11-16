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

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;

/**
 * Factory interface for the {@link XslTypeTransformation} extension.
 * 
 * @author Simon Templer
 */
public interface XslTypeTransformationFactory extends ExtensionObjectFactory<XslTypeTransformation> {

	/**
	 * Get the identifier of the function the {@link XslTypeTransformation} is
	 * linked to.
	 * 
	 * @return the function identifier
	 */
	public String getFunctionId();

	/**
	 * Get the function the {@link XslTypeTransformation} is linked to.
	 * 
	 * @return the function
	 */
	public TypeFunction getFunction();

}
