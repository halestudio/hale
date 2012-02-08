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

import net.jcip.annotations.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default {@link TransformationTree} implementation
 * @author Simon Templer
 */
@Immutable
public class TransformationTreeImpl extends AbstractGroupNode implements TransformationTree {
	
	private final TypeDefinition type;
	private final SourceNodeFactory sourceNodes;
	private final List<TargetNode> children;

	/**
	 * Create a transformation tree
	 * @param type the type definition serving as root
	 * @param alignment the alignment holding the cells
	 */
	public TransformationTreeImpl(TypeDefinition type, Alignment alignment) {
		super(null);
		this.type = type;
		
		sourceNodes = new SourceNodeFactory();
		
		TypeEntityDefinition targetType = new TypeEntityDefinition(type, 
				SchemaSpaceID.TARGET);
		Collection<? extends Cell> cells = alignment.getPropertyCells(null, targetType);
		
		// partition cells by child
		ListMultimap<EntityDefinition, CellNode> childCells = ArrayListMultimap.create();
		for (Cell cell : cells) {
			CellNode node = new CellNodeImpl(cell, sourceNodes);
			for (Entity target : cell.getTarget().values()) {
				if (target.getDefinition().getType().equals(type)) {
					List<ChildContext> path = target.getDefinition().getPropertyPath();
					if (path != null && !path.isEmpty()) {
						// store cell with child
						childCells.put(AlignmentUtil.deriveEntity(
								target.getDefinition(), 1), node);
					}
				}
				else {
					// now, that's bad - obviously a cell with targets in more than one type!
					throw new IllegalStateException();
				}
			}
		}
		
		// create child cells
		List<TargetNode> childList = new ArrayList<TargetNode>();
		for (Entry<EntityDefinition, Collection<CellNode>> childEntry : childCells.asMap().entrySet()) {
			TargetNode childNode = new TargetNodeImpl(childEntry.getKey(), 
					childEntry.getValue(), type, 1, this);
			childList.add(childNode);
		}
		
		children = Collections.unmodifiableList(childList);
	}

	/**
	 * @see TransformationNode#accept(TransformationNodeVisitor)
	 */
	@Override
	public void accept(TransformationNodeVisitor visitor) {
		if (visitor.isFromTargetToSource()) {
			if (visitor.visit(this)) {
				// visit children
				for (TargetNode child : getChildren(
						visitor.includeAnnotatedNodes())) {
					child.accept(visitor);
				}
			}
		}
		else {
			// visit leafs
			for (SourceNode node : sourceNodes.getNodes()) {
				if (node.getParent() == null) {
					node.accept(visitor);
				}
			}
		}
	}

	/**
	 * @see TransformationTree#getSourceNode(TypeEntityDefinition)
	 */
	@Override
	public SourceNode getSourceNode(TypeEntityDefinition type) {
		return sourceNodes.getSourceNode(type);
	}

	/**
	 * @see AbstractGroupNode#getFixedChildren()
	 */
	@Override
	public List<TargetNode> getFixedChildren() {
		return children;
	}

	/**
	 * @see TransformationTree#getType()
	 */
	@Override
	public TypeDefinition getType() {
		return type;
	}

}
