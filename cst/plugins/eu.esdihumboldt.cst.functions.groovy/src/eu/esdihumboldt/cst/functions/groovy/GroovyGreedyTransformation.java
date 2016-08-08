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

package eu.esdihumboldt.cst.functions.groovy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.cst.functions.groovy.internal.InstanceAccessorArrayList;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Property transformation based on a Groovy script. With greedy variables in
 * contrast to {@link GroovyTransformation}.
 * 
 * @author Kai Schwierczek
 */
public class GroovyGreedyTransformation extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine>implements GroovyConstants {

	/**
	 * The function/transformation ID.
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.groovy.greedy";

	/**
	 * Name of the parameter specifying if instances should be used as variables
	 * in the binding.
	 */
	public static final String PARAM_INSTANCE_VARIABLES = "variablesAsInstances";

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		// determine if instances should be used in variables or their values
		boolean useInstanceVariables = getOptionalParameter(PARAM_INSTANCE_VARIABLES,
				Value.of(false)).as(Boolean.class);

		// instance builder
		InstanceBuilder builder = GroovyTransformation.createBuilder(resultProperty);

		// create the script binding
		Binding binding = createGroovyBinding(variables.get(ENTITY_VARIABLE),
				getCell().getSource().get(ENTITY_VARIABLE), getCell(), getTypeCell(), builder,
				useInstanceVariables, log, getExecutionContext(),
				resultProperty.getDefinition().getPropertyType());

		Object result;
		try {
			GroovyService service = getExecutionContext().getService(GroovyService.class);
			Script groovyScript = GroovyUtil.getScript(this, binding, service);

			// evaluate the script
			result = GroovyTransformation.evaluate(groovyScript, builder,
					resultProperty.getDefinition().getPropertyType(), service);
		} catch (NoResultException | TransformationException e) {
			throw e;
		} catch (Throwable e) {
			throw new TransformationException("Error evaluating the cell script", e);
		}

		if (result == null) {
			throw new NoResultException();
		}
		return result;
	}

	/**
	 * Create a Groovy binding from the list of variables.
	 * 
	 * @param vars the variable values
	 * @param varDefs definition of the assigned variables, in case some
	 *            variable values are not set, may be <code>null</code>
	 * @param cell the cell the binding is created for
	 * @param typeCell the type cell the binding is created for, may be
	 *            <code>null</code>
	 * @param builder the instance builder for creating target instances, or
	 *            <code>null</code> if not applicable
	 * @param useInstanceVariables if instances should be used as variables for
	 *            the binding instead of extracting the instance values
	 * @param log the transformation log
	 * @param context the execution context
	 * @param targetInstanceType the type of the target instance
	 * @return the binding for use with {@link GroovyShell}
	 */
	public static Binding createGroovyBinding(List<PropertyValue> vars,
			List<? extends Entity> varDefs, Cell cell, Cell typeCell, InstanceBuilder builder,
			boolean useInstanceVariables, TransformationLog log, ExecutionContext context,
			TypeDefinition targetInstanceType) {
		Binding binding = GroovyUtil.createBinding(builder, cell, typeCell, log, context,
				targetInstanceType);

		// collect definitions to check if all were provided
		Set<EntityDefinition> notDefined = new HashSet<>();
		if (varDefs != null) {
			for (Entity entity : varDefs) {
				notDefined.add(entity.getDefinition());
			}
		}
		// keep only defs where no value is provided
		if (!notDefined.isEmpty()) {
			for (PropertyValue var : vars) {
				notDefined.remove(var.getProperty());
			}
		}
		// add empty lists to environment if necessary
		if (!notDefined.isEmpty()) {
			for (EntityDefinition entity : notDefined) {
				GroovyTransformation.addToBinding(binding, (PropertyEntityDefinition) entity,
						Collections.emptyList());
			}
		}

		Map<PropertyEntityDefinition, InstanceAccessorArrayList<Object>> bindingMap = new HashMap<>();

		// collect the values
		for (PropertyValue var : vars) {
			PropertyEntityDefinition property = var.getProperty();
			InstanceAccessorArrayList<Object> valueList = bindingMap.get(property);
			if (valueList == null) {
				valueList = new InstanceAccessorArrayList<>();
				bindingMap.put(property, valueList);
			}
			valueList.add(GroovyTransformation.getUseValue(var.getValue(), useInstanceVariables));
		}

		// add collected values to environment
		for (Entry<PropertyEntityDefinition, InstanceAccessorArrayList<Object>> entry : bindingMap
				.entrySet()) {
			GroovyTransformation.addToBinding(binding, entry.getKey(), entry.getValue());
		}

		return binding;
	}

}
