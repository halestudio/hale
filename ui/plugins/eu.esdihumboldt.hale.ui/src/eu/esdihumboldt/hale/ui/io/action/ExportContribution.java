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

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOAction;
import eu.esdihumboldt.hale.common.core.io.extension.IOActionExtension;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorFactory;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Contribution for export advisors
 * @author Simon Templer
 */
public class ExportContribution extends ActionUIContribution {
	
	private static final ALogger log = ALoggerFactory.getLogger(ExportContribution.class);
	
	/**
	 * Filter for export {@link ActionUI}s
	 */
	public static final FactoryFilter<IOWizard<?>, ActionUI> EXPORT_FILTER = new FactoryFilter<IOWizard<?>, ActionUI>() {
		
		@Override
		public boolean acceptFactory(ActionUI factory) {
			// accept if action is an export action
			final String actionId = factory.getActionID();
			IOAction action = IOActionExtension.getInstance().get(actionId);
			boolean isExport = ExportProvider.class.isAssignableFrom(action.getProviderType());
			if (isExport) {
				// and if there are any advisors present for the action
				for (IOAdvisorFactory advisorFactory : IOAdvisorExtension.getInstance().getFactories()) {
					if (advisorFactory.getActionID().equals(actionId)) {
						return true;
					}
				}
				
				log.warn("No advisors present for action " + actionId);
			}
			return false;
		}
		
		@Override
		public boolean acceptCollection(
				ExtensionObjectFactoryCollection<IOWizard<?>, ActionUI> collection) {
			return true;
		}
	};

	/**
	 * Default constructor
	 */
	public ExportContribution() {
		super();
		
		setFilter(EXPORT_FILTER);
	}

}
