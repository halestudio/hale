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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.TypeFilter;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default instance export provider for table files (xls and csv)
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractTableInstanceWriter extends AbstractInstanceWriter {

	private List<String> sheetNames;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * Iterates over properties of the instance and creates a map of the given
	 * properties
	 * 
	 * @param instance the Instance to check
	 * @param headerRow the current header row of the table
	 * @param solveNestedProperties <code>true</code> if nested properties
	 *            should be solved, otherwise <code>false</code>
	 * @return a map of properties with string of localpart of the QName of the
	 *         property as key
	 */
	protected Map<String, Object> getPropertyMap(Instance instance, List<String> headerRow,
			boolean solveNestedProperties) {
		// properties of current instance
		Iterable<QName> allProperties = instance.getPropertyNames();

		// write properties to map; currently only the first property of nested
		// properties is selected
		Map<String, Object> row = new HashMap<String, Object>();
		for (QName qname : allProperties) {

			// get properties of the current instance
			Object[] properties = instance.getProperty(qname);
			if (properties != null && properties.length != 0) {
				String cellValue = "";
				// only the first property is evaluated
				Object property = properties[0];
				if (shouldBeDisplayed(property)) {
					cellValue = qname.getLocalPart();
				}

				// if property is an OInstance or OGroup, it's a nested property
				if (solveNestedProperties && property instanceof Group) {
					Group nextInstance = (Group) property;
					iterateBuild(nextInstance, qname, headerRow, row, cellValue);
//					Group inst = (Group) property;
//					// check if property has a value and add it
//					checkValue(inst, headerRow, row, cellValue);
//					// go through nested properties to get other properties
//					Iterator<QName> propertyIt = inst.getPropertyNames().iterator();
//					if (propertyIt.hasNext()) {
//						QName value = propertyIt.next();
//						Object nextProp = inst.getProperty(value)[0];
//						// check if current property should be displayed in map
//						if (shouldBeDisplayed(nextProp)) {
//							cellValue += ".";
//							cellValue += value.getLocalPart();
//						}
//
//						// iterate over all nested properties
//						while (nextProp instanceof Group) {
//							Group oinst = (Group) nextProp;
//							checkValue(oinst, headerRow, row, cellValue);
//
//							// get localparts of all nested properties
//							Iterator<QName> qnames = oinst.getPropertyNames().iterator();
//							if (qnames.hasNext()) {
//								value = qnames.next();
//								nextProp = oinst.getProperty(value)[0];
//								if (shouldBeDisplayed(nextProp)) {
//									cellValue += ".";
//									cellValue += value.getLocalPart();
//								}
//								else
//									continue;
//							}
//							else
//								break;
//						}
//						// add property with corresponding cellValue (localpart)
//						// to map
//						// no resolving of nested properties
//						addProperty(headerRow, row, nextProp, cellValue);
//					}
				}
				else {
					// add property with corresponding cellValue (localpart) to
					// map
					if (property instanceof Group && shouldBeDisplayed(property)) {
						checkValue((Group) property, headerRow, row, cellValue);
					}
					else {
						addProperty(headerRow, row, property, cellValue);
					}

				}
			}
		}
		return row;
	}

	/**
	 * 
	 * @param instance the actual instance
	 * @param qNameOfTheInstance the qname of the instance
	 * @param headerRow property names
	 * @param row actual map with data
	 * @param propertyPath the path, e.g. att1.att2.value, points at the actual
	 *            instance name/path
	 */
	private void iterateBuild(Group instance, QName qNameOfTheInstance, List<String> headerRow,
			Map<String, Object> row, String propertyPath) {

		if (shouldBeDisplayed(instance)) {
			// check if the actual instance has a value an add it
			checkValue(instance, headerRow, row, propertyPath);
		}
		// children properties of current instance
		Iterable<QName> children = instance.getPropertyNames();

		for (QName qname : children) {
			if (instance.getProperty(qname).length > 0) {
				// only the first instance
				Object child = instance.getProperty(qname)[0];
				if (child instanceof Group && shouldBeDisplayed(child)) {
					iterateBuild((Group) instance.getProperty(qname)[0], qname, headerRow, row,
							propertyPath + "." + qname.getLocalPart());
				}
				else if (child instanceof Group) {
					// the child is a choice or packege, etc.
					iterateBuild((Group) instance.getProperty(qname)[0], qname, headerRow, row,
							propertyPath);
				}
				else {
					// child is an attribute
					addProperty(headerRow, row, child, propertyPath + "." + qname.getLocalPart());
				}
			}

		}
	}

	// currently names of group property definitions should not be displayed, so
	// filter them
	private boolean shouldBeDisplayed(Object obj) {
		if (obj instanceof Group) {
			return !(((Group) obj).getDefinition() instanceof GroupPropertyDefinition);
		}
		return true;
	}

	/**
	 * @return the sheetNames
	 */
	public List<String> getSheetNames() {
		return sheetNames;
	}

	private void addProperty(List<String> headerRow, Map<String, Object> row, Object property,
			String propertyTypeName) {
		if (!headerRow.contains(propertyTypeName)) {
			headerRow.add(propertyTypeName);
		}
		row.put(propertyTypeName, property);
	}

	// check if value of current property isn't null and add it
	private void checkValue(Group group, List<String> headerRow, Map<String, Object> row,
			String propertyTypeName) {
		if (group instanceof Instance) {
			Object value = ((Instance) group).getValue();
			if (value != null) {
				addProperty(headerRow, row, value, propertyTypeName);
			}
		}
	}

	/**
	 * Call this to get an instance collection based on the selected Type
	 * 
	 * @param typeName the QName of the typeDefinition
	 * @return The instance collection for the given type, can be empty
	 *         collection
	 */
	protected InstanceCollection getInstanceCollection(QName typeName) {

		if (typeName == null) {
			return getInstances();
		}

		// get all instances of the selected Type
		InstanceCollection instances = null;
		TypeDefinition selectedType = getTargetSchema().getType(typeName);

		if (selectedType != null) {
			instances = getInstances().select(new TypeFilter(selectedType));
		}
		// if there is no selected type return random instance ???
		else
			instances = getInstances();

		return instances;
	}

}
