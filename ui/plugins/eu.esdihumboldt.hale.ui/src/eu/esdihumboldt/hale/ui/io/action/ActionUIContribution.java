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

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.expressions.Expression;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

import de.cs3d.ui.util.eclipse.extension.AbstractExtensionContribution;
import de.cs3d.ui.util.eclipse.extension.AbstractFactoryAction;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorFactory;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Contribution for launching {@link IOWizard}s based on an {@link IOAdvisor}
 * @author Simon Templer
 */
public class ActionUIContribution extends
		AbstractExtensionContribution<IOWizard<?>, ActionUI, ActionUIExtension> {
	
	/**
	 * Action that launches an {@link IOWizard}
	 */
	private static class IOWizardAction extends AbstractFactoryAction<ActionUI> {
		
		private static final ALogger log = ALoggerFactory.getLogger(IOWizardAction.class);
		
		/**
		 * Constructor
		 * 
		 * @param factory the extension object factory
		 */
		public IOWizardAction(ActionUI factory) {
			super(factory, IAction.AS_PUSH_BUTTON);
			
			Expression enabledWhen = factory.getEnabledWhen();
			if (enabledWhen != null) {
				IEvaluationService es = (IEvaluationService) PlatformUI.getWorkbench().getService(IEvaluationService.class);
				es.addEvaluationListener(enabledWhen, new IPropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						setEnabled((Boolean) event.getNewValue());
					}
				}, "enabled");
				// listener should be removed when not needed, but there is no dispose method
				// as AbstractExtensionContribution caches the action it is OK like this (the action is only created once per contribution)
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
				
				// find associated advisor(s)
				List<IOAdvisorFactory> advisors = IOAdvisorExtension.getInstance().getFactories(new FactoryFilter<IOAdvisor<?>, IOAdvisorFactory>() {
					
					@Override
					public boolean acceptFactory(IOAdvisorFactory factory) {
						return factory.getActionID().equals(actionId);
					}
					
					@Override
					public boolean acceptCollection(
							ExtensionObjectFactoryCollection<IOAdvisor<?>, IOAdvisorFactory> collection) {
						return true;
					}
				});
				
				// create advisor if possible
				IOAdvisor<?> advisor;
				if (advisors == null || advisors.isEmpty()) {
					throw new IllegalStateException(MessageFormat.format(
							"No advisor for action {0} found", actionId));
				}
				else {
					if (advisors.size() > 1) {
						log.warn(MessageFormat.format(
								"Multiple advisors for action {0} found", 
								actionId));
					}
					
					advisor = advisors.get(0).createExtensionObject();
				}
				
				// create wizard
				IOWizard<?> wizard = getFactory().createExtensionObject();
				// set advisor and action ID
				((IOWizard) wizard).setAdvisor(advisor, actionId);
				Shell shell = Display.getCurrent().getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.open();
			} catch (Exception e) {
				log.error("Could not launch I/O wizard for advisor " + 
						getFactory().getIdentifier(), e);
			}
		}

	}

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
