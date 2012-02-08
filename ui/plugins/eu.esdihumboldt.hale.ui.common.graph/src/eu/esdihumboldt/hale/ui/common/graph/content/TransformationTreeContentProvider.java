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

package eu.esdihumboldt.hale.ui.common.graph.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.InstanceVisitor;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.Pair;

/**
 * Transformation graph based on {@link TransformationTree} derived from an 
 * {@link Alignment}
 * @author Simon Templer
 */
public class TransformationTreeContentProvider extends ArrayContentProvider
		implements IGraphEntityContentProvider {

	/**
	 * @see ArrayContentProvider#getElements(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
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
			
			if (instances != null && !instances.isEmpty()) {
				Collection<Object> result = new ArrayList<Object>();
				// create transformation trees for each instance
				for (Instance instance : instances) {
					Collection<? extends Cell> associatedCells = alignment
							.getCells(new TypeEntityDefinition(instance
									.getDefinition(), SchemaSpaceID.SOURCE));
					
					for (Cell cell : associatedCells) {
						for (Entity target : cell.getTarget().values()) {
							EntityDefinition def = target.getDefinition();
							if (def.getDefinition() instanceof TypeDefinition) {
								//XXX ensure that each type definition is only used once?!
								TypeDefinition targetType = (TypeDefinition) def
										.getDefinition();
								
								TransformationTree tree = createInstanceTree(
										instance, targetType, alignment);
								if (tree != null) {
									result.addAll(collectNodes(tree));
								}
							}
						}
					}
				}
				return result.toArray();
			}
			
			Collection<? extends Cell> cells = alignment.getTypeCells();
			if (!cells.isEmpty()) {
				// collect target types
				Set<TypeDefinition> types = new HashSet<TypeDefinition>();
				for (Cell cell : cells) {
					TypeDefinition type = cell.getTarget().values().iterator()
							.next().getDefinition().getType();
					types.add(type);
				}
				
				Collection<Object> result = new ArrayList<Object>();
				for (TypeDefinition type : types) {
					// create tree and add nodes for each type
					result.addAll(collectNodes(new TransformationTreeImpl(
							type, alignment)));
				}
				return result.toArray();
			}
		}
		
		return super.getElements(inputElement);
	}

	/**
	 * Create a transformation tree based on a source instance.
	 * @param instance the source instance
	 * @param targetType the target type
	 * @param alignment the alignment
	 * @return the transformation tree or <code>null</code>
	 */
	private TransformationTree createInstanceTree(Instance instance,
			TypeDefinition targetType, Alignment alignment) {
		TransformationTree tree = new TransformationTreeImpl(
				targetType, alignment);
		
		// process and annotate the tree
		InstanceVisitor visitor = new InstanceVisitor(instance);
		tree.accept(visitor);
		
		return tree;
	}

	/**
	 * Collect all nodes related to from a type node
	 * @param typeNode the type node
	 * @return the nodes
	 */
	private Collection<Object> collectNodes(TransformationTree typeNode) {
		Queue<Object> toTest = new LinkedList<Object>();
		Set<Object> nodes = new LinkedHashSet<Object>();
		
		toTest.offer(typeNode);
		
		while (!toTest.isEmpty()) {
			Object node = toTest.poll();
			
			// add node
			nodes.add(node);
			
			// test children
			Iterable<? extends Object> children = getChilddren(node);
			for (Object child : children) {
				if (!nodes.contains(child)) {
					toTest.offer(child);
				}
			}
		}
		
		return nodes;
	}

	/**
	 * Get the children of a node
	 * @param node the node
	 * @return the node's children
	 */
	private Collection<? extends Object> getChilddren(Object node) {
		if (node instanceof TransformationTree) {
			return ((TransformationTree) node).getChildren(true);
		}
		if (node instanceof TargetNode) {
			Collection<Object> children = new ArrayList<Object>();
			children.addAll(((TargetNode) node).getChildren(true));
			children.addAll(((TargetNode) node).getAssignments());
			return children;
		}
		if (node instanceof CellNode) {
			return ((CellNode) node).getSources();
		}
		if (node instanceof SourceNode) {
			SourceNode parent = ((SourceNode) node).getParent();
			if (parent != null) {
				return Collections.singleton(parent);
			}
		}
		
		return Collections.emptyList();
	}

	/**
	 * @see IGraphEntityContentProvider#getConnectedTo(Object)
	 */
	@Override
	public Object[] getConnectedTo(Object entity) {
		return getChilddren(entity).toArray();
	}

}
