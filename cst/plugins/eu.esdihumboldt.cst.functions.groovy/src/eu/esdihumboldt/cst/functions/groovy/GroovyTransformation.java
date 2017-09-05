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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.cst.functions.groovy.internal.TargetCollector;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService.ResultProcessor;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Property transformation based on a Groovy script.
 * 
 * @author Simon Templer
 */
public class GroovyTransformation extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine>implements GroovyConstants {

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
		InstanceBuilder builder = createBuilder(resultProperty);

		// create the script binding
		List<? extends Entity> varDefs = null;
		if (getCell().getSource() != null) {
			varDefs = getCell().getSource().get(ENTITY_VARIABLE);
		}
		Binding binding = createGroovyBinding(variables.get(ENTITY_VARIABLE), varDefs, getCell(),
				getTypeCell(), builder, useInstanceVariables, log, getExecutionContext(),
				resultProperty.getDefinition().getPropertyType());

		Object result;
		try {
			GroovyService service = getExecutionContext().getService(GroovyService.class);
			Script groovyScript = GroovyUtil.getScript(this, binding, service);

			// evaluate the script
			result = evaluate(groovyScript, builder,
					resultProperty.getDefinition().getPropertyType(), service, log);
		} catch (TransformationException | NoResultException e) {
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
	 * Evaluate a Groovy script.
	 * 
	 * @param groovyScript the script
	 * @param builder the instance builder, may be <code>null</code>
	 * @param targetType the type definition of the target property
	 * @param service the Groovy service
	 * @param log the log
	 * @return the result property value or instance
	 * @throws TransformationException if the evaluation fails
	 * @throws NoResultException if no result returned from the evaluation
	 */
	public static Object evaluate(Script groovyScript, final InstanceBuilder builder,
			final TypeDefinition targetType, GroovyService service, SimpleLog log)
					throws TransformationException, NoResultException {
		try {
			return service.evaluate(groovyScript, new ResultProcessor<Object>() {

				@Override
				public Object process(Script groovyScript, Object scriptResult) throws Exception {
					Object result = scriptResult;
					Object target = groovyScript.getBinding().getVariable(BINDING_TARGET);

					if (target instanceof TargetCollector) {
						TargetCollector collector = (TargetCollector) target;
						if (collector.size() == 0) {
							// use script result as result
							result = scriptResult;
						}
						else if (collector.size() == 1) {
							// use single collector value as result
							// -> instance value is set to return value if
							// applicable
							result = collector.toMultiValue(builder, targetType, log).get(0);
						}
						else {
							// use collector MultiValue as result
							result = collector.toMultiValue(builder, targetType, log);
						}
					}
					else if (target instanceof Closure<?>) {
						// legacy way to set target binding
						if (builder != null) {
							result = builder.createInstance(targetType, (Closure<?>) target);
						}
						else {
							throw new TransformationException(
									"An instance is not applicable for the target.");
						}
					}
					else if (target != null) {
						// use target as result
						result = target;
					}

					// use script result as instance value (if possible)
					if (result instanceof MutableInstance && scriptResult != target
							&& scriptResult != result) {
						MutableInstance resInstance = ((MutableInstance) result);
						if (resInstance.getValue() == null) {
							// only override value with script result if current
							// value is null
							// XXX there may still be cases there this is not
							// desired and users instead have to make sure they
							// return null from the function
							resInstance.setValue(scriptResult);
						}
					}

					return result;
				}
			});
		} catch (RuntimeException | TransformationException | NoResultException e) {
			throw e;
		} catch (Exception e) {
			throw new TransformationException(e.getMessage(), e);
		}

	}

	/**
	 * Creates an instance builder for the given result property if applicable.
	 * 
	 * @param resultProperty the result property the instance builder should be
	 *            created for
	 * @return an instance builder or <code>null</code> if for the property no
	 *         instance builder should be used
	 */
	public static InstanceBuilder createBuilder(PropertyEntityDefinition resultProperty) {
		if (!resultProperty.getDefinition().getPropertyType().getChildren().isEmpty()) {
			// property has children and is thus represented as instance
			return new InstanceBuilder(false);
		}

		return null;
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

		// add null value for missing variables
		if (!notDefined.isEmpty()) {
			vars = new ArrayList<>(vars);
			for (EntityDefinition entityDef : notDefined) {
				if (entityDef instanceof PropertyEntityDefinition) {
					vars.add(new PropertyValueImpl(null, (PropertyEntityDefinition) entityDef));
				}
			}
		}

		for (PropertyValue var : vars) {
			// add the variable to the environment
			addToBinding(binding, var.getProperty(),
					getUseValue(var.getValue(), useInstanceVariables));
		}

		return binding;
	}

	/**
	 * Returns the full variable name used for the given entity definition.
	 * 
	 * @param entityDefinition the entity definition
	 * @return the full variable name used
	 */
	public static String getVariableName(PropertyEntityDefinition entityDefinition) {
		List<String> names = new ArrayList<String>();
		for (ChildContext context : entityDefinition.getPropertyPath()) {
			names.add(context.getChild().getName().getLocalPart());
		}
		return Joiner.on('_').join(names);
	}

	/**
	 * Adds the variable to the binding.
	 * 
	 * @param binding the binding to add to
	 * @param prop the property of the variable
	 * @param value the value of the variable
	 */
	public static void addToBinding(Binding binding, PropertyEntityDefinition prop, Object value) {
		// determine the variable name
		String name = prop.getDefinition().getName().getLocalPart();

		// add with short name, but ensure no variable with only a short
		// name is overridden
		if (binding.getVariables().get(name) == null || prop.getPropertyPath().size() == 1) {
			binding.setVariable(name, value);
		}

		// add with long name if applicable
		if (prop.getPropertyPath().size() > 1) {
			binding.setVariable(getVariableName(prop), value);
		}
	}

	/**
	 * Extracts the value to be used in the binding from the present value.
	 * 
	 * @param value the original unmodified value
	 * @param useInstanceVariables if instances should be used as variables for
	 *            the binding instead of extracting the instance values
	 * @return the value to be used by the script
	 */
	public static Object getUseValue(Object value, boolean useInstanceVariables) {
		// determine the variable value
		if (value instanceof Instance) {
			if (!useInstanceVariables) {
				// extract value from instance
				value = ((Instance) value).getValue();
			}
		}

		return value;
	}
}
