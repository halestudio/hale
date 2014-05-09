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

import eu.esdihumboldt.hale.io.csv.ui.DefaultSchemaTypePage;
import eu.esdihumboldt.hale.io.xls.AnalyseXLSSchemaTable;

/**
 * Schema type configuration page for loading xls/xlsx schema files
 * 
 * @author Patrick Lieb
 * 
 */
public class XLSSchemaTypePage extends DefaultSchemaTypePage {

	/**
	 * Default constructor
	 */
	public XLSSchemaTypePage() {
		super("XLS.SchemaTypePage");
	}

	@Override
	protected void onShowPage(boolean firstShow) {

		try {
			AnalyseXLSSchemaTable analyser = new AnalyseXLSSchemaTable(getWizard().getProvider()
					.getSource().getLocation());
			setHeader(analyser.getHeader().toArray(new String[0]));
			setSecondRow(analyser.getSecondRow().toArray(new String[0]));
			super.onShowPage(firstShow);

			Workbook wb = WorkbookFactory.create(getWizard().getProvider().getSource().getInput());
			Sheet sheet = wb.getSheetAt(0);
			sfe.setStringValue(sheet.getSheetName());
		} catch (Exception e) {
			setMessage("File cannot be loaded!", WARNING);
		}
	}

	private String getFileName(String path) {
		int indexStart = 0;
		int indexEnd = path.length() - 1;

		indexStart = path.lastIndexOf("/") + 1;
		if (path.lastIndexOf(".") >= 0) {
			indexEnd = path.lastIndexOf(".");
		}
		return path.substring(indexStart, indexEnd);
	}
}