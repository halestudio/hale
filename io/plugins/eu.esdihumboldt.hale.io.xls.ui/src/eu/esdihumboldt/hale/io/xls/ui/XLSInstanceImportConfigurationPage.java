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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.ui.TypeSelectionPage;
import eu.esdihumboldt.hale.io.xls.AbstractAnalyseTable;
import eu.esdihumboldt.hale.io.xls.reader.ReaderSettings;

/**
 * Configuration page for the instance export provider of Excel files
 * 
 * @author Patrick Lieb
 */

public class XLSInstanceImportConfigurationPage extends TypeSelectionPage {

	private static final ALogger log = ALoggerFactory
			.getLogger(XLSInstanceImportConfigurationPage.class);

	private Combo sheetSelection;

	/**
	 * Default Constructor
	 */
	public XLSInstanceImportConfigurationPage() {
		setTitle("Sheet selection");
		setDescription("Select sheet to import instances, your Type and Data reading setting");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {

		page.setLayout(new GridLayout(2, false));

		Label sheetLabel = new Label(page, SWT.None);
		sheetLabel.setText("Select sheet");

		sheetSelection = new Combo(page, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(sheetSelection);

		super.createContent(page);
	}

	@Override
	protected List<String> createDatePatternsList() {
		return Arrays.asList(
				// Standard date formats
				"yyyy-MM-dd", "yy-MM-dd", "dd-MM-yyyy", "MM-dd-yyyy", "yyyy/MM/dd", "dd/MM/yyyy",
				"dd/MMM/yyyy", "MM/dd/yyyy", "yyyy.MM.dd", "dd.MM.yyyy", "MM.dd.yyyy", "yyyyMMdd",
				// Custom date format
				"MMMM d, yyyy", TypeSelectionPage.CUSTOM_FORMAT_LABEL);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		if (!firstShow) {
			setErrorMessage(null);
		}

		try (InputStream input = getWizard().getProvider().getSource().getInput()) {
			Workbook wb = AbstractAnalyseTable.loadWorkbook(input,
					getWizard().getProvider().getSource().getLocation(),
					ReaderSettings.isXlsxContentType(getWizard().getContentType()));
			int numberOfSheets = wb.getNumberOfSheets();
			String[] items = new String[numberOfSheets];
			for (int i = 0; i < numberOfSheets; i++) {
				items[i] = wb.getSheetAt(i).getSheetName();
			}
			sheetSelection.setItems(items);
		} catch (Exception e) {
			log.error("Error loading Excel file", e);
			setErrorMessage("Cannot load Excel file!");
			setPageComplete(false);
			return;
		}
		super.onShowPage(firstShow);

		dateFormatterLabel
				.setText("Format for imported date values\r\n(values are represented as strings)");
		sheetSelection.select(0);

		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceReader provider) {
		provider.setParameter(InstanceTableIOConstants.SHEET_INDEX,
				Value.of(sheetSelection.getSelectionIndex()));

		return super.updateConfiguration(provider);
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
