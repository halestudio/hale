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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import eu.esdihumboldt.cst.MultiValue;
import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.helper.DefaultHelperContext;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperContext;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctions;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTransformationFunction;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexService;
import eu.esdihumboldt.hale.common.instance.index.spatial.SpatialIndexService;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.groovy.snippets.GroovySnippets;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService.ResultProcessor;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.Script;
import groovy.transform.CompileStatic;

/**
 * Groovy function utilities.
 * 
 * @author Simon Templer
 */
@CompileStatic
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
	public static Script getScript(AbstractTransformationFunction<?> function, Binding binding,
			GroovyService service) throws TransformationException {
		return getScript(function, binding, service, false);
	}

	/**
	 * Get the compiled script.
	 * 
	 * @param function the transformation function the script is associated to
	 * @param binding the binding to set on the script
	 * @param service the Groovy service
	 * @param functionCached if the script should be cached per function instead
	 *            of per cell
	 * @return the script
	 * @throws TransformationException if getting the script parameter from the
	 *             function fails
	 */
	@SuppressWarnings("unchecked")
	public static Script getScript(AbstractTransformationFunction<?> function, Binding binding,
			GroovyService service, boolean functionCached) throws TransformationException {
		/*
		 * The compiled script is stored in a ThreadLocal variable in the
		 * execution context, so it needs only to be created once per
		 * transformation thread.
		 */
		ThreadLocal<Script> localScript;
		Map<Object, Object> context = (functionCached)
				? (function.getExecutionContext().getFunctionContext())
				: (function.getExecutionContext().getCellContext());
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
	 * @param log the log
	 * @return the created instance
	 * @throws TransformationException if the target binding does not contain
	 *             exactly one result after script evaluation or an internal
	 *             error occurs
	 * @throws NoResultException if the script implies that no result should be
	 *             created
	 */
	public static MutableInstance evaluate(Script script, final InstanceBuilder builder,
			final TypeDefinition type, GroovyService service, SimpleLog log)
			throws TransformationException, NoResultException {
		Iterable<MutableInstance> results = evaluateAll(script, builder, type, service, log);
		Iterator<MutableInstance> it = results.iterator();
		MutableInstance result;
		if (it.hasNext()) {
			result = it.next();

			if (it.hasNext()) {
				throw new TransformationException(
						"Cell script does not produce exactly one result.");
			}
		}
		else {
			throw new NoResultException();
		}

		return result;
	}

	/**
	 * Evaluate a Groovy type script.
	 * 
	 * @param script the script
	 * @param builder the instance builder
	 * @param type the type of the instance to create
	 * @param service the Groovy service
	 * @param log the log
	 * @return the created instance
	 * @throws TransformationException if an internal error occurs
	 * @throws NoResultException if the script implies that no result should be
	 *             created
	 */
	public static Iterable<MutableInstance> evaluateAll(Script script,
			final InstanceBuilder builder, final TypeDefinition type, GroovyService service,
			SimpleLog log) throws TransformationException, NoResultException {
		try {
			return service.evaluate(script, new ResultProcessor<Iterable<MutableInstance>>() {

				@Override
				public Iterable<MutableInstance> process(Script script, Object returnValue)
						throws Exception {
					// get target binding
					Object result = script.getBinding().getVariable(BINDING_TARGET);

					// collector or closure
					if (result instanceof TargetCollector) {
						if (((TargetCollector) result).size() <= 0) {
							// TODO warn?
						}
						MultiValue instances = ((TargetCollector) result).toMultiValue(builder,
								type, log);
						Collection<MutableInstance> multiResult = new ArrayList<>(instances.size());
						for (Object instance : instances) {
							multiResult.add((MutableInstance) instance);
						}
						return multiResult;
					}
					else {
						result = builder.createInstance(type, (Closure<?>) result);
					}
					return Collections.singleton((MutableInstance) result);
				}
			});
		} catch (RuntimeException | TransformationException | NoResultException e) {
			throw e;
		} catch (Exception e) {
			throw new TransformationException(e.getMessage(), e);
		}
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
	 * @param targetInstanceType the type of the target instance to create
	 * @return a basic binding
	 */
	public static Binding createBinding(InstanceBuilder builder, Cell cell, Cell typeCell,
			TransformationLog log, ExecutionContext executionContext,
			TypeDefinition targetInstanceType) {
		Binding binding = new Binding();
		HelperContext helperContext = new DefaultHelperContext(executionContext, executionContext,
				cell, typeCell);
		binding.setVariable(BINDING_HELPER_FUNCTIONS, HelperFunctions.createDefault(helperContext));
		binding.setVariable(BINDING_BUILDER, builder);
		binding.setVariable(BINDING_CELL, cell);
		TransformationLogWrapper cellLog = new TransformationLogWrapper(log);
		binding.setVariable(BINDING_LOG, cellLog);
		binding.setVariable(BINDING_CELL_CONTEXT,
				SynchronizedContextProvider.getContextClosure(executionContext.getCellContext()));
		binding.setVariable(BINDING_FUNCTION_CONTEXT, SynchronizedContextProvider
				.getContextClosure(executionContext.getFunctionContext()));
		binding.setVariable(BINDING_TRANSFORMATION_CONTEXT, SynchronizedContextProvider
				.getContextClosure(executionContext.getTransformationContext()));

		// init type cell types
		ArrayList<TypeEntityDefinition> sourceTypes = null;
		TypeEntityDefinition targetType = null;
		if (typeCell != null) {
			targetType = ((Type) CellUtil.getFirstEntity(typeCell.getTarget())).getDefinition();
			if (typeCell.getSource() != null) {
				Collection<? extends Entity> sources = typeCell.getSource().values();
				sourceTypes = new ArrayList<>(sources.size());
				for (Object entity : sources) {
					sourceTypes.add(((Type) entity).getDefinition());
				}
			}
		}
		binding.setVariable(BINDING_SOURCE_TYPES, sourceTypes);
		binding.setVariable(BINDING_TARGET_TYPE, targetType);

		binding.setVariable(BINDING_TARGET, new TargetCollector(builder, targetInstanceType));

		binding.setVariable(BINDING_PROJECT, new ProjectAccessor(
				executionContext.getService(ProjectInfoService.class), cellLog, executionContext));

		binding.setVariable(BINDING_SNIPPETS, new GroovySnippets(executionContext, binding));

		binding.setVariable(BINDING_SPATIAL_INDEX,
				executionContext.getService(SpatialIndexService.class));

		binding.setVariable(BINDING_INSTANCE_INDEX,
				executionContext.getService(InstanceIndexService.class));

		return binding;
	}
}
