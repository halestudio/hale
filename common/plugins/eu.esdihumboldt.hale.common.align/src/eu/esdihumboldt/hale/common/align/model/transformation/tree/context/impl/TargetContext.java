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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.GroupNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.TransformationContext;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.CellNodeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.SourceNodeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TargetNodeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractSourceToTargetVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTargetToSourceVisitor;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.util.IdentityWrapper;
import eu.esdihumboldt.util.Pair;

/**
 * Transformation context that duplicates subgraphs leading to certain target
 * nodes.
 * 
 * @author Simon Templer
 */
public class TargetContext implements TransformationContext {

	/**
	 * Context of a duplication process. It serves to ensure that each cell or
	 * target node is only created once per context duplication.
	 */
	public static class DuplicationContext {

		private final Map<Cell, CellNodeImpl> cellNodes = new HashMap<Cell, CellNodeImpl>();

		private final List<Pair<CellNodeImpl, CellNode>> cellNodePairs = new ArrayList<Pair<CellNodeImpl, CellNode>>();

		private final Map<EntityDefinition, TargetNodeImpl> targetNodes = new HashMap<EntityDefinition, TargetNodeImpl>();

		private final List<Pair<TargetNodeImpl, TargetNode>> targetNodePairs = new ArrayList<Pair<TargetNodeImpl, TargetNode>>();

		private final Set<Cell> ignoreCells;

		/**
		 * Create a duplication context
		 * 
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
		 * 
		 * @param cell the cell
		 * @return the cell node or <code>null</code> if none has yet been
		 *         associated to the cell
		 */
		public CellNodeImpl getNode(Cell cell) {
			return cellNodes.get(cell);
		}

		/**
		 * Add a node to the duplication context.
		 * 
		 * @param cellNode the cell node to add to the duplication context
		 * @param originalCell the original cell node where the cell node was
		 *            duplicated from
		 */
		public void addNode(CellNodeImpl cellNode, CellNode originalCell) {
			cellNodes.put(cellNode.getCell(), cellNode);
			cellNodePairs.add(new Pair<CellNodeImpl, CellNode>(cellNode, originalCell));
		}

		/**
		 * Get the target node associated to the given entity.
		 * 
		 * @param entity the entity
		 * @return the target node or <code>null</code> if none has yet been
		 *         associated to the entity
		 */
		public TargetNodeImpl getNode(EntityDefinition entity) {
			return targetNodes.get(entity);
		}

		/**
		 * Add a node to the duplication context.
		 * 
		 * @param targetNode the target node to add to the duplication context
		 * @param originalTarget the original target node where the target node
		 *            was duplicated from
		 */
		public void addNode(TargetNodeImpl targetNode, TargetNode originalTarget) {
			targetNodes.put(targetNode.getEntityDefinition(), targetNode);
			targetNodePairs.add(new Pair<TargetNodeImpl, TargetNode>(targetNode, originalTarget));
		}

		/**
		 * Get the cells to be ignored during duplication.
		 * 
		 * @return the cells to be ignored
		 */
		public Set<Cell> getIgnoreCells() {
			return ignoreCells;
		}

		/**
		 * Get cell nodes that have incomplete sources compared to the original.
		 * 
		 * @return the incomplete cell node paired with the original cell node
		 *         it was duplicated from
		 */
		public Collection<Pair<CellNodeImpl, CellNode>> getIncompleteCellNodes() {
			return Collections2.filter(cellNodePairs,
					new Predicate<Pair<CellNodeImpl, CellNode>>() {

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
		 * 
		 * @return the incomplete target node paired with the original target
		 *         node it was duplicated from
		 */
		public Collection<Pair<TargetNodeImpl, TargetNode>> getIncompleteTargetNodes() {
			return new ArrayList<Pair<TargetNodeImpl, TargetNode>>(Collections2
					.filter(targetNodePairs, new Predicate<Pair<TargetNodeImpl, TargetNode>>() {

						@Override
						public boolean apply(Pair<TargetNodeImpl, TargetNode> input) {
							TargetNodeImpl duplicate = input.getFirst();
							TargetNode original = input.getSecond();

							return original.getChildren(true).size() > duplicate.getChildren(true)
									.size()
									|| original.getAssignments().size() > duplicate.getAssignments()
											.size();
						}
					}));
		}

	}

	private static class DuplicationInformation {

		private final SourceNode duplicatedNode;
		private final Set<Cell> ignoreCells;
		private final Set<TargetNode> contextTargets;

		// Existing cell nodes for this duplication.
		private final Multimap<Cell, IdentityWrapper<CellNode>> oldCellNodes;
		// Existing target nodes for this duplication.
		// All nodes needed!
		// For example "T(a*, b*) -> T(x(a, b)*)": duplication of "a" leads to
		// multiple "x"s, which should be used one after another for the "b"s.
		private final Multimap<EntityDefinition, IdentityWrapper<TargetNode>> oldTargetNodes;
		// Existing source nodes for this duplication.
		private final Multimap<EntityDefinition, SourceNode> oldSourceNodes;
		// Created cell nodes in this duplication. A cell node is only created
		// once in a duplication!
		private final Map<Cell, CellNodeImpl> newCellNodes;
		// Created target nodes in this duplication. XXX Are multiple created
		// target nodes of the same definition important?
		private final Multimap<EntityDefinition, TargetNode> newTargetNodes;

		/**
		 * Create a duplication information.
		 * 
		 * @param duplicatedNode the duplicated node this is all about
		 * @param ignoreCells the cells to be ignored
		 * @param contextTargets the target nodes that can be used as subgraph
		 *            end-points
		 */
		DuplicationInformation(SourceNode duplicatedNode, Set<Cell> ignoreCells,
				Set<TargetNode> contextTargets) {
			this.duplicatedNode = duplicatedNode;
			if (ignoreCells != null)
				this.ignoreCells = Collections.unmodifiableSet(ignoreCells);
			else
				this.ignoreCells = Collections.emptySet();
			this.contextTargets = contextTargets;
			oldCellNodes = LinkedHashMultimap.create();
			oldTargetNodes = LinkedHashMultimap.create();
			oldSourceNodes = ArrayListMultimap.create();
			newCellNodes = new HashMap<Cell, CellNodeImpl>();
			newTargetNodes = ArrayListMultimap.create();
		}

		/**
		 * Returns the node that this duplication is all about.
		 * 
		 * @return the duplicated node
		 */
		public SourceNode getDuplicatedNode() {
			return duplicatedNode;
		}

		/**
		 * Checks whether the given cell is in the ignored cells.
		 * 
		 * @param cell the cell to check
		 * @return true, if the given cell is in the ignored cells, false
		 *         otherwise
		 */
		boolean isIgnoreCell(Cell cell) {
			return ignoreCells.contains(cell);
		}

		/**
		 * Adds the given target node to the map of existing nodes.
		 * 
		 * @param entityDef the entity definition
		 * @param target the target node
		 */
		void addOldTargetNode(EntityDefinition entityDef, TargetNode target) {
			oldTargetNodes.put(entityDef, new IdentityWrapper<>(target));
		}

		/**
		 * Adds the given source node to the map of existing nodes.
		 * 
		 * @param entityDef the entity definition
		 * @param source the source node
		 */
		void addOldSourceNode(EntityDefinition entityDef, SourceNode source) {
			oldSourceNodes.put(entityDef, source);
		}

		/**
		 * Adds the given cell node to the map of existing nodes.
		 * 
		 * @param cell the cell
		 * @param cellNode the cell node
		 */
		void addOldCellNode(Cell cell, CellNode cellNode) {
			oldCellNodes.put(cell, new IdentityWrapper<>(cellNode));
		}

		/**
		 * Adds the given target node to the map of newly created nodes.
		 * 
		 * @param entityDef the entity definition
		 * @param target the target node
		 */
		void addNewTargetNode(EntityDefinition entityDef, TargetNode target) {
			newTargetNodes.put(entityDef, target);
		}

		/**
		 * Adds the given cell node to the map of newly created nodes.
		 * 
		 * @param cell the cell
		 * @param cellNode the cell node
		 */
		void addNewCellNode(Cell cell, CellNodeImpl cellNode) {
			newCellNodes.put(cell, cellNode);
		}

		/**
		 * Returns a collection of existing cell nodes with the given cell.
		 * 
		 * @param cell the cell
		 * @return a collection of existing cell nodes, the collection uses the
		 *         objects identity instead of equals
		 */
		Collection<IdentityWrapper<CellNode>> getOldCellNodes(Cell cell) {
			return oldCellNodes.get(cell);
		}

		/**
		 * Returns a newly created cell node with the given cell.
		 * 
		 * @param cell the cell
		 * @return a newly created cell node or null
		 */
		CellNodeImpl getNewCellNode(Cell cell) {
			return newCellNodes.get(cell);
		}

		/**
		 * Returns a collection of existing source nodes with the given entity
		 * definition.
		 * 
		 * @param entityDef the entity definition
		 * @return a collection of existing source nodes, the collection uses
		 *         the objects identity instead of equals
		 */
		Collection<SourceNode> getOldSourceNodes(EntityDefinition entityDef) {
			return oldSourceNodes.get(entityDef);
		}

		/**
		 * Returns all target nodes of the given definition. First newly
		 * created, then existing ones.
		 * 
		 * @param entityDef the entity definition
		 * @return existing target nodes
		 */
		Iterable<TargetNode> getAllTargetNodes(EntityDefinition entityDef) {
			Collection<TargetNode> newTargets = newTargetNodes.get(entityDef);
			Collection<TargetNode> oldTargets = Collections2.transform(
					oldTargetNodes.get(entityDef),
					new Function<IdentityWrapper<TargetNode>, TargetNode>() {

						@Override
						public TargetNode apply(IdentityWrapper<TargetNode> input) {
							return input.getValue();
						}
					});
			if (newTargets.isEmpty())
				return oldTargets;
			if (oldTargets.isEmpty())
				return newTargets;
			return Iterables.concat(newTargets, oldTargets);
		}

		/**
		 * Returns existing target nodes of the given definition.
		 * 
		 * @param entityDef the entity definition
		 * @return existing target nodes, the collection uses the objects
		 *         identity instead of equals
		 */
		Collection<IdentityWrapper<TargetNode>> getOldTargetNodes(EntityDefinition entityDef) {
			return oldTargetNodes.get(entityDef);
		}

		/**
		 * Returns the target nodes that can be used as subgraph end-points.
		 * 
		 * @return the target nodes that can be used as subgraph end-points
		 */
		public Set<TargetNode> getContextTargets() {
			return contextTargets;
		}
	}

	private final Set<TargetNode> contextTargets;
	private final ServiceProvider serviceProvider;

	/**
	 * Create a transformation context that duplicates subgraphs leading to
	 * given target nodes.
	 * 
	 * @param serviceProvider the service provider
	 */
	public TargetContext(ServiceProvider serviceProvider) {
		super();
		this.contextTargets = new HashSet<TargetNode>();
		this.serviceProvider = serviceProvider;
	}

	/**
	 * Adds the given target nodes as duplication targets.
	 * 
	 * @param targets the target nodes to use as subgraph end-points
	 */
	public void addContextTargets(Collection<TargetNode> targets) {
		contextTargets.addAll(targets);
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
	 * @see TransformationContext#duplicateContext(SourceNode, SourceNode, Set,
	 *      TransformationLog)
	 */
	@Override
	public void duplicateContext(SourceNode originalSource, final SourceNode duplicate,
			Set<Cell> ignoreCells, TransformationLog log) {
		DuplicationInformation info = new DuplicationInformation(duplicate, ignoreCells,
				contextTargets);

		SourceNode parent = duplicate.getParent();
		if (parent == null)
			parent = duplicate.getAnnotatedParent();

		if (parent != null) {
			// Find existing cell/target nodes over children of the parent node.
			Map<EntityDefinition, SourceNode> contextPath = new HashMap<EntityDefinition, SourceNode>();
			SourceNode pathNode = duplicate;
			SourceNode root;
			do {
				contextPath.put(pathNode.getEntityDefinition(), pathNode);
				root = pathNode;
				if (pathNode.getParent() != null)
					pathNode = pathNode.getParent();
				else
					pathNode = pathNode.getAnnotatedParent();
			} while (pathNode != null);
			collectExistingNodes(root, contextPath, info, false);
			// add target nodes reachable through original source node
			// XXX not all target nodes are needed, collection of some of them
			// could even lead to wrong results
			collectExistingNodes(originalSource,
					Collections.<EntityDefinition, SourceNode> emptyMap(), info, true);

			duplicateTree(originalSource, duplicate, info, log, serviceProvider);
		}
		else
			throw new IllegalStateException(
					"Duplicate node neither got a parent, nor an annotated parent.");

		// Code matching the old stuff at bottom!
//		// create a new duplication context
//		DuplicationContext duplicationContext = new DuplicationContext(ignoreCells);
//		
//		// configure duplicate, but don't add to parent (as it is already added as annotated child)
//		// at this point duplicate may have a parent, even if the original source hasn't
//		configureSourceDuplicate(originalSource, duplicate, 
//				duplicate.getParent(), duplicationContext, false);
//		
//		// track back to sources from cells where sources are missing
//		for (Pair<CellNodeImpl, CellNode> cellPair : duplicationContext.getIncompleteCellNodes()) {
//			CellNodeImpl cellNode = cellPair.getFirst();
//			CellNode originalCell = cellPair.getSecond();
//			
//			cellTrackback(cellNode, originalCell);
//		}
//		
//		// track back from targets where augmentations are missing
//		for (Pair<TargetNodeImpl, TargetNode> targetPair : duplicationContext.getIncompleteTargetNodes()) {
////			TargetNodeImpl targetNode = targetPair.getFirst();
//			TargetNode originalTarget = targetPair.getSecond();
//			
//			augmentationTrackback(originalTarget, duplicationContext);
//		}
	}

	/**
	 * Duplicates the transformation tree for the given source node to the given
	 * duplicate source node.
	 * 
	 * @param source the original source node
	 * @param duplicate the duplication target
	 * @param info the duplication info object
	 * @param log the transformation log
	 * @param serviceProvider service provider for resolving functions
	 */
	private static void duplicateTree(SourceNode source, SourceNode duplicate,
			DuplicationInformation info, TransformationLog log, ServiceProvider serviceProvider) {
		// Duplicate relations.
		for (CellNode cell : source.getRelations(false)) {

			// check whether the cell is eager for the source node
			if (TransformationTreeUtil.isEager(cell, source, log, serviceProvider))
				continue;

			// Check whether the cell is ignored.
			if (info.isIgnoreCell(cell.getCell()))
				continue;

			// XXX prioritize already created cell nodes (in this run) over old
			// nodes?

			// First check whether an old cell node with a missing source
			// exists.
			// If so, add this source to the first so found cell.
			boolean usedOld = false;
			for (IdentityWrapper<CellNode> wrapper : info.getOldCellNodes(cell.getCell())) {
				CellNode oldCellNode = wrapper.getValue();
				// Skip the cell if it's full.
				if (oldCellNode.getSources().size() == cell.getSources().size())
					continue;
				// XXX Other logic to decide which cell to use?
				// Check whether out duplicate source node is missing...
				if (!oldCellNode.getSources().contains(duplicate)) {
					usedOld = true;
					// Has to be a cell created with the cell constructor.
					((CellNodeImpl) oldCellNode).addSource(cell.getSourceNames(source), duplicate);
					duplicate.addRelation(oldCellNode);
					break;
				}
			}

			// If no old cell was used, use a newly created one / create one.
			if (!usedOld) {
				CellNodeImpl duplicatedCell = info.getNewCellNode(cell.getCell());
				if (duplicatedCell == null) {
					// Create a new cell, augment it with existing sources, add
					// it to info and duplicate targets.
					duplicatedCell = new CellNodeImpl(cell.getCell());
					augmentCell(cell, duplicatedCell, info);
					info.addNewCellNode(cell.getCell(), duplicatedCell);
					duplicateTree(cell, duplicatedCell, info, log);
				}
				// Add as relation/source.
				duplicate.addRelation(duplicatedCell);
				duplicatedCell.addSource(cell.getSourceNames(source), duplicate);
			}
		}

		// Duplicate children.
		for (SourceNode child : source.getChildren(false)) {
			SourceNode duplicatedChild = new SourceNodeImpl(child.getEntityDefinition(), duplicate,
					true);
			duplicatedChild.setContext(child.getContext());
			duplicateTree(child, duplicatedChild, info, log, serviceProvider);
		}
	}

	/**
	 * Adds existing sources to the duplicated cell.
	 * 
	 * @param cell the original cell
	 * @param duplicatedCell the duplicated cell
	 * @param info the duplication info object
	 */
	private static void augmentCell(CellNode cell, CellNodeImpl duplicatedCell,
			DuplicationInformation info) {
		// This handles the case "a,b* -(a,b)> c": If there is exactly one a and
		// multiple "b"s and there is a
		// cell using an "a" and a "b" the a is used multiple times.
		for (SourceNode source : cell.getSources()) {
			if (!AlignmentUtil.isParent(info.getDuplicatedNode().getEntityDefinition(),
					source.getEntityDefinition())) {
				// This source node does not belong to the duplicated tree, add
				// an existing fitting source node!
				Collection<SourceNode> possibleSources = info
						.getOldSourceNodes(source.getEntityDefinition());
						// Only add the node here if there is exactly one
						// possible
						// source.
						// For zero there is nothing to add, for more they are
						// duplicates
						// which should add themselves to this cell when they
						// are
						// handled.

				// False?
				// In case of zero there may be something to add, not duplicated
				// yet.
				// -> no problem? it adds itself later.
				// In case of one it is possible, that some parent of the one
				// has unhandled leftovers
				// which would result in more than one! Should the parents be
				// checked for leftovers?

				if (possibleSources.size() == 1)
					duplicatedCell.addSource(cell.getSourceNames(source),
							possibleSources.iterator().next());
			}
		}
	}

	/**
	 * Duplicates the transformation tree for the given cell node to the given
	 * duplicate cell node.
	 * 
	 * @param cell the original cell node
	 * @param duplicateCell the duplication target
	 * @param info the duplication info object
	 * @param log the transformation log
	 */
	private static void duplicateTree(CellNode cell, CellNode duplicateCell,
			DuplicationInformation info, TransformationLog log) {
		// Duplicate targets.
		for (TargetNode target : cell.getTargets()) {
			TargetNodeImpl duplicatedTarget = duplicateTree(target, info, log);
			if (duplicatedTarget != null) {
				duplicateCell.addTarget(duplicatedTarget);
				duplicatedTarget.addAssignment(target.getAssignmentNames(cell), duplicateCell);
			}
		}
	}

	/**
	 * Duplicates the transformation tree for the given target node to the given
	 * duplicate target node.
	 * 
	 * @param target the original target node
	 * @param info the duplication info object
	 * @param log the transformation log
	 * @return a collection of newly created target nodes
	 */
	private static TargetNodeImpl duplicateTree(TargetNode target, DuplicationInformation info,
			TransformationLog log) {
		GroupNode parent = null;
		// Check if the parent node exists in the given context already.
		if (target.getParent() instanceof TargetNode) {
			Iterator<TargetNode> possibleParents = info
					.getAllTargetNodes(((TargetNode) target.getParent()).getEntityDefinition())
					.iterator();

			while (possibleParents.hasNext()) {
				TargetNode possibleParent = possibleParents.next();
				if (info.getContextTargets().contains(target)
						|| !possibleParent.getChildren(false).contains(target)) {
					parent = possibleParent;
					break;
				}
			}
		}
		else if (target.getParent() instanceof TransformationTree
				&& info.getContextTargets().contains(target)) {
			// Reached root, but this is a possible target.
			parent = target.getParent();
		}
		else {
			// Reached root, but this is no possible target or the parent is
			// null!
			// XXX instead log and return null or not connected TargetNode? See
			// T O D O below
			log.warn(log.createMessage("DuplicationContext present, but no matching target found.",
					null));
			return null;
//			throw new IllegalStateException(
//					"DuplicationContext present, but no matching target found.");
		}

		// TODO What about cases where contextTargets parent doesn't exist yet,
		// and there is no
		// place to build (no direct free place, and no other contextTarget) it
		// on?
		// If yes, what do? Right now it would end at the exception in the
		// beginning of this method.
		// Basically the duplication should fail, right? Completely, or only of
		// this target?
		// Construct an example where that happens.

		if (parent == null) {
			// Does not exist: recursion.
			TargetNodeImpl duplicatedTarget = duplicateTree((TargetNode) target.getParent(), info,
					log);
			if (duplicatedTarget == null) {
				return null;
			}
			TargetNodeImpl newTarget = new TargetNodeImpl(target.getEntityDefinition(),
					duplicatedTarget);
			info.addNewTargetNode(newTarget.getEntityDefinition(), newTarget);
			duplicatedTarget.addChild(newTarget);
			return newTarget;
		}
		else {
			// Exists: add as child.
			TargetNodeImpl newTarget = new TargetNodeImpl(target.getEntityDefinition(), parent);
			info.addNewTargetNode(newTarget.getEntityDefinition(), newTarget);
			// If the child is not already present, add it directly, otherwise
			// as annotated child.
			if (parent instanceof TargetNodeImpl && !parent.getChildren(false).contains(newTarget))
				((TargetNodeImpl) parent).addChild(newTarget);
			else
				parent.addAnnotatedChild(newTarget);
			return newTarget;
		}
	}

	/**
	 * Track back target nodes and duplicate any augmentation cells.
	 * 
	 * @param tree the tree to work on
	 */
	public static void augmentationTrackback(TransformationTree tree) {
		final Map<EntityDefinition, TargetNode> targetNodesWithAugmentations = new HashMap<EntityDefinition, TargetNode>();

		// Search for original target nodes
		tree.accept(new AbstractTargetToSourceVisitor() {

			Deque<Boolean> hasAugmentation = new ArrayDeque<Boolean>();

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTransformationNodeVisitor#visit(eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode)
			 */
			@Override
			public boolean visit(CellNode cell) {
				// If the cell is an augmentation cell, set the last entry in
				// hasAugmentation to true.
				if (cell.getSources().isEmpty()) {
					if (!hasAugmentation.getLast()) {
						hasAugmentation.pop();
						hasAugmentation.push(true);
					}
				}
				return false;
			}

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTransformationNodeVisitor#visit(eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode)
			 */
			@Override
			public boolean visit(TargetNode target) {
				// Simply add a new level to hasAugmentation starting with
				// false.
				hasAugmentation.push(false);
				return true;
			}

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTransformationNodeVisitor#leave(eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode)
			 */
			@Override
			public void leave(TargetNode target) {
				// If this nodes level in hasAugmentation is true...
				if (hasAugmentation.pop()) {
					// ... add it to targetNodesWithAugmentations and set
					// parents level to true, too.
					targetNodesWithAugmentations.put(target.getEntityDefinition(), target);
					if (!hasAugmentation.isEmpty() && !hasAugmentation.getLast()) {
						hasAugmentation.pop();
						hasAugmentation.push(true);
					}
				}
			}

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor#includeAnnotatedNodes()
			 */
			@Override
			public boolean includeAnnotatedNodes() {
				// Only look for original target nodes.
				return false;
			}
		});

		// Add augmentations to all target nodes (no copied target node got them
		// yet)
		tree.accept(new AbstractTargetToSourceVisitor() {

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTransformationNodeVisitor#visit(eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode)
			 */
			@Override
			public boolean visit(CellNode cell) {
				return false;
			}

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTransformationNodeVisitor#visit(eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode)
			 */
			@Override
			public boolean visit(TargetNode target) {

				// TODO more intelligent behavior of when NOT to create the
				// augmentation.
				// For example if the augmentation belongs to a complex
				// structure that can
				// occur 0..n times, it should not be created for the first time
				// due to an
				// augmentation.

				TargetNode originalTarget = targetNodesWithAugmentations
						.get(target.getEntityDefinition());
				// Only have to do something if the node is present in the map.
				if (originalTarget != null && originalTarget != target) {
					// Check for missing relations (all relations without
					// sources are missing).
					for (CellNode originalAssignment : originalTarget.getAssignments()) {
						if (originalAssignment.getSources().isEmpty()) {
							CellNodeImpl duplicatedAssignment = new CellNodeImpl(
									originalAssignment.getCell());
							duplicatedAssignment.addTarget(target);
							((TargetNodeImpl) target).addAssignment(
									originalTarget.getAssignmentNames(originalAssignment),
									duplicatedAssignment);
						}
					}

					// Check for missing children.
					for (TargetNode child : originalTarget.getChildren(false)) {
						// Only add missing children that need an augmentation.
						if (targetNodesWithAugmentations.containsKey(child.getEntityDefinition())
								&& !target.getChildren(false).contains(child)) {
							TargetNodeImpl duplicatedChild = new TargetNodeImpl(
									child.getEntityDefinition(), target);
							((TargetNodeImpl) target).addChild(duplicatedChild);
							// The child will be handled by this visior later.
						}
					}
					return true;
				}
				else
					return false;
			}

			@Override
			public boolean includeAnnotatedNodes() {
				return true;
			}
		});
	}

	/**
	 * Collects all TargetNodes associated with the given SourceNode excluding
	 * SourceNodes with the given EntityDefinition.
	 * 
	 * @param source the source to start from
	 * @param contextPath source nodes with a definition in this map are only
	 *            followed if they are exactly the node in this map
	 * @param info the duplication info object
	 * @param targetsOnly whether to only collect target nodes, or all nodes
	 */
	private void collectExistingNodes(SourceNode source,
			final Map<EntityDefinition, SourceNode> contextPath, final DuplicationInformation info,
			final boolean targetsOnly) {
		source.accept(new AbstractSourceToTargetVisitor() {

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor#includeAnnotatedNodes()
			 */
			@Override
			public boolean includeAnnotatedNodes() {
				return true; // Really include annotated nodes? Maybe only
								// special handling for types in
								// visit(SourceNode)?
			}

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTransformationNodeVisitor#visit(eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode)
			 */
			@Override
			public boolean visit(TargetNode target) {
				// TargetNodes can be found more than once...
				Collection<IdentityWrapper<TargetNode>> targetNodes = info
						.getOldTargetNodes(target.getEntityDefinition());
				if (targetNodes.contains(new IdentityWrapper<>(target))) {
					return false; // already found & followed...
				}
				info.addOldTargetNode(target.getEntityDefinition(), target);
				return true;
			}

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTransformationNodeVisitor#visit(eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode)
			 */
			@Override
			public boolean visit(CellNode cell) {
				// CellNodes can be found more than once...
				Collection<IdentityWrapper<CellNode>> cellNodes = info
						.getOldCellNodes(cell.getCell());
				if (cellNodes.contains(new IdentityWrapper<>(cell))) {
					return false; // already found & followed...
				}
				if (!targetsOnly)
					info.addOldCellNode(cell.getCell(), cell);
				return true;
			}

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTransformationNodeVisitor#visit(eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree)
			 */
			@Override
			public boolean visit(TransformationTree root) {
				return false;
			}

			/**
			 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTransformationNodeVisitor#visit(eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode)
			 */
			@Override
			public boolean visit(SourceNode source) {
				SourceNode contextNode = contextPath.get(source.getEntityDefinition());
				if (contextNode == null || source == contextNode) {
					if (!targetsOnly)
						info.addOldSourceNode(source.getEntityDefinition(), source);
					return true;
				}
				else
					return false;
			}
		});
	}

//	/**
//	 * Checks whether the given source node is part of the context for the duplication.
//	 *
//	 * @param source the source node to check
//	 * @param info the context information
//	 * @return true, if the given node is in the context for the duplication, false otherwise
//	 */
//	private boolean isInContext(SourceNode source, DuplicationInformation info) {
//		Collection<SourceNode> contextNodes = info.getOldSourceNodes(source.getEntityDefinition());
//		for (SourceNode node : contextNodes)
//			if (node == source)
//				return true;
//		return false;
//	}

	// OLD STUFF!

//	/**
//	 * Track back target nodes and duplicate any augmentation cells.
//	 * @param originalTarget the original target node
//	 * @param duplicationContext the duplication context
//	 */
//	private void augmentationTrackback(TargetNode originalTarget, 
//			DuplicationContext duplicationContext) {
//		// track back child augmentations
//		for (TargetNode child : originalTarget.getChildren(false)) { //XXX should annotated children be included?
//			augmentationTrackback(child, duplicationContext);
//		}
//		
//		// track back augmentations
//		for (CellNode originalAssignment : originalTarget.getAssignments()) {
//			/*
//			 * Duplicated target does not contain an assignment representing
//			 * the same cell as originalAssignment. 
//			 */
//			if (originalAssignment.getSources().isEmpty()) {
//				// the cell is an augmentation, thus we duplicate it
//				duplicateCell(originalAssignment, null, duplicationContext);
//				/*
//				 * it is automatically added to the target nodes (which are 
//				 * retrieved from the duplication context or created as
//				 * necessary)
//				 */
//			}
//		}
//	}
//
//	/**
//	 * Track the graph back to sources that are missing in a cell node compared
//	 * to the original cell node.
//	 * @param cellNode the cell node
//	 * @param originalCell the original cell node the node was duplicated from
//	 */
//	private void cellTrackback(CellNodeImpl cellNode, CellNode originalCell) {
//		for (SourceNode originalSource : originalCell.getSources()) {
//			if (!cellNode.getSources().contains(originalSource)) {
//				/*
//				 * Duplicated cell does not contain a source representing the
//				 * same entity as originalSource.
//				 */
//				SourceNode newSource = null;
//				
//				// now there are several possible cases
//				// a) the original source has leftovers and we grab one
//				Leftovers leftovers = originalSource.getLeftovers();
//				if (leftovers != null) {
//					newSource = leftovers.consumeValue(originalCell.getCell());
//					
//					if (newSource != null) {
//						// interconnect both
//						newSource.addRelation(cellNode);
//						cellNode.addSource(originalCell.getSourceNames(originalSource), 
//								newSource);
//						//XXX hard connections are OK here, as a leftover source is a duplicate
//					}
//					else {
//						//TODO add an undefined source node in this case?
//					}
//				}
//				
//				// b) the original source has a parent (ot it has a parent etc.)
//				//    that has leftovers
//				if (newSource == null) {
//					//TODO
//				}
//				
//				// c) we use the original source node
//				if (newSource == null) {
//					newSource = originalSource;
//					
//					// interconnect both
//					newSource.addAnnotatedRelation(cellNode); //FIXME should be an augmentated relation!!!!
//					cellNode.addSource(originalCell.getSourceNames(originalSource), 
//							newSource);
//				}
//			}
//		}
//	}

	/**
	 * Duplicate a source node.
	 * 
	 * @param source the source node to duplicate
	 * @param parent the parent of the new source node
	 * @param addToParent if the new source node should be added as child to the
	 *            parent
	 * @param duplicationContext the duplication context
	 * @return the new duplicated source node or <code>null</code> if
	 *         duplication was prohibited
	 */
	private SourceNode duplicateSource(SourceNode source, SourceNode parent, boolean addToParent,
			DuplicationContext duplicationContext) {
		// create duplicate
		SourceNode duplicate = new SourceNodeImpl(source.getEntityDefinition(), parent,
				addToParent);
		duplicate.setContext(source.getContext());

		return configureSourceDuplicate(source, duplicate, parent, duplicationContext, addToParent);
	}

	/**
	 * Configure a duplicated source node.
	 * 
	 * @param originalSource the original source node
	 * @param duplicate the duplicated source node
	 * @param parent the parent for the duplicated source node
	 * @param duplicationContext the duplication context
	 * @param addToParent if the duplicated source node should be added as child
	 *            to its parent
	 * @return the duplicated source node or <code>null</code> if it has no
	 *         further connections
	 */
	private SourceNode configureSourceDuplicate(SourceNode originalSource, SourceNode duplicate,
			SourceNode parent, DuplicationContext duplicationContext, boolean addToParent) {
		// duplicate relations
		List<CellNode> relations = new ArrayList<CellNode>(
				originalSource.getRelations(false).size());
		// though each cell node is only duplicated once per duplication context
		for (CellNode relation : originalSource.getRelations(false)) { // XXX
																		// should
																		// the
																		// annotated
																		// relations
																		// be
																		// included?
			CellNode duplicatedRelation = duplicateCell(relation, duplicate, duplicationContext);
			if (duplicatedRelation != null) {
				relations.add(duplicatedRelation);
			}
		}

		// duplicate children
		List<SourceNode> children = new ArrayList<SourceNode>(
				originalSource.getChildren(false).size());
		for (SourceNode child : originalSource.getChildren(false)) { // XXX
																		// should
																		// the
																		// annotated
																		// children
																		// be
																		// included?
			SourceNode duplicatedChild = duplicateSource(child, duplicate, true,
					duplicationContext);
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
	 * 
	 * @param relation the original cell node
	 * @param duplicateSource the duplicated source node to be associated with
	 *            the duplicated cell node, may be <code>null</code> if the cell
	 *            is an augmentation
	 * @param duplicationContext the context of the current duplication process
	 * @return the duplicated cell node or <code>null</code> if duplication was
	 *         prohibited
	 */
	private CellNode duplicateCell(CellNode relation, SourceNode duplicateSource,
			DuplicationContext duplicationContext) {
		if (duplicationContext.getIgnoreCells().contains(relation.getCell())) {
			// cancel if the cell has to be ignored (which usually means it was
			// already handled)
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
				// FIXME if a cell has different associated sources the cell
				// node will be repeatedly created and discarded
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
	 * 
	 * @param target the original target node
	 * @param relation the relation to associated to the target node
	 * @param duplicationContext the duplication context
	 * @return the duplicated target node or <code>null</code> if duplication
	 *         was prohibited
	 */
	private TargetNode duplicateTarget(TargetNode target, CellNode relation,
			DuplicationContext duplicationContext) {
		TargetNodeImpl duplicatedTarget = duplicationContext.getNode(target.getEntityDefinition());

		if (duplicatedTarget == null) {
			// target node not created yet

			boolean duplicateParent = true;
			if (contextTargets.contains(target)) {
				// this is an endpoint, as such this is the last node to be
				// duplicated
				duplicateParent = false;
			}

			GroupNode duplicatedParent;
			if (duplicateParent) {
				GroupNode parent = target.getParent();
				if (parent instanceof TargetNode) {
					// create a duplicated parent
					duplicatedParent = duplicateTarget((TargetNode) parent, null,
							duplicationContext);
					if (duplicatedParent == null) {
						return null;
					}
				}
				else {
					// parent is either null or the root
					// this means there was no match for a context endpoint
					// along the way
					// thus this is not a valid path for duplication
					return null;
				}
			}
			else {
				duplicatedParent = target.getParent();
			}

			// create duplicate
			duplicatedTarget = new TargetNodeImpl(target.getEntityDefinition(), duplicatedParent);

			// add as annotated child to parent
			duplicatedParent.addAnnotatedChild(duplicatedTarget);

			// add to duplication context
			duplicationContext.addNode(duplicatedTarget, target);
		}

		if (relation != null) {
			// assign relation
			duplicatedTarget.addAssignment(target.getAssignmentNames(relation), relation);
		}

		return duplicatedTarget;
	}

}
