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

package eu.esdihumboldt.hale.io.xls;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Analyse xls/xlsx schema files
 * 
 * @author Patrick Lieb
 */
public class AnalyseXLSSchemaTable extends AbstractAnalyseTable {

	private final List<String> header;

	private final Map<Integer, List<String>> rows;

	/**
	 * Default constructor
	 * 
	 * @param source the source to load the file from
	 * @param sheetNum number of the sheet in Excel file (0-based)
	 * 
	 * @throws Exception thrown if the analysis fails
	 */
	public AnalyseXLSSchemaTable(LocatableInputSupplier<? extends InputStream> source, boolean xlsx,
			int sheetNum) throws Exception {

		header = new ArrayList<String>();
		rows = new HashMap<Integer, List<String>>();

		analyse(source, xlsx, sheetNum);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xls.AbstractAnalyseTable#headerCell(int,
	 *      java.lang.String)
	 */
	@Override
	protected void headerCell(int num, String text) {
		if (num == header.size()) {
			header.add(text);
		}
		header.set(num, text);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xls.AbstractAnalyseTable#analyseRow(int,
	 *      org.apache.poi.ss.usermodel.Row)
	 */
	@Override
	protected void analyseRow(int num, Row row, Sheet sheet) {
		List<String> rowContent = new ArrayList<String>();
		for (int i = 0; i < row.getLastCellNum(); i++) {
			rowContent.add(extractText(row.getCell(i), sheet));
		}
		if (!rowContent.isEmpty() && rowContent.stream().anyMatch(text -> text != null)) {
			rows.put(num, rowContent);
		}
	}

	/**
	 * @return the header
	 */
	public List<String> getHeader() {
		return header;
	}

	/**
	 * @return the second row
	 */
	public List<String> getSecondRow() {
		return rows.get(1);
	}

	/**
	 * @return a map of all rows with row number as keys
	 */
	public Collection<List<String>> getRows() {
		return rows.values();
	}

}
