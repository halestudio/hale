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

package eu.esdihumboldt.hale.ui.io.align;

import eu.esdihumboldt.hale.io.csv.writer.AbstractAlignmentMappingExport;
import eu.esdihumboldt.hale.ui.io.ExportWizard;

/**
 * Wizard for export alignment mappings
 * 
 * @author Patrick
 */
public class AlignmentMappingExportWizard extends ExportWizard<AbstractAlignmentMappingExport> {

	/**
	 * Create an alignment mapping export wizard
	 */
	public AlignmentMappingExportWizard() {
		super(AbstractAlignmentMappingExport.class);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		// add the alignment mapping export configuration page
		addPage(new AlignmentMappingExportConfigurationPage());
		super.addPages();
	}

}
