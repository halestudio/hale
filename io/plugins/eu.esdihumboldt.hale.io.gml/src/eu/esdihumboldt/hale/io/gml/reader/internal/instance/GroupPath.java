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

package eu.esdihumboldt.hale.io.gml.reader.internal.instance;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultGroup;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.AllGroupFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;

/**
 * Represents a path of groups in two parts, the existing parent
 * {@link MutableGroup}s and the non-existent children represented by a
 * {@link DefinitionGroup}
 * 
 * @author Simon Templer
 */
public class GroupPath {

	private final List<MutableGroup> parents;

	private final List<DefinitionGroup> children;

	/**
	 * Create a group path
	 * 
	 * @param parents the list of parent groups, may neither be
	 *            <code>null</code> nor empty
	 * @param children the list of child definition groups, may be
	 *            <code>null</code>
	 */
	public GroupPath(List<MutableGroup> parents, List<DefinitionGroup> children) {
		super();

		checkNotNull(parents);
		checkArgument(!parents.isEmpty());

		this.parents = parents;
		this.children = children;
	}

	/**
	 * @return the parents
	 */
	public List<MutableGroup> getParents() {
		return parents;
	}

	/**
	 * @return the children, may be <code>null</code>
	 */
	public List<DefinitionGroup> getChildren() {
		return children;
	}

	/**
	 * Create groups for the children in the path (which are only represented as
	 * definitions). May only be called if the path is valid. This will also
	 * update the path to include the groups instead of the definitions.
	 * 
	 * @return the list of created groups
	 * 
	 * @see #isValid()
	 */
	protected List<MutableGroup> createChildGroups() {
		MutableGroup parent = parents.get(parents.size() - 1);
		final List<MutableGroup> result = new ArrayList<MutableGroup>();

		for (DefinitionGroup child : children) {
			checkState(child instanceof GroupPropertyDefinition);

			// create group
			MutableGroup group = new DefaultGroup(child);

			// add to parent
			QName propertyName = ((GroupPropertyDefinition) child).getName();
			parent.addProperty(propertyName, group);

			// add to result
			result.add(group);

			// prepare for next iteration
			parent = group;
		}

		// update children and parents
		children.clear();
		parents.addAll(result);

		return result;
	}

	/**
	 * Determines if the group path in this configuration is valid in respect to
	 * the creation of new groups based on the contained definition groups.
	 * 
	 * @return if the path is valid
	 */
	public boolean isValid() {
		if (children == null || children.isEmpty()) {
			return true; // parents is assumed to be valid
		}

		MutableGroup parentGroup = parents.get(parents.size() - 1);
		DefinitionGroup parentDef = parentGroup.getDefinition();
		for (DefinitionGroup child : children) {
			if (child instanceof GroupPropertyDefinition) {
				if (!GroupUtil.allowAdd(parentGroup, parentDef,
						((GroupPropertyDefinition) child).getName())) {
					return false;
				}
			}
			else {
				// XXX TypeDefinitions not supported in path
				return false;
			}

			// prepare next iteration
			parentGroup = null;
			parentDef = child;
		}

		return true;
	}

	/**
	 * Determines if the adding a property value for the given property to the
	 * last element in the path is allowed.
	 * 
	 * @param propertyName the property name
	 * @param strict states if additional checks are applied apart from whether
	 *            the property exists
	 * @param ignoreNamespaces if a property with a differing namespace may be
	 *            accepted
	 * @return if adding the property value to the last element in the path is
	 *         allowed
	 */
	public boolean allowAdd(QName propertyName, boolean strict, boolean ignoreNamespaces) {
		if (children == null || children.isEmpty()) {
			// check last parent
			MutableGroup parent = parents.get(parents.size() - 1);
			ChildDefinition<?> child = GroupUtil.findChild(parent.getDefinition(), propertyName,
					ignoreNamespaces);
			if (child.asProperty() != null) {
				return !strict || GroupUtil.allowAdd(parent, null, child.asProperty().getName());
			}
			else {
				return false;
			}
		}
		else {
			// check last child
			DefinitionGroup child = children.get(children.size() - 1);
			ChildDefinition<?> property = GroupUtil.findChild(child, propertyName,
					ignoreNamespaces);
			if (property == null) {
				return false;
			}

			if (child instanceof GroupPropertyDefinition) {
				if (((GroupPropertyDefinition) child).getConstraint(ChoiceFlag.class).isEnabled()) {
					// group is a choice
					return true;
				}
				else if (((GroupPropertyDefinition) child).getConstraint(AllGroupFlag.class)
						.isEnabled()) {
					// TODO must check if property was added already (in an
					// "all" group each element must not occur more than once)
					return true; // XXX
				}
			}

			return !strict || GroupUtil.allowAdd(null, child, property.getName());
		}
	}

	/**
	 * Get the last definition group in the path
	 * 
	 * @return the last definition group
	 */
	public DefinitionGroup getLastDefinition() {
		if (children != null && !children.isEmpty()) {
			return children.get(children.size() - 1);
		}

		return parents.get(parents.size() - 1).getDefinition();
	}

	/**
	 * Get the last group in the path.<br>
	 * <br>
	 * Will create the child groups for which only definitions are present and
	 * update the path accordingly before getting the last group object.
	 * 
	 * @param strict if the path should be checked for validity
	 * @return the last group in the path
	 * @throws IllegalStateException if the path is not valid
	 */
	public MutableGroup getLast(boolean strict) throws IllegalStateException {
		return getAllGroups(strict).peek();
	}

	/**
	 * Get all groups in the path.<br>
	 * <br>
	 * Will create the child groups for which only definitions are present and
	 * update the path accordingly before returning all group objects.
	 * 
	 * @param strict if the path should be checked for validity
	 * @return the all groups in the path
	 * @throws IllegalStateException if the path is not valid
	 */
	public Stack<MutableGroup> getAllGroups(boolean strict) throws IllegalStateException {
		if (children != null && !children.isEmpty()) {
			if (strict && !isValid()) {
				throw new IllegalStateException("Attempt to create groups in an invalid path.");
			}
			createChildGroups();
		}

		Stack<MutableGroup> result = new Stack<MutableGroup>();
		result.addAll(parents);
		return result;
	}

}
