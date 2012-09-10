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
import eu.esdihumboldt.hale.ui.io.action.ImportContribution;
import eu.esdihumboldt.hale.ui.util.wizard.MultiWizard;

/**
 * Generic import wizard
 * 
 * @author Simon Templer
 */
public class SelectImportWizard extends MultiWizard<ActionUIWizardPage> {

	/**
	 * Default constructor
	 */
	public SelectImportWizard() {
		super();

		setWindowTitle("Import wizard");
		setDefaultPageImageDescriptor(HALEUIPlugin.imageDescriptorFromPlugin(
				HALEUIPlugin.PLUGIN_ID, "/icons/banner/import_wiz.png"));
	}

	/**
	 * @see MultiWizard#createPage()
	 */
	@Override
	protected ActionUIWizardPage createPage() {
		return new ActionUIWizardPage(ImportContribution.IMPORT_FILTER,
				"Select the resource to import");
	}

}
