/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.xls.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import eu.esdihumboldt.hale.io.csv.writer.AbstractTableInstanceWriter;
import eu.esdihumboldt.hale.io.xls.XLSCellStyles;

/**
 * Instance export provider for xls files
 * 
 * @author Patrick Lieb
 */
public class XLSInstanceWriter extends AbstractTableInstanceWriter {

	private Workbook wb;

	private CellStyle headerStyle;
	private CellStyle cellStyle;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		super.execute(progress, reporter);

		// write xls file
		if (getContentType().getId().equals("eu.esdihumboldt.hale.io.xls.xls")) {
			wb = new HSSFWorkbook();
		}
		// write xlsx file
		else if (getContentType().getId().equals("eu.esdihumboldt.hale.io.xls.xlsx")) {
			wb = new XSSFWorkbook();
		}
		else {
			reporter.error(new IOMessageImpl("Content type is invalid!", null));
			return reporter;
		}

		cellStyle = XLSCellStyles.getNormalStyle(wb, false);
		headerStyle = XLSCellStyles.getHeaderStyle(wb);

		List<List<List<Object>>> sheets = getTable();
		List<String> sheetNames = getSheetNames();

		for (int i = 0; i < sheets.size(); i++) {
			Sheet sh = wb.createSheet(sheetNames.get(i));

			// store instances with other type definitions in extra sheet
			write(sheets.get(i), sh);
		}

		// write file
		FileOutputStream out = new FileOutputStream(getTarget().getLocation().getPath());
		wb.write(out);
		out.close();

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XLS file";
	}

	// the first instance has to be handled independently since it determines
	// the type definition of the current sheet
	private void write(List<List<Object>> table, Sheet sheet) {

		for (int i = 0; i < table.size(); i++) {
			Row row = sheet.createRow(i);
			List<Object> tableRow = table.get(i);
			writeRow(row, tableRow, i == 0);

		}
		resizeSheet(sheet);
	}

	private void writeRow(Row row, List<Object> tableRow, boolean header) {
		for (int k = 0; k < tableRow.size(); k++) {
			Cell cell = row.createCell(k);
			if (header)
				cell.setCellStyle(headerStyle);
			else
				cell.setCellStyle(cellStyle);
			setValueOfCell(cell, tableRow.get(k));
		}
	}

	// only based on first row
	private void resizeSheet(Sheet sheet) {
		for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void setValueOfCell(Cell cell, Object value) {
		if (value instanceof Double)
			cell.setCellValue((Double) value);
		else if (value instanceof Boolean)
			cell.setCellValue((Boolean) value);
		else if (value instanceof Calendar)
			cell.setCellValue((Calendar) value);
		else if (value instanceof Date)
			cell.setCellValue((Date) value);
		else if (value instanceof RichTextString)
			cell.setCellValue((RichTextString) value);
		else if (value instanceof String)
			cell.setCellValue((String) value);
		else {
			if (value instanceof OInstance) {
				Object instValue = ((OInstance) value).getValue();
				if (instValue != null) {
					cell.setCellValue(instValue.toString());
					return;
				}
			}
			if (value != null)
				cell.setCellValue(value.toString());
			else
				cell.setCellValue("");
		}
	}
}
