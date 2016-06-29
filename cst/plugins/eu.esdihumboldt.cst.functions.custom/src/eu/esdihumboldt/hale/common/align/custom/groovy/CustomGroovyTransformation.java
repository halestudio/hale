/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.custom.groovy;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.groovy.GroovyTransformation;
import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.cst.functions.groovy.internal.InstanceAccessorArrayList;
import eu.esdihumboldt.hale.common.align.custom.CustomPropertyFunctionType;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionEntity;
import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.mdexpl.ParameterBinding;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * Implementation of a custom transformation as Groovy transformation.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class CustomGroovyTransformation
		extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements PropertyTransformation<TransformationEngine> {

	/**
	 * Binding parameter name for function parameters.
	 */
	public static final String BINDING_PARAMS = "_params";

	private final DefaultCustomPropertyFunction customFunction;

	/**
	 * @param customFunction the custom function definition
	 */
	public CustomGroovyTransformation(DefaultCustomPropertyFunction customFunction) {
		this.customFunction = customFunction;
	}

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		// store script as parameter
		if (!CustomPropertyFunctionType.GROOVY.equals(customFunction.getFunctionType())) {
			throw new TransformationException("Custom function is not of type groovy");
		}
		Value scriptValue = customFunction.getFunctionDefinition();
		if (scriptValue.isEmpty()) {
			throw new NoResultException("Script not defined");
		}
		ListMultimap<String, ParameterValue> params = ArrayListMultimap.create();
		params.put(GroovyTransformation.PARAMETER_SCRIPT, new ParameterValue(scriptValue));
		setParameters(params);

		// instance builder
		InstanceBuilder builder = GroovyTransformation.createBuilder(resultProperty);

		// create the script binding
		Binding binding = createGroovyBinding(variables, getCell(), getTypeCell(), builder, log,
				getExecutionContext());

		Object result;
		try {
			GroovyService service = getExecutionContext().getService(GroovyService.class);
			Script groovyScript = GroovyUtil.getScript(this, binding, service, true);

			// evaluate the script
			result = GroovyTransformation.evaluate(groovyScript, builder,
					resultProperty.getDefinition().getPropertyType(), service);
		} catch (Throwable e) {
			throw new TransformationException("Error evaluating the custom function script", e);
		}

		if (result == null) {
			throw new NoResultException();
		}
		return result;
	}

	private Binding createGroovyBinding(ListMultimap<String, PropertyValue> variables, Cell cell,
			Cell typeCell, InstanceBuilder builder, TransformationLog log,
			ExecutionContext executionContext) {
		Binding binding = GroovyUtil.createBinding(builder, cell, typeCell, log, executionContext);

		// create bindings for inputs
		for (DefaultCustomPropertyFunctionEntity source : customFunction.getSources()) {
			String varName = source.getName();

			boolean useInstanceVariable = useInstanceVariableForSource(source);
			List<PropertyValue> values = variables.get(varName);

			if (source.isEager() || source.getMaxOccurrence() > 1
					|| source.getMaxOccurrence() == ParameterDefinition.UNBOUNDED) {
				// multiple values
				InstanceAccessorArrayList<Object> valueList = new InstanceAccessorArrayList<>();
				for (PropertyValue value : values) {
					valueList.add(GroovyTransformation.getUseValue(value.getValue(),
							useInstanceVariable));
				}
				binding.setVariable(varName, valueList);
			}
			else {
				// single value
				if (values.isEmpty()) {
					// no value
					// -> use null value for missing variable
					binding.setVariable(varName, null);
				}
				else {
					// value
					binding.setVariable(varName, GroovyTransformation
							.getUseValue(values.get(0).getValue(), useInstanceVariable));
				}
			}
		}

		// create binding(s) for parameters
		binding.setVariable(BINDING_PARAMS,
				new ParameterBinding(cell, customFunction.getDescriptor()));

		return binding;
	}

	/**
	 * Determine if for a given input variable an instance value should be used.
	 * 
	 * @param source the input variable definition
	 * @return if instance values should be used
	 */
	public static boolean useInstanceVariableForSource(DefaultCustomPropertyFunctionEntity source) {
		return source.getBindingType() != null && !source.getBindingType().getChildren().isEmpty();
	}

}
