/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.xls;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * General utilities when working with Excel files.
 * 
 * @author Simon Templer
 */
public class XLSUtil {

	/**
	 * Extract the text from a given cell. Formulas are evaluated, for blank or
	 * error cells <code>null</code> is returned
	 * 
	 * @param cell the cell
	 * @param evaluator the formula evaluator
	 * @return the cell text
	 */
	public static String extractText(Cell cell, FormulaEvaluator evaluator, Sheet sheet) {
		if (cell == null)
			return null;

		if (isCellPartOfMergedRegion(cell, sheet)) {
			// Get the merged region
			CellRangeAddress mergedRegion = getMergedRegion(cell, sheet);

			// Get the first cell of the merged region (top-left cell)
			Row mergedRow = sheet.getRow(mergedRegion.getFirstRow());
			cell = mergedRow.getCell(mergedRegion.getFirstColumn());
		}

		if (cell.getCellType() == CellType.BLANK) {
			// do this check here as the evaluator seems to return null on a
			// blank
			return null;
		}

		CellValue value = evaluator.evaluate(cell);

		switch (value.getCellType()) {
		case BLANK:
			return null;
		case BOOLEAN:
			return String.valueOf(value.getBooleanValue());
		case NUMERIC:
			double number = value.getNumberValue();
			if (number == Math.floor(number)) {
				return String.valueOf((int) number);
			}
			return String.valueOf(value.getNumberValue());
		case STRING:
			return value.getStringValue();
		default:
			return null;
		}

	}

	private static boolean isCellPartOfMergedRegion(Cell cell, Sheet sheet) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress region = sheet.getMergedRegion(i);
			if (region.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
				return true;
			}
		}
		return false;
	}

	private static CellRangeAddress getMergedRegion(Cell cell, Sheet sheet) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress region = sheet.getMergedRegion(i);
			if (region.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
				return region;
			}
		}
		return null;
	}

}
