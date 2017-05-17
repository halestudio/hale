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
import eu.esdihumboldt.hale.io.xls.XLSCellStyles;

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

		// create cell style of the header
		CellStyle headerStyle = XLSCellStyles.getHeaderStyle(workbook);

		// create cell style
		CellStyle cellStyle = XLSCellStyles.getNormalStyle(workbook, false);

		// create highlight style for type cells
		CellStyle highlightStyle = XLSCellStyles.getHighlightedStyle(workbook, false);

		// create disabled style
		CellStyle disabledStyle = XLSCellStyles.getNormalStyle(workbook, true);

		// create disabled highlight style
		CellStyle disabledTypeStyle = XLSCellStyles.getHighlightedStyle(workbook, true);

		List<Map<CellType, CellInformation>> mapping = getMappingList();

		// determine if cells are organized by type cell
		boolean byTypeCell = isByTypeCell();

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
				disabled = !transformationDisabled.isEmpty()
						&& !transformationDisabled
								.contains(TransformationMode.active.displayName());
			}

			// create a row
			row = sheet.createRow(rownum);

			CellStyle rowStyle = cellStyle;

			String targetProp = getCellValue(entry, CellType.TARGET_PROPERTIES);
			boolean isTypeCell = targetProp == null || targetProp.isEmpty();

			if (isTypeCell && byTypeCell) {
				// organized by type cells and this is a type cell

				if (disabled) {
					// disabled type cell
					rowStyle = disabledTypeStyle;
				}
				else {
					// normal type cell
					rowStyle = highlightStyle;
				}
			}
			else if (disabled) {
				// disabled property cell
				rowStyle = disabledStyle;
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
		return "XLS hale Alignment";
	}

	private int calculateWidth(int maxColWidth) {
		return (int) (maxColWidth * 35.536);
	}
}
