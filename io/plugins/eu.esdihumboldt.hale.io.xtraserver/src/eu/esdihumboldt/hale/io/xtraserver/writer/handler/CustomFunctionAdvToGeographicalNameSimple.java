/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.writer.handler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils;

/**
 * * Transforms the custom function
 * 'custom:alignment:adv.inspire.GeographicalName.simple' to a
 * {@link MappingValue}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class CustomFunctionAdvToGeographicalNameSimple extends FormattedStringHandler {

	public final static String FUNCTION_ID = "custom:alignment:adv.inspire.GeographicalName.simple";

	CustomFunctionAdvToGeographicalNameSimple(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.AbstractPropertyTransformationHandler#doHandle(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      eu.esdihumboldt.hale.common.align.model.Property,
	 *      de.interactive_instruments.xtraserver.config.util.api.MappingValue)
	 */
	@Override
	public void doHandle(final Cell propertyCell, final Property targetProperty,
			final MappingValue mappingValue) {

		final ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();

		final String currentPath = buildPathWithoutLast(
				targetProperty.getDefinition().getPropertyPath());

		mappingValue.setTarget(
				currentPath + "/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text");

		mappingValue.setValue(propertyName(AppSchemaMappingUtils.getSourceProperty(propertyCell)
				.getDefinition().getPropertyPath()));

		final List<MappingValue> constantValues = new ArrayList<MappingValue>();

		final String nameStatusParam = getSingleProperty(parameters, "nameStatus");
		setCodeListValue(currentPath + "/gn:GeographicalName/gn:nameStatus", "NameStatusValue",
				nameStatusParam, "official", constantValues);

		final String nativenessParam = getSingleProperty(parameters, "nativeness");
		setCodeListValue(currentPath + "/gn:GeographicalName/gn:nativeness", "NativenessValue",
				nativenessParam, "endonym", constantValues);

		// Determine source with priorities: source first, ADV_MODELLART project
		// property, AA_Modellart.advStandardModell
		final String sourceParam = getSingleProperty(parameters, "source");
		final String sourceOfName;
		if (sourceParam != null) {
			sourceOfName = sourceParam;
		}
		else {
			final Value projectPropertySource = this.mappingContext
					.getTransformationProperty(MappingContext.PROPERTY_ADV_MODELLART);
			if (!projectPropertySource.isEmpty()) {
				sourceOfName = projectPropertySource.as(String.class);
			}
			else {
				sourceOfName = "unknown";
			}
		}
		constantValues.add(createConstantValueMapping(
				currentPath + "/gn:GeographicalName/gn:sourceOfName", sourceOfName));

		constantValues.add(createConstantValueMapping(
				currentPath + "/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:script",
				"Latn"));

		constantValues.add(createConstantValueMapping(
				currentPath + "/gn:GeographicalName/gn:pronunciation/@xsi:nil", "true"));
		constantValues.add(createConstantValueMapping(
				currentPath + "/gn:GeographicalName/gn:pronunciation/@nilReason",
				"other:unpopulated"));

		final String tableName = ((CellParentWrapper) propertyCell).getTableName();

		constantValues
				.forEach(c -> mappingContext.addValueMappingToTable(targetProperty, c, tableName));

	}

	private void setCodeListValue(final String propertyPath, final String codeListValue,
			final String code, final String fallbackValue,
			final List<MappingValue> constantValues) {
		if (code == null) {
			// default if not specified
			constantValues.add(createConstantValueMapping(propertyPath + "/@xlink:href",
					"http://inspire.ec.europa.eu/codelist/" + codeListValue + "/" + fallbackValue));
		}
		else {
			if ("unpopulated".equals(code)) {
				constantValues.add(createConstantValueMapping(propertyPath + "/@nilReason",
						"other:unpopulated"));
				constantValues.add(createConstantValueMapping(propertyPath + "/@xsi:nil", "true"));
			}
			else if ("unknown".equals(code)) {
				constantValues
						.add(createConstantValueMapping(propertyPath + "/@nilReason", "unknown"));
				constantValues.add(createConstantValueMapping(propertyPath + "/@xsi:nil", "true"));
			}
			else {
				constantValues.add(createConstantValueMapping(propertyPath + "/@xlink:href",
						"http://inspire.ec.europa.eu/codelist/" + codeListValue + "/" + code));
			}
		}
	}

	private MappingValue createConstantValueMapping(final String target, final String value) {
		final MappingValue constantValue = MappingValue.create(mappingContext.getNamespaces());
		setConstantType(constantValue);
		constantValue.setTarget(target);
		constantValue.setValue(value);
		return constantValue;
	}

}
