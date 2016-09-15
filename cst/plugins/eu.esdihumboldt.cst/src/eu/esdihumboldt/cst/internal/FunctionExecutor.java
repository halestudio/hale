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

package eu.esdihumboldt.cst.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.core.convert.ConversionException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.MultiValue;
import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationFactory;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.CellNodeValidator;
import eu.esdihumboldt.hale.common.align.service.TransformationFunctionService;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.CellLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.TransformationMessageImpl;
import eu.esdihumboldt.hale.common.convert.ConversionUtil;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;
import eu.esdihumboldt.util.Pair;

/**
 * Function executor on a transformation tree.
 * 
 * @author Simon Templer
 */
public class FunctionExecutor extends CellNodeValidator {

	private final EngineManager engines;
	private final TransformationFunctionService transformations;
	private final TransformationContext context;
	private final Priority functionPriority;
	private final ThreadLocal<Cell> typeCell = new ThreadLocal<>();

	/**
	 * Create a function executor.
	 * 
	 * @param reporter the transformation reporter
	 * @param engines the transformation engine manager
	 * @param context the transformation execution context
	 * @param functionPriority the prioritylevel of the function
	 */
	public FunctionExecutor(TransformationReporter reporter, EngineManager engines,
			TransformationContext context, Priority functionPriority) {
		super(reporter, context.getServiceProvider());
		this.engines = engines;
		this.context = context;
		this.functionPriority = functionPriority;

		this.transformations = context.getServiceProvider().getService(
				TransformationFunctionService.class);
	}

	/**
	 * @see CellNodeValidator#processValid(Cell, ListMultimap, ListMultimap)
	 */
	@Override
	protected void processValid(Cell cell, ListMultimap<String, Pair<SourceNode, Entity>> sources,
			ListMultimap<String, Pair<TargetNode, Entity>> targets) {
		if (cell.getPriority() != functionPriority) {
			// ignore the priorities that do not match
			return;
		}
		/*
		 * if the result node is only one and its value had already been set, it
		 * is not necessary to execute this function of a lower priority. (if
		 * there are more than one target node we will need to execute them all
		 * and check at the end of the transformation.
		 */
		if (targets.size() == 1) {
			TargetNode targetNode = targets.values().iterator().next().getFirst();
			if (targetNode.isDefined()) {
				// a result has been set already, being in lower priority we now
				// pass
				return;
			}
		}

		String functionId = cell.getTransformationIdentifier();

		List<PropertyTransformationFactory> transformations = this.transformations
				.getPropertyTransformations(functionId);

		if (transformations == null || transformations.isEmpty()) {
			reporter.error(new TransformationMessageImpl(cell, MessageFormat.format(
					"No transformation for function {0} found. Skipping property transformation.",
					functionId), null));
		}
		else {
			// TODO select based on e.g. preferred transformation engine?
			PropertyTransformationFactory transformation = transformations.iterator().next();

			executeTransformation(transformation, cell, sources, targets);
		}
	}

	/**
	 * Execute a property transformation.
	 * 
	 * @param transformation the transformation factory
	 * @param cell the alignment cell
	 * @param sources the named source entities and nodes
	 * @param targets the named target entities and nodes
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void executeTransformation(PropertyTransformationFactory transformation, Cell cell,
			ListMultimap<String, Pair<SourceNode, Entity>> sources,
			ListMultimap<String, Pair<TargetNode, Entity>> targets) {
		TransformationLog cellLog = new CellLog(reporter, cell);

		PropertyTransformation<?> function;
		try {
			// TODO cache function objects?
			function = transformation.createExtensionObject();
		} catch (Exception e) {
			cellLog.error(cellLog.createMessage("Error creating transformation function.", e));
			return;
		}

		TransformationEngine engine = engines.get(transformation.getEngineId(), cellLog);

		if (engine == null) {
			// TODO instead try another transformation
			cellLog.error(cellLog.createMessage(
					"Skipping property transformation: No matching transformation engine found",
					null));
			return;
		}

		// configure function

		// set expected result
		ListMultimap<String, PropertyEntityDefinition> expectedResult = ArrayListMultimap.create(
				targets.keySet().size(), 1);
		for (Entry<String, Pair<TargetNode, Entity>> targetEntry : targets.entries()) {
			EntityDefinition def = targetEntry.getValue().getSecond().getDefinition();
			expectedResult.put(targetEntry.getKey(), toPropertyEntityDefinition(def));
		}
		function.setExpectedResult(expectedResult);

		// set source variables
		ListMultimap<String, PropertyValue> variables = ArrayListMultimap.create();
		for (Entry<String, Pair<SourceNode, Entity>> sourceEntry : sources.entries()) {
			EntityDefinition def = sourceEntry.getValue().getSecond().getDefinition();
			SourceNode sourceNode = sourceEntry.getValue().getFirst();
			if (TransformationTreeUtil.isEager(cell, sourceNode, cellLog,
					context.getServiceProvider())) {
				// eager source - all values
				Object[] values = sourceNode.getAllValues();
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						PropertyValue propertyValue = new PropertyValueImpl(values[i],
								toPropertyEntityDefinition(def));
						variables.put(sourceEntry.getKey(), propertyValue);
					}
				}
			}
			else {
				// non-eager source - one value
				Object value = sourceNode.getValue();
				PropertyValue propertyValue = new PropertyValueImpl(value,
						toPropertyEntityDefinition(def));
				variables.put(sourceEntry.getKey(), propertyValue);
			}
		}
		function.setVariables(variables);

		// set parameters
		function.setParameters(cell.getTransformationParameters());

		// set context
		function.setExecutionContext(context.getCellContext(cell));
		// set target type
		TypeDefinition targetType = null;
		if (!targets.isEmpty()) {
			TargetNode target = targets.values().iterator().next().getFirst();
			targetType = target.getEntityDefinition().getType();
		}
		function.setTargetType(targetType);
		function.setTypeCell(typeCell.get());

		// execute function
		try {
			((PropertyTransformation) function).execute(transformation.getIdentifier(), engine,
					transformation.getExecutionParameters(), cellLog, cell);
		} catch (Throwable e) {
			// TODO instead try another transformation?
			cellLog.error(cellLog.createMessage(
					"Skipping property transformation: Executing property transformation failed.",
					e));
			return;
		}

		// apply function results
		ListMultimap<String, Object> results = function.getResults();
		if (results != null) {
			for (String name : results.keySet()) {
				List<Object> values = results.get(name);
				List<Pair<TargetNode, Entity>> nodes = targets.get(name);

				if (nodes.size() > values.size()) {
					cellLog.warn(cellLog.createMessage(MessageFormat.format(
							"Transformation result misses values for result with name {0}", name),
							null));
				}
				if (values.size() > nodes.size()) {
					cellLog.warn(cellLog.createMessage(
							MessageFormat
									.format("More transformation results than target nodes for result with name {0}",
											name), null));
				}

				int count = Math.min(values.size(), nodes.size());

				// FIXME if multiple target nodes should be implemented ever
				// ith value is ignored if ith node already has a value
				// instead it should probably look to put ith value into i+1th
				// node...
				for (int i = 0; i < count; i++) {
					Object value = values.get(i);
					TargetNode node = nodes.get(i).getFirst();

					if (value instanceof MultiValue) {
						MultiValue originalValue = (MultiValue) value;
						MultiValue processedValue = new MultiValue(originalValue.size());
						for (Object o : originalValue) {
							processedValue.add(processValue(cellLog, function, o, node));
						}
						value = processedValue;
					}
					else {
						value = processValue(cellLog, function, value, node);
					}

					/*
					 * TODO
					 * 
					 * set node value only if no result has already been set. If
					 * a value is already there and we are in a lower priority
					 * executor, we do not overwrite.
					 */
					if (!node.isDefined()) {
						node.setResult(value);
					}
				}
			}
		}
	}

	/**
	 * Processes the given value. Does not handle {@link MultiValue}!
	 * 
	 * @param cellLog the transformation log
	 * @param function the property function
	 * @param value the value to process
	 * @param node the target node
	 * @return the processed value
	 */
	private Object processValue(TransformationLog cellLog, PropertyTransformation<?> function,
			Object value, TargetNode node) {
		if (function.allowAutomatedResultConversion()) {
			if (!(value instanceof Group)) {
				// convert value for target
				try {
					value = convert(value, toPropertyEntityDefinition(node.getEntityDefinition()));
				} catch (Throwable e) {
					// ignore, but create error
					cellLog.error(cellLog
							.createMessage(
									"Conversion according to target property failed, using value as is.",
									e));
				}
			}
			else {
				// TODO any conversion necessary/possible
			}
		}
		else {
			// unwrap value
			if (value instanceof Value) {
				value = ((Value) value).getValue();
			}
		}

		/*
		 * If the value is no group, but it should be one, create an instance
		 * wrapping the value
		 */
		TypeDefinition propertyType = toPropertyEntityDefinition(node.getEntityDefinition())
				.getDefinition().getPropertyType();
		if (!(value instanceof Group) && !propertyType.getChildren().isEmpty()) {
			MutableInstance instance = new DefaultInstance(propertyType, null);
			instance.setValue(value);
			value = instance;
		}
		return value;
	}

	/**
	 * Convert a value according to a target property entity definition.
	 * 
	 * @param value the value to convert
	 * @param propertyEntityDefinition the target property entity definition
	 * @return the converted object
	 * @throws ConversionException if an error occurs during conversion
	 */
	private Object convert(Object value, PropertyEntityDefinition propertyEntityDefinition)
			throws ConversionException {
		if (value == null) {
			return null;
		}

		PropertyDefinition def = propertyEntityDefinition.getDefinition();
		Binding binding = def.getPropertyType().getConstraint(Binding.class);
		Class<?> target = binding.getBinding();

		// special handling for Value
		if (value instanceof Value) {
			// try value's internal conversion
			Object result = ((Value) value).as(target);
			if (result != null) {
				return result;
			}
			else {
				// unwrap value
				value = ((Value) value).getValue();
				if (value == null) {
					return null;
				}
			}
		}

		if (target.isAssignableFrom(value.getClass())) {
			return value;
		}

		if (Collection.class.isAssignableFrom(target) && target.isAssignableFrom(List.class)) {
			// collection / list
			ElementType elementType = def.getPropertyType().getConstraint(ElementType.class);
			return ConversionUtil.getAsList(value, elementType.getBinding(), true);
		}

		// XXX what about a value that is a collection but the target is no
		// collection?

		return ConversionUtil.getAs(value, target);
	}

	/**
	 * Returns a {@link PropertyEntityDefinition} for a given entity definition.
	 * 
	 * @param def the entity definition
	 * @return the property entity definition
	 */
	private PropertyEntityDefinition toPropertyEntityDefinition(EntityDefinition def) {
		if (def instanceof PropertyEntityDefinition) {
			return (PropertyEntityDefinition) def;
		}

		return new PropertyEntityDefinition(def.getType(), new ArrayList<ChildContext>(
				def.getPropertyPath()), def.getSchemaSpace(), def.getFilter());
	}

	/**
	 * Set the current type cell. The value is stored in a {@link ThreadLocal}.
	 * 
	 * @param typeCell the current type cell
	 */
	public void setTypeCell(Cell typeCell) {
		this.typeCell.set(typeCell);
	}
}
