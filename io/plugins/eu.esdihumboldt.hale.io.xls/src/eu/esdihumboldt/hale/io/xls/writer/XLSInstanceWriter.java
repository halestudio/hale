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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.xls.XLSCellStyles;

/**
 * Instance export provider for xls files
 * 
 * @author Patrick Lieb
 */
public class XLSInstanceWriter extends AbstractInstanceWriter {

	private FormulaEvaluator evaluator;
	private Workbook wb;

	private CellStyle headerStyle;
	private CellStyle cellStyle;

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

		// only needed for copy cell operation
		evaluator = wb.getCreationHelper().createFormulaEvaluator();

		// get all instances
		InstanceCollection instances = getInstances();
		Iterator<Instance> instanceIterator = instances.iterator();
		Instance instance = instanceIterator.next();

		// all instances with equal type definitions are stored in an extra
		// sheet
		TypeDefinition definition = instance.getDefinition();

		// the sheet name is defined by the type definition
		Sheet sh = wb.createSheet(definition.getDisplayName());

		// store instances with other type definitions in extra sheet
		List<Instance> remaining = write(instanceIterator, instance, definition, sh);
		while (!remaining.isEmpty()) {
			// this type defines the type of all instances in this sheet
			TypeDefinition currentType = remaining.get(0).getDefinition();
			Sheet newSheet = wb.createSheet(currentType.getDisplayName());
			instanceIterator = remaining.iterator();
			instance = instanceIterator.next();
			remaining = write(instanceIterator, instance, currentType, newSheet);
		}

		// write file
		FileOutputStream out = new FileOutputStream(getTarget().getLocation().getPath());
		wb.write(out);
		out.close();

		reporter.setSuccess(true);
		return reporter;
	}

	// the first instance has to be handled independently since it determines
	// the type definition for the current sheet
	private List<Instance> write(Iterator<Instance> instances, Instance firstInstance,
			TypeDefinition definition, Sheet sheet) {

		// position of current row
		int rowNum = 0;
		Row row = sheet.createRow(rowNum++);

		// the instances that are currently processed
		List<Instance> currentInstances = new ArrayList<Instance>();
		// the instances that will be processed in next sheet
		List<Instance> remaining = new ArrayList<Instance>();

		// position of current cell in current row
		int cellNum = 0;
		currentInstances.add(firstInstance);

		// write header of the sheet; contains of all local parts of the
		// instance properties
		Set<QName> allProperties = new HashSet<QName>();
		writeHeader(allProperties, firstInstance, row, cellNum);
		while (instances.hasNext()) {
			Instance instance = instances.next();
			if (instance.getDefinition().equals(definition)) {
				currentInstances.add(instance);
				writeHeader(allProperties, instance, row, cellNum);
			}
			else
				// other instances have to handled separately afterwards
				remaining.add(instance);
		}
// XXX use children instead of all instances?
//		for (Iterator<? extends ChildDefinition<?>> iter = definition.getChildren().iterator(); iter
//				.hasNext();) {
//			QName qname = iter.next().getName();
//			if (allProperties.add(qname)) {
//				Cell cell = row.createCell(cellNum++);
//				cell.setCellValue(qname.getLocalPart());
//				cell.setCellStyle(headerStyle);
//			}
//		}

		cellNum = 0;

		// write properties; currently only the first property of nested
		// properties is selected
		for (int i = 0; i < currentInstances.size(); i++) {
			Instance instance = currentInstances.get(i);
			row = sheet.createRow(rowNum++);
			cellNum = 0;
			for (QName qname : allProperties) {
				Cell cell = row.createCell(cellNum); //
				cell.setCellStyle(cellStyle);

				// get property of the current instance
				Object[] properties = instance.getProperty(qname);
				if (properties != null && properties.length != 0) {
					String cellValue = "";
					// only the first property is evaluated
					Object property = properties[0];
					// if property is an OInstance, it's a nested property
					if (property instanceof OInstance) {
						OInstance inst = (OInstance) property;
						Iterator<QName> propertyIt = inst.getPropertyNames().iterator();
						if (propertyIt.hasNext()) {
							QName value = propertyIt.next();
//							cell.setCellValue(value.getLocalPart());
//							cell.setCellStyle(headerStyle);
							cellValue += ".";
							cellValue += value.getLocalPart();

							Object nextProp = inst.getProperty(value)[0];
//							Row rowAfter = sheet.createRow(rowNum++);
//							Cell cellBelow = rowAfter.createCell(cellNum);

							// go through all nested properties
							while (nextProp instanceof OInstance) {
								OInstance oinst = (OInstance) nextProp;
								value = oinst.getPropertyNames().iterator().next();
								cellValue += "\n.";
								cellValue += value.getLocalPart();

//								cellBelow.setCellValue(value.getLocalPart());
//								cellBelow.setCellStyle(headerStyle);
//
//								rowAfter = sheet.createRow(internalRowNum++);
//								cellBelow = rowAfter.createCell(cellNum); //
								nextProp = oinst.getProperty(value)[0];
							}
							cellValue += "\n" + nextProp.toString();
							cell.setCellValue(cellValue);
//							cell.setCellStyle(cellStyle);

//							while (propertyIt.hasNext()) {
//								insertNewColumn(sheet, cellNum);
//								row = sheet.createRow(rowNum++);
//								cell = row.createCell(cellNum++);
//								cell.setCellValue(propertyIt.next().getLocalPart());
//								cell.setCellStyle(headerStyle);
//								System.out.println("here");
//								cellBelow = rowAfter.createCell(cellNum++);
//								cellBelow.setCellValue(inst.getProperty(value).toString());
//								cellBelow.setCellStyle(cellStyle);
//							}
						}
					}
					else {
						cell.setCellValue(property.toString());
					}
				}
				cellNum++;
			}
		}

		resizeSheet(sheet);

		return remaining;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XLS file";
	}

	// insert a new column before the column with index 'beforeColumn'
	// moves all columns including 'beforeColumn' to the right
	private void insertNewColumn(Sheet sheet, int beforeColumn) {
		int rowNum = sheet.getPhysicalNumberOfRows();
		for (int currentRow = 0; currentRow < rowNum; currentRow++) {
			Row row = sheet.getRow(currentRow);
			int columnNum = row.getPhysicalNumberOfCells();
			if (columnNum == 0)
				continue;
			for (int currentColumn = columnNum - 1; currentColumn >= beforeColumn; currentColumn--) {
				Cell cell = row.getCell(currentColumn);
				if (cell != null) {
					copyCell(row, currentColumn + 1, cell);
					row.removeCell(cell);
				}
			}
		}
	}

	// XXX: what about other information?
	private Cell copyCell(Row row, int column, Cell oldCell) {
		CellValue value = evaluator.evaluate(oldCell);
		String cellValue;

		if (value != null)
			switch (value.getCellType()) {
			case Cell.CELL_TYPE_BLANK:
				cellValue = "";
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				cellValue = String.valueOf(value.getBooleanValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				// number formatting
				double number = value.getNumberValue();
				if (number == Math.floor(number)) {
					// it's an integer
					cellValue = String.valueOf((int) number);
				}
				cellValue = String.valueOf(value.getNumberValue());
				break;
			case Cell.CELL_TYPE_STRING:
				cellValue = value.getStringValue();
				break;
			case Cell.CELL_TYPE_FORMULA:
				// will not happen as we used the evaluator
			case Cell.CELL_TYPE_ERROR:
				// fall through
			default:
				cellValue = "";
			}
		else
			cellValue = "";
		Cell newCell = row.createCell(column);
		newCell.setCellStyle(oldCell.getCellStyle());
		newCell.setCellValue(cellValue);

		return newCell;
	}

	// only based on first row
	private void resizeSheet(Sheet sheet) {
		for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}
	}

	// write header of the sheet; properties contains the qnames of all
	// properties
	private void writeHeader(Set<QName> properties, Instance instance, Row row, int cellIndex) {
		for (Iterator<QName> iter = instance.getPropertyNames().iterator(); iter.hasNext();) {
			QName qname = iter.next();
			if (properties.add(qname)) {
				Cell cell = row.createCell(cellIndex++);
				cell.setCellValue(qname.getLocalPart());
				cell.setCellStyle(headerStyle);
			}
		}
	}
}
