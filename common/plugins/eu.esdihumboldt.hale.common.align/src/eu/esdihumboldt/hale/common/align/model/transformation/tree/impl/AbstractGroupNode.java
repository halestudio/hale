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
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.GroupNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;

/**
 * Abstract group node implementation.
 * 
 * @author Simon Templer
 */
public abstract class AbstractGroupNode extends AbstractTransformationNode implements GroupNode {

	private final GroupNode parent;

	/**
	 * Create a group node.
	 * 
	 * @param parent the parent node, may be <code>null</code>
	 */
	public AbstractGroupNode(GroupNode parent) {
		super();
		this.parent = parent;
	}

	/**
	 * Get the fixed target node children, i.e. those that are not represented
	 * through an annotation.
	 * 
	 * @return the list of fixed child target nodes
	 */
	public abstract List<TargetNode> getFixedChildren();

	/**
	 * @see GroupNode#getParent()
	 */
	@Override
	public GroupNode getParent() {
		return parent;
	}

	/**
	 * @see GroupNode#getChildren(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TargetNode> getChildren(boolean includeAnnotations) {
		if (!includeAnnotations || getAnnotation(ANNOTATION_CHILDREN) == null) {
			return getFixedChildren();
		}
		else {
			List<TargetNode> result = new ArrayList<TargetNode>(getFixedChildren());
			result.addAll((Collection<TargetNode>) getAnnotation(ANNOTATION_CHILDREN));
			return result;
		}
	}

	/**
	 * @see GroupNode#addAnnotatedChild(TargetNode)
	 */
	@Override
	public void addAnnotatedChild(TargetNode node) {
		@SuppressWarnings("unchecked")
		List<TargetNode> ac = (List<TargetNode>) getAnnotation(ANNOTATION_CHILDREN);
		if (ac == null) {
			ac = new ArrayList<TargetNode>();
			setAnnotation(ANNOTATION_CHILDREN, ac);
		}
		ac.add(node);
	}

}
