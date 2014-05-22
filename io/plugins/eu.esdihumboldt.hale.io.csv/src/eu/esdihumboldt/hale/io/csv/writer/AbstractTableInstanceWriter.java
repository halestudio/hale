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

package eu.esdihumboldt.hale.io.csv.writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.orient.OGroup;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;

/**
 * Default instance export provider for table files (xls and csv)
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractTableInstanceWriter extends AbstractInstanceWriter {

	private List<List<List<Object>>> table;
	private boolean solveNestedProperties;
	private List<String> sheetNames;

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

		table = new ArrayList<List<List<Object>>>();
		sheetNames = new ArrayList<String>();

		solveNestedProperties = getParameter(InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES).as(
				Boolean.class);

		// get all instances
		InstanceCollection instances = getInstances();
		Iterator<Instance> instanceIterator = instances.iterator();
		Instance instance = instanceIterator.next();

		// all instances with equal type definitions are stored in an extra
		// sheet
		TypeDefinition definition = instance.getDefinition();
		sheetNames.add(definition.getDisplayName());

		// the sheet name is defined by the type definition
		List<List<Object>> currentSheet = new ArrayList<List<Object>>();

		List<Object> headerRow = new ArrayList<Object>();

		// store instances with other type definitions in extra sheet
		List<Instance> remaining = write(instanceIterator, instance, definition, currentSheet,
				headerRow);
		while (!remaining.isEmpty()) {
			// this type defines the type of all instances in this sheet
			TypeDefinition currentType = remaining.get(0).getDefinition();
			sheetNames.add(currentType.getDisplayName());
			List<List<Object>> newSheet = new ArrayList<List<Object>>();
			instanceIterator = remaining.iterator();
			instance = instanceIterator.next();
			headerRow = new ArrayList<Object>();
			remaining = write(instanceIterator, instance, currentType, newSheet, headerRow);
		}

		return reporter;
	}

	// the first instance has to be handled independently since it determines
	// the type definition of the current sheet
	private List<Instance> write(Iterator<Instance> instances, Instance firstInstance,
			TypeDefinition definition, List<List<Object>> sheet, List<Object> headerRow) {
		// position of current row
		// the instances that are currently processed
		List<Instance> currentInstances = new ArrayList<Instance>();
		// the instances that will be processed in next sheet
		List<Instance> remaining = new ArrayList<Instance>();

		// position of current cell in current row
		currentInstances.add(firstInstance);

		while (instances.hasNext()) {
			Instance instance = instances.next();
			if (instance.getDefinition().equals(definition)) {
				currentInstances.add(instance);
			}
			else
				remaining.add(instance);
		}
		// properties of current instance
		Iterable<QName> allProperties = firstInstance.getPropertyNames();

		// content of the cells of the header row
		List<String> headerCells = new ArrayList<String>();

		// write properties; currently only the first property of nested
		// properties is selected
		for (int i = 0; i < currentInstances.size(); i++) {
			Instance instance = currentInstances.get(i);
			List<Object> row = new ArrayList<Object>();
			for (QName qname : allProperties) {

				// get property of the current instance
				Object[] properties = instance.getProperty(qname);
				if (properties != null && properties.length != 0) {
					String cellValue = qname.getLocalPart();
					// only the first property is evaluated
					Object property = properties[0];
					// if property is an OInstance, it's a nested property
					if (solveNestedProperties && property instanceof OGroup) {
						OGroup inst = (OGroup) property;
						Iterator<QName> propertyIt = inst.getPropertyNames().iterator();
						if (propertyIt.hasNext()) {
							QName value = propertyIt.next();
							Object nextProp = inst.getProperty(value)[0];
							if (shouldBeDisplayed(nextProp)) {
								cellValue += ".";
								cellValue += value.getLocalPart();
							}

							// iterate over all nested properties
							while (nextProp instanceof OGroup) {
								OGroup oinst = (OGroup) nextProp;

								Iterator<QName> qnames = oinst.getPropertyNames().iterator();
								if (qnames.hasNext()) {
									value = qnames.next();
									nextProp = oinst.getProperty(value)[0];
									if (shouldBeDisplayed(nextProp)) {
										cellValue += ".";
										cellValue += value.getLocalPart();
									}
									else
										continue;
								}
								else
									break;
							}
							if (!headerCells.contains(cellValue)) {
								headerRow.add(cellValue);

								row.add(nextProp);

								headerCells.add(cellValue);
							}
							else {
								int cellIndex = headerCells.indexOf(cellValue);
								row.add(cellIndex, nextProp);
							}
						}
					}
					else {
						if (!headerCells.contains(cellValue)) {
							headerRow.add(qname.getLocalPart());

							row.add(property);

							headerCells.add(cellValue);
						}
						else {
							int cellIndex = headerCells.indexOf(cellValue);
							row.add(cellIndex, property);
						}
					}
				}
			}
			sheet.add(row);
		}

		sheet.add(0, headerRow);
		table.add(sheet);

		return remaining;
	}

	private boolean shouldBeDisplayed(Object obj) {
		if (obj instanceof OGroup) {
			return !(((OGroup) obj).getDefinition() instanceof GroupPropertyDefinition);
		}
		return true;
	}

	/**
	 * @return the table
	 */
	public List<List<List<Object>>> getTable() {
		return table;
	}

	/**
	 * @return the sheetNames
	 */
	public List<String> getSheetNames() {
		return sheetNames;
	}

}
