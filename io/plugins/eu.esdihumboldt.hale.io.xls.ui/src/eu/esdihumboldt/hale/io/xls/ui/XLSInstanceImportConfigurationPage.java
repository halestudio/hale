/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.xls.ui;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.instance.InstanceImportWizard;

/**
 * Configuration page for the instance export provider of Excel files
 * 
 * @author Patrick Lieb
 */
public class XLSInstanceImportConfigurationPage extends
		AbstractConfigurationPage<InstanceReader, InstanceImportWizard> {

	private Combo sheetSelection;

	/**
	 * Default Constructor
	 */
	public XLSInstanceImportConfigurationPage() {
		super("xls.instance.import.sheet.selection");
		setTitle("Sheet selection");
		setDescription("Select sheet to import instances");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {

		page.setLayout(new GridLayout(1, false));

		Composite menu = new Composite(page, SWT.NONE);
		menu.setLayout(new GridLayout(2, false));

		GridDataFactory.fillDefaults().grab(true, false).applyTo(menu);

		Label sheetLabel = new Label(menu, SWT.None);
		sheetLabel.setText("Select sheet");

		sheetSelection = new Combo(menu, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(sheetSelection);

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {

		try {
			Workbook wb = WorkbookFactory.create(getWizard().getProvider().getSource().getInput());
			int numberOfSheets = wb.getNumberOfSheets();
			String[] items = new String[numberOfSheets];
			for (int i = 0; i < numberOfSheets; i++) {
				items[i] = wb.getSheetAt(i).getSheetName();
			}
			sheetSelection.setItems(items);
		} catch (Exception e) {
			setErrorMessage("Cannot load Excel file!");
			return;
		}
		super.onShowPage(firstShow);
		sheetSelection.select(0);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceReader provider) {
		provider.setParameter(InstanceTableIOConstants.SHEET_INDEX,
				Value.of(sheetSelection.getSelectionIndex()));
		return true;
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

}
