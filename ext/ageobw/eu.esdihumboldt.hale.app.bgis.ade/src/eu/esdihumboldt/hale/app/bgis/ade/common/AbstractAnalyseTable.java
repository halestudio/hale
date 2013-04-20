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

package eu.esdihumboldt.hale.app.bgis.ade.common;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Do analysis on simple Excel tables.
 * 
 * @author Simon Templer
 */
public abstract class AbstractAnalyseTable {

	private FormulaEvaluator evaluator;

	private Sheet sheet;

	/**
	 * Load table to analyse from an Excel file.
	 * 
	 * @param location the file location
	 * @throws Exception if an error occurs loading the file
	 */
	protected void analyse(URI location) throws Exception {
		InputStream inp = new BufferedInputStream(location.toURL().openStream());

		try {
			Workbook wb = WorkbookFactory.create(inp);
			sheet = wb.getSheetAt(0);
			evaluator = wb.getCreationHelper().createFormulaEvaluator();

			// the first row represents the header
			analyseHeader();

			// load configuration entries
			analyseContent(sheet);
		} finally {
			// unclear whether the POI API closes the stream
			inp.close();
		}
	}

	/**
	 * Analyzes the table header.
	 */
	protected void analyseHeader() {
		Row header = sheet.getRow(0);

		// identify columns
		for (int i = header.getFirstCellNum(); i < header.getLastCellNum(); i++) {
			Cell cell = header.getCell(i);
			String text = extractText(cell);

			headerCell(i, text);
		}
	}

	/**
	 * @param num zero based column index
	 * @param text the header
	 */
	protected abstract void headerCell(int num, String text);

	/**
	 * Analyse the table content.
	 * 
	 * @param sheet the table sheet
	 */
	private void analyseContent(Sheet sheet) {
		// for each row starting from the second
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			analyseRow(i, row);
		}
	}

	/**
	 * Analyse a content row.
	 * 
	 * @param num the row number (starting from one as the header row is handled
	 *            separately)
	 * @param row the table row
	 */
	protected abstract void analyseRow(int num, Row row);

	/**
	 * Extract the text from a given cell. Formulas are evaluated, for blank or
	 * error cells <code>null</code> is returned
	 * 
	 * @param cell the cell
	 * @return the cell text
	 */
	protected String extractText(Cell cell) {
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
