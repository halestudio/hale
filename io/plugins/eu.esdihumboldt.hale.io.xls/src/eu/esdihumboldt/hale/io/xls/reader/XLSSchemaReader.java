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
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.PropertyType;
import eu.esdihumboldt.hale.io.csv.PropertyTypeExtension;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.AbstractTableSchemaReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConfiguration;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVUtil;
import eu.esdihumboldt.hale.io.xls.AnalyseXLSSchemaTable;

/**
 * Schema reader for xls/xlsx files
 * 
 * @author Patrick Lieb
 */
public class XLSSchemaReader extends AbstractTableSchemaReader {

	private List<String> header = new ArrayList<String>();

	/**
	 * XXX does 0 reference the first sheet?
	 */
	private int sheetNum = 0;

	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();
		if (getParameter(CommonSchemaConstants.PARAM_TYPENAME).isEmpty()) {
			fail("No Typename specified");
		}
	}

	@Override
	protected Schema loadFromSource(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		sheetNum = getParameter(InstanceTableIOConstants.SHEET_INDEX).as(int.class, 0);
		String dateTime = getParameter(ReaderSettings.PARAMETER_DATE_FORMAT).as(String.class);

		progress.begin("Load XLS/XLSX schema", ProgressIndicator.UNKNOWN);

		String namespace = "http://www.esdi-humboldt.eu/hale/xls";
		DefaultSchema schema = new DefaultSchema(namespace, getSource().getLocation());
		AnalyseXLSSchemaTable analyser;

		try {
			analyser = new AnalyseXLSSchemaTable(getSource(),
					ReaderSettings.isXlsxContentType(getContentType()), sheetNum, 0, dateTime);
			header = analyser.getHeader();

			// create type definition
			String typename = getParameter(CommonSchemaConstants.PARAM_TYPENAME).as(String.class);
			if (typename == null || typename.isEmpty()) {
				reporter.setSuccess(false);
				reporter.error(new IOMessageImpl("No Typename was set", null));
				return null;
			}
			DefaultTypeDefinition type = new DefaultTypeDefinition(new QName(typename));

			// constraints on main type
			type.setConstraint(MappingRelevantFlag.ENABLED);
			type.setConstraint(MappableFlag.ENABLED);
			type.setConstraint(HasValueFlag.DISABLED);
			type.setConstraint(AbstractFlag.DISABLED);

			// set metadata for main type
			type.setLocation(getSource().getLocation());

			StringBuffer defaultPropertyTypeBuffer = new StringBuffer();
			String[] comboSelections;
			if (getParameter(PARAM_PROPERTYTYPE).isEmpty()) {
				for (int i = 0; i < header.size(); i++) {
					defaultPropertyTypeBuffer.append("java.lang.String");
					defaultPropertyTypeBuffer.append(",");
				}
				defaultPropertyTypeBuffer.deleteCharAt(defaultPropertyTypeBuffer.lastIndexOf(","));
				String combs = defaultPropertyTypeBuffer.toString();
				comboSelections = combs.split(",");
			}
			else {
				comboSelections = getParameter(PARAM_PROPERTYTYPE).as(String.class).split(",");
			}
			String[] properties;
			if (getParameter(PARAM_PROPERTY).isEmpty()) {
				properties = header.toArray(new String[0]);
			}
			else {
				properties = getParameter(PARAM_PROPERTY).as(String.class).split(",");
			}
			// fails if there are less or more property names or property types
			// than the entries in the first line
			if ((header.size() != properties.length && properties.length != 0)
					|| (header.size() != comboSelections.length && comboSelections.length != 0)) {
				fail("Not the same number of entries for property names, property types and words in the first line of the file");
			}
			for (int i = 0; i < comboSelections.length; i++) {
				PropertyType propertyType = PropertyTypeExtension.getInstance()
						.getFactory(comboSelections[i]).createExtensionObject();

				DefaultPropertyDefinition property = new DefaultPropertyDefinition(
						new QName(properties[i]), type, propertyType.getTypeDefinition());
				configureProperty(property);
			}

			boolean skipFirst = Arrays.equals(properties, header.toArray(new String[0]));

			type.setConstraint(new CSVConfiguration(CSVUtil.getSep(this), CSVUtil.getQuote(this),
					CSVUtil.getEscape(this), skipFirst ? 1 : 0));

			schema.addType(type);

		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Cannot load xls/xlsx schema", e));
			reporter.setSuccess(false);
			return null;
		}

		reporter.setSuccess(true);
		return schema;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XLS file (schema)";
	}

	/**
	 * @see eu.esdihumboldt.hale.io.csv.reader.internal.AbstractTableSchemaReader#getHeaderContent()
	 */
	@Override
	public String[] getHeaderContent() {
		return header.toArray(new String[0]);
	}

	private void configureProperty(DefaultPropertyDefinition property) {
		// set constraints on property
		property.setConstraint(NillableFlag.ENABLED); // nillable FIXME
		// should be configurable per field (see also CSVInstanceReader)
		property.setConstraint(Cardinality.CC_EXACTLY_ONCE); // cardinality

		// set metadata of property
		property.setLocation(getSource().getLocation());
	}

}
