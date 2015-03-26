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
		setNeedsProgressMonitor(true);
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
