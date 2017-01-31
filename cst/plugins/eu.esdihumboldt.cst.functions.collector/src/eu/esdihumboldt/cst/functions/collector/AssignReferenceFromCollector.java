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

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.MultiValue;
import eu.esdihumboldt.cst.functions.collector.helpers.CollectorGroovyHelper;
import eu.esdihumboldt.cst.functions.groovy.helpers.ContextHelpers;
import eu.esdihumboldt.cst.functions.groovy.helpers.util.Collector;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.AssignReferenceFromCollectorFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Function to assign references collected by a {@link Collector} to a
 * multi-valued target property. In case the target property cannot take the
 * values itself a child property is looked up to assign the references to.
 * 
 * @author Florian Esser
 */
public class AssignReferenceFromCollector
		extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements AssignReferenceFromCollectorFunction {

	private final CollectorGroovyHelper helper = new CollectorGroovyHelper();

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

		// assign the value supplied as parameter
		// conversion will be applied automatically to fit the binding
//		return getTransformedParameterChecked(PARAMETER_VALUE);

		Collector mainCollector = (Collector) getExecutionContext().getTransformationContext()
				.get(ContextHelpers.KEY_COLLECTOR);
		if (mainCollector == null) {
			throw new TransformationException(
					"Fatal: No collector has been created yet. Check function priority.");
		}

		ParameterValue collectorName = getParameterChecked(PARAMETER_COLLECTOR);
		if (collectorName == null || collectorName.isEmpty()) {
			throw new TransformationException("Fatal: No collector name was specified.");
		}

		Collector collector = mainCollector.getAt(collectorName.getValue().toString());
		if (collector == null) {
			throw new TransformationException(MessageFormat.format(
					"Collector \"{0}\" could not (yet) be found. Check spelling and function priority.",
					collectorName.getValue().toString()));
		}

		MultiValue result;

		List<Object> collectedReferences = helper.extractCollectedValues(collector);
		final TypeDefinition resultPropertyType = resultProperty.getDefinition().getPropertyType();
		if (resultPropertyType.getConstraint(HasValueFlag.class).isEnabled()) {
			// Target property can take values, assign directly
			result = new MultiValue(collectedReferences);
		}
		else {
			// Find child element/attribute that can be assigned the reference
			final PropertyDefinition suitableChild = Optional
					.ofNullable(findReferenceChildProperty(resultPropertyType))
					.orElseThrow(() -> new TransformationException(
							"Fatal: No child property could be found to assign a reference to."));

			final Reference referenceConstraint = suitableChild.getConstraint(Reference.class);

			result = new MultiValue();
			collectedReferences.forEach(ref -> result.add(helper.createInstance(resultPropertyType,
					suitableChild, referenceConstraint.toId(ref))));
		}

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

}
