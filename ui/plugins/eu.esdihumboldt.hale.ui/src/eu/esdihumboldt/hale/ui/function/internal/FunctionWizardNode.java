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

package eu.esdihumboldt.hale.ui.function.internal;

import java.util.List;

import org.eclipse.jface.wizard.IWizardContainer;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardExtension;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.function.generic.GenericPropertyFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.GenericTypeFunctionWizard;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.util.wizard.AbstractWizardNode;
import eu.esdihumboldt.hale.ui.util.wizard.ExtendedWizardNode;

/**
 * Wizard node for a function.
 * 
 * @author Simon Templer
 */
public class FunctionWizardNode extends AbstractWizardNode {

	private final FunctionDefinition<?> function;

	private final SchemaSelection initialSelection;

	/**
	 * Create a wizard node
	 * 
	 * @param function the function
	 * @param container the wizard container
	 */
	public FunctionWizardNode(FunctionDefinition<?> function, IWizardContainer container) {
		this(function, container, null);
	}

	/**
	 * Create a wizard node
	 * 
	 * @param function the function
	 * @param container the wizard container
	 * @param initialSelection the initial selection to initialize the wizard
	 *            with, may be <code>null</code> to start with an empty
	 *            configuration
	 */
	public FunctionWizardNode(FunctionDefinition<?> function, IWizardContainer container,
			SchemaSelection initialSelection) {
		super(container);

		this.function = function;
		this.initialSelection = initialSelection;
	}

	/**
	 * @see ExtendedWizardNode#getDescription()
	 */
	@Override
	public String getDescription() {
		return function.getDescription();
	}

	/**
	 * @see AbstractWizardNode#createWizard()
	 */
	@Override
	protected FunctionWizard createWizard() {
		FunctionWizard result = null;
		List<FunctionWizardDescriptor<?>> factories = FunctionWizardExtension.getInstance()
				.getFactories(
						new FactoryFilter<FunctionWizardFactory, FunctionWizardDescriptor<?>>() {

							@Override
							public boolean acceptFactory(FunctionWizardDescriptor<?> factory) {
								return factory.getFunctionId().equals(function.getId());
							}

							@Override
							public boolean acceptCollection(
									ExtensionObjectFactoryCollection<FunctionWizardFactory, FunctionWizardDescriptor<?>> collection) {
								return true;
							}
						});

		if (!factories.isEmpty()) {
			// create registered wizard
			FunctionWizardDescriptor<?> fwd = factories.get(0);
			result = fwd.createNewWizard(initialSelection);
		}

		if (result == null) {
			// create generic wizard
			if (function instanceof TypeFunction) {
				result = new GenericTypeFunctionWizard(initialSelection, function.getId());
			}
			else {
				result = new GenericPropertyFunctionWizard(initialSelection, function.getId());
			}
		}

		// initialize wizard
		result.init();

		return result;
	}

	/**
	 * @see AbstractWizardNode#getWizard()
	 */
	@Override
	public FunctionWizard getWizard() {
		return (FunctionWizard) super.getWizard();
	}

	/**
	 * @return the function
	 */
	public FunctionDefinition<?> getFunction() {
		return function;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((function == null) ? 0 : function.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionWizardNode other = (FunctionWizardNode) obj;
		if (function == null) {
			if (other.function != null)
				return false;
		}
		else if (!function.equals(other.function))
			return false;
		return true;
	}

}
