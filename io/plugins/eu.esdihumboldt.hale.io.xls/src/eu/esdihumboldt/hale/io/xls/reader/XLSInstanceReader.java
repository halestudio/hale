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
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVInstanceReader;
import eu.esdihumboldt.hale.io.xls.AnalyseXLSSchemaTable;
import eu.esdihumboldt.hale.io.xls.reader.ReaderSettings.SheetInfo;

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

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Load Excel file", ProgressIndicator.UNKNOWN);
		try {
			ReaderSettings settings = ReaderSettings.load(this);
			Collection<? extends SheetInfo> sheets = settings.getSheetsToRead();
			if (sheets.isEmpty()) {
				reporter.info("No sheets to load");
				reporter.setSuccess(true);
				return reporter;
			}

			// instance collection where all instances are collected
			instances = new DefaultInstanceCollection(new ArrayList<Instance>());

			boolean failed = false;
			for (SheetInfo sheet : sheets) {
				try {
					loadSheet(sheet, reporter);
				} catch (Exception e) {
					failed = true;
					reporter.error("Reading the excel sheet {0} at index {1} failed",
							sheet.getName(), sheet.getIndex(), e);
				}
			}

			reporter.setSuccess(!failed);
		} catch (Exception e) {
			reporter.error("Reading the excel file failed", e);
		} finally {
			progress.end();
		}
		return reporter;
	}

	/**
	 * Try to match the given type name to a schema type based on the name.
	 * Prefers a full match of the qualified name but also test for display name
	 * and local name matching the provided local name.
	 * 
	 * @param typeName the type name to match
	 * @param schema the schema to check
	 * @return the matched type or <code>null</code>
	 */
	public static TypeDefinition matchTypeByName(QName typeName, TypeIndex schema) {
		TypeDefinition type = schema.getType(typeName);

		if (type == null) {
			// try matching display name (since this is used when writing to
			// Excel)
			type = schema.getMappingRelevantTypes().stream()
					.filter(t -> typeName.getLocalPart().equals(t.getDisplayName())).findFirst()
					.orElse(null);
		}

		if (type == null) {
			// try matching local name
			type = schema.getMappingRelevantTypes().stream()
					.filter(t -> typeName.getLocalPart().equals(t.getName().getLocalPart()))
					.findFirst().orElse(null);
		}

		return type;
	}

	private void loadSheet(SheetInfo sheet, IOReporter reporter) throws Exception {
		int skipNlines = sheet.getSettings().getSkipLines() != null
				? sheet.getSettings().getSkipLines()
				: 0;

		AnalyseXLSSchemaTable analyser = new AnalyseXLSSchemaTable(getSource(),
				ReaderSettings.isXlsxContentType(getContentType()), sheet.getIndex(), skipNlines);

		// get type definition of the schema
		QName typeName = sheet.getSettings().getTypeName();
		TypeDefinition type = null;
		if (typeName != null) {
			type = getSourceSchema().getType(typeName);
		}
		if (type == null) {
			// look for match based on sheet name
			QName qname = null;
			try {
				qname = QName.valueOf(sheet.getName());
			} catch (Exception e) {
				qname = new QName(sheet.getName());
			}
			type = matchTypeByName(qname, getSourceSchema());
		}

		// get property definitions
		Collection<? extends PropertyDefinition> children = DefinitionUtil.getAllProperties(type);
		PropertyDefinition[] propAr = children.toArray(new PropertyDefinition[children.size()]);
		Collection<List<String>> rows = analyser.getRows();

		int line = 0;

		// iterate over all rows to create the instances
		Iterator<List<String>> allRows = rows.iterator();
		while (allRows.hasNext()) {
			List<String> row = allRows.next();
			if (row != null && !row.stream().allMatch(s -> s == null || s.isEmpty())) {
				addInstanceForRow(row, type, propAr, line, reporter);
			}
			line++;
		}
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
	private void addInstanceForRow(List<String> row, TypeDefinition type,
			PropertyDefinition[] propAr, int line, IOReporter reporter) {
		MutableInstance instance = new DefaultInstance(type, null);
		if (row == null) {
			return;
		}

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

	@Override
	protected String getDefaultTypeName() {
		return "Excel file";
	}

}
