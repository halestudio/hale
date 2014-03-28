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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import eu.esdihumboldt.hale.common.core.io.Value;

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
	 * @return the lookup table as map
	 */
	public Map<Value, Value> read(Workbook workbook, boolean skipFirst, int keyColumn,
			int valueColumn) {
		Map<Value, Value> map = new LinkedHashMap<Value, Value>();
		Sheet sheet = workbook.getSheetAt(0);
		int row = 0;
		if (skipFirst)
			row++;
		for (; row < sheet.getPhysicalNumberOfRows(); row++) {
			Row currentRow = sheet.getRow(row);
			map.put(Value.of(currentRow.getCell(keyColumn).getStringCellValue()),
					Value.of(currentRow.getCell(valueColumn).getStringCellValue()));
		}

		return map;
	}

}
