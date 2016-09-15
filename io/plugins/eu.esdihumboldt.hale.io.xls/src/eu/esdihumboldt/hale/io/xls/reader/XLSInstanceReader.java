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

package eu.esdihumboldt.hale.io.xls.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.springframework.core.convert.ConversionService;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVInstanceReader;
import eu.esdihumboldt.hale.io.xls.AnalyseXLSSchemaTable;

/**
 * Read source data of xls instance files (based on the
 * {@link CSVInstanceReader}
 * 
 * @author Patrick Lieb
 */
public class XLSInstanceReader extends AbstractInstanceReader {

	private DefaultInstanceCollection instances;
	private PropertyDefinition[] propAr;
	private TypeDefinition type;
	private AnalyseXLSSchemaTable analyser;

	// only needed for correct error description
	private int line = 0;

	// first sheet as default
	private int sheetNum = 0;

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.InstanceReader#getInstances()
	 */
	@Override
	public InstanceCollection getInstances() {
		return instances;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		boolean skipFirst = getParameter(CommonSchemaConstants.PARAM_SKIP_FIRST_LINE).as(
				Boolean.class);

		// first sheet as default
		sheetNum = getParameter(InstanceTableIOConstants.SHEET_INDEX).as(int.class, 0);

		instances = new DefaultInstanceCollection(new ArrayList<Instance>());

		try {
			// analyze the excel sheet to get all information
			analyser = new AnalyseXLSSchemaTable(getSource().getLocation(), sheetNum);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Reading the excel sheet has failed", e));
			return reporter;
		}

		// get type definition of the schema
		type = getSourceSchema().getType(
				QName.valueOf(getParameter(CommonSchemaConstants.PARAM_TYPENAME).as(String.class)));

		// get property definition
		propAr = type.getChildren().toArray(new PropertyDefinition[type.getChildren().size()]);
		Collection<List<String>> rows = analyser.getRows();

		// skip if first row is a header
		if (!skipFirst) {
			// otherwise first line is also an instance
			createInstanceCollection(analyser.getHeader(), reporter);
			line++;
		}

		// iterate over all rows to create the instances
		Iterator<List<String>> allRows = rows.iterator();
		while (allRows.hasNext()) {
			List<String> row = allRows.next();
			createInstanceCollection(row, reporter);
			line++;
		}

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * create instances, see
	 * {@link CSVInstanceReader#execute(ProgressIndicator, IOReporter)}
	 * 
	 * @param row the current row
	 * @param reporter the reporter of the writer
	 * @param solveNestedProperties true, if schema should not be flat <b>(not
	 *            implemented yet)</b>
	 **/
	@SuppressWarnings("javadoc")
	private void createInstanceCollection(List<String> row, IOReporter reporter) {
		MutableInstance instance = new DefaultInstance(type, null);

//		int propertyIndex = 0;
		for (int index = 0; index < propAr.length; index++) {

			String part = null;
			if (index < row.size())
				part = row.get(index);

			if (part != null) {
				PropertyDefinition property = propAr[index];

				if (part.isEmpty()) {
					// FIXME make this configurable
					part = null;
				}

				Object value = part;
				if (value != null) {
					Binding binding = property.getPropertyType().getConstraint(Binding.class);
					try {
						if (!binding.getBinding().equals(String.class)) {
							ConversionService conversionService = HalePlatform
									.getService(ConversionService.class);
							if (conversionService.canConvert(String.class, binding.getBinding())) {
								value = conversionService.convert(part, binding.getBinding());
							}
							else {
								throw new IllegalStateException("Conversion not possible!");
							}
						}
					} catch (Exception e) {
						reporter.error(new IOMessageImpl("Cannot convert property value to {0}", e,
								line, -1, binding.getBinding().getSimpleName()));
					}
					instance.addProperty(property.getName(), value);
				}
//				propertyIndex++;
			}
		}
		instances.add(instance);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XLS Instance Reader";
	}

}
