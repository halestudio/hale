/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.collector;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.MultiValue;
import eu.esdihumboldt.cst.functions.collector.helpers.CollectorGroovyHelper;
import eu.esdihumboldt.cst.functions.groovy.helpers.ContextHelpers;
import eu.esdihumboldt.cst.functions.groovy.helpers.util.Collector;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.TransformationMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Function to assign values collected by a {@link Collector} to a multi-valued
 * target property. In case the target property has the {@link Reference}
 * constraint, the values are passed to {@link Reference#idToReference(Object)}
 * before assignment.
 * 
 * If the target property cannot take the values itself, a child property with
 * the {@link Reference} constraint is looked up to assign the values to.
 * 
 * @author Florian Esser
 */
public class AssignFromCollector
		extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements AssignFromCollectorFunction {

	private static final CollectorGroovyHelper helper = new CollectorGroovyHelper();

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation#evaluate(java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine,
	 *      com.google.common.collect.ListMultimap, java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition,
	 *      java.util.Map,
	 *      eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {

		// XXX check anchor?

		final Collector mainCollector = (Collector) getExecutionContext().getTransformationContext()
				.get(ContextHelpers.KEY_COLLECTOR);
		if (mainCollector == null) {
			throw new TransformationException(
					"Fatal: No collector has been created yet. Check function priority.");
		}

		final ParameterValue collectorName = getParameterChecked(PARAMETER_COLLECTOR);
		if (collectorName == null || collectorName.isEmpty()) {
			throw new TransformationException("Fatal: No collector name was specified.");
		}

		final Collector collector = mainCollector.getAt(collectorName.getValue().toString());
		if (collector == null) {
			throw new TransformationException(MessageFormat.format(
					"Error retrieving collector \"{0}\"", collectorName.getValue().toString()));
		}
		else if (collector.values().isEmpty()) {
			log.warn(new TransformationMessageImpl(getCell(),
					MessageFormat.format(
							"Collector \"{0}\" contains no values. If this is unexpected, check the spelling of the collector name and the priority of the transformation function.",
							collectorName.getStringRepresentation()),
					null));
		}

		// Determine where to assign the collected values
		final TypeDefinition resultPropertyType = resultProperty.getDefinition().getPropertyType();
		final PropertyDefinition targetProperty;
		final ResultStrategy resultStrategy;
		if (resultPropertyType.getConstraint(HasValueFlag.class).isEnabled()) {
			// The result property can take values, therefore assign directly to
			// property
			targetProperty = resultProperty.getDefinition();
			// No instance creation is required in this case
			resultStrategy = ResultStrategy.USE_VALUE;
		}
		else {
			// Find child element/attribute that can be assigned the reference
			targetProperty = Optional.ofNullable(findReferenceChildProperty(resultPropertyType))
					.orElseThrow(() -> new TransformationException(
							"Fatal: No child property could be found to assign a reference to."));
			resultStrategy = ResultStrategy.BUILD_INSTANCE;
		}

		List<Object> collectedReferences = helper.extractCollectedValues(collector);

		// Process collected values if target property is a reference, otherwise
		// use plain values
		final Function<Object, Object> referenceStrategy;
		if (targetProperty.getConstraint(Reference.class).isReference()) {
			final Reference referenceConstraint = targetProperty.getConstraint(Reference.class);
			// Use the idToReference method to construct the reference
			referenceStrategy = referenceConstraint::idToReference;
		}
		else {
			referenceStrategy = Function.identity();
		}

		MultiValue result = new MultiValue();
		collectedReferences.forEach(ref -> result.add(resultStrategy
				.createResult(resultPropertyType, targetProperty, referenceStrategy.apply(ref))));
		return result;
	}

	/**
	 * Find first child property of the given {@link TypeDefinition} where
	 * <code>child.getConstraint(Reference.class).isReference()</code> returns
	 * true.
	 * 
	 * @param propertyType The <code>TypeDefinition</code> to search
	 * 
	 * @return The first child that matches or <code>null</code> if none match
	 */
	private PropertyDefinition findReferenceChildProperty(TypeDefinition propertyType) {
		return DefinitionUtil.getAllProperties(propertyType).stream()
				.filter(prop -> prop.getConstraint(Reference.class).isReference()).findFirst()
				.orElse(null);
	}

	private interface ResultStrategy {

		public final ResultStrategy USE_VALUE = new ResultStrategy() {

			@Override
			public Object createResult(TypeDefinition type, PropertyDefinition property,
					Object value) {
				return value;
			}
		};

		public final ResultStrategy BUILD_INSTANCE = new ResultStrategy() {

			@Override
			public Object createResult(TypeDefinition type, PropertyDefinition property,
					Object value) {
				return helper.createInstance(type, property, value);
			}
		};

		Object createResult(TypeDefinition type, PropertyDefinition property, Object value);
	}
}
