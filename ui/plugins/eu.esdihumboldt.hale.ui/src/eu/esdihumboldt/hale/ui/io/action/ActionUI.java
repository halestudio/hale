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

}
