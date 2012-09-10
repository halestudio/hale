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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.GroupNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default {@link TargetNode} implementation
 * 
 * @author Simon Templer
 */
@Immutable
public class TargetNodeImpl extends AbstractGroupNode implements TargetNode {

	private final EntityDefinition entity;
	private final SetMultimap<CellNode, String> assignments;
	private final List<TargetNode> children;

	/**
	 * Create a target node that is populated with assignments and children
	 * according to the given parameters.
	 * 
	 * @param entity the associated definition
	 * @param cells the cells associated with this node or its children
	 * @param parentType the type representing the root
	 * @param depth the depth down from the root node
	 * @param parent the parent node
	 */
	public TargetNodeImpl(EntityDefinition entity, Collection<CellNode> cells,
			TypeDefinition parentType, int depth, GroupNode parent) {
		super(parent);
		this.entity = entity;

		// partition cells by child
		ListMultimap<EntityDefinition, CellNode> childCells = ArrayListMultimap.create();
		// ... and for this node
		SetMultimap<CellNode, String> assignSet = HashMultimap.create();
		for (CellNode cell : cells) {
			for (Entry<String, ?> entry : cell.getCell().getTarget().asMap().entrySet()) {
				String name = entry.getKey();
				@SuppressWarnings("unchecked")
				Collection<? extends Entity> entities = (Collection<? extends Entity>) entry
						.getValue();

				for (Entity target : entities) {
					if (target.getDefinition().getType().equals(parentType)) {
						List<ChildContext> path = target.getDefinition().getPropertyPath();
						if (path.get(depth - 1).getChild().equals(entity.getDefinition())) {
							if (path.size() <= depth) {
								// this cell belongs to this node
								assignSet.put(cell, name);
								cell.addTarget(this);
							}
							else {
								// this cell belongs to a child node
								childCells.put(AlignmentUtil.deriveEntity(target.getDefinition(),
										depth + 1), cell);
							}
						}
					}
				}
			}
		}

		assignments = Multimaps.unmodifiableSetMultimap(assignSet);

		// create child cells
		List<TargetNode> childList = new ArrayList<TargetNode>();
		for (Entry<EntityDefinition, Collection<CellNode>> childEntry : childCells.asMap()
				.entrySet()) {
			TargetNode childNode = new TargetNodeImpl(childEntry.getKey(), childEntry.getValue(),
					parentType, depth + 1, this);
			childList.add(childNode);
		}

		children = Collections.unmodifiableList(childList);
	}

	/**
	 * Create a target node associated with the given entity definition but
	 * unpopulated.
	 * 
	 * @param entity the entity definition
	 * @param parent the parent node
	 */
	public TargetNodeImpl(EntityDefinition entity, GroupNode parent) {
		super(parent);
		this.entity = entity;
		this.assignments = HashMultimap.create();
		this.children = new ArrayList<TargetNode>();
	}

	/**
	 * Add an assignment to the target node. May only be called if the target
	 * node was created using the
	 * {@link #TargetNodeImpl(EntityDefinition, GroupNode)} constructor.
	 * 
	 * @param names the entity names associated to the assignment
	 * @param cell the cell node representing the assignment
	 */
	public void addAssignment(Set<String> names, CellNode cell) {
		assignments.putAll(cell, names);
	}

	/**
	 * Add a child to the target node. May only be called if the target node was
	 * created using the {@link #TargetNodeImpl(EntityDefinition, GroupNode)}
	 * constructor.
	 * 
	 * @param node the node to add as child, this node will be set as its parent
	 */
	public void addChild(TargetNode node) {
		children.add(node);
	}

//	/**
//	 * Set the parent node.
//	 * @param parent the parent node
//	 */
//	public void setParent(GroupNode parent) {
//		this.parent = parent;
//	}

	/**
	 * @see TransformationNode#accept(TransformationNodeVisitor)
	 */
	@Override
	public void accept(TransformationNodeVisitor visitor) {
		if (visitor.visit(this)) {
			if (visitor.isFromTargetToSource()) {
				// visit children
				for (TargetNode child : getChildren(visitor.includeAnnotatedNodes())) {
					child.accept(visitor);
				}
				// visit cells
				for (CellNode cell : assignments.keySet()) {
					cell.accept(visitor);
				}
			}
			else {
				// visit parent
				if (getParent() != null) {
					GroupNode parent = getParent();
					if (parent instanceof TargetNode) {
						((TargetNode) parent).accept(visitor);
					}
					else if (parent instanceof TransformationTree) {
						((TransformationTree) parent).accept(visitor);
					}
				}
			}
		}
		visitor.leave(this);
	}

	/**
	 * @see AbstractGroupNode#getFixedChildren()
	 */
	@Override
	public List<TargetNode> getFixedChildren() {
		return children;
	}

	/**
	 * @see TargetNode#getAssignments()
	 */
	@Override
	public Set<CellNode> getAssignments() {
		return assignments.keySet();
	}

	/**
	 * @see TargetNode#getAssignmentNames(CellNode)
	 */
	@Override
	public Set<String> getAssignmentNames(CellNode assignment) {
		return assignments.get(assignment);
	}

	/**
	 * @see TargetNode#getDefinition()
	 */
	@Override
	public ChildDefinition<?> getDefinition() {
		return (ChildDefinition<?>) entity.getDefinition();
	}

	/**
	 * @see TargetNode#getEntityDefinition()
	 */
	@Override
	public EntityDefinition getEntityDefinition() {
		return entity;
	}

	/**
	 * @see TargetNode#isDefined()
	 */
	@Override
	public boolean isDefined() {
		Object value = getAnnotation(ANNOTATION_RESULT_DEFINED);
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		return false;
	}

	/**
	 * @see TargetNode#setDefined(boolean)
	 */
	@Override
	public void setDefined(boolean defined) {
		setAnnotation(ANNOTATION_RESULT_DEFINED, defined);
	}

	/**
	 * @see TargetNode#getResult()
	 */
	@Override
	public Object getResult() {
		return getAnnotation(ANNOTATION_RESULT);
	}

	/**
	 * @see TargetNode#setResult(Object)
	 */
	@Override
	public void setResult(Object value) {
		setAnnotation(ANNOTATION_RESULT, value);
		setDefined(true);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TargetNodeImpl other = (TargetNodeImpl) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		}
		else if (!entity.equals(other.entity))
			return false;
		return true;
	}

}
