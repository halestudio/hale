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

import eu.esdihumboldt.cst.MultiValue;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.GroupNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultGroup;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;

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
	 * @param typeLog the type transformation log
	 */
	public void populate(MutableInstance target, TransformationTree tree, TransformationLog typeLog) {
		populateGroup(target, tree, typeLog);
	}

	/**
	 * Get the value for a target node.
	 * 
	 * @param node the target node
	 * @param typeLog the type transformation log
	 * @return the value or {@link NoObject#NONE} representing no value
	 */
	private Object getValue(TargetNode node, TransformationLog typeLog) {
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

		boolean isProperty = node.getDefinition().asProperty() != null;
		boolean isGroup = node.getDefinition().asGroup() != null;
		if (isProperty && node.isDefined()) {
			// it's a property and we have a value/values
			Object nodeValue = node.getResult();
			if (!(nodeValue instanceof MultiValue)) {
				// pack single value into multivalue
				MultiValue nodeMultiValue = new MultiValue();
				nodeMultiValue.add(nodeValue);
				nodeValue = nodeMultiValue;
			}
			MultiValue nodeMultiValue = (MultiValue) nodeValue;
			if (!nodeMultiValue.isEmpty()) {
				// Create n instances
				MultiValue resultMultiValue = new MultiValue(nodeMultiValue.size());

				for (Object value : nodeMultiValue) {
					// the value may have been wrapped in an Instance
					if (value instanceof Instance) {
						value = ((Instance) value).getValue();
					}

					MutableInstance instance = new DefaultInstance(node.getDefinition()
							.asProperty().getPropertyType(), null);
					instance.setValue(value);

					// XXX since this is the same for all instances maybe do
					// this on a dummy and only copy properties for each?
					// XXX MultiValue w/ target node children => strange results
					populateGroup(instance, node, typeLog);

					resultMultiValue.add(instance);
				}

				return resultMultiValue;
			}
			// if nodeMultiValue is empty fall through to below
			// it the instance could still have children even without a value
		}

		// it's a property or group with no value
		MutableGroup group;
		if (isGroup) {
			group = new DefaultGroup(node.getDefinition().asGroup());
		}
		else if (isProperty) {
			group = new DefaultInstance(node.getDefinition().asProperty().getPropertyType(), null);
		}
		else {
			throw new IllegalStateException("Illegal child definition");
		}

		// populate with children
		if (populateGroup(group, node, typeLog)) {
			return group;
		}
		else {
			return NoObject.NONE;
		}
	}

	/**
	 * Populates a group with values from its children.
	 * 
	 * @param group the group
	 * @param node the node associated with the group
	 * @param typeLog the type transformation log
	 * @return if any values were added to the group
	 */
	private boolean populateGroup(MutableGroup group, GroupNode node, TransformationLog typeLog) {
		boolean anyValue = false;
		for (TargetNode child : node.getChildren(true)) {
			Object value = getValue(child, typeLog);
			if (value != NoObject.NONE) {
				// add value to group
				if (value instanceof MultiValue) {
					MultiValue multiValue = (MultiValue) value;
					int toAdd = multiValue.size();

					// check cardinality
					Cardinality card = DefinitionUtil.getCardinality(child.getDefinition());
					if (card.getMaxOccurs() != Cardinality.UNBOUNDED && card.getMaxOccurs() < toAdd) {
						toAdd = (int) card.getMaxOccurs();
						typeLog.warn(typeLog.createMessage("Too many values present for "
								+ child.getDefinition().getDisplayName()
								+ ". Limiting to match cardinality.", null));
					}

					// add properties
					for (int i = 0; i < toAdd; i++) {
						group.addProperty(child.getDefinition().getName(), multiValue.get(i));
					}

					if (toAdd > 0) {
						anyValue = true;
					}
				}
				else {
					group.addProperty(child.getDefinition().getName(), value);
					anyValue = true;
				}
			}
		}
		return anyValue;
	}

}
