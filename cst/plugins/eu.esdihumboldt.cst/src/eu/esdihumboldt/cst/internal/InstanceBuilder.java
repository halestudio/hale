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

package eu.esdihumboldt.cst.internal;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.GroupNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.OGroup;

/**
 * Populates an instance from a transformation tree.
 * 
 * @author Simon Templer
 */
public class InstanceBuilder {

	/**
	 * Represents no object.
	 */
	private static enum NoObject {
		NONE;
	}

	/**
	 * Populate the given instance from a transformation tree.
	 * 
	 * @param target the target instance
	 * @param tree the transformation tree
	 */
	public void populate(MutableInstance target, TransformationTree tree) {
		populateGroup(target, tree);
	}

	/**
	 * Get the value for a target node.
	 * 
	 * @param node the target node
	 * @return the value or {@link NoObject#NONE} representing no value
	 */
	private Object getValue(TargetNode node) {
		if (node.getChildren(true).isEmpty()) {
			// simple leaf
			if (node.isDefined()) {
				// XXX case where an Instance should be returned? XXX according
				// to the definition?!
				// XXX this is done in FunctionExecutor
				return node.getResult();
			}
			else {
				return NoObject.NONE;
			}
		}

		// group or complex property
		boolean empty = true;
		MutableGroup group;
		if (node.getDefinition().asGroup() != null) {
			group = new OGroup(node.getDefinition().asGroup());
		}
		else if (node.getDefinition().asProperty() != null) {
			group = new DefaultInstance(node.getDefinition().asProperty().getPropertyType(), null);
		}
		else {
			throw new IllegalStateException("Illegal child definition");
		}

		// populate with children
		empty = !populateGroup(group, node);

		// populate with instance value (if applicable)
		if (group instanceof MutableInstance) {
			if (node.isDefined()) {
				Object nodeValue = node.getResult();
				// FunctionExecutor may have wrapped the value in an instance
				if (nodeValue instanceof Instance) {
					// extract the value
					nodeValue = ((Instance) nodeValue).getValue();
				}

				MutableInstance instance = (MutableInstance) group;
				instance.setValue(nodeValue);
				empty = false;
			}
		}

		if (empty) {
			return NoObject.NONE;
		}
		else {
			return group;
		}
	}

	/**
	 * Populates a group with values from its children.
	 * 
	 * @param group the group
	 * @param node the node associated with the group
	 * @return if any values were added to the group
	 */
	private boolean populateGroup(MutableGroup group, GroupNode node) {
		boolean anyValue = false;
		for (TargetNode child : node.getChildren(true)) {
			Object value = getValue(child);
			if (value != NoObject.NONE) {
				// add value to group
				group.addProperty(child.getDefinition().getName(), value);

				// valid value
				anyValue = true;
			}
		}
		return anyValue;
	}

}
