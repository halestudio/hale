/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.csv.reader.internal;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.springframework.core.convert.ConversionService;

import au.com.bytecode.opencsv.CSVReader;
import de.fhg.igd.osgi.util.OsgiUtils;
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

/**
 * Reads instances from a CSVfile
 * 
 * @author Kevin Mais
 */
public class CSVInstanceReader extends AbstractInstanceReader {

	/**
	 * the parameter specifying the reader setting
	 */
	public static final String PARAM_SKIP_FIRST_LINE = "skip";

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

		boolean skipFirst = Boolean.parseBoolean(getParameter(PARAM_SKIP_FIRST_LINE));
		instances = new DefaultInstanceCollection(new ArrayList<Instance>());
		int line = 0;

		CSVReader reader = CSVUtil.readFirst(this);

		// build instances
		TypeDefinition type = getSourceSchema().getType(
				QName.valueOf(getParameter(CSVConstants.PARAM_TYPENAME)));

		PropertyDefinition[] propAr = type.getChildren().toArray(
				new PropertyDefinition[type.getChildren().size()]);
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
				PropertyDefinition property = propAr[index];

				if (part != null && part.isEmpty()) {
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
