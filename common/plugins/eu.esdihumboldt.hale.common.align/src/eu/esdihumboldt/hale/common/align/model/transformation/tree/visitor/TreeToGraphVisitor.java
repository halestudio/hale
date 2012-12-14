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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.util.Identifiers;

/**
 * {@link TransformationTree} Visitor for generating parameters to create a
 * dot-graph from a tree
 * 
 * @author Sebastian Reinhardt
 */
public class TreeToGraphVisitor extends AbstractTargetToSourceVisitor {

	private final Deque<String> visited;
	private final SetMultimap<String, String> dotMap;
	private final Identifiers<TransformationNode> ids;

	/**
	 * standard constructor
	 */
	public TreeToGraphVisitor() {
		super();
		visited = new LinkedList<String>();
		dotMap = HashMultimap.create();
		ids = new Identifiers<TransformationNode>(TransformationNode.class, false);
	}

	/**
	 * @see TransformationNodeVisitor#visit(TransformationTree)
	 */
	@Override
	public boolean visit(TransformationTree root) {

		String id = ids.getId(root);

		visited.addFirst(id);

		return true;
	}

	/**
	 * @see TransformationNodeVisitor#visit(TargetNode)
	 */
	@Override
	public boolean visit(TargetNode target) {
		boolean cont = true;

		if (ids.fetchId(target) != null) {
			cont = false;
		}

		String id = ids.getId(target);
		String parentId = visited.peekFirst();
		dotMap.put(parentId, id);
		visited.addFirst(id);
		return cont;
	}

	/**
	 * @see TransformationNodeVisitor#visit(CellNode)
	 */
	@Override
	public boolean visit(CellNode cell) {
		boolean cont = true;

		if (ids.fetchId(cell) != null) {
			cont = false;
		}

		String id = ids.getId(cell);
		String parentId = visited.peekFirst();
		dotMap.put(parentId, id);
		visited.addFirst(id);
		return cont;
	}

	/**
	 * @see TransformationNodeVisitor#visit(SourceNode)
	 */
	@Override
	public boolean visit(SourceNode source) {
		boolean cont = true;

		if (ids.fetchId(source) != null) {
			cont = false;
		}

		String id = ids.getId(source);
		String parentId = visited.peekFirst();
		dotMap.put(parentId, id);
		visited.addFirst(id);
		return cont;
	}

	/**
	 * @see TransformationNodeVisitor#leave(TransformationTree)
	 */
	@Override
	public void leave(TransformationTree root) {
		visited.poll();
	}

	/**
	 * @see TransformationNodeVisitor#leave(TargetNode)
	 */
	@Override
	public void leave(TargetNode target) {
		visited.pollFirst();
	}

	/**
	 * @see TransformationNodeVisitor#leave(CellNode)
	 */
	@Override
	public void leave(CellNode cell) {
		visited.pollFirst();
	}

	/**
	 * @see TransformationNodeVisitor#leave(SourceNode)
	 */
	@Override
	public void leave(SourceNode source) {
		visited.pollFirst();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor#includeAnnotatedNodes()
	 */
	@Override
	public boolean includeAnnotatedNodes() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * returns all collected node-connections from the tree
	 * 
	 * @return a multi map with all connections
	 */
	public SetMultimap<String, String> getAllConnections() {
		return dotMap;
	}

	/**
	 * returns the identifiers of all collected nodes from the tree
	 * 
	 * @return a set of identifiers
	 */
	public Set<String> getAllIds() {
		return ids.getIds();
	}

	/**
	 * returns a certain node through his identifier
	 * 
	 * @param key the identifer
	 * @return the transformation node
	 */
	public TransformationNode getNode(String key) {
		return ids.getObject(key);
	}
}
