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

import static com.google.common.base.Preconditions.checkState;

import org.eclipse.core.expressions.Expression;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationReference;
import org.eclipse.ui.services.IEvaluationService;

import de.cs3d.ui.util.eclipse.extension.AbstractFactoryAction;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Action that launches an {@link IOWizard}
 */
public class IOWizardAction extends AbstractFactoryAction<ActionUI> {

	private static final ALogger log = ALoggerFactory.getLogger(IOWizardAction.class);
	private IEvaluationReference evaluationReference;

	/**
	 * Constructor
	 * 
	 * @param actionId the actionId
	 * @throws NullPointerException if no ActionUI can be found for the given
	 *             actionId
	 */
	public IOWizardAction(String actionId) {
		this(ActionUIExtension.getInstance().findActionUI(actionId));
	}

	/**
	 * Constructor
	 * 
	 * @param factory the extension object factory
	 */
	public IOWizardAction(ActionUI factory) {
		super(factory, IAction.AS_PUSH_BUTTON);

		Expression enabledWhen = factory.getEnabledWhen();
		if (enabledWhen != null) {
			IEvaluationService es = (IEvaluationService) PlatformUI.getWorkbench().getService(
					IEvaluationService.class);
			evaluationReference = es.addEvaluationListener(enabledWhen,
					new IPropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent event) {
							if (event.getNewValue() != null)
								setEnabled((Boolean) event.getNewValue());
						}
					}, "enabled");
			// listener should be removed when not needed, use the dispose
			// method.
			// AbstractExtensionContribution caches the action it is OK without
			// using the dispose method (the action is only created once per
			// contribution)
		}
	}

	/**
	 * Disposes this action. Should be called when the action isn't needed
	 * anymore.
	 */
	public void dispose() {
		if (evaluationReference != null) {
			IEvaluationService es = (IEvaluationService) PlatformUI.getWorkbench().getService(
					IEvaluationService.class);
			es.removeEvaluationListener(evaluationReference);
		}
	}

	/**
	 * @see Action#run()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void run() {
		try {
			// retrieve action ID
			final String actionId = getFactory().getActionID();

			// find associated advisor
			IOAdvisor<?> advisor = IOAdvisorExtension.getInstance().findAdvisor(actionId);
			checkState(advisor != null, "No advisor for action found");

			// create wizard
			IOWizard<?> wizard = getFactory().createExtensionObject();
			// set advisor and action ID
			((IOWizard) wizard).setAdvisor(advisor, actionId);
			Shell shell = Display.getCurrent().getActiveShell();
			WizardDialog dialog = new WizardDialog(shell, wizard);
			notifyResult(dialog.open() == Window.OK);
		} catch (Exception e) {
			log.error("Could not launch I/O wizard for advisor " + getFactory().getIdentifier(), e);
		}
	}
}