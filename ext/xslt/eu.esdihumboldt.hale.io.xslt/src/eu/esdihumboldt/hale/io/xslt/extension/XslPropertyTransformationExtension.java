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

import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.io.xslt.XslPropertyTransformation;

/**
 * Extension for {@link XslPropertyTransformation}s.
 * 
 * @author Simon Templer
 */
public class XslPropertyTransformationExtension extends
		AbstractXslTransformationExtension<XslPropertyTransformation, PropertyFunction> {

	private static XslPropertyTransformationExtension instance;

	/**
	 * Get the extension singleton instance.
	 * 
	 * @return the extension instance
	 */
	public static XslPropertyTransformationExtension getInstance() {
		synchronized (XslPropertyTransformationExtension.class) {
			if (instance == null) {
				instance = new XslPropertyTransformationExtension();
			}
		}
		return instance;
	}

	@Override
	protected PropertyFunction getFunction(String functionId) {
		return PropertyFunctionExtension.getInstance().get(functionId);
	}

	@Override
	protected String getConfigurationElementName() {
		return "propertyTransformation";
	}

}
