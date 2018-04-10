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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.w3c.dom.Node;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;
import eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAppInfo;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Abstract Property Transformation Handler
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class AbstractPropertyTransformationHandler implements PropertyTransformationHandler {

	protected final MappingContext mappingContext;

	protected AbstractPropertyTransformationHandler(final MappingContext mappingContext) {
		this.mappingContext = mappingContext;
	}

	protected List<QName> buildPath(final List<ChildContext> path) {
		return buildPath(path, false);
	}

	protected List<QName> buildPathWithoutLast(final List<ChildContext> path) {
		return buildPath(path, true);
	}

	protected List<QName> buildPath(final List<ChildContext> path, final boolean withoutLast) {
		return path.stream().map(segment -> segment.getChild().asProperty())
				.filter(Objects::nonNull).map(toPropertyNameWithAttributePrefix())
				.limit(withoutLast ? path.size() - 1 : path.size()).collect(Collectors.toList());
	}

	private Function<PropertyDefinition, QName> toPropertyNameWithAttributePrefix() {
		return property -> property.getConstraint(XmlAttributeFlag.class).isEnabled() ? new QName(
				property.getName().getNamespaceURI(), "@" + property.getName().getLocalPart())
				: property.getName();
	}

	protected static String propertyName(final List<ChildContext> path) {
		if (path == null || path.isEmpty()) {
			return "";
		}
		return path.get(path.size() - 1).getChild().getName().getLocalPart();
	}

	protected static String getSingleProperty(final ListMultimap<String, ParameterValue> parameters,
			final String name) {
		if (parameters != null) {
			final List<ParameterValue> parameterValues = parameters.get(name);
			if (parameterValues != null && !parameterValues.isEmpty()) {
				return parameterValues.get(0).as(String.class);
			}
		}
		return null;
	}

	/**
	 * Check if the property cell is a reference and if yes add the association
	 * target that is found in the schema.
	 * 
	 * @param propertyCell Property cell
	 * @param lastValue associated value mapping for the property
	 * @return possibly changed value mapping
	 */
	private MappingValue ensureAssociationTarget(final Cell propertyCell,
			final MappingValue lastValue) {
		final Property targetProperty = AppSchemaMappingUtils.getTargetProperty(propertyCell);
		if (targetProperty.getDefinition().getDefinition().getConstraint(Reference.class)
				.isReference()) {
			final String associationTargetRef = getTargetFromSchema(targetProperty);
			if (associationTargetRef != null) {
				return new MappingValueBuilder().reference()
						.referencedFeatureType(associationTargetRef)
						.qualifiedTargetPath(lastValue.getQualifiedTargetPath())
						.value(lastValue.getValue()).build();
			}
		}

		return lastValue;
	}

	/**
	 * Find the association target from the AppInfo annotation in the XSD
	 * 
	 * @param targetProperty target property to analyze
	 * @return association target as String
	 */
	private String getTargetFromSchema(final Property targetProperty) {
		if (targetProperty.getDefinition().getPropertyPath().isEmpty()) {
			return null;
		}

		final ChildDefinition<?> firstChild = targetProperty.getDefinition().getPropertyPath()
				.get(0).getChild();
		if (!(firstChild instanceof PropertyDefinition)) {
			return null;
		}
		final XmlAppInfo appInfoAnnotation = ((PropertyDefinition) firstChild)
				.getConstraint(XmlAppInfo.class);

		for (final XmlSchemaAppInfo appInfo : appInfoAnnotation.getAppInfos()) {
			for (int i = 0; i < appInfo.getMarkup().getLength(); i++) {
				final Node item = appInfo.getMarkup().item(i);
				if ("targetElement".equals(item.getNodeName())) {
					final String target = item.getTextContent();
					return target;
				}
			}
		}
		return null;
	}

	@Override
	public final MappingValue handle(final Cell propertyCell) {
		final Property targetProperty = AppSchemaMappingUtils.getTargetProperty(propertyCell);
		final Property sourceProperty = AppSchemaMappingUtils.getSourceProperty(propertyCell);

		if (targetProperty == null || (sourceProperty == null && !((this instanceof AssignHandler)
				|| (this instanceof CustomFunctionAdvToNamespace)
				|| (this instanceof SqlExpressionHandler)))) {
			CellParentWrapper cellParentWrapper = (CellParentWrapper) propertyCell;
			mappingContext.getReporter().warn(
					"Cell could not be exported, source or target property is not set (Table: {0}, Source: {1}, Target: {2})",
					cellParentWrapper.getTableName(), sourceProperty, targetProperty);
			return null;
		}

		final Optional<MappingValue> optionalMappingValue = doHandle(propertyCell, targetProperty);

		final String tableName = ((CellParentWrapper) propertyCell).getTableName();

		optionalMappingValue.ifPresent(mappingValue -> {
			mappingValue = ensureAssociationTarget(propertyCell, mappingValue);

			mappingContext.addValueMappingToTable(targetProperty, mappingValue, tableName);
		});

		return optionalMappingValue.orElse(null);
	}

	protected abstract Optional<MappingValue> doHandle(final Cell propertyCell,
			final Property targetProperty);
}
