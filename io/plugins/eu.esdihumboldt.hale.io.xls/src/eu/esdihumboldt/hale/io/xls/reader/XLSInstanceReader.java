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
import eu.esdihumboldt.hale.common.schema.SchemaConstants;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
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
		boolean skipFirst = getParameter(PARAM_SKIP_FIRST_LINE).as(Boolean.class);
		instances = new DefaultInstanceCollection(new ArrayList<Instance>());
		int line = 0;

		AnalyseXLSSchemaTable analyser;
		try {
			analyser = new AnalyseXLSSchemaTable(getSource().getLocation());
		} catch (Exception e1) {
			// XXX set message
			reporter.setSuccess(false);
			return reporter;
		}

		// build instances
		TypeDefinition type = getSourceSchema().getType(
				QName.valueOf(getParameter(SchemaConstants.PARAM_TYPENAME).as(String.class)));

		PropertyDefinition[] propAr = type.getChildren().toArray(
				new PropertyDefinition[type.getChildren().size()]);
		Collection<List<String>> rows = analyser.getRows();

		Iterator<List<String>> allRows = rows.iterator();
		List<String> rowList;

		if (skipFirst) {
			allRows.next();
			line++;
		}

		while (allRows.hasNext()) {
			MutableInstance instance = new DefaultInstance(type, null);

			rowList = allRows.next();

			int index = 0;
			for (String part : rowList) {
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
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XLS Instance Reader";
	}

}
