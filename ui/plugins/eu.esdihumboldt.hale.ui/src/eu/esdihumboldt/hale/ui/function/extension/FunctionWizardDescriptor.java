/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.function.extension;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;

/**
 * Function wizard descriptor
 * 
 * @param <T> the type of the function definition
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface FunctionWizardDescriptor<T extends AbstractFunction<?>> extends
		ExtensionObjectFactory<FunctionWizardFactory>, FunctionWizardFactory {

	/**
	 * Get the ID of the associated function
	 * 
	 * @return the function ID
	 */
	public String getFunctionId();

	/**
	 * Get the function definition
	 * 
	 * @return the function definition
	 */
	public T getFunction();

}
