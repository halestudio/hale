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

package eu.esdihumboldt.hale.io.xls.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;
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
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.io.csv.PropertyType;
import eu.esdihumboldt.hale.io.csv.PropertyTypeExtension;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVInstanceReader;
import eu.esdihumboldt.hale.io.xls.AnalyseXLSSchemaTable;
import eu.esdihumboldt.hale.io.xls.XLSConstants;

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

	// only needed for correct error description
	private int line = 0;

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

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		boolean skipFirst = getParameter(CommonSchemaConstants.PARAM_SKIP_FIRST_LINE).as(
				Boolean.class);
		boolean solveNestedProperties = getParameter(XLSConstants.SOLVE_NESTED_PROPERTIES).as(
				Boolean.class);
		instances = new DefaultInstanceCollection(new ArrayList<Instance>());

		AnalyseXLSSchemaTable analyser;
		try {
			// analyse the excel sheet to get all information
			analyser = new AnalyseXLSSchemaTable(getSource().getLocation());
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
			createInstanceCollection(analyser.getHeader(), reporter, solveNestedProperties);
			line++;
		}

		// iterate over all rows to create the instances
		Iterator<List<String>> allRows = rows.iterator();
		while (allRows.hasNext()) {
			List<String> row = allRows.next();
			createInstanceCollection(row, reporter, solveNestedProperties);
			line++;
		}

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * create instances see
	 * {@link CSVInstanceReader#execute(ProgressIndicator, IOReporter)}
	 * 
	 * @param row the current row
	 * @param reporter the reporter of the writer
	 **/
	@SuppressWarnings("javadoc")
	private void createInstanceCollection(List<String> row, IOReporter reporter,
			boolean solveNestedProperties) {
		MutableInstance instance = new DefaultInstance(type, null);

		int propertyIndex = 0;
		for (int index = 0; index < row.size(); index++) {
			String part = row.get(index);
			if (part != null) {
				PropertyDefinition property = propAr[propertyIndex];

				if (solveNestedProperties) {
					while (part.startsWith(".")) {
						PropertyType propertyType;
						try {
							propertyType = PropertyTypeExtension.getInstance()
									.getFactory("java.lang.String").createExtensionObject();
							part = part.substring(1, part.length());
							DefaultPropertyDefinition prop;
							String currentProp;
							if (part.contains("\n")) {
								currentProp = part.substring(0, part.indexOf("\n"));
								prop = new DefaultPropertyDefinition(new QName(currentProp),
										property.getPropertyType(),
										propertyType.getTypeDefinition());
							}
							else {
								currentProp = part.substring(0, part.length()).replace("\n", "");
								prop = new DefaultPropertyDefinition(new QName(currentProp),
										property.getPropertyType(),
										propertyType.getTypeDefinition());
							}

							// set constraints on property
//							property.setConstraint(NillableFlag.DISABLED); // nillable
							prop.setConstraint(NillableFlag.ENABLED); // nillable
																		// FIXME
							// should be configurable per field (see also
							// CSVInstanceReader)
							prop.setConstraint(Cardinality.CC_EXACTLY_ONCE); // cardinality
							// set metadata for property
							prop.setLocation(getSource().getLocation());

							property = prop;
							part = part.replace(currentProp + "\n", "");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				if (part.isEmpty()) {
					// FIXME make this configurable
					part = null;
				}

				Object value = part;

				if (value != null) {
					Binding binding = property.getPropertyType().getConstraint(Binding.class);
					try {
						if (!binding.getBinding().equals(String.class)) {
							ConversionService conversionService = OsgiUtils
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
				}
				instance.addProperty(property.getName(), value);
				propertyIndex++;
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
