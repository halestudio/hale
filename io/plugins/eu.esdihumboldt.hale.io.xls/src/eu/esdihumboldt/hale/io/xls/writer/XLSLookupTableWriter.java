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

package eu.esdihumboldt.hale.io.xls.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.lookup.impl.AbstractLookupExport;
import eu.esdihumboldt.hale.io.csv.writer.LookupTableExportConstants;

/**
 * Export provider for xls/xlsx lookup table files
 * 
 * @author Patrick Lieb
 */
public class XLSLookupTableWriter extends AbstractLookupExport {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		Workbook workbook;
		// write xls file
		if (getContentType().getId().equals("eu.esdihumboldt.hale.io.xls.xls")) {
			workbook = new HSSFWorkbook();
		}
		// write xlsx file
		else if (getContentType().getId().equals("eu.esdihumboldt.hale.io.xls.xlsx")) {
			workbook = new XSSFWorkbook();
		}
		else {
			reporter.error(new IOMessageImpl("Content type is invalid!", null));
			reporter.setSuccess(false);
			return reporter;
		}

		Sheet sheet = workbook.createSheet();
		workbook.setSheetName(0, "Lookup table");
		Row row = null;
		Cell cell = null;
		DataFormat df = workbook.createDataFormat();

		// create cell style of the header
		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		// use bold font
//		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		// set a medium border
//		headerStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
		headerStyle.setBorderBottom(BorderStyle.MEDIUM);
		// set cell data format to text
		headerStyle.setDataFormat(df.getFormat("@"));

		// create cell style
		CellStyle rowStyle = workbook.createCellStyle();
		// set thin border around the cell
		rowStyle.setBorderBottom(BorderStyle.THIN);
		rowStyle.setBorderLeft(BorderStyle.THIN);
		rowStyle.setBorderRight(BorderStyle.THIN);
		// set cell data format to text
		rowStyle.setDataFormat(df.getFormat("@"));
		// display multiple lines
		rowStyle.setWrapText(true);

		Map<Value, Value> table = getLookupTable().getTable().asMap();

		int rownum = 0;

		// write header
		row = sheet.createRow(rownum++);
		cell = row.createCell(0);
		cell.setCellValue(
				getParameter(LookupTableExportConstants.PARAM_SOURCE_COLUMN).as(String.class));
		cell.setCellStyle(headerStyle);

		cell = row.createCell(1);
		cell.setCellValue(
				getParameter(LookupTableExportConstants.PARAM_TARGET_COLUMN).as(String.class));
		cell.setCellStyle(headerStyle);

		for (Value key : table.keySet()) {
			// create a row
			row = sheet.createRow(rownum);

			cell = row.createCell(0);
			cell.setCellValue(key.as(String.class));
			cell.setCellStyle(rowStyle);

			Value entry = table.get(key);
			cell = row.createCell(1);
			cell.setCellValue(entry.as(String.class));
			cell.setCellStyle(rowStyle);
			rownum++;
		}

		// write file
		FileOutputStream out = new FileOutputStream(getTarget().getLocation().getPath());
		workbook.write(out);
		out.close();

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XLS Lookup Table";
	}

}
