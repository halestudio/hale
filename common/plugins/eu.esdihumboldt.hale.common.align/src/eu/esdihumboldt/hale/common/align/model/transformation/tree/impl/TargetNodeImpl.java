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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.GroupNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default {@link TargetNode} implementation
 * @author Simon Templer
 */
@Immutable
public class TargetNodeImpl implements TargetNode {

	private final ChildDefinition<?> child;
	private final Set<CellNode> assignments;
	private final List<TargetNode> children;

	/**
	 * Constructor
	 * @param child the associated definition
	 * @param cells the cells associated with this node or its children
	 * @param parentType the type representing the root
	 * @param depth the depth down from the root node
	 */
	public TargetNodeImpl(ChildDefinition<?> child, Collection<CellNode> cells,
			TypeDefinition parentType, int depth) {
		this.child = child;
		
		// partition cells by child
		ListMultimap<ChildDefinition<?>, CellNode> childCells = ArrayListMultimap.create();
		//... and for this node
		Set<CellNode> assignSet = new LinkedHashSet<CellNode>();
		for (CellNode cell : cells) {
			for (Entity target : cell.getCell().getTarget().values()) {
				if (target.getDefinition().getType().equals(parentType)) {
					List<ChildContext> path = target.getDefinition().getPropertyPath();
					if (path.get(depth - 1).getChild().equals(child)) {
						if (path.size() <= depth) {
							// this cell belongs to this node
							assignSet.add(cell);
						}
						else {
							// this cell belongs to a child node
							childCells.put(path.get(depth).getChild(), cell);
						}
					}
				}
			}
		}
		
		assignments = Collections.unmodifiableSet(assignSet);
		
		// create child cells
		List<TargetNode> childList = new ArrayList<TargetNode>();
		for (Entry<ChildDefinition<?>, Collection<CellNode>> childEntry : childCells.asMap().entrySet()) {
			TargetNode childNode = new TargetNodeImpl(childEntry.getKey(), 
					childEntry.getValue(), parentType, depth + 1);
			childList.add(childNode);
		}
		
		children = Collections.unmodifiableList(childList);
	}

	/**
	 * @see GroupNode#getChildren()
	 */
	@Override
	public List<TargetNode> getChildren() {
		return children;
	}

	/**
	 * @see TargetNode#getAssignments()
	 */
	@Override
	public Set<CellNode> getAssignments() {
		return assignments;
	}

	/**
	 * @see TargetNode#getDefinition()
	 */
	@Override
	public ChildDefinition<?> getDefinition() {
		return child;
	}

}
