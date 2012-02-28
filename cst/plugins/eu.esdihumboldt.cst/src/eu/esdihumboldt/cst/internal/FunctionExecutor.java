/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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

import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationFactory;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.CellNodeValidator;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.CellLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.TransformationMessageImpl;
import eu.esdihumboldt.hale.common.convert.ConversionUtil;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;
import eu.esdihumboldt.util.Pair;

/**
 * Function executor on a transformation tree.
 * @author Simon Templer
 */
public class FunctionExecutor extends CellNodeValidator {

	private final TransformationReporter reporter;
	private final EngineManager engines;
	private final PropertyTransformationExtension transformations;

	/**
	 * Create a function executor.
	 * @param reporter the transformation reporter
	 * @param engines the transformation engine manager
	 */
	public FunctionExecutor(TransformationReporter reporter, 
			EngineManager engines) {
		this.reporter = reporter;
		this.engines = engines;
		
		this.transformations = PropertyTransformationExtension.getInstance();
	}

	/**
	 * @see CellNodeValidator#processValid(Cell, ListMultimap, ListMultimap)
	 */
	@Override
	protected void processValid(Cell cell,
			ListMultimap<String, Pair<SourceNode, Entity>> sources,
			ListMultimap<String, Pair<TargetNode, Entity>> targets) {
		String functionId = cell.getTransformationIdentifier();
		
		List<PropertyTransformationFactory> transformations = 
				this.transformations.getTransformations(functionId);
		
		if (transformations == null || transformations.isEmpty()) {
			reporter.error(new TransformationMessageImpl(cell, 
					MessageFormat.format("No transformation for function {0} found. Skipping property transformation.",
							functionId), null));
		}
		else {
			//TODO select based on e.g. preferred transformation engine?
			PropertyTransformationFactory transformation = transformations.iterator().next();
			
			executeTransformation(transformation, cell, sources, targets);
		}
	}

	/**
	 * Execute a property transformation.
	 * @param transformation the transformation factory
	 * @param cell the alignment cell
	 * @param sources the named source entities and nodes
	 * @param targets the named target entities and nodes
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void executeTransformation(
			PropertyTransformationFactory transformation, Cell cell,
			ListMultimap<String, Pair<SourceNode, Entity>> sources,
			ListMultimap<String, Pair<TargetNode, Entity>> targets) {
		TransformationLog cellLog = new CellLog(reporter, cell); 
		
		PropertyTransformation<?> function;
		try {
			//TODO cache function objects?
			function = transformation.createExtensionObject();
		} catch (Exception e) {
			cellLog.error(cellLog.createMessage(
					"Error creating transformation function.", e));
			return;
		}
		
		TransformationEngine engine = engines.get(
				transformation.getEngineId(), cellLog);
		
		if (engine == null) {
			//TODO instead try another transformation
			cellLog.error(cellLog.createMessage(
					"Skipping property transformation: No matching transformation engine found", null));
			return;
		}
		
		// configure function
		
		// set expected result
		ListMultimap<String, PropertyEntityDefinition> expectedResult = 
				ArrayListMultimap.create(targets.keySet().size(), 1);
		for (Entry<String, Pair<TargetNode, Entity>> targetEntry : targets.entries()) {
			EntityDefinition def = targetEntry.getValue().getSecond().getDefinition();
			expectedResult.put(targetEntry.getKey(), toPropertyEntityDefinition(def));
		}
		function.setExpectedResult(expectedResult);
		
		// set source variables
		ListMultimap<String, PropertyValue> variables = ArrayListMultimap.create();
		for (Entry<String, Pair<SourceNode, Entity>> sourceEntry : sources.entries()) {
			EntityDefinition def = sourceEntry.getValue().getSecond().getDefinition();
			Object value = sourceEntry.getValue().getFirst().getValue();
			PropertyValue propertyValue = new PropertyValueImpl(value, 
					toPropertyEntityDefinition(def));
			variables.put(sourceEntry.getKey(), propertyValue );
		}
		function.setVariables(variables);
		
		// set parameters
		function.setParameters(cell.getTransformationParameters());
		
		// execute function
		try {
			((PropertyTransformation) function).execute(
					transformation.getIdentifier(), 
					engine, 
					transformation.getExecutionParameters(), 
					cellLog);
		} catch (Throwable e) {
			//TODO instead try another transformation?
			cellLog.error(cellLog.createMessage(
					"Skipping property transformation: Executing property transformation failed.", e));
			return;
		}
		
		// apply function results
		ListMultimap<String, Object> results = function.getResults();
		if (results != null) {
			for (String name : results.keySet()) {
				List<Object> values = results.get(name);
				List<Pair<TargetNode, Entity>> nodes = targets.get(name);
				
				int count = Math.min(values.size(), nodes.size());
				
				if (count > values.size()) {
					cellLog.warn(cellLog.createMessage(MessageFormat.format(
							"Transformation result misses values for result with name {0}",
							name), null));
				}
				if (count > nodes.size()) {
					cellLog.warn(cellLog.createMessage(MessageFormat.format(
							"More transformation results than target nodes for result with name {0}",
							name), null));
				}
				
				for (int i = 0; i < count; i++) {
					Object value = values.get(i);
					TargetNode node = nodes.get(i).getFirst();
					
					if (function.allowAutomatedResultConversion()) {
						// convert value for target
						try {
							value = convert(value, toPropertyEntityDefinition(
									node.getEntityDefinition()));
						} catch (Throwable e) {
							// ignore, but create error
							cellLog.error(cellLog.createMessage(
									"Conversion according to target property failed, using value as is.", e));
						}
					}
					
					/*
					 * If the value is no group, but it should be one, create
					 * an instance wrapping the value 
					 */
					TypeDefinition propertyType = toPropertyEntityDefinition(
							node.getEntityDefinition()).getDefinition()
							.getPropertyType();
					if (!(value instanceof Group)
							&& !propertyType .getChildren().isEmpty()) {
						MutableInstance instance = new OInstance(propertyType, null);
						instance.setValue(value);
						value = instance;
					}
					
					// set node value
					node.setResult(value);
				}
			}
		}
	}

	/**
	 * Convert a value according to a target property entity definition.
	 * @param value the value to convert
	 * @param propertyEntityDefinition the target property entity definition
	 * @return the converted object
	 * @throws ConversionException if an error occurs during conversion
	 */
	private Object convert(Object value,
			PropertyEntityDefinition propertyEntityDefinition) throws ConversionException {
		if (value == null) {
			return null;
		}
		
		PropertyDefinition def = propertyEntityDefinition.getDefinition();
		Binding binding = def.getPropertyType().getConstraint(Binding.class);
		Class<?> target = binding.getBinding();
		
		if (target.isAssignableFrom(value.getClass())) {
			return value;
		}
		
		if (Collection.class.isAssignableFrom(target) && target.isAssignableFrom(List.class)) {
			// collection / list
			ElementType elementType = def.getPropertyType().getConstraint(ElementType.class);
			return ConversionUtil.getAsList(value, elementType.getBinding(), true);
		}
		
		//XXX what about a value that is a collection but the target is no collection?
		
		return ConversionUtil.getAs(value, target);
	}

	/**
	 * Returns a {@link PropertyEntityDefinition} for a given entity definition.
	 * @param def the entity definition
	 * @return the property entity definition
	 */
	private PropertyEntityDefinition toPropertyEntityDefinition(
			EntityDefinition def) {
		if (def instanceof PropertyEntityDefinition) {
			return (PropertyEntityDefinition) def;
		}
		
		return new PropertyEntityDefinition(def.getType(), 
				new ArrayList<ChildContext>(def.getPropertyPath()), 
				def.getSchemaSpace(), def.getFilter());
	}
	
}
