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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.csv.writer.AbstractAlignmentMappingExport;
import eu.esdihumboldt.hale.io.csv.writer.CellInformation;
import eu.esdihumboldt.hale.io.csv.writer.CellType;

/**
 * Provider to write the alignment to a xls/xlsx file
 * 
 * @author Patrick Lieb
 */
public class XLSAlignmentMappingWriter extends AbstractAlignmentMappingExport {

	// in pixels
	private final int maxWidth = 500;

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		super.execute(progress, reporter);

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
		workbook.setSheetName(0, "Mapping table");
		Row row = null;
		Cell cell = null;
		DataFormat df = workbook.createDataFormat();

		// create cell style of the header
		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		// use bold font
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerStyle.setFont(headerFont);
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

		// create highlight style
		CellStyle highlightStyle = workbook.createCellStyle();
		// set thin border around the cell
		highlightStyle.setBorderBottom(CellStyle.BORDER_THIN);
		highlightStyle.setBorderLeft(CellStyle.BORDER_THIN);
		highlightStyle.setBorderRight(CellStyle.BORDER_THIN);
		// set cell data format to text
		highlightStyle.setDataFormat(df.getFormat("@"));
		// display multiple lines
		highlightStyle.setWrapText(true);
		highlightStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		highlightStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		// create disabled style
		CellStyle disabledStyle = workbook.createCellStyle();
		// set thin border around the cell
		disabledStyle.setBorderBottom(CellStyle.BORDER_THIN);
		disabledStyle.setBorderLeft(CellStyle.BORDER_THIN);
		disabledStyle.setBorderRight(CellStyle.BORDER_THIN);
		// set cell data format to text
		disabledStyle.setDataFormat(df.getFormat("@"));
		// display multiple lines
		disabledStyle.setWrapText(true);
		Font disabledFont = workbook.createFont();
		// use bold font
		disabledFont.setStrikeout(true);
		disabledFont.setColor(IndexedColors.GREY_40_PERCENT.getIndex());
		disabledStyle.setFont(disabledFont);

		List<Map<CellType, CellInformation>> mapping = getMappingList();

		// determine if cells are organized by type cell
		String mode = getParameter(PARAMETER_MODE).as(String.class);
		boolean byTypeCell = mode.equals(MODE_BY_TYPE_CELLS);

		int rownum = 0;

		// write header
		row = sheet.createRow(rownum++);
		for (int i = 0; i < getMappingHeader().size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(getMappingHeader().get(i));
			cell.setCellStyle(headerStyle);
		}

		// write all mappings
		for (Map<CellType, CellInformation> entry : mapping) {

			boolean disabled = false;
			if (getParameter(TRANSFORMATION_AND_DISABLED_FOR).as(Boolean.class)) {
				List<String> transformationDisabled = entry.get(
						CellType.TRANSFORMATION_AND_DISABLED).getText();
				disabled = !transformationDisabled.contains("")
						&& !transformationDisabled
								.contains(TransformationMode.active.displayName());
			}

			// create a row
			row = sheet.createRow(rownum);

			CellStyle rowStyle = cellStyle;
			// XXX
			if (disabled)
				rowStyle = disabledStyle;
			else if (byTypeCell) {
				// check if the current cell is a type cell
				String targetProp = getCellValue(entry, CellType.TARGET_PROPERTIES);
				if (targetProp == null || targetProp.isEmpty()) {
					rowStyle = highlightStyle;
				}
			}

			List<CellType> celltypes = getCellTypes();
			for (int i = 0; i < celltypes.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(getCellValue(entry, celltypes.get(i)));
				cell.setCellStyle(rowStyle);
			}
			rownum++;
		}

		// could be integrated in configuration page
//		int maxColWidth = calculateWidth(getParameter(MAX_COLUMN_WIDTH).as(Integer.class));
		int maxColWidth = calculateWidth(maxWidth);
		// autosize all columns
		for (int i = 0; i < getMappingHeader().size(); i++) {
			sheet.autoSizeColumn(i);
			if (sheet.getColumnWidth(i) > maxColWidth)
				sheet.setColumnWidth(i, maxColWidth);
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

	private int calculateWidth(int maxColWidth) {
		return (int) (maxColWidth * 35.536);
	}
}
