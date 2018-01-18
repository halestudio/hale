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

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils;
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

	protected static void setConstantType(final MappingValue mappingValue) {
		mappingValue.setValueType("constant");
	}

	protected static void setExpressionType(final MappingValue mappingValue) {
		mappingValue.setValueType("expression");
	}

	protected String buildPath(final List<ChildContext> path) {
		return buildPath(path, false);
	}

	protected String buildPathWithoutLast(final List<ChildContext> path) {
		return buildPath(path, true);
	}

	protected String buildPathWithoutAttribute(final List<ChildContext> path) {
		final StringBuilder builder = new StringBuilder();
		for (final Iterator<ChildContext> it = Objects.requireNonNull(path, "Path not set")
				.iterator(); it.hasNext();) {
			final ChildContext segment = it.next();
			final PropertyDefinition property = segment.getChild().asProperty();
			if (property == null) {
				// ignore choice definition
				continue;
			}
			if (property.getConstraint(XmlAttributeFlag.class).isEnabled()) {
				if (builder.length() > 0) {
					builder.setLength(builder.length() - 1);
				}
				return builder.toString();
			}
			final String prefixedName = mappingContext.getNamespaces()
					.getPrefixedName(segment.getChild().getName());
			builder.append(prefixedName);
			if (it.hasNext()) {
				builder.append('/');
			}
		}
		return builder.toString();
	}

	private String buildPath(final List<ChildContext> path, final boolean withoutLast) {
		final StringBuilder builder = new StringBuilder();
		for (final Iterator<ChildContext> it = Objects.requireNonNull(path, "Path not set")
				.iterator(); it.hasNext();) {
			final ChildContext segment = it.next();
			final PropertyDefinition property = segment.getChild().asProperty();
			if (property == null) {
				// ignore choice definition
				continue;
			}
			if (withoutLast && !it.hasNext()) {
				if (builder.length() > 0) {
					builder.setLength(builder.length() - 1);
				}
				break;
			}

			if (property.getConstraint(XmlAttributeFlag.class).isEnabled()) {
				builder.append('@');
			}
			final String prefixedName = mappingContext.getNamespaces()
					.getPrefixedName(segment.getChild().getName());
			builder.append(prefixedName);
			if (it.hasNext()) {
				builder.append('/');
			}
		}
		return builder.toString();
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

	@Override
	public final MappingValue handle(final Cell propertyCell) {
		final MappingValue mappingValue = MappingValue.create(mappingContext.getNamespaces());
		final Property targetProperty = AppSchemaMappingUtils.getTargetProperty(propertyCell);

		doHandle(propertyCell, targetProperty, mappingValue);

		final String tableName = ((CellParentWrapper) propertyCell).getTableName();
		mappingContext.addValueMappingToTable(targetProperty, mappingValue, tableName);
		return mappingValue;
	}

	protected abstract void doHandle(final Cell propertyCell, final Property targetProperty,
			final MappingValue mappingValue);
}
