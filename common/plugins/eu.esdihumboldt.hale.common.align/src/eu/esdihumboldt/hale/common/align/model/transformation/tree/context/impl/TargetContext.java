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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.GroupNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.TransformationContext;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.CellNodeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.SourceNodeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TargetNodeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.InstanceVisitor;
import eu.esdihumboldt.hale.common.instance.model.Group;

/**
 * Transformation context that duplicates subgraphs leading to certain target
 * nodes.
 * @author Simon Templer
 */
public class TargetContext implements TransformationContext {
	
	/**
	 * Context of a duplication process. It serves to ensure that each cell or 
	 * target node is only created once per context duplication. 
	 */
	public static class DuplicationContext {
		
		private final Map<Cell, CellNodeImpl> cellNodes = new HashMap<Cell, CellNodeImpl>();
		
		private final Map<EntityDefinition, TargetNodeImpl> targetNodes = new HashMap<EntityDefinition, TargetNodeImpl>();
		
		/**
		 * Get the cell node associated to the given cell.
		 * @param cell the cell
		 * @return the cell node or <code>null</code> if none has yet been 
		 *   associated to the cell
		 */
		public CellNodeImpl getNode(Cell cell) {
			return cellNodes.get(cell);
		}
		
		/**
		 * Add a node to the duplication context.
		 * @param cellNode the cell node to add to the duplication context
		 */
		public void addNode(CellNodeImpl cellNode) {
			cellNodes.put(cellNode.getCell(), cellNode);
		}
		
		/**
		 * Get the target node associated to the given entity.
		 * @param entity the entity
		 * @return the target node or <code>null</code> if none has yet been 
		 *   associated to the entity
		 */
		public TargetNodeImpl getNode(EntityDefinition entity) {
			return targetNodes.get(entity);
		}
		
		/**
		 * Add a node to the duplication context.
		 * @param targetNode the target node to add to the duplication context
		 */
		public void addNode(TargetNodeImpl targetNode) {
			targetNodes.put(targetNode.getEntityDefinition(), targetNode);
		}

	}

	private final Set<TargetNode> contextTargets;

	/**
	 * Create a transformation context that duplicates subgraphs leading to 
	 * the given target nodes.
	 * @param contextTargets the target nodes to use as subgraph end-points
	 */
	public TargetContext(Set<TargetNode> contextTargets) {
		super();
		this.contextTargets = contextTargets;
	}

	/**
	 * @see TransformationContext#duplicateContext(SourceNode, Object)
	 */
	@Override
	public void duplicateContext(SourceNode contextSource, Object value) {
		// create a new duplication context
		DuplicationContext duplicationContext = new DuplicationContext();
		
		// duplicate context source
		SourceNode source = duplicateSource(contextSource, 
				contextSource.getParent(), false, duplicationContext);
		
		if (source != null) {
			// add duplicated source as annotation to context source parent
			contextSource.getParent().addAnnotatedChild(source);
			
			// apply value to source
			if (value instanceof Group) {
				// value is a group, duplication may again be necessary for its properties
				InstanceVisitor visitor = new InstanceVisitor((Group) value);
				source.accept(visitor);
			}
			else {
				// value is a simple value
				source.setValue(value);
			}
		}
	}

	/**
	 * Duplicate a source node.
	 * @param source the source node to duplicate
	 * @param parent the parent of the new source node
	 * @param addToParent if the new source node should be added as child to the
	 *   parent
	 * @param duplicationContext the duplication context
	 * @return the new duplicated source node or <code>null</code> if 
	 *   duplication was prohibited
	 */
	private SourceNode duplicateSource(SourceNode source,
			SourceNode parent, boolean addToParent, 
			DuplicationContext duplicationContext) {
		// create duplicate
		SourceNode duplicate = new SourceNodeImpl(source.getEntityDefinition(), 
				parent, addToParent);
		duplicate.setContext(source.getContext());
		
		// duplicate relations
		List<CellNode> relations = new ArrayList<CellNode>(source.getRelations().size());
		// though each cell node is only duplicated once per duplication context
		for (CellNode relation : source.getRelations()) {
			CellNode duplicatedRelation = duplicateCell(relation, duplicate,
					duplicationContext);
			if (duplicatedRelation != null) {
				relations.add(duplicatedRelation);
			}
		}
		
		// duplicate children
		List<SourceNode> children = new ArrayList<SourceNode>(source.getChildren(false).size());
		for (SourceNode child : source.getChildren(false)) { //XXX should the annotated children be included?
			SourceNode duplicatedChild = duplicateSource(child, duplicate, 
					true, duplicationContext);
			if (duplicatedChild != null) {
				children.add(duplicatedChild);
			}
		}
		
		if (children.isEmpty() && relations.isEmpty()) {
			// abort 
			return null;
		}
		
		// add duplicated relations
		for (CellNode relation : relations) {
			duplicate.addRelation(relation);
		}
		
		// add duplicated children
		for (SourceNode child : children) {
			duplicate.addChild(child);
		}
		
		return duplicate;
	}

	/**
	 * Get the duplicated cell node.
	 * @param relation the original cell node
	 * @param duplicateSource the duplicated source node to be associated with
	 *  the duplicated cell node
	 * @param duplicationContext the context of the current duplication process
	 * @return the duplicated cell node or <code>null</code> if duplication was
	 *   prohibited
	 */
	private CellNode duplicateCell(CellNode relation, SourceNode duplicateSource,
			DuplicationContext duplicationContext) {
		// try to retrieve cell node from context
		CellNodeImpl duplicate = duplicationContext.getNode(relation.getCell());
		
		if (duplicate == null) {
			// if not already done, create a duplicate of the cell
			duplicate = new CellNodeImpl(relation.getCell());
			
			// duplicate the target nodes as necessary
			List<TargetNode> targets = new ArrayList<TargetNode>(relation.getTargets().size());
			for (TargetNode target : relation.getTargets()) {
				TargetNode duplicatedTarget = duplicateTarget(target, duplicate,
						duplicationContext);
				if (duplicatedTarget != null) {
					targets.add(duplicatedTarget);
				}
			}
			
			if (targets.isEmpty()) {
				// exit if there are no targets nodes
				return null;
				//FIXME if a cell has different associated sources the cell node will be repeatedly created and discarded 
			}
			
			// add duplicated targets to duplicated cell
			for (TargetNode target : targets) {
				duplicate.addTarget(target);
			}
			
			// store duplicate in duplication context
			duplicationContext.addNode(duplicate);
		}
		
		// add the duplicated source
		duplicate.addSource(relation.getSourceNames(duplicateSource), duplicateSource);
		
		return duplicate;
	}

	/**
	 * Duplicate a target node.
	 * @param target the original target node
	 * @param relation the relation to associated to the target node
	 * @param duplicationContext the duplication context
	 * @return the duplicated target node or <code>null</code> if duplication
	 *   was prohibited
	 */
	private TargetNode duplicateTarget(TargetNode target, CellNode relation,
			DuplicationContext duplicationContext) {
		TargetNodeImpl duplicatedTarget = duplicationContext.getNode(
				target.getEntityDefinition());
		
		if (duplicatedTarget == null) {
			// target node not created yet
			
			boolean duplicateParent = true;
			if (contextTargets.contains(target)) {
				// this is an endpoint, as such this is the last node to be duplicated
				duplicateParent = false;
			}
			
			GroupNode duplicatedParent;
			if (duplicateParent) {
				GroupNode parent = target.getParent();
				if (parent instanceof TargetNode) {
					// create a duplicated parent
					duplicatedParent = duplicateTarget((TargetNode) parent, 
							null, duplicationContext);
				}
				else {
					// parent is either null or the root
					// this means there was no match for a context endpoint along the way
					// thus this is not a valid path for duplication
					return null;
				}
			}
			else {
				duplicatedParent = target.getParent();
			}
			
			// create duplicate
			duplicatedTarget = new TargetNodeImpl(
					target.getEntityDefinition(), duplicatedParent);
			
			// add as annotated child to parent
			duplicatedParent.addAnnotatedChild(duplicatedTarget);
			
			//TODO create child duplicates? XXX for Augmentations!
			
			// add to duplication context
			duplicationContext.addNode(duplicatedTarget);
		}
		
		if (relation != null) {
			// assign relation
			duplicatedTarget.addAssignment(
					target.getAssignmentNames(relation), relation);
		}
		
		return duplicatedTarget;
	}

}
