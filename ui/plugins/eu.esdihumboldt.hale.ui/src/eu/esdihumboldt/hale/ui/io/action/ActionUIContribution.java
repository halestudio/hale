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

package eu.esdihumboldt.hale.ui.io.action;

import org.eclipse.jface.action.IAction;

import de.fhg.igd.eclipse.ui.util.extension.AbstractExtensionContribution;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
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
