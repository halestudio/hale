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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

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
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
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

	private List<String> headerRowStrings;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		boolean solveNestedProperties = getParameter(
				InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES).as(Boolean.class, false);

		// get the parameter to get the type definition
		String exportType = getParameter(InstanceTableIOConstants.EXPORT_TYPE).as(String.class);
		QName selectedTypeName = null;

		if (exportType != null && !exportType.equals("") && !exportType.equals(" ")) {
			selectedTypeName = QName.valueOf(exportType);
		}

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

		// get all instances of the selected Type
		InstanceCollection instances = getInstanceCollection(selectedTypeName);
		Iterator<Instance> instanceIterator = instances.iterator();
		Instance instance = null;
		try {
			instance = instanceIterator.next();
		} catch (NoSuchElementException e) {
			reporter.error(new IOMessageImpl("There are no instances for the selected type.", e));
			return reporter;
		}

		List<Instance> remainingInstances = new ArrayList<Instance>();

		headerRowStrings = new ArrayList<String>();

		boolean useSchema = getParameter(InstanceTableIOConstants.USE_SCHEMA).as(Boolean.class,
				false);

		// all instances with equal type definitions are stored in an extra
		// sheet
		TypeDefinition definition = instance.getDefinition();

		Sheet sheet = wb.createSheet(definition.getDisplayName());
		Row headerRow = sheet.createRow(0);
		int rowNum = 1;
		Row row = sheet.createRow(rowNum++);
		writeRow(row,
				super.getPropertyMap(instance, headerRowStrings, solveNestedProperties, useSchema));

		while (instanceIterator.hasNext()) {
			Instance nextInst = instanceIterator.next();
			if (nextInst.getDefinition().equals(definition)) {
				row = sheet.createRow(rowNum++);
				writeRow(row, super.getPropertyMap(nextInst, headerRowStrings,
						solveNestedProperties, useSchema));
			}
			else
				remainingInstances.add(nextInst);
		}
		writeHeaderRow(headerRow, headerRowStrings);
		setCellStyle(sheet, headerRowStrings.size());
		resizeSheet(sheet);

		// write file
		FileOutputStream out = new FileOutputStream(getTarget().getLocation().getPath());
		wb.write(out);
		out.close();

		reporter.setSuccess(true);
		return reporter;
	}

	@Override
	public boolean isPassthrough() {
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XLS file";
	}

	private void writeHeaderRow(Row row, List<String> cells) {
		for (int k = 0; k < cells.size(); k++) {
			Cell cell = row.createCell(k);
			cell.setCellStyle(headerStyle);
			setValueOfCell(cell, cells.get(k));
		}
	}

	private void writeRow(Row row, Map<String, Object> tableRow) {
		for (int k = 0; k < headerRowStrings.size(); k++) {
			Cell cell = row.createCell(k);
//			cell.setCellStyle(cellStyle);
			setValueOfCell(cell, tableRow.get(headerRowStrings.get(k)));
		}
	}

	// only based on first row
	private void resizeSheet(Sheet sheet) {
		for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void setValueOfCell(Cell cell, Object value) {
		if (value == null) {
			cell.setCellValue("");
		}
		else {
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
				if (value instanceof Instance) {
					Object instValue = ((Instance) value).getValue();
					if (instValue != null) {
						cell.setCellValue(instValue.toString());
						return;
					}
				}
				cell.setCellValue(value.toString());
			}
		}

	}

	private void setCellStyle(Sheet sheet, int rowSize) {
		int rowNum = sheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rowNum; i++) {
			Row row = sheet.getRow(i);
			for (int k = 0; k < rowSize; k++) {
				Cell cell = row.getCell(k);
				if (cell != null)
					cell.setCellStyle(cellStyle);
				else
					row.createCell(k).setCellStyle(cellStyle);
			}
		}

	}
}
