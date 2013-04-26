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
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.csv.writer.AbstractExportAlignment;
import eu.esdihumboldt.hale.io.csv.writer.CellInfo;

/**
 * Provider to write the alignment to a xls/xlsx file
 * 
 * @author Patrick Lieb
 */
public class XLSAlignmentWriter extends AbstractExportAlignment {

	@Override
	public boolean isCancelable() {
		return false;
	}

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
		workbook.setSheetName(0, "XLS HALE Alignment");
		Row row = null;
		Cell cell = null;
		DataFormat df = workbook.createDataFormat();

		// create cell style of the header
		CellStyle headerStyle = workbook.createCellStyle();
		Font f = workbook.createFont();
		// use bold font
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerStyle.setFont(f);
		// set a medium border
		headerStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
		// set cell data format to text
		headerStyle.setDataFormat(df.getFormat("@"));

		// create cell style
		CellStyle cellStyle = workbook.createCellStyle();
		// set thin border around the cell
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		// set cell data format to text
		cellStyle.setDataFormat(df.getFormat("@"));
		// display multiple lines
		cellStyle.setWrapText(true);

		List<Map<CellType, CellInfo>> mapping = getMappingList();

		int rownum = 0;

		// write header
		row = sheet.createRow(rownum++);
		for (int i = 0; i < MAPPING_HEADER.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(MAPPING_HEADER.get(i));
			cell.setCellStyle(headerStyle);
		}

		// write all mappings
		for (Map<CellType, CellInfo> entry : mapping) {
			// create a row
			row = sheet.createRow(rownum);

			cell = row.createCell(0);
			cell.setCellValue(getCellValue(entry, CellType.SOURCE_TYPE));
			cell.setCellStyle(cellStyle);

			cell = row.createCell(1);
			cell.setCellValue(getCellValue(entry, CellType.SOURCE_TYPE_CONDITIONS));
			cell.setCellStyle(cellStyle);

			cell = row.createCell(2);
			cell.setCellValue(getCellValue(entry, CellType.SOURCE_PROPERTIES));
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(getCellValue(entry, CellType.SOURCE_PROPERTY_CONDITIONS));
			cell.setCellStyle(cellStyle);

			cell = row.createCell(4);
			cell.setCellValue(getCellValue(entry, CellType.TARGET_TYPE));
			cell.setCellStyle(cellStyle);

			cell = row.createCell(5);
			cell.setCellValue(getCellValue(entry, CellType.TARGET_PROPERTIES));
			cell.setCellStyle(cellStyle);

			cell = row.createCell(6);
			cell.setCellValue(getCellValue(entry, CellType.RELATION_NAME));
			cell.setCellStyle(cellStyle);

			cell = row.createCell(7);
			cell.setCellValue(getCellValue(entry, CellType.CELL_EXPLANATION));
			cell.setCellStyle(cellStyle);

			cell = row.createCell(8);
			cell.setCellValue(getCellValue(entry, CellType.CELL_NOTES));
			cell.setCellStyle(cellStyle);

			rownum++;

		}
		// autosize all columns
		for (int i = 0; i < 9; i++) {
			sheet.autoSizeColumn(i);
		}

		// write file
		FileOutputStream out = new FileOutputStream(getTarget().getLocation().getPath());
		workbook.write(out);
		out.close();

		reporter.setSuccess(true);
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "XLS HALE Alignment";
	}
}
