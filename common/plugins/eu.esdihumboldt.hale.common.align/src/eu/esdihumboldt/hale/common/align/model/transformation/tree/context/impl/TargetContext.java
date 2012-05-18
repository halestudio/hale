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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.GroupNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.Leftovers;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.TransformationContext;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.CellNodeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.SourceNodeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TargetNodeImpl;
import eu.esdihumboldt.util.Pair;

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
		
		private final List<Pair<CellNodeImpl, CellNode>> cellNodePairs = new ArrayList<Pair<CellNodeImpl,CellNode>>();
		
		private final Map<EntityDefinition, TargetNodeImpl> targetNodes = new HashMap<EntityDefinition, TargetNodeImpl>();
		
		private final List<Pair<TargetNodeImpl, TargetNode>> targetNodePairs = new ArrayList<Pair<TargetNodeImpl,TargetNode>>();
		
		private final Set<Cell> ignoreCells;
		
		/**
		 * Create a duplication context
		 * @param ignoreCells the cells to be ignored
		 */
		public DuplicationContext(Set<Cell> ignoreCells) {
			super();
			if (ignoreCells != null) {
				this.ignoreCells = Collections.unmodifiableSet(ignoreCells);
			}
			else {
				this.ignoreCells = Collections.emptySet();
			}
		}

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
		 * @param originalCell the original cell node where the cell node was 
		 *   duplicated from
		 */
		public void addNode(CellNodeImpl cellNode, CellNode originalCell) {
			cellNodes.put(cellNode.getCell(), cellNode);
			cellNodePairs.add(new Pair<CellNodeImpl, CellNode>(cellNode, originalCell));
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
		 * @param originalTarget the original target node where the target node
		 *   was duplicated from
		 */
		public void addNode(TargetNodeImpl targetNode, TargetNode originalTarget) {
			targetNodes.put(targetNode.getEntityDefinition(), targetNode);
			targetNodePairs.add(new Pair<TargetNodeImpl, TargetNode>(targetNode, originalTarget));
		}

		/**
		 * Get the cells to be ignored during duplication. 
		 * @return the cells to be ignored
		 */
		public Set<Cell> getIgnoreCells() {
			return ignoreCells;
		}

		/**
		 * Get cell nodes that have incomplete sources compared to the original.
		 * @return the incomplete cell node paired with the original cell node
		 *   it was duplicated from
		 */
		public Collection<Pair<CellNodeImpl, CellNode>> getIncompleteCellNodes() {
			return Collections2.filter(cellNodePairs, new Predicate<Pair<CellNodeImpl, CellNode>>() {
				@Override
				public boolean apply(Pair<CellNodeImpl, CellNode> input) {
					CellNodeImpl duplicate = input.getFirst();
					CellNode original = input.getSecond();
					
					return original.getSources().size() > duplicate.getSources().size();
				}
			});
		}
		
		/**
		 * Get target nodes that have incomplete children or assignments 
		 * compared to the original.
		 * @return the incomplete target node paired with the original target
		 *   node it was duplicated from
		 */
		public Collection<Pair<TargetNodeImpl, TargetNode>> getIncompleteTargetNodes() {
			return new ArrayList<Pair<TargetNodeImpl,TargetNode>>(Collections2.filter(
					targetNodePairs, new Predicate<Pair<TargetNodeImpl, TargetNode>>() {
				@Override
				public boolean apply(Pair<TargetNodeImpl, TargetNode> input) {
					TargetNodeImpl duplicate = input.getFirst();
					TargetNode original = input.getSecond();
					
					return original.getChildren(true).size() > duplicate.getChildren(true).size()
							|| original.getAssignments().size() > duplicate.getAssignments().size();
				}
			}));
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

//	/**
//	 * @see TransformationContext#duplicateContext(SourceNode, Object)
//	 */
//	@Override
//	public void duplicateContext(SourceNode contextSource, Object value) {
//		// create a new duplication context
//		DuplicationContext duplicationContext = new DuplicationContext();
//		
//		// duplicate context source
//		SourceNode source = duplicateSource(contextSource, 
//				contextSource.getParent(), false, duplicationContext);
//		
//		if (source != null) {
//			// add duplicated source as annotation to context source parent
//			contextSource.getParent().addAnnotatedChild(source);
//			
//			// apply value to source
//			if (value instanceof Group) {
//				// value is a group, duplication may again be necessary for its properties
//				InstanceVisitor visitor = new InstanceVisitor((Group) value);
//				source.accept(visitor);
//			}
//			else {
//				// value is a simple value
//				source.setValue(value);
//			}
//		}
//	}

	/**
	 * @see TransformationContext#duplicateContext(SourceNode, SourceNode, Set)
	 */
	@Override
	public void duplicateContext(SourceNode originalSource,
			SourceNode duplicate, Set<Cell> ignoreCells) {
		// create a new duplication context
		DuplicationContext duplicationContext = new DuplicationContext(ignoreCells);
		
		// configure duplicate, but don't add to parent (as it is already added as annotated child)
		configureSourceDuplicate(originalSource, duplicate, 
				originalSource.getParent(), duplicationContext, false);
		
		// track back to sources from cells where sources are missing
		for (Pair<CellNodeImpl, CellNode> cellPair : duplicationContext.getIncompleteCellNodes()) {
			CellNodeImpl cellNode = cellPair.getFirst();
			CellNode originalCell = cellPair.getSecond();
			
			cellTrackback(cellNode, originalCell);
		}
		
		// track back from targets where augmentations are missing
		for (Pair<TargetNodeImpl, TargetNode> targetPair : duplicationContext.getIncompleteTargetNodes()) {
//			TargetNodeImpl targetNode = targetPair.getFirst();
			TargetNode originalTarget = targetPair.getSecond();
			
			augmentationTrackback(originalTarget, duplicationContext);
		}
	}

	/**
	 * Track back target nodes and duplicate any augmentation cells.
	 * @param originalTarget the original target node
	 * @param duplicationContext the duplication context
	 */
	private void augmentationTrackback(TargetNode originalTarget, 
			DuplicationContext duplicationContext) {
		// track back child augmentations
		for (TargetNode child : originalTarget.getChildren(false)) { //XXX should annotated children be included?
			augmentationTrackback(child, duplicationContext);
		}
		
		// track back augmentations
		for (CellNode originalAssignment : originalTarget.getAssignments()) {
			/*
			 * Duplicated target does not contain an assignment representing
			 * the same cell as originalAssignment. 
			 */
			if (originalAssignment.getSources().isEmpty()) {
				// the cell is an augmentation, thus we duplicate it
				duplicateCell(originalAssignment, null, duplicationContext);
				/*
				 * it is automatically added to the target nodes (which are 
				 * retrieved from the duplication context or created as
				 * necessary)
				 */
			}
		}
	}

	/**
	 * Track the graph back to sources that are missing in a cell node compared
	 * to the original cell node.
	 * @param cellNode the cell node
	 * @param originalCell the original cell node the node was duplicated from
	 */
	private void cellTrackback(CellNodeImpl cellNode, CellNode originalCell) {
		for (SourceNode originalSource : originalCell.getSources()) {
			if (!cellNode.getSources().contains(originalSource)) {
				/*
				 * Duplicated cell does not contain a source representing the
				 * same entity as originalSource.
				 */
				SourceNode newSource = null;
				
				// now there are several possible cases
				// a) the original source has leftovers and we grab one
				Leftovers leftovers = originalSource.getLeftovers();
				if (leftovers != null) {
					newSource = leftovers.consumeValue(originalCell.getCell());
					
					if (newSource != null) {
						// interconnect both
						newSource.addRelation(cellNode);
						cellNode.addSource(originalCell.getSourceNames(originalSource), 
								newSource);
						//XXX hard connections are OK here, as a leftover source is a duplicate
					}
					else {
						//TODO add an undefined source node in this case?
					}
				}
				
				// b) the original source has a parent (ot it has a parent etc.)
				//    that has leftovers
				if (newSource == null) {
					//TODO
				}
				
				// c) we use the original source node
				if (newSource == null) {
					newSource = originalSource;
					
					// interconnect both
					newSource.addAnnotatedRelation(cellNode); //FIXME should be an augmentated relation!!!!
					cellNode.addSource(originalCell.getSourceNames(originalSource), 
							newSource);
				}
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
		
		return configureSourceDuplicate(source, duplicate, parent, 
				duplicationContext, addToParent);
	}

	/**
	 * Configure a duplicated source node.
	 * @param originalSource the original source node
	 * @param duplicate the duplicated source node
	 * @param parent the parent for the duplicated source node
	 * @param duplicationContext the duplication context
	 * @param addToParent if the duplicated source node should be added as child
	 *   to its parent
	 * @return the duplicated source node or <code>null</code> if it has no
	 *   further connections
	 */
	private SourceNode configureSourceDuplicate(SourceNode originalSource,
			SourceNode duplicate, SourceNode parent, 
			DuplicationContext duplicationContext,
			boolean addToParent) {
		// duplicate relations
		List<CellNode> relations = new ArrayList<CellNode>(originalSource.getRelations(false).size());
		// though each cell node is only duplicated once per duplication context
		for (CellNode relation : originalSource.getRelations(false)) { //XXX should the annotated relations be included?
			CellNode duplicatedRelation = duplicateCell(relation, duplicate,
					duplicationContext);
			if (duplicatedRelation != null) {
				relations.add(duplicatedRelation);
			}
		}
		
		// duplicate children
		List<SourceNode> children = new ArrayList<SourceNode>(originalSource.getChildren(false).size());
		for (SourceNode child : originalSource.getChildren(false)) { //XXX should the annotated children be included?
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
		
		if (addToParent) {
			parent.addChild(duplicate);
		}
		
		return duplicate;
	}

	/**
	 * Get the duplicated cell node.
	 * @param relation the original cell node
	 * @param duplicateSource the duplicated source node to be associated with
	 *  the duplicated cell node, may be <code>null</code> if the cell is an
	 *  augmentation
	 * @param duplicationContext the context of the current duplication process
	 * @return the duplicated cell node or <code>null</code> if duplication was
	 *   prohibited
	 */
	private CellNode duplicateCell(CellNode relation, SourceNode duplicateSource,
			DuplicationContext duplicationContext) {
		if (duplicationContext.getIgnoreCells().contains(relation.getCell())) {
			// cancel if the cell has to be ignored (which usually means it was already handled)
			return null;
		}
		
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
			duplicationContext.addNode(duplicate, relation);
		}
		
		// add the duplicated source
		if (duplicateSource != null) {
			duplicate.addSource(relation.getSourceNames(duplicateSource), duplicateSource);
		}
		
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
					if (duplicatedParent == null) {
						return null;
					}
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
			
			// add to duplication context
			duplicationContext.addNode(duplicatedTarget, target);
		}
		
		if (relation != null) {
			// assign relation
			duplicatedTarget.addAssignment(
					target.getAssignmentNames(relation), relation);
		}
		
		return duplicatedTarget;
	}

}
