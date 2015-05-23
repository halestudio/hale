/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.custom;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomFunction;

/**
 * Abstract custom function wizard
 * 
 * @param <C> the type of the custom function
 * @author Simon Templer
 */
public abstract class AbstractCustomFunctionWizard<C extends CustomFunction<?, ?>> extends Wizard
		implements CustomFunctionWizard<C> {

	private final C initFunction;

	/**
	 * Create a custom function wizard from scratch or based on an existing
	 * function.
	 * 
	 * @param function the existing custom function, or <code>null</code>
	 */
	public AbstractCustomFunctionWizard(C function) {
		super();

		this.initFunction = function;
	}

	/**
	 * Calls {@link #init(CustomFunction)}
	 */
	@Override
	public void init() {
		init(initFunction);
	}

	/**
	 * Initialize the wizard based on an existing custom function.
	 * 
	 * @param function the custom function, may be <code>null</code>
	 */
	protected void init(C function) {
		// override me
	}

}
