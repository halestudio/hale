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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
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
	private final static String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	private final static String XLINK_NS = "http://www.w3.org/1999/xlink";

	CustomFunctionAdvToGeographicalNameSimple(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.AbstractPropertyTransformationHandler#doHandle(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      eu.esdihumboldt.hale.common.align.model.Property)
	 */
	@Override
	public Optional<MappingValue> doHandle(final Cell propertyCell, final Property targetProperty) {

		final ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();

		final List<QName> geographicalNamePath = buildPath(
				targetProperty.getDefinition().getPropertyPath());

		final MappingValue mappingValue = new MappingValueBuilder().column()
				.qualifiedTargetPath(
						addToPath(geographicalNamePath, "spelling", "SpellingOfName", "text"))
				.value(propertyName(AppSchemaMappingUtils.getSourceProperty(propertyCell)
						.getDefinition().getPropertyPath()))
				.build();

		final List<MappingValue> constantValues = new ArrayList<MappingValue>();

		constantValues.add(
				createConstantValueMapping(addToPath(geographicalNamePath, "language"), "deu"));

		final String nativenessParam = getSingleProperty(parameters, "nativeness");
		setCodeListValue(addToPath(geographicalNamePath, "nativeness"), "NativenessValue",
				nativenessParam, "endonym", constantValues);

		final String nameStatusParam = getSingleProperty(parameters, "nameStatus");
		setCodeListValue(addToPath(geographicalNamePath, "nameStatus"), "NameStatusValue",
				nameStatusParam, "official", constantValues);

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
				addToPath(geographicalNamePath, "sourceOfName"), sourceOfName));

		constantValues.add(createConstantValueMapping(
				addToPath(XSI_NS, addToPath(geographicalNamePath, "pronunciation"), "@nil"),
				"true"));
		constantValues.add(createConstantValueMapping(
				addToPath("", addToPath(geographicalNamePath, "pronunciation"), "@nilReason"),
				"other:unpopulated"));

		constantValues.add(createConstantValueMapping(
				addToPath(geographicalNamePath, "spelling", "SpellingOfName", "script"), "Latn"));

		final String tableName = ((CellParentWrapper) propertyCell).getTableName();

		constantValues
				.forEach(c -> mappingContext.addValueMappingToTable(targetProperty, c, tableName));

		return Optional.of(mappingValue);
	}

	private void setCodeListValue(final List<QName> propertyPath, final String codeListValue,
			final String code, final String fallbackValue,
			final List<MappingValue> constantValues) {
		if (code == null) {
			// default if not specified
			constantValues.add(createConstantValueMapping(
					addToPath(XLINK_NS, propertyPath, "@href"),
					"http://inspire.ec.europa.eu/codelist/" + codeListValue + "/" + fallbackValue));
		}
		else {
			if ("unpopulated".equals(code)) {
				constantValues.add(createConstantValueMapping(
						addToPath("", propertyPath, "@nilReason"), "other:unpopulated"));
				constantValues.add(createConstantValueMapping(
						addToPath(XSI_NS, propertyPath, "@nil"), "true"));
			}
			else if ("unknown".equals(code)) {
				constantValues.add(createConstantValueMapping(
						addToPath("", propertyPath, "@nilReason"), "unknown"));
				constantValues.add(createConstantValueMapping(
						addToPath(XSI_NS, propertyPath, "@nil"), "true"));
			}
			else {
				constantValues.add(createConstantValueMapping(
						addToPath(XLINK_NS, propertyPath, "@href"),
						"http://inspire.ec.europa.eu/codelist/" + codeListValue + "/" + code));
			}
		}
	}

	private List<QName> addToPath(final List<QName> basePath, final String... elements) {
		final String gnNamespaceUri = basePath.get(basePath.size() - 1).getNamespaceURI();

		return addToPath(gnNamespaceUri, basePath, elements);
	}

	private List<QName> addToPath(final String namespaceUri, final List<QName> basePath,
			final String... elements) {
		return Stream
				.concat(basePath.stream(),
						Arrays.stream(elements).map(element -> new QName(namespaceUri, element)))
				.collect(Collectors.toList());
	}

	private MappingValue createConstantValueMapping(final List<QName> target, final String value) {
		return new MappingValueBuilder().constant().qualifiedTargetPath(target).value(value)
				.build();
	}

}
