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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import java.util.List;

/**
 * A target type, group or property node
 * 
 * @author Simon Templer
 */
public interface GroupNode extends TransformationNode {

	/**
	 * Name of the children annotation. It represents a list of additional
	 * children.
	 */
	public static final String ANNOTATION_CHILDREN = "children";

	/**
	 * Get the type/group/property's children
	 * 
	 * @param includeAnnotations if annotated children should be included
	 * @return the node children
	 */
	public List<TargetNode> getChildren(boolean includeAnnotations);

	/**
	 * Add a node as annotated child. This means the child is removed on
	 * {@link #reset()}.
	 * 
	 * @param node the target node to add as annotated child
	 */
	public void addAnnotatedChild(TargetNode node);

	/**
	 * Get the parent group node
	 * 
	 * @return the parent group node, may be <code>null</code>
	 */
	public GroupNode getParent();

}
