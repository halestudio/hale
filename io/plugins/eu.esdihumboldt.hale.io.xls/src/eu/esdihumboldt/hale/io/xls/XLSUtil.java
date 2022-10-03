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

		if (cell.getCellType() == CellType.BLANK) {
			// do this check here as the evaluator seems to return null on a
			// blank
			return null;
		}

		CellValue value = evaluator.evaluate(cell);

		if (CellType.BLANK.equals(value.getCellType())) {
			return null;
		}
		else if (CellType.BOOLEAN.equals(value.getCellType())) {
			return String.valueOf(value.getBooleanValue());
		}
		else if (CellType.NUMERIC.equals(value.getCellType())) {
			// number formatting
			double number = value.getNumberValue();
			if (number == Math.floor(number)) {
				// it's an integer
				return String.valueOf((int) number);
			}
			return String.valueOf(value.getNumberValue());
		}
		else if (CellType.STRING.equals(value.getCellType())) {
			return value.getStringValue();
		}
		else {
//			if (CellType.FORMULA.equals(value.getCellType()))
			// if (CellType.ERROR.equals(value.getCellType()))
			// fall through
			return null;
		}
	}

}
