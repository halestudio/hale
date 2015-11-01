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
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

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
	public static String extractText(Cell cell, FormulaEvaluator evaluator) {
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
