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

import org.eclipse.core.expressions.Expression;
import org.eclipse.ui.services.IEvaluationService;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Interface for {@link IOWizard} factories provided by the
 * {@link ActionUIExtension}.
 * 
 * @author Simon Templer
 */
public interface ActionUI extends ExtensionObjectFactory<IOWizard<?>> {

	/**
	 * Get the identifier of the associated action
	 * 
	 * @return the associated action ID
	 */
	public String getActionID();

	/**
	 * Get the UI action advisor.
	 * 
	 * @return the action advisor or <code>null</code> if none is available
	 */
	public ActionUIAdvisor<?> getUIAdvisor();

	/**
	 * States if I/O operations based on this advisor represent project
	 * resources and thus shall be remembered, i.e. the configuration stored in
	 * the project. If stored in a project the resource will be loaded again
	 * when the project is loaded.
	 * 
	 * @return if operations based on this advisor shall be remembered
	 */
	public boolean isProjectResource();

	/**
	 * Get the expression that specifies whether the command to show the I/O
	 * wizard for the advisor should be enabled.
	 * 
	 * @see IEvaluationService
	 * @return the expression or <code>null</code> if the command should always
	 *         be enabled
	 */
	public Expression getEnabledWhen();

	/**
	 * Get the reason why the action is disabled in a human understandable way.
	 * May for instance state what the user must do to enable the action.
	 * 
	 * @return the reason message
	 */
	public String getDisabledReason();

	/**
	 * Get the custom wizard title.
	 * 
	 * @return the wizard title, may be <code>null</code>
	 */
	public String getCustomTitle();

}
