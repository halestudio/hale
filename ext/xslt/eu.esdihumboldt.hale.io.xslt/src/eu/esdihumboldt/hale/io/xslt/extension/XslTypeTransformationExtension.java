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

import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;

/**
 * Extension for {@link XslTypeTransformation}s.
 * 
 * @author Simon Templer
 */
public class XslTypeTransformationExtension extends
		AbstractXslTransformationExtension<XslTypeTransformation, TypeFunction> {

	private static XslTypeTransformationExtension instance;

	/**
	 * Get the extension singleton instance.
	 * 
	 * @return the extension instance
	 */
	public static XslTypeTransformationExtension getInstance() {
		synchronized (XslTypeTransformationExtension.class) {
			if (instance == null) {
				instance = new XslTypeTransformationExtension();
			}
		}
		return instance;
	}

	@Override
	protected TypeFunction getFunction(String functionId) {
		return TypeFunctionExtension.getInstance().get(functionId);
	}

	@Override
	protected String getConfigurationElementName() {
		return "typeTransformation";
	}

}
