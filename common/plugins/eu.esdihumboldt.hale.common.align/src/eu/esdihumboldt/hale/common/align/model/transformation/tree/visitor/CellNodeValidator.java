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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor;

import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.util.Pair;

/**
 * Visitor that validates cell nodes. The tree should have already been
 * annotated with the source instance values when applying this visitor.
 * @author Simon Templer
 */
public class CellNodeValidator extends AbstractTargetToSourceVisitor {
	
	private static final ALogger log = ALoggerFactory.getLogger(CellNodeValidator.class);

	/**
	 * @see AbstractTargetToSourceVisitor#visit(CellNode)
	 */
	@Override
	public boolean visit(CellNode node) {
		// evaluate if for the cell all needed inputs are set
		Cell cell = node.getCell();
		
		// collect source and target nodes per entity name
		
		ListMultimap<String, Pair<SourceNode, Entity>> sources = ArrayListMultimap.create();
		if (cell.getSource() != null) {
			for (Entry<String, ? extends Entity> sourceEntry : cell.getSource().entries()) {
				String name = sourceEntry.getKey();
				Entity entity = sourceEntry.getValue();
				SourceNode sourceNode = findSourceNode(node, entity);
				if (sourceNode != null) {
					if (sourceNode.isDefined()) {
						sources.put(name, new Pair<SourceNode, Entity>(sourceNode, entity));
					}
				}
				else {
					log.error("Source node for entity not found.");
				}
			}
		}
		
		ListMultimap<String, Pair<TargetNode, Entity>> targets = ArrayListMultimap.create();
		for (Entry<String, ? extends Entity> targetEntry : cell.getTarget().entries()) {
			String name = targetEntry.getKey();
			Entity entity = targetEntry.getValue();
			TargetNode targetNode = findTargetNode(node, entity);
			if (targetNode != null) {
				targets.put(name, new Pair<TargetNode, Entity>(targetNode, entity));
			}
			else {
				log.error("Target node for entity not found.");
			}
		}
		
		boolean valid = validate(node, sources, targets);
		
		if (valid) {
			processValid(cell, sources, targets);
		}
		
		node.setValid(valid);
		
		// don't visit source nodes
		return false;
	}

	/**
	 * Validate a cell node.
	 * @param node the cell node
	 * @param sources the named source entities and nodes
	 * @param targets the named target entities and nodes
	 * @return if the cell node is valid for execution
	 */
	protected boolean validate(CellNode node,
			ListMultimap<String, Pair<SourceNode, Entity>> sources,
			ListMultimap<String, Pair<TargetNode, Entity>> targets) {
		String functionId = node.getCell().getTransformationIdentifier();
		AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
		if (function != null) {
			// check source node occurrence for mandatory source entities
			for (AbstractParameter sourceParam : function.getSource()) {
				int min = sourceParam.getMinOccurrence();
				if (sources.get(sourceParam.getName()).size() < min) {
					return false;
				}
			}
			
			//TODO additional validation?
			
			return true;
		}
		else {
			log.error("Invalid cell - function not found: " + functionId);
		}
		
		return false;
	}

	/**
	 * Process a valid cell node.
	 * @param cell the associated cell
	 * @param sources the named source entities and nodes
	 * @param targets the named target entities and nodes
	 */
	protected void processValid(Cell cell,
			ListMultimap<String, Pair<SourceNode, Entity>> sources,
			ListMultimap<String, Pair<TargetNode, Entity>> targets) {
		// override me
	}

	/**
	 * Find the matching target node for the given entity in a cell node.
	 * @param node the cell node the source node must be associated to
	 * @param entity the entity
	 * @return the target node or <code>null</code>
	 */
	private TargetNode findTargetNode(CellNode node, Entity entity) {
		for (TargetNode target : node.getTargets()) {
			if (target.getEntityDefinition().equals(entity.getDefinition())) {
				return target;
			}
		}
		
		return null;
	}

	/**
	 * Find the matching source node for the given entity in a cell node.
	 * @param node the cell node the source node must be associated to
	 * @param entity the entity
	 * @return the source node or <code>null</code>
	 */
	private SourceNode findSourceNode(CellNode node, Entity entity) {
		for (SourceNode source : node.getSources()) {
			if (source.getEntityDefinition().equals(entity.getDefinition())) {
				return source;
			}
		}
		return null;
	}

}
