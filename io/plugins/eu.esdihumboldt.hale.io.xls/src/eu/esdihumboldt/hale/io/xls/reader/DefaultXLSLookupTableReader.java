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

package eu.esdihumboldt.hale.io.xls.reader;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.xls.XLSUtil;

/**
 * Default lookup table reader for xls files
 * 
 * @author Patrick Lieb
 */
public class DefaultXLSLookupTableReader {

	/**
	 * Reads a xls/xlsx lookup table workbook (from apache POI). The selected
	 * columns specified by parameters keyColumn and valueColumn are mapped
	 * together.
	 * 
	 * @param workbook the workbook to read
	 * @param skipFirst true, if first row should be skipped
	 * @param keyColumn source column of the lookup table
	 * @param valueColumn target column of the lookup table
	 * @param ignoreEmptyStrings if empty strings should be ignored and treated
	 *            as <code>null</code>
	 * @return the lookup table as map
	 */
	public Map<Value, Value> read(Workbook workbook, boolean skipFirst, int keyColumn,
			int valueColumn, boolean ignoreEmptyStrings) {
		Map<Value, Value> map = new LinkedHashMap<Value, Value>();
		Sheet sheet = workbook.getSheetAt(0);
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		int row = 0;
		if (skipFirst)
			row++;
		for (; row < sheet.getPhysicalNumberOfRows(); row++) {
			Row currentRow = sheet.getRow(row);
			if (currentRow != null) {
				String value = XLSUtil.extractText(currentRow.getCell(valueColumn), evaluator,
						sheet, null);
				if (value != null && (!ignoreEmptyStrings || !value.isEmpty())) {
					map.put(Value.of(XLSUtil.extractText(currentRow.getCell(keyColumn), evaluator,
							sheet, null)), Value.of(value));
				}
			}
		}

		return map;
	}

}
