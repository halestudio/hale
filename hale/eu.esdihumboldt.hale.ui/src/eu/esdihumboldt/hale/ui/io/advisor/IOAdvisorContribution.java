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

package eu.esdihumboldt.hale.ui.io.advisor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtensionContribution;
import de.fhg.igd.eclipse.util.extension.AbstractFactoryAction;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.core.io.IOAdvisor;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Contribution for launching {@link IOWizard}s based on an {@link IOAdvisor}
 * @author Simon Templer
 */
public class IOAdvisorContribution extends
		AbstractExtensionContribution<IOAdvisor<?>, IOAdvisorFactory, IOAdvisorExtension> {
	
	/**
	 * Action that launches an {@link IOWizard}
	 */
	private static class IOWizardAction extends AbstractFactoryAction<IOAdvisorFactory> {
		
		private static final ALogger log = ALoggerFactory.getLogger(IOWizardAction.class);
		
		/**
		 * Constructor
		 * 
		 * @param factory the extension object factory
		 */
		public IOWizardAction(IOAdvisorFactory factory) {
			super(factory, IAction.AS_PUSH_BUTTON);
		}

		/**
		 * @see Action#run()
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void run() {
			try {
				IOWizard<?, ?> wizard = getFactory().createWizard();
				IOAdvisor<?> advisor = getFactory().createExtensionObject();
				((IOWizard) wizard).setAdvisor(advisor);
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
	protected IAction createFactoryAction(IOAdvisorFactory factory) {
		return new IOWizardAction(factory);
	}

	/**
	 * @see AbstractExtensionContribution#initExtension()
	 */
	@Override
	protected IOAdvisorExtension initExtension() {
		return new IOAdvisorExtension();
	}

	/**
	 * @see AbstractExtensionContribution#onAdd(ExtensionObjectFactory)
	 */
	@Override
	protected void onAdd(IOAdvisorFactory factory) {
		// do nothing
	}

	/**
	 * @see AbstractExtensionContribution#onConfigure(ExtensionObjectFactory)
	 */
	@Override
	protected void onConfigure(IOAdvisorFactory factory) {
		// do nothing
	}

	/**
	 * @see AbstractExtensionContribution#onRemove(ExtensionObjectFactory)
	 */
	@Override
	protected void onRemove(IOAdvisorFactory factory) {
		// do nothing
	}

}
