/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTransformationFunction;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.Script;

/**
 * Groovy function utilities.
 * 
 * @author Simon Templer
 */
public class GroovyUtil implements GroovyConstants {

	/**
	 * Get the script string.
	 * 
	 * @param function the transformation function the script is associated to
	 * @return the script string
	 * @throws TransformationException if getting the script parameter from the
	 *             function fails
	 */
	public static String getScriptString(AbstractTransformationFunction<?> function)
			throws TransformationException {
		ParameterValue scriptValue = function.getParameterChecked(PARAMETER_SCRIPT);
		String script;
		// try retrieving as text
		Text text = scriptValue.as(Text.class);
		if (text != null) {
			script = text.getText();
		}
		else {
			// fall back to string value
			script = scriptValue.as(String.class);
		}
		return script;
	}

	/**
	 * Get the compiled script.
	 * 
	 * @param function the transformation function the script is associated to
	 * @param binding the binding to set on the script
	 * @param service the Groovy service
	 * @return the script
	 * @throws TransformationException if getting the script parameter from the
	 *             function fails
	 */
	@SuppressWarnings("unchecked")
	public static Script getScript(AbstractTransformationFunction<?> function, Binding binding,
			GroovyService service) throws TransformationException {
		/*
		 * The compiled script is stored in a ThreadLocal variable in the
		 * execution context, so it needs only to be created once per
		 * transformation thread.
		 */
		ThreadLocal<Script> localScript;
		Map<Object, Object> context = function.getExecutionContext().getCellContext();
		synchronized (context) {
			Object tmp = context.get(CONTEXT_SCRIPT);

			if (tmp instanceof ThreadLocal<?>) {
				localScript = (ThreadLocal<Script>) tmp;
			}
			else {
				localScript = new ThreadLocal<Script>();
				context.put(CONTEXT_SCRIPT, localScript);
			}
		}

		Script groovyScript = localScript.get();
		if (groovyScript == null) {
			// create the script
			String script = getScriptString(function);

			groovyScript = service.parseScript(script, null);

			localScript.set(groovyScript);
		}
		groovyScript.setBinding(binding);
		return groovyScript;
	}

	/**
	 * Evaluate a Groovy type script.
	 * 
	 * @param script the script
	 * @param builder the instance builder
	 * @param type the type of the instance to create
	 * @param service the Groovy service
	 * @return the created instance
	 */
	public static MutableInstance evaluate(Script script, InstanceBuilder builder,
			TypeDefinition type, GroovyService service) {
		service.evaluate(script);

		// retrieve the target builder closure
		Closure<?> closure = (Closure<?>) script.getBinding().getVariable(BINDING_TARGET);

		// create the instance
		Instance instance = builder.createInstance(type, closure);
		return (MutableInstance) instance;
	}

	/**
	 * Creates a basic binding used by all Groovy functions.
	 * 
	 * @param builder the instance builder, may be <code>null</code>
	 * @param cell the cell of the function
	 * @param typeCell the type cell the function works on, may be
	 *            <code>null</code>
	 * @param log the transformation log
	 * @param executionContext the execution context
	 * @return a basic binding
	 */
	public static Binding createBinding(InstanceBuilder builder, Cell cell, Cell typeCell,
			TransformationLog log, ExecutionContext executionContext) {
		Binding binding = new Binding();
		binding.setVariable(BINDING_TARGET, null);
		binding.setVariable(BINDING_BUILDER, builder);
		binding.setVariable(BINDING_CELL, cell);
		binding.setVariable(BINDING_LOG, log);
		binding.setVariable(BINDING_EXECUTION_CONTEXT, executionContext);

		// init type cell types
		ArrayList<TypeEntityDefinition> sourceTypes = null;
		TypeEntityDefinition targetType = null;
		if (typeCell != null) {
			targetType = ((Type) CellUtil.getFirstEntity(typeCell.getTarget())).getDefinition();
			if (typeCell.getSource() != null) {
				Collection<? extends Entity> sources = typeCell.getSource().values();
				sourceTypes = new ArrayList<>(sources.size());
				for (Entity entity : sources) {
					sourceTypes.add(((Type) entity).getDefinition());
				}
			}
		}
		binding.setVariable(BINDING_SOURCE_TYPES, sourceTypes);
		binding.setVariable(BINDING_TARGET_TYPE, targetType);

		return binding;
	}
}
