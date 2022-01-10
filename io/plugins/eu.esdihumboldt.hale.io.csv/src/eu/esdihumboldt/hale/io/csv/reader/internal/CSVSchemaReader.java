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
import java.util.Arrays;

import javax.xml.namespace.QName;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
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
import eu.esdihumboldt.hale.io.csv.CSVFileIO;
import eu.esdihumboldt.hale.io.csv.PropertyType;
import eu.esdihumboldt.hale.io.csv.PropertyTypeExtension;
import eu.esdihumboldt.hale.io.csv.reader.CSVConstants;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;

/**
 * Reads a schema from a CSV file.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class CSVSchemaReader extends AbstractTableSchemaReader implements CSVConstants {

	/**
	 * The first line of the CSV file
	 */
	public static String[] firstLine;

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
		progress.begin("Load CSV schema", ProgressIndicator.UNKNOWN); //$NON-NLS-1$

		String namespace = CSVFileIO.CSVFILE_NS;
		DefaultSchema schema = new DefaultSchema(namespace, getSource().getLocation());

		CSVReader reader = CSVUtil.readFirst(this);

		try {
			// initializes the first line of the table (names of the columns)
			firstLine = reader.readNext();

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
				for (int i = 0; i < firstLine.length; i++) {
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
				properties = firstLine;
			}
			else {
				properties = getParameter(PARAM_PROPERTY).as(String.class).split(",");
			}
			// fails if there are less or more property names or property types
			// than the entries in the first line
			if ((firstLine.length != properties.length && properties.length != 0)
					|| (firstLine.length != comboSelections.length
							&& comboSelections.length != 0)) {
				fail("Not the same number of entries for property names, property types and words in the first line of the file");
			}
			for (int i = 0; i < comboSelections.length; i++) {
				PropertyType propertyType;
				propertyType = PropertyTypeExtension.getInstance().getFactory(comboSelections[i])
						.createExtensionObject();

				DefaultPropertyDefinition property = new DefaultPropertyDefinition(
						new QName(properties[i]), type, propertyType.getTypeDefinition());

				// set constraints on property
//				property.setConstraint(NillableFlag.DISABLED); // nillable
				property.setConstraint(NillableFlag.ENABLED); // nillable FIXME
																// should be
																// configurable
																// per field
																// (see also
																// CSVInstanceReader)
				property.setConstraint(Cardinality.CC_EXACTLY_ONCE); // cardinality

				// set metadata for property
				property.setLocation(getSource().getLocation());
			}

			boolean skip = Arrays.equals(properties, firstLine);

			type.setConstraint(new CSVConfiguration(CSVUtil.getSep(this), CSVUtil.getQuote(this),
					CSVUtil.getEscape(this), skip ? 1 : 0));

			schema.addType(type);

		} catch (Exception ex) {
			reporter.error(new IOMessageImpl("Cannot load csv schema", ex));
			reporter.setSuccess(false);
			return null;
		}

		reporter.setSuccess(true);
		return schema;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return CSVFileIO.DEFAULT_TYPE_NAME;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.csv.reader.internal.AbstractTableSchemaReader#getHeaderContent()
	 */
	@Override
	public String[] getHeaderContent() {
		try {
			return CSVUtil.readFirst(this).readNext();
		} catch (IOException e) {
			return new String[0];
		}
	}

}
