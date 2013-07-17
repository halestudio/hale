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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.csv.writer.AbstractAlignmentMappingExport;
import eu.esdihumboldt.hale.io.csv.writer.MappingTableConstants;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for export alignment mappings
 * 
 * @author Patrick Lieb
 */
public class AlignmentMappingExportConfigurationPage extends
		AbstractConfigurationPage<AbstractAlignmentMappingExport, AlignmentMappingExportWizard>
		implements MappingTableConstants {

	private Button defaultExport;
	private Button noBaseAlignments;
	private Button propertyCells;
	private Button namespaces;
	private Button transformationAndDisabled;
	private Text maxColumnWidth;

	/**
	 * Create a mapping export configuration page for alignments
	 */
	public AlignmentMappingExportConfigurationPage() {
		super("sel.mappingExportCells");
		setTitle("Mapping table");
		setDescription("Please specify which mapping cells should be part of the mapping table.");
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

		provider.setParameter(INCLUDE_NAMESPACES, Value.of(namespaces.getSelection()));
		provider.setParameter(TRANSFORMATION_AND_DISABLED_FOR,
				Value.of(transformationAndDisabled.getSelection()));
		provider.setParameter(MAX_COLUMN_WIDTH, Value.of(maxColumnWidth.getText()));
		// set export mode in provider
		if (defaultExport.getSelection()) {
			provider.setParameter(PARAMETER_MODE, Value.of(MODE_ALL));
		}
		else if (noBaseAlignments.getSelection()) {
			provider.setParameter(PARAMETER_MODE, Value.of(MODE_EXCLUDE_BASE));
		}
		else if (propertyCells.getSelection()) {
			provider.setParameter(PARAMETER_MODE, Value.of(MODE_BY_TYPE_CELLS));
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

		propertyCells = new Button(page, SWT.RADIO);
		propertyCells.setText("Type cells with associated property cells");
		propertyCells.setSelection(true);
		propertyCells.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (propertyCells.getSelection()) {
					transformationAndDisabled.setEnabled(true);
				}
				else {
					transformationAndDisabled.setSelection(false);
					transformationAndDisabled.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// same behavior as widgetselected
				widgetSelected(e);
			}
		});

		defaultExport = new Button(page, SWT.RADIO);
		defaultExport.setText("All mapping cells (unordered)");

		noBaseAlignments = new Button(page, SWT.RADIO);
		noBaseAlignments
				.setText("Only mapping cells defined in this alignment (exclude base alignments)");

		transformationAndDisabled = new Button(page, SWT.CHECK);
		transformationAndDisabled.setText("Show disabled cells");
		transformationAndDisabled.setSelection(false);

		namespaces = new Button(page, SWT.CHECK);
		namespaces.setText("Include namespaces");
		namespaces.setSelection(false);

		Composite comp = new Composite(page, NONE);
		comp.setLayout(new GridLayout(4, false));

		final Label maxColLabel = new Label(comp, NONE);
		maxColLabel.setText("Set maximum size of column width:");

		maxColumnWidth = new Text(comp, SWT.SINGLE);
		maxColumnWidth.setText(String.valueOf(500));
		maxColumnWidth.setLayoutData(new GridData(40, SWT.DEFAULT));
		// only digits are allowed to enter
		maxColumnWidth.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(final VerifyEvent event) {
				if (!Character.isDigit(event.character)) {
					event.doit = false;
				}
			}
		});
		Label pixels = new Label(comp, NONE);
		pixels.setText("Pixels");
	}
}
