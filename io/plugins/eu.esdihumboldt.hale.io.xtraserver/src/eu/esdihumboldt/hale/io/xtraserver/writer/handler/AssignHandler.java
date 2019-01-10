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

import static eu.esdihumboldt.hale.common.align.model.functions.AssignFunction.PARAMETER_VALUE;

import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;

/**
 * Transforms the {@link AssignFunction} to a {@link MappingValue}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class AssignHandler extends AbstractPropertyTransformationHandler {

	private final static String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	private final static String NIL_REASON = "@nilReason";

	AssignHandler(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public Optional<MappingValue> doHandle(final Cell propertyCell, final Property targetProperty) {

		// Assign constant value from parameters
		final ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();
		final List<ParameterValue> valueParams = parameters.get(PARAMETER_VALUE);
		final String value = valueParams.get(0).getStringRepresentation();

		final List<QName> path = buildPath(targetProperty.getDefinition().getPropertyPath());

		// if nilReason is set and property is nillable, set xsi:nil to true
		if (path.get(path.size() - 1).getLocalPart().equals(NIL_REASON)
				&& isNillable(targetProperty.getDefinition())) {
			final List<QName> nilPath = ImmutableList.<QName> builder()
					.addAll(path.subList(0, path.size() - 1)).add(new QName(XSI_NS, "@nil"))
					.build();
			final MappingValue nilMappingValue = new MappingValueBuilder().constant()
					.qualifiedTargetPath(nilPath).value("true").build();
			final String tableName = ((CellParentWrapper) propertyCell).getTableName();
			mappingContext.addValueMappingToTable(targetProperty, nilMappingValue, tableName);
		}

		final MappingValue mappingValue = new MappingValueBuilder().constant()
				.qualifiedTargetPath(path).value(mappingContext.resolveProjectVars(value)).build();

		return Optional.of(mappingValue);
	}

	private boolean isNillable(PropertyEntityDefinition definition) {

		if (definition.getPropertyPath().size() > 1) {
			final PropertyDefinition property = definition.getPropertyPath()
					.get(definition.getPropertyPath().size() - 2).getChild().asProperty();
			if (property != null) {
				return property.getConstraint(NillableFlag.class).isEnabled();
			}
		}
		return false;
	}
}
