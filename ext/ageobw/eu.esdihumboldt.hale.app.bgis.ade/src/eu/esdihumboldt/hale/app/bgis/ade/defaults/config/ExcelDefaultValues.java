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

import java.net.URI;
import java.text.MessageFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import eu.esdihumboldt.hale.io.xls.AbstractAnalyseTable;

/**
 * Reads default values configuration from an Excel file.
 * 
 * @author Simon Templer
 */
public class ExcelDefaultValues extends AbstractAnalyseTable {

	private static final String COLUMN_ATTRIBUTE = "AAlpha";

	private static final String COLUMN_DEF_VALUE = "VAlpha";

	private static final String COLUMN_DEF_VALUE_2 = "Wert";

	private int attColIndex = -1, valColIndex = -1, valColIndex2 = -1;

	private DefaultValues result;

	/**
	 * Load default values configuration from an Excel file.
	 * 
	 * @param location the file location
	 * @return the default values configuration
	 * @throws Exception if an error occurs loading the configuration
	 */
	public DefaultValues loadDefaultValues(URI location) throws Exception {
		result = new DefaultValues();
		analyse(location);
		return result;
	}

	@Override
	protected void headerCell(int num, String text) {
		// identify columns
		if (attColIndex < 0 && COLUMN_ATTRIBUTE.equalsIgnoreCase(text)) {
			attColIndex = num;
		}
		if (valColIndex < 0 && COLUMN_DEF_VALUE.equalsIgnoreCase(text)) {
			valColIndex = num;
		}
		if (valColIndex2 < 0 && COLUMN_DEF_VALUE_2.equalsIgnoreCase(text)) {
			valColIndex2 = num;
		}
	}

	@Override
	protected void analyseRow(int num, Row row) {
		if (attColIndex < 0 || (valColIndex < 0 && valColIndex2 < 0)) {
			throw new IllegalArgumentException("Configuration table has the wrong format.");
		}

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

				System.out.println(MessageFormat.format("Default value for attribute ''{0}'': {1}",
						attribute, value));
			}
		}
	}

}
