/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.function.internal;

import java.util.List;

import org.eclipse.jface.wizard.IWizardContainer;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardExtension;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.function.generic.GenericPropertyFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.GenericTypeFunctionWizard;
import eu.esdihumboldt.hale.ui.util.wizard.AbstractWizardNode;
import eu.esdihumboldt.hale.ui.util.wizard.ExtendedWizardNode;

/**
 * Wizard node for a function.
 * 
 * @author Simon Templer
 */
public class FunctionWizardNode extends AbstractWizardNode {

	private final AbstractFunction<?> function;

	/**
	 * Create a wizard node
	 * 
	 * @param function the function
	 * @param container the wizard container
	 */
	public FunctionWizardNode(AbstractFunction<?> function, IWizardContainer container) {
		super(container);

		this.function = function;
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
			result = fwd.createNewWizard(null);
		}

		if (result == null) {
			// create generic wizard
			if (function instanceof TypeFunction) {
				result = new GenericTypeFunctionWizard(null, function.getId());
			}
			else {
				result = new GenericPropertyFunctionWizard(null, function.getId());
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
	public AbstractFunction<?> getFunction() {
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
