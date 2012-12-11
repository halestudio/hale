/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.transformations.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Traverser for a {@link TransformationTree} that visits target nodes in the
 * correct order needed for valid XML output.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationTraverser {

	/**
	 * Traverse the given transformation tree.
	 * 
	 * @param tree the transformation tree
	 */
	public void traverse(TransformationTree tree) {
		traverseTargets(tree.getChildren(includeAnnotations()), tree.getType());
	}

	/**
	 * @return if annotated children should be included during the traversal
	 */
	protected boolean includeAnnotations() {
		return false;
	}

	/**
	 * Traverse the given target nodes in the correct order.
	 * 
	 * @param nodes the target nodes
	 * @param parent the parent type or group
	 */
	protected void traverseTargets(Iterable<TargetNode> nodes, DefinitionGroup parent) {
		// organized nodes by name
		Map<QName, TargetNode> namedNodes = new HashMap<QName, TargetNode>();
		for (TargetNode node : nodes) {
			namedNodes.put(node.getDefinition().getName(), node);
		}

		List<QName> childNames = new ArrayList<QName>();
		// determine correct order for children
		for (ChildDefinition<?> child : DefinitionUtil.getAllChildren(parent)) {
			if (child.asProperty() != null
					&& child.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
				// child is an XML attribute
				// and must be handled first
				childNames.add(0, child.getName());
			}
			else {
				// child must be handled in order as defined
				childNames.add(child.getName());
			}
		}

		// handle target nodes in order
		for (QName childName : childNames) {
			TargetNode node = namedNodes.get(childName);
			if (node != null) {
				visitProperty(node);

				// traverse children
				DefinitionGroup group = (node.getDefinition().asGroup() == null) //
				? (node.getDefinition().asProperty().getPropertyType())
						: (node.getDefinition().asGroup());
				traverseTargets(node.getChildren(includeAnnotations()), group);

				leaveProperty(node);
			}
			else {
				ChildDefinition<?> child = parent.getChild(childName);
				if (DefinitionUtil.getCardinality(child).getMinOccurs() > 0) {
					/*
					 * Allow to handle unmapped mandatory properties, e.g.
					 * elements that are mandatory but nillable.
					 */
					handleUnmappedProperty(child);
				}
			}
		}
	}

	/**
	 * Handle a child for which not target node exists.
	 * 
	 * @param definition the child definition
	 */
	protected abstract void handleUnmappedProperty(ChildDefinition<?> definition);

	/**
	 * Called when leaving a target node and its children.
	 * 
	 * @param node the target node
	 */
	protected abstract void leaveProperty(TargetNode node);

	/**
	 * Visit a target node.
	 * 
	 * @param node the target node
	 */
	protected abstract void visitProperty(TargetNode node);

}
