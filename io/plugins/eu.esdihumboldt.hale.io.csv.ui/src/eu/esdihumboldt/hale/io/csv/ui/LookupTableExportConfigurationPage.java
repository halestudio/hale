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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.lookup.LookupTableExport;
import eu.esdihumboldt.hale.io.csv.writer.LookupTableExportConstants;
import eu.esdihumboldt.hale.io.csv.writer.MappingTableConstants;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for the lookup table export setting the header for keys
 * and values
 * 
 * @author Patrick Lieb
 */
public class LookupTableExportConfigurationPage extends
		AbstractConfigurationPage<LookupTableExport, LookupTableExportWizard> implements
		MappingTableConstants {

	private Text source;
	private Text target;

	/**
	 * Create a mapping export configuration page for alignments
	 */
	public LookupTableExportConfigurationPage() {
		super("conf.csvlookuptable_export");
		setTitle("General lookup table settings");
		setDescription("Please set the header of the output file.");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// not required
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// not required
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(LookupTableExport provider) {

		provider.setParameter(LookupTableExportConstants.PARAM_SOURCE_COLUMN, Value.of(source.getText()));
		provider.setParameter(LookupTableExportConstants.PARAM_TARGET_COLUMN, Value.of(target.getText()));
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {

		// layout of the page is a grid layout with two columns
		page.setLayout(new GridLayout(2, false));

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Label sourceLabel = new Label(page, SWT.NONE);
		sourceLabel.setText("First column (source):");
		source = new Text(page, SWT.SINGLE | SWT.BORDER);
		source.setText("Source");
		source.setLayoutData(gridData);

		Label targetLabel = new Label(page, SWT.NONE);
		targetLabel.setText("Second column (target):");
		target = new Text(page, SWT.SINGLE | SWT.BORDER);
		target.setText("Target");
		target.setLayoutData(gridData);

		setPageComplete(true);
	}
}
