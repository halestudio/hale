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
