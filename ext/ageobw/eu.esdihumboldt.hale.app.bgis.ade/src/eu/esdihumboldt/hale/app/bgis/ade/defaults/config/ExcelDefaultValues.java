/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.defaults.config;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Reads default values configuration from an Excel file.
 * 
 * @author Simon Templer
 */
public class ExcelDefaultValues {

	private static final String COLUMN_ATTRIBUTE = "AAlpha";

	private static final String COLUMN_DEF_VALUE = "VAlpha";

	private static final String COLUMN_DEF_VALUE_2 = "Wert";

	private int attColIndex = -1, valColIndex = -1, valColIndex2 = -1;

	private FormulaEvaluator evaluator;

	private Sheet sheet;

	/**
	 * Load default values configuration from an Excel file.
	 * 
	 * @param location the file location
	 * @return the default values configuration
	 * @throws Exception if an error occurs loading the configuration
	 */
	public DefaultValues loadDefaultValues(URI location) throws Exception {
		DefaultValues result = new DefaultValues();

		InputStream inp = new BufferedInputStream(location.toURL().openStream());

		try {
			Workbook wb = WorkbookFactory.create(inp);
			sheet = wb.getSheetAt(0);
			evaluator = wb.getCreationHelper().createFormulaEvaluator();

			// the first row represents the header
			analyseHeader();

			// load configuration entries
			addConfigs(result);
		} finally {
			// unclear whether the POI API closes the stream
			inp.close();
		}

		return result;
	}

	private void analyseHeader() {
		Row header = sheet.getRow(0);

		// identify columns
		for (int i = header.getFirstCellNum(); i < header.getLastCellNum(); i++) {
			Cell cell = header.getCell(i);
			String text = extractText(cell);

			if (attColIndex < 0 && COLUMN_ATTRIBUTE.equalsIgnoreCase(text)) {
				attColIndex = i;
			}
			if (valColIndex < 0 && COLUMN_DEF_VALUE.equalsIgnoreCase(text)) {
				valColIndex = i;
			}
			if (valColIndex2 < 0 && COLUMN_DEF_VALUE_2.equalsIgnoreCase(text)) {
				valColIndex2 = i;
			}
		}
	}

	private void addConfigs(DefaultValues result) {
		if (attColIndex < 0 || (valColIndex < 0 && valColIndex2 < 0)) {
			throw new IllegalArgumentException("Configuration table has the wrong format.");
		}

		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			/*
			 * Create configuration entries for each row beginning with the
			 * second, if attribute and value are given.
			 */
			Row row = sheet.getRow(i);
			Cell attCell = row.getCell(attColIndex);
			String attribute = extractText(attCell);

			if (attribute != null && !attribute.isEmpty()) {
				String value = null;
				if (valColIndex > 0) {
					value = extractText(row.getCell(valColIndex));
				}
				if (value == null && valColIndex2 > 0) {
					value = extractText(row.getCell(valColIndex2));
				}

				if (value != null) {
					ConfigEntry entry = new ConfigEntry();
					entry.setAttribute(attribute);
					entry.setDefaultValue(value);
					result.addEntry(entry);

					System.out.println(MessageFormat.format(
							"Default value for attribute ''{0}'': {1}", attribute, value));
				}
			}
		}
	}

	/**
	 * Extract the text from a given cell. Formulas are evaluated, for blank or
	 * error cells <code>null</code> is returned
	 * 
	 * @param cell the cell
	 * @return the cell text
	 */
	private String extractText(Cell cell) {
		if (cell == null)
			return null;

		if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			// do this check here as the evaluator seems to return null on a
			// blank
			return null;
		}

		CellValue value = evaluator.evaluate(cell);

		switch (value.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_BOOLEAN:
			return String.valueOf(value.getBooleanValue());
		case Cell.CELL_TYPE_NUMERIC:
			// number formatting
			double number = value.getNumberValue();
			if (number == Math.floor(number)) {
				// it's an integer
				return String.valueOf((int) number);
			}
			return String.valueOf(value.getNumberValue());
		case Cell.CELL_TYPE_STRING:
			return value.getStringValue();
		case Cell.CELL_TYPE_FORMULA:
			// will not happen as we used the evaluator
		case Cell.CELL_TYPE_ERROR:
			// fall through
		default:
			return null;
		}
	}

}
