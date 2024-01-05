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

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.ui.DefaultSchemaTypePage;
import eu.esdihumboldt.hale.io.xls.AbstractAnalyseTable;
import eu.esdihumboldt.hale.io.xls.AnalyseXLSSchemaTable;
import eu.esdihumboldt.hale.io.xls.reader.ReaderSettings;

/**
 * Schema type configuration page for loading xls/xlsx schema files. Adds a
 * selector for the xls sheet to {@link DefaultSchemaTypePage}
 * 
 * @author Patrick Lieb
 * 
 */
public class XLSSchemaTypePage extends DefaultSchemaTypePage {

	private static final ALogger log = ALoggerFactory.getLogger(XLSSchemaTypePage.class);

	private int sheetNum = 0;
	private Combo sheet;
	private URI oldLocation;

	/**
	 * Default constructor
	 */
	public XLSSchemaTypePage() {
		super("XLS.SchemaTypePage");
		setPageComplete(false);
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
				int newSheetNum = sheet.getSelectionIndex();
				try {
					setStringFieldEditorValue(sheet.getText());
					// update(sheetNum);
					sheetSelectionChanged(newSheetNum);
					sheetNum = newSheetNum;
				} catch (Exception e) {
					setPageComplete(false);
					clearSuperPage();
					setErrorMessage("The sheet is empty or the header is not valid!");
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				int newSheetNum = sheet.getSelectionIndex();
				try {
					setStringFieldEditorValue(sheet.getText());
					// update(sheetNum);
					sheetSelectionChanged(newSheetNum);
					sheetNum = newSheetNum;
				} catch (Exception e1) {
					setPageComplete(false);
					clearSuperPage();
					setErrorMessage("The sheet is empty or the header is not valid!");
				}
			}

		});

		Composite defaultSchemaTyePage = new Composite(parent, SWT.NONE);
		defaultSchemaTyePage.setLayout(new FillLayout());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(defaultSchemaTyePage);

		oldLocation = null;

		super.createContent(defaultSchemaTyePage);
	}

	/**
	 * Call this, if the property and type elements are not valid any more
	 */
	private void clearSuperPage() {
		setHeader(new String[0]);
		setSecondRow(new String[0]);
		super.onShowPage(false);
		setStringFieldEditorValue("");
	}

	@Override
	protected void onShowPage(boolean firstShow) {

		URI newLocation = getWizard().getProvider().getSource().getLocation();
		if (!firstShow && newLocation != null && !newLocation.equals(oldLocation)) {
			sheetNum = 0;
		}

		try (InputStream input = getWizard().getProvider().getSource().getInput()) {
			Workbook wb = AbstractAnalyseTable.loadWorkbook(input, newLocation,
					ReaderSettings.isXlsxContentType(getWizard().getContentType()));

			int numberOfSheets = wb.getNumberOfSheets();
			if (sheetNum >= numberOfSheets) {
				sheetNum = 0;
			}
			ArrayList<String> items = new ArrayList<String>();
			for (int i = 0; i < numberOfSheets; i++) {
				// only add items if there is a header (no empty sheet)
				Row row = wb.getSheetAt(i).getRow(0);
				items.add(wb.getSheetAt(i).getSheetName());
				if (row != null) {
					update(i);
					sheetNum = i;
				}
			}

			sheet.setItems(items.toArray(new String[items.size()]));
			sheet.select(sheetNum);

			// try to update
			update(sheetNum);

			super.onShowPage(firstShow);

			// Overwrite super string field editor value
			Sheet sheet = wb.getSheetAt(sheetNum);
			setStringFieldEditorValue(sheet.getSheetName());

			oldLocation = newLocation;

		} catch (OldExcelFormatException e) {
			// the setup is not in a valid state
			setErrorMessage(
					"Old excel format detected (format 5.0/7.0 (BIFF5)). Please convert the excel file to BIFF8 from Excel versions 97/2000/XP/2003.");
			clearFromException();
		} catch (Exception e) {
			log.error("Error loading Excel file", e);
			setErrorMessage("Excel file cannot be loaded!");
			clearFromException();
		}
	}

	/**
	 * clear page and super page
	 */
	private void clearFromException() {
		clearPage();
		clearSuperPage();
		setPageComplete(false);
	}

	/**
	 * Use this if an error occurs
	 * 
	 */
	private void clearPage() {
		sheetNum = 0;
		sheet.clearSelection();
		sheet.removeAll();
		oldLocation = null;
	}

	/**
	 * Call this to update the content if the user changed the sheet num
	 * 
	 * @param sheetNum The number of the selected sheet (start at 0)
	 * 
	 * @throws Exception , if an error occurs while reading or parsing the xml
	 *             file
	 */
	private void sheetSelectionChanged(int sheetNum) throws Exception {
		update(sheetNum);
		super.update();
	}

	// update whole page with current sheet number
	// no lines should be skipped
	private void update(int sheetNum) throws Exception {

		// if the sheet is empty an Exception occurs
		AnalyseXLSSchemaTable analyser = new AnalyseXLSSchemaTable(
				getWizard().getProvider().getSource(),
				ReaderSettings.isXlsxContentType(getWizard().getContentType()), sheetNum, 0, null);

		setHeader(analyser.getHeader().toArray(new String[0]));

		if (analyser.getSecondRow() != null) {
			setSecondRow(analyser.getSecondRow().toArray(new String[0]));
		}
		else {
			// there is no second row, so the header is the data
			setSecondRow(analyser.getHeader().toArray(new String[0]));
		}

	}

}