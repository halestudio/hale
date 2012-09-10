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

package eu.esdihumboldt.hale.ui.io.action.wizard;

import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.io.action.ExportContribution;
import eu.esdihumboldt.hale.ui.util.wizard.MultiWizard;

/**
 * Generic export wizard
 * 
 * @author Simon Templer
 */
public class SelectExportWizard extends MultiWizard<ActionUIWizardPage> {

	/**
	 * Default constructor
	 */
	public SelectExportWizard() {
		super();

		setWindowTitle("Export wizard");
		setDefaultPageImageDescriptor(HALEUIPlugin.imageDescriptorFromPlugin(
				HALEUIPlugin.PLUGIN_ID, "/icons/banner/export_wiz.png"));
	}

	/**
	 * @see MultiWizard#createPage()
	 */
	@Override
	protected ActionUIWizardPage createPage() {
		return new ActionUIWizardPage(ExportContribution.EXPORT_FILTER,
				"Select the resource to export");
	}

}
