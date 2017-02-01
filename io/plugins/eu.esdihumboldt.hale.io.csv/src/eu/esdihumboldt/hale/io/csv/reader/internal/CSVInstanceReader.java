/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.csv.reader.internal;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.springframework.core.convert.ConversionService;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.io.csv.CSVFileIO;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;

/**
 * Reads instances from a CSVfile
 * 
 * @author Kevin Mais
 */
public class CSVInstanceReader extends AbstractInstanceReader {

	private DefaultInstanceCollection instances;

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		boolean skipFirst = getParameter(CommonSchemaConstants.PARAM_SKIP_FIRST_LINE)
				.as(Boolean.class, false);
		instances = new DefaultInstanceCollection(new ArrayList<Instance>());
		int line = 0;

		CSVReader reader = CSVUtil.readFirst(this);

		// Decimal Character
		char dec = CSVUtil.getDecimal(this);

		// build instances
		TypeDefinition type = getSourceSchema().getType(
				QName.valueOf(getParameter(CommonSchemaConstants.PARAM_TYPENAME).as(String.class)));

		PropertyDefinition[] propAr = type.getChildren()
				.toArray(new PropertyDefinition[type.getChildren().size()]);
		String[] nextLine;

		if (skipFirst) {
			// nextLine[] is an array of values in the first line (we don't need
			// them)
			nextLine = reader.readNext();
			line++;
		}

		while ((nextLine = reader.readNext()) != null) {
			MutableInstance instance = new DefaultInstance(type, null);
			line++;
			// nextLine[] is now an array of all values in the line (starting in
			// second line if skipFirst == true)
			int index = 0;
			for (String part : nextLine) {
				if (index >= propAr.length) {
					// break if line has more columns than the specified type
					reporter.warn(new IOMessageImpl(
							"More data columns encountered than defined in the schema ", null, line,
							-1));
					break;
				}
				PropertyDefinition property = propAr[index];

				if (part != null && part.isEmpty()) {
					// FIXME make this configurable
					part = null;
				}

				Object value = part;

				if (part != null) {
					Binding binding = property.getPropertyType().getConstraint(Binding.class);
					try {
						if (!binding.getBinding().equals(String.class)) {

							if (property.getPropertyType().getConstraint(Binding.class).getBinding()
									.equals(Float.class) && dec != '.')
								part = part.replace(dec, '.');

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
				}

				instance.addProperty(property.getName(), value);
				index++;
			}

			instances.add(instance);
		}

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return CSVFileIO.DEFAULT_TYPE_NAME;
	}

	/**
	 * @see InstanceReader#getInstances()
	 */
	@Override
	public InstanceCollection getInstances() {
		return instances;
	}
}
