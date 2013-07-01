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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.Leftovers;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.TransformationContext;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Default {@link SourceNode} implementation
 * 
 * @author Simon Templer
 */
public class SourceNodeImpl extends AbstractTransformationNode implements SourceNode {

	private final EntityDefinition entityDefinition;
	private final SourceNode parent;
	private final Set<SourceNode> children = new HashSet<SourceNode>();
//	private final SourceNodeFactory sourceNodeFactory;
	private final Set<CellNode> relations = new HashSet<CellNode>();

	private TransformationContext context;

	/**
	 * Constructor
	 * 
	 * @param definition the associated entity definition
	 * @param sourceNodeFactory the factory for creating new source nodes
	 */
	public SourceNodeImpl(EntityDefinition definition, SourceNodeFactory sourceNodeFactory) {
		this.entityDefinition = definition;
//		this.sourceNodeFactory = sourceNodeFactory;

		EntityDefinition parentDef = AlignmentUtil.getParent(definition);
		if (parentDef != null) {
			parent = sourceNodeFactory.getSourceNode(parentDef);
			parent.addChild(this);
		}
		else {
			parent = null;
		}
	}

	/**
	 * Constructor for nodes not associated to a source node factory.
	 * 
	 * @param definition the associated entity definition
	 * @param parent the parent source node
	 * @param addToParent if the created node should be added as a child to the
	 *            given parent
	 */
	public SourceNodeImpl(EntityDefinition definition, SourceNode parent, boolean addToParent) {
		this.entityDefinition = definition;
//		this.sourceNodeFactory = null;
		this.parent = parent;

		if (addToParent && parent != null) {
			parent.addChild(this);
		}
	}

	/**
	 * @see SourceNode#getDefinition()
	 */
	@Override
	public Definition<?> getDefinition() {
		return entityDefinition.getDefinition();
	}

	/**
	 * @see SourceNode#getParent()
	 */
	@Override
	public SourceNode getParent() {
		return parent;
	}

	/**
	 * @see SourceNode#getEntityDefinition()
	 */
	@Override
	public EntityDefinition getEntityDefinition() {
		return entityDefinition;
	}

	/**
	 * @see SourceNode#addChild(SourceNode)
	 */
	@Override
	public void addChild(SourceNode child) {
		children.add(child);
	}

	/**
	 * @see SourceNode#getChildren(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<SourceNode> getChildren(boolean includeAnnotated) {
		if (!includeAnnotated || getAnnotation(ANNOTATION_CHILDREN) == null) {
			return Collections.unmodifiableCollection(children);
		}
		else {
			Collection<SourceNode> result = new ArrayList<SourceNode>(children);
			result.addAll((Collection<SourceNode>) getAnnotation(ANNOTATION_CHILDREN));
			return result;
		}
	}

	/**
	 * @see SourceNode#addRelation(CellNode)
	 */
	@Override
	public void addRelation(CellNode cellNode) {
		relations.add(cellNode);
	}

	/**
	 * @see SourceNode#getRelations(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<CellNode> getRelations(boolean includeAnnotated) {
		if (!includeAnnotated || getAnnotation(ANNOTATION_RELATIONS) == null) {
			return Collections.unmodifiableCollection(relations);
		}
		else {
			Collection<CellNode> result = new ArrayList<CellNode>(relations);
			result.addAll((Collection<CellNode>) getAnnotation(ANNOTATION_RELATIONS));
			return result;
		}
	}

	/**
	 * @see TransformationNode#accept(TransformationNodeVisitor)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void accept(TransformationNodeVisitor visitor) {
		if (visitor.visit(this)) {
			if (visitor.isFromTargetToSource()) {
				if (parent != null) {
					parent.accept(visitor);
				}
			}
			else {
				// visit children
				for (SourceNode child : children) {
					child.accept(visitor);
				}
				// visit annotated children
				if (visitor.includeAnnotatedNodes() && getAnnotation(ANNOTATION_CHILDREN) != null) {
					for (SourceNode child : (Iterable<SourceNode>) getAnnotation(ANNOTATION_CHILDREN)) {
						child.accept(visitor);
					}
				}
				// visit relations
				for (CellNode relation : getRelations(visitor.includeAnnotatedNodes())) {
					relation.accept(visitor);
				}
			}
		}
		visitor.leave(this);
	}

	/**
	 * @see SourceNode#isDefined()
	 */
	@Override
	public boolean isDefined() {
		Object value = getAnnotation(ANNOTATION_VALUE_DEFINED);
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		return false;
	}

	/**
	 * @see SourceNode#setDefined(boolean)
	 */
	@Override
	public void setDefined(boolean defined) {
		setAnnotation(ANNOTATION_VALUE_DEFINED, defined);
	}

	/**
	 * @see SourceNode#getValue()
	 */
	@Override
	public Object getValue() {
		return getAnnotation(ANNOTATION_VALUE);
	}

	/**
	 * @see SourceNode#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		setAnnotation(ANNOTATION_VALUE, value);
		setDefined(true);
		if (getAllValues() == null || getAllValues().length == 0) {
			setAllValues(value);
		}
	}

	@Override
	public void setAllValues(Object... values) {
		setAnnotation(ANNOTATION_ALL_VALUES, values);
	}

	@Override
	public Object[] getAllValues() {
		return (Object[]) getAnnotation(ANNOTATION_ALL_VALUES);
	}

	/**
	 * @see SourceNode#setLeftovers(Leftovers)
	 */
	@Override
	public void setLeftovers(Leftovers leftovers) {
		setAnnotation(ANNOTATION_LEFTOVERS, leftovers);
	}

	/**
	 * @see SourceNode#getLeftovers()
	 */
	@Override
	public Leftovers getLeftovers() {
		return (Leftovers) getAnnotation(ANNOTATION_LEFTOVERS);
	}

	/**
	 * @see SourceNode#addAnnotatedChild(SourceNode)
	 */
	@Override
	public void addAnnotatedChild(SourceNode child) {
		@SuppressWarnings("unchecked")
		List<SourceNode> ac = (List<SourceNode>) getAnnotation(ANNOTATION_CHILDREN);
		if (ac == null) {
			ac = new ArrayList<SourceNode>();
			setAnnotation(ANNOTATION_CHILDREN, ac);
		}
		ac.add(child);
	}

	/**
	 * @see SourceNode#addAnnotatedRelation(CellNode)
	 */
	@Override
	public void addAnnotatedRelation(CellNode relation) {
		@SuppressWarnings("unchecked")
		List<CellNode> ar = (List<CellNode>) getAnnotation(ANNOTATION_RELATIONS);
		if (ar == null) {
			ar = new ArrayList<CellNode>();
			setAnnotation(ANNOTATION_RELATIONS, ar);
		}
		ar.add(relation);
	}

	/**
	 * @see SourceNode#setContext(TransformationContext)
	 */
	@Override
	public void setContext(TransformationContext context) {
		this.context = context;
	}

	/**
	 * @see SourceNode#getContext()
	 */
	@Override
	public TransformationContext getContext() {
		return context;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entityDefinition == null) ? 0 : entityDefinition.hashCode());
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
		SourceNodeImpl other = (SourceNodeImpl) obj;
		if (entityDefinition == null) {
			if (other.entityDefinition != null)
				return false;
		}
		else if (!entityDefinition.equals(other.entityDefinition))
			return false;
		return true;
	}

	/**
	 * @see SourceNode#setAnnotatedParent(SourceNode)
	 */
	@Override
	public void setAnnotatedParent(SourceNode parent) {
		setAnnotation(ANNOTATION_PARENT, parent);
	}

	/**
	 * @see SourceNode#setAnnotatedParent(SourceNode)
	 */
	@Override
	public SourceNode getAnnotatedParent() {
		return (SourceNode) getAnnotation(ANNOTATION_PARENT);
	}

}
