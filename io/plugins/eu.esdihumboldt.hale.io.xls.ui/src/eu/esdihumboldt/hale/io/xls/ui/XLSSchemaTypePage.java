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

package eu.esdihumboldt.hale.io.xls.ui;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.ui.DefaultSchemaTypePage;
import eu.esdihumboldt.hale.io.xls.AnalyseXLSSchemaTable;

/**
 * Schema type configuration page for loading xls/xlsx schema files. Adds a
 * selector for the xls sheet to {@link DefaultSchemaTypePage}
 * 
 * @author Patrick Lieb
 * 
 */
public class XLSSchemaTypePage extends DefaultSchemaTypePage {

	private int sheetNum = 0;
	private Combo sheet;

	/**
	 * Default constructor
	 */
	public XLSSchemaTypePage() {
		super("XLS.SchemaTypePage");
	}

	/**
	 * @see eu.esdihumboldt.hale.io.csv.ui.DefaultSchemaTypePage#updateConfiguration(eu.esdihumboldt.hale.common.schema.io.SchemaReader)
	 */
	@Override
	public boolean updateConfiguration(SchemaReader provider) {
		provider.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(sheetNum));
		return super.updateConfiguration(provider);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.csv.ui.DefaultSchemaTypePage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite parent) {

		parent.setLayout(new GridLayout(1, false));

		Composite menu = new Composite(parent, SWT.NONE);
		menu.setLayout(new GridLayout(2, false));

		GridDataFactory.fillDefaults().grab(true, false).applyTo(menu);

		// create label and combo
		Label sheetLabel = new Label(menu, SWT.NONE);
		sheetLabel.setText("Select sheet:");
		sheet = new Combo(menu, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(sheet);

		sheet.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				sheetNum = sheet.getSelectionIndex();
				try {
					sfe.setStringValue(sheet.getText());
					update(sheetNum);
				} catch (Exception e) {
					setErrorMessage("Cannot read Excel file!");
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				sheetNum = sheet.getSelectionIndex();
				try {
					sfe.setStringValue(sheet.getText());
					update(sheetNum);
				} catch (Exception e1) {
					setErrorMessage("Cannot read Excel file!");
				}

			}
		});

		Composite defaultSchemaTyePage = new Composite(parent, SWT.NONE);
		defaultSchemaTyePage.setLayout(new FillLayout());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(defaultSchemaTyePage);

		super.createContent(defaultSchemaTyePage);
	}

	@Override
	protected void onShowPage(boolean firstShow) {

		try {
			Workbook wb = WorkbookFactory.create(getWizard().getProvider().getSource().getInput());
			int numberOfSheets = wb.getNumberOfSheets();
			String[] items = new String[numberOfSheets];
			for (int i = 0; i < numberOfSheets; i++) {
				items[i] = wb.getSheetAt(i).getSheetName();
			}

			sheet.setItems(items);

			update(sheetNum);
			sheet.select(sheetNum);
			super.onShowPage(firstShow);

			Sheet sheet = wb.getSheetAt(0);
			sfe.setStringValue(sheet.getSheetName());
		} catch (Exception e) {
			setMessage("Excel file cannot be loaded!", WARNING);
		}
	}

	// update whole page with current sheet number
	private void update(int sheetNum) throws Exception {
		AnalyseXLSSchemaTable analyser = new AnalyseXLSSchemaTable(getWizard().getProvider()
				.getSource().getLocation(), sheetNum);
		setHeader(analyser.getHeader().toArray(new String[0]));
		setSecondRow(analyser.getSecondRow().toArray(new String[0]));

		super.update();
	}
}