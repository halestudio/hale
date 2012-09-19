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

package eu.esdihumboldt.hale.ui.function.generic;

import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;

/**
 * Factory for generic function wizards
 * 
 * @author Simon Templer
 */
public abstract class AbstractGenericFunctionWizardFactory implements FunctionWizardFactory {

	private final String functionId;

	/**
	 * Create a generic function wizard factory for the function with the given
	 * identifier.
	 * 
	 * @param functionId the function identifier
	 */
	public AbstractGenericFunctionWizardFactory(String functionId) {
		super();
		this.functionId = functionId;
	}

	/**
	 * Get the function identifier
	 * 
	 * @return the function identifier
	 */
	public String getFunctionId() {
		return functionId;
	}

}
