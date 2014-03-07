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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.orient.OGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.xls.XLSCellStyles;
import eu.esdihumboldt.hale.io.xls.XLSConstants;

/**
 * Instance export provider for xls files
 * 
 * @author Patrick Lieb
 */
public class XLSInstanceWriter extends AbstractInstanceWriter {

	private Workbook wb;

	private CellStyle headerStyle;
	private CellStyle cellStyle;

	private boolean solveNestedProperties;

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

		solveNestedProperties = getParameter(XLSConstants.SOLVE_NESTED_PROPERTIES)
				.as(Boolean.class);

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
	// the type definition of the current sheet
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
		currentInstances.add(firstInstance);

		Collection<? extends PropertyDefinition> allDefProp = DefinitionUtil
				.getAllProperties(definition);

		Set<QName> allProperties = new HashSet<QName>();
		for (PropertyDefinition currentDef : allDefProp) {
			allProperties.add(currentDef.getName());
		}

		// write header of the sheet; contains of all local parts of the
		// instance properties
		writeHeader(allProperties, row);
		while (instances.hasNext()) {
			Instance instance = instances.next();
			if (instance.getDefinition().equals(definition)) {
				currentInstances.add(instance);
				writeHeader(allProperties, row);
			}
			else
				// other instances have to handled separately afterwards
				remaining.add(instance);
		}

		// write properties; currently only the first property of nested
		// properties is selected
		for (int i = 0; i < currentInstances.size(); i++) {
			Instance instance = currentInstances.get(i);
			row = sheet.createRow(rowNum++);
			int cellNum = 0;
			for (QName qname : allProperties) {
				// initialize cell
				Cell cell = row.createCell(cellNum++); //
				cell.setCellStyle(cellStyle);

				// get property of the current instance
				Object[] properties = instance.getProperty(qname);
				if (properties != null && properties.length != 0) {
					String cellValue = "";
					// only the first property is evaluated
					Object property = properties[0];
					// if property is an OInstance, it's a nested property
					if (solveNestedProperties && property instanceof OGroup) {
						OGroup inst = (OGroup) property;
						Iterator<QName> propertyIt = inst.getPropertyNames().iterator();
						if (propertyIt.hasNext()) {
							QName value = propertyIt.next();
							cellValue += ".";
							cellValue += value.getLocalPart();

							Object nextProp = inst.getProperty(value)[0];

							// go through all nested properties
							while (nextProp instanceof OGroup) {
								OGroup oinst = (OGroup) nextProp;
								Iterator<QName> qnames = oinst.getPropertyNames().iterator();
								if (qnames.hasNext()) {
									value = qnames.next();
									cellValue += "\n.";
									cellValue += value.getLocalPart();
									nextProp = oinst.getProperty(value)[0];
								}
								else
									break;
							}
							cellValue += "\n." + nextProp.toString();
							cell.setCellValue(cellValue);
						}
					}
					else {
						setValueOfCell(cell, property);
					}
				}
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
			if (value != null)
				cell.setCellValue(value.toString());
			else
				cell.setCellValue("");
		}

	}

	// only based on first row
	private void resizeSheet(Sheet sheet) {
		for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}
	}

	// write header of the sheet; properties contains the qnames of all
	// properties
	private void writeHeader(Set<QName> properties, Row row) {
		int cellIndex = 0;
		for (Iterator<QName> iter = properties.iterator(); iter.hasNext();) {
			QName qname = iter.next();
			Cell cell = row.createCell(cellIndex++);
			cell.setCellValue(qname.getLocalPart());
			cell.setCellStyle(headerStyle);
		}
	}
}
