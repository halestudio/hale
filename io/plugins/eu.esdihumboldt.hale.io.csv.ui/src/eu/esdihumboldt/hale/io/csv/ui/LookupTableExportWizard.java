/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.csv.ui;

import eu.esdihumboldt.hale.common.lookup.LookupTableExport;
import eu.esdihumboldt.hale.ui.io.ExportWizard;

/**
 * Wizard for exporting lookup tables
 * 
 * @author Patrick Lieb
 */
public class LookupTableExportWizard extends ExportWizard<LookupTableExport> {

	/**
	 * Default constructor
	 */
	public LookupTableExportWizard() {
		super(LookupTableExport.class);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
//		addPage(new LookupTableExportConfigurationPage());
		addPage(new WriterConfigurationPage());
	}
}
