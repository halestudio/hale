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
import java.util.List;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationFactory;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.CellNodeValidator;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.CellLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.TransformationMessageImpl;
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
		
		//TODO configure function
//		function.setParameters(parameters);
//		function.setSource(sourceProperties, sourceInstance);
//		function.setTarget(targetProperties, targetInstance);
		//TODO execute function
//		function.execute(transformationIdentifier, engine, executionParameters, log);
		//TODO apply function results
		//XXX
	}
	
}
