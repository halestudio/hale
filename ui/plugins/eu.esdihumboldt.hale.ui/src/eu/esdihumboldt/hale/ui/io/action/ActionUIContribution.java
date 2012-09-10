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

package eu.esdihumboldt.hale.ui.io.action;

import org.eclipse.jface.action.IAction;

import de.cs3d.ui.util.eclipse.extension.AbstractExtensionContribution;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Contribution for launching {@link IOWizard}s based on an {@link IOAdvisor}
 * 
 * @author Simon Templer
 */
public class ActionUIContribution extends
		AbstractExtensionContribution<IOWizard<?>, ActionUI, ActionUIExtension> {

	/**
	 * @see AbstractExtensionContribution#allowConfiguration()
	 */
	@Override
	protected boolean allowConfiguration() {
		return false;
	}

	/**
	 * @see AbstractExtensionContribution#createConfigurationAction()
	 */
	@Override
	protected IAction createConfigurationAction() {
		return null;
	}

	/**
	 * @see AbstractExtensionContribution#createFactoryAction(ExtensionObjectFactory)
	 */
	@Override
	protected IAction createFactoryAction(ActionUI factory) {
		// The dispose method of this IOWizardAction is never automatically
		// called.
		// That's okay, since the action is only created once so the evaluation
		// listener
		// mustn't be removed.
		return new IOWizardAction(factory);
	}

	/**
	 * @see AbstractExtensionContribution#initExtension()
	 */
	@Override
	protected ActionUIExtension initExtension() {
		return ActionUIExtension.getInstance();
	}

	/**
	 * @see AbstractExtensionContribution#onAdd(ExtensionObjectFactory)
	 */
	@Override
	protected void onAdd(ActionUI factory) {
		// do nothing
	}

	/**
	 * @see AbstractExtensionContribution#onConfigure(ExtensionObjectFactory)
	 */
	@Override
	protected void onConfigure(ActionUI factory) {
		// do nothing
	}

	/**
	 * @see AbstractExtensionContribution#onRemove(ExtensionObjectFactory)
	 */
	@Override
	protected void onRemove(ActionUI factory) {
		// do nothing
	}

}
