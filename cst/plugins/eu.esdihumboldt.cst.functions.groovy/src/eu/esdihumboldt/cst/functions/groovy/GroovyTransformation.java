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
import eu.esdihumboldt.cst.functions.groovy.internal.RestrictiveGroovyInterceptor;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.convert.ConversionUtil;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
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
		AbstractSingleTargetPropertyTransformation<TransformationEngine> implements GroovyConstants {

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
		Binding binding = createGroovyBinding(variables.get(ENTITY_VARIABLE), getCell().getSource()
				.get(ENTITY_VARIABLE), builder, useInstanceVariables);

		Object result;
		try {
			Script groovyScript = GroovyUtil.getScript(this, binding);

			// evaluate the script
			result = evaluate(groovyScript, builder, resultProperty.getDefinition()
					.getPropertyType());
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
	 * @return the result property value or instance
	 */
	public static Object evaluate(Script groovyScript, InstanceBuilder builder,
			TypeDefinition targetType) {
		RestrictiveGroovyInterceptor interceptor = new RestrictiveGroovyInterceptor();
		interceptor.register();
		try {
			Object result = groovyScript.run();

			if (builder != null) {
				Object target = groovyScript.getBinding().getVariable(BINDING_TARGET);
				if (target != null) {
					if (target instanceof Closure<?>) {
						// builder closure
						Instance instance = builder.createInstance(targetType, (Closure<?>) target);

						/*
						 * Set the instance value to the script result, if
						 * different from the target closure.
						 */
						if (instance instanceof MutableInstance && result != target) {
							((MutableInstance) instance).setValue(result);
						}

						result = instance;
					}
					else {
						// treat target as value
						// overriding the result
						result = target;
					}
				}
			}

			if (result instanceof Closure<?>) {
				throw new IllegalStateException("A closure cannnot be used as result");
			}

			return result;
		} finally {
			interceptor.unregister();
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
			return new InstanceBuilder();
		}

		return null;
	}

	/**
	 * Create a Groovy binding from the list of variables.
	 * 
	 * @param vars the variable values
	 * @param varDefs definition of the assigned variables, in case some
	 *            variable values are not set, may be <code>null</code>
	 * @param builder the instance builder for creating target instances, or
	 *            <code>null</code> if not applicable
	 * @param useInstanceVariables if instances should be used as variables for
	 *            the binding instead of extracting the instance values
	 * @return the binding for use with {@link GroovyShell}
	 */
	public static Binding createGroovyBinding(List<PropertyValue> vars,
			List<? extends Entity> varDefs, InstanceBuilder builder, boolean useInstanceVariables) {
		Binding binding = new Binding();

		// init builder and target bindings
		binding.setVariable(BINDING_TARGET, null);
		binding.setVariable(BINDING_BUILDER, builder);

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

			// determine the variable value
			Object value = var.getValue();
			boolean asIs = false;
			if (value instanceof Instance) {
				if (useInstanceVariables) {
					// use instance as is
					asIs = true;
				}
				else {
					// extract value from instance
					value = ((Instance) value).getValue();
				}
			}
			if (value instanceof Number) {
				// use numbers as is
			}
			else if (!asIs) {
				// try conversion to String as default
				try {
					value = ConversionUtil.getAs(value, String.class);
				} catch (Exception e) {
					// ignore
				}
			}

			// determine the variable name
			String name = var.getProperty().getDefinition().getName().getLocalPart();

			// add with short name, but ensure no variable with only a short
			// name is overridden
			if (binding.getVariables().get(name) == null
					|| var.getProperty().getPropertyPath().size() == 1) {
				binding.setVariable(name, value);
			}

			// add with long name if applicable
			if (var.getProperty().getPropertyPath().size() > 1) {
				List<String> names = new ArrayList<String>();
				for (ChildContext context : var.getProperty().getPropertyPath()) {
					names.add(context.getChild().getName().getLocalPart());
				}
				String longName = Joiner.on('_').join(names);
				binding.setVariable(longName, value);
			}
		}

		return binding;
	}

}
