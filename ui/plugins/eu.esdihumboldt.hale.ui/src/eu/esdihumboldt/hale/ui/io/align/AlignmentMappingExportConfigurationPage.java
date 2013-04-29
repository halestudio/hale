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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.csv.writer.AbstractAlignmentMappingExport;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for export alignment mappings
 * 
 * @author Patrick Lieb
 */
public class AlignmentMappingExportConfigurationPage extends
		AbstractConfigurationPage<AbstractAlignmentMappingExport, AlignmentMappingExportWizard> {

	private final String mappingMode = "alignmentMappingExportMode";

	private final String defaultExportParam = "defaultExport";
	private final String noBaseAlignmentsParam = "noBaseAlignments";
	private final String propertyCellsParam = "propertyCells";

	private Button defaultExport;
	private Button noBaseAlignments;
	private Button propertyCells;

	/**
	 * Create a mapping export configuration page for alignments
	 */
	public AlignmentMappingExportConfigurationPage() {
		super("sel.mappingExportCells");
		setTitle("Please select export mode");
		setDescription("Choose the cells which should be exported into the mapping");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// do nothing
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(AbstractAlignmentMappingExport provider) {
		// set export mode in provider
		if (defaultExport.getSelection()) {
			provider.setParameter(mappingMode, Value.of(defaultExportParam));
		}
		else if (noBaseAlignments.getSelection()) {
			provider.setParameter(mappingMode, Value.of(noBaseAlignmentsParam));
		}
		else if (propertyCells.getSelection()) {
			provider.setParameter(mappingMode, Value.of(propertyCellsParam));
		}
		else
			return false;
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {

		// layout of the page is a grid layout with one column
		page.setLayout(new GridLayout(1, false));

		defaultExport = new Button(page, SWT.RADIO);
		defaultExport.setSelection(true);
		defaultExport.setText("Default Export - all Alignment Cells");

		noBaseAlignments = new Button(page, SWT.RADIO);
		noBaseAlignments.setText("No export of Base Alignments");

		propertyCells = new Button(page, SWT.RADIO);
		propertyCells.setText("Export Type Cells with associated Property Cells");
	}

}
