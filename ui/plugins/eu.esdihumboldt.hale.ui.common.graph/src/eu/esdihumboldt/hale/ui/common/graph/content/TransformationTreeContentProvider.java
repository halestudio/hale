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

package eu.esdihumboldt.hale.ui.common.graph.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.ContextMatcher;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl.matcher.AsDeepAsPossible;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.DuplicationVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.InstanceVisitor;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.CellLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.DefaultTransformationReporter;
import eu.esdihumboldt.hale.common.core.report.ReportLog;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.util.IdentityWrapper;
import eu.esdihumboldt.util.Pair;

/**
 * Transformation graph based on {@link TransformationTree} derived from an
 * {@link Alignment}
 * 
 * @author Simon Templer
 */
public class TransformationTreeContentProvider extends ArrayContentProvider implements
		IGraphEntityContentProvider {

	/**
	 * @see ArrayContentProvider#getElements(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TransformationTree)
			return collectNodes((TransformationTree) inputElement).toArray();

		Collection<Instance> instances = null;
		if (inputElement instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) inputElement;
			inputElement = pair.getFirst();

			if (pair.getSecond() instanceof Collection<?>) {
				instances = (Collection<Instance>) pair.getSecond();
			}
		}

		if (inputElement instanceof Alignment) {
			Alignment alignment = (Alignment) inputElement;

			// input contained specific instances
			if (instances != null && !instances.isEmpty()) {
				Collection<Object> result = new ArrayList<Object>();
				// create transformation trees for each instance
				for (Instance instance : instances) {
					// find type cells matching the instance
					Collection<? extends Cell> typeCells = alignment.getActiveTypeCells();
					Collection<Cell> associatedTypeCells = new LinkedList<Cell>();
					for (Cell typeCell : typeCells)
						for (Entity entity : typeCell.getSource().values()) {
							TypeEntityDefinition type = (TypeEntityDefinition) entity
									.getDefinition();
							if (type.getDefinition().equals(instance.getDefinition())
									&& (type.getFilter() == null || type.getFilter()
											.match(instance))) {
								associatedTypeCells.add(typeCell);
								break;
							}
						}

					// for each type cell one tree
					for (Cell cell : associatedTypeCells) {
						TransformationTree tree = createInstanceTree(instance, cell, alignment);
						if (tree != null)
							result.addAll(collectNodes(tree));
					}
				}
				return result.toArray();
			}

			// input was alignment only, show trees for all type cells
			Collection<? extends Cell> typeCells = alignment.getActiveTypeCells();
			Collection<Object> result = new ArrayList<Object>(typeCells.size());
			for (Cell typeCell : typeCells) {
				// create tree and add nodes for each cell
				result.addAll(collectNodes(new TransformationTreeImpl(alignment, typeCell)));
			}
			return result.toArray();
		}

		return super.getElements(inputElement);
	}

	/**
	 * Create a transformation tree based on a source instance.
	 * 
	 * @param instance the source instance
	 * @param typeCell the type cell
	 * @param alignment the alignment
	 * @return the transformation tree or <code>null</code>
	 */
	private TransformationTree createInstanceTree(Instance instance, Cell typeCell,
			Alignment alignment) {
		TransformationTree tree = new TransformationTreeImpl(alignment, typeCell);

		ReportLog<TransformationMessage> reporter = new DefaultTransformationReporter(
				"Transformation tree", true);
		TransformationLog log = new CellLog(reporter, typeCell);

		// context matching
		// XXX instead through service/extension point?
		ContextMatcher matcher = new AsDeepAsPossible(null);
		matcher.findMatches(tree);

		// process and annotate the tree
		InstanceVisitor visitor = new InstanceVisitor(new FamilyInstanceImpl(instance), tree, log);
		tree.accept(visitor);

		// duplicate subtree as necessary
		DuplicationVisitor duplicationVisitor = new DuplicationVisitor(tree, log);
		tree.accept(duplicationVisitor);

		duplicationVisitor.doAugmentationTrackback();

		return tree;
	}

	/**
	 * Collect all nodes related to from a type node
	 * 
	 * @param typeNode the type node
	 * @return the nodes
	 */
	private Collection<? extends Object> collectNodes(TransformationTree typeNode) {
		Queue<IdentityWrapper<?>> toTest = new LinkedList<IdentityWrapper<?>>();
		Set<IdentityWrapper<?>> nodes = new LinkedHashSet<IdentityWrapper<?>>();

		IdentityWrapper<?> wrapper = new IdentityWrapper<Object>(typeNode);
		toTest.offer(wrapper);

		while (!toTest.isEmpty()) {
			IdentityWrapper<?> node = toTest.poll();

			// add node
			nodes.add(node);

			// test children
			Iterable<? extends Object> children = getChilddren(node.getValue());
			for (Object child : children) {
				if (!(child instanceof IdentityWrapper<?>)) {
					child = new IdentityWrapper<Object>(child);
				}
				if (!nodes.contains(child)) {
					toTest.offer((IdentityWrapper<?>) child);
				}
			}
		}

		return nodes;
	}

	/**
	 * Get the children of a node
	 * 
	 * @param node the node
	 * @return the node's children
	 */
	private Collection<? extends Object> getChilddren(Object node) {
		if (node instanceof IdentityWrapper<?>) {
			node = ((IdentityWrapper<?>) node).getValue();
		}

		if (node instanceof TransformationTree) {
			return wrapNodes(((TransformationTree) node).getChildren(true));
		}
		if (node instanceof TargetNode) {
			List<Object> children = new ArrayList<Object>();
			children.addAll(((TargetNode) node).getChildren(true));
			children.addAll(((TargetNode) node).getAssignments());
			return wrapNodes(children);
		}
		if (node instanceof CellNode) {
			return wrapNodes(((CellNode) node).getSources());
		}
		if (node instanceof SourceNode) {
			SourceNode parent = ((SourceNode) node).getParent();
			if (parent != null) {
				return Collections.singleton(new IdentityWrapper<Object>(parent));
			}
		}

		return Collections.emptyList();
	}

	private Collection<? extends Object> wrapNodes(Collection<? extends Object> nodes) {
		Collection<IdentityWrapper<?>> wrappers = new ArrayList<IdentityWrapper<?>>(nodes.size());
		for (Object node : nodes) {
			wrappers.add(new IdentityWrapper<Object>(node));
		}
		return wrappers;
	}

	/**
	 * @see IGraphEntityContentProvider#getConnectedTo(Object)
	 */
	@Override
	public Object[] getConnectedTo(Object entity) {
		return getChilddren(entity).toArray();
	}

}
