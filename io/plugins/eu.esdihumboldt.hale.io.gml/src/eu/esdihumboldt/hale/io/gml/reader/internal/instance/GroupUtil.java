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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.IgnoreOrderFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Utility methods regarding group handling
 * 
 * @author Simon Templer
 */
public class GroupUtil {

	private static final ALogger log = ALoggerFactory.getLogger(GroupUtil.class);

	/**
	 * Determine the property definition for the given property name.
	 * 
	 * @param groups the stack of the current group objects. The topmost element
	 *            is the current group object
	 * @param propertyName the property name
	 * @param allowFallback states if falling back to non-strict mode is allowed
	 *            for determining the property definition
	 * @param ignoreNamespaces if a property with a differing namespace may be
	 *            accepted
	 * @return the group property or <code>null</code> if none is found
	 */
	static GroupProperty determineProperty(List<MutableGroup> groups, QName propertyName,
			boolean allowFallback, boolean ignoreNamespaces) {
		return determineProperty(groups, propertyName, true, allowFallback, ignoreNamespaces);
	}

	/**
	 * Determine the property definition for the given property name.
	 * 
	 * @param groups the stack of the current group objects. The topmost element
	 *            is the current group object
	 * @param propertyName the property name
	 * @param strict states if for assessing possible property definitions
	 *            strict checks regarding the structure are applied
	 * @param allowFallback states if with strict mode being enabled, falling
	 *            back to non-strict mode is allowed (this will not be
	 *            propagated to subsequent calls)
	 * @param ignoreNamespaces if a property with a differing namespace may be
	 *            accepted
	 * @return the group property or <code>null</code> if none is found
	 */
	private static GroupProperty determineProperty(List<MutableGroup> groups, QName propertyName,
			boolean strict, boolean allowFallback, boolean ignoreNamespaces) {
		if (groups.isEmpty()) {
			return null;
		}

		// the current group
		final MutableGroup currentGroup = groups.get(groups.size() - 1);
		// the queue to collect the siblings of the current group with
		LinkedList<GroupPath> siblings = new LinkedList<GroupPath>();

		/*
		 * Policy: find the property as high in the hierarchy as possible
		 * 
		 * This might lead to problems with some special schemas, e.g. if a
		 * group is defined that allows unbounded occurrences of an element X
		 * and the parent type allows one occurrence there will be trouble if we
		 * have more than two or three of those elements (depending on group and
		 * element cardinalities).
		 * 
		 * If this really poses a problem in the practice we might need
		 * configuration parameters to use different policies. IMHO (ST) in well
		 * designed schemas this problem will not occur.
		 * 
		 * This problem only arises because we read all the data from the stream
		 * and don't know anything about what comes ahead - another possibility
		 * could be to change this behavior where needed.
		 */

		// preferred 1: property of a parent group
		List<MutableGroup> keep = new ArrayList<MutableGroup>(groups);
		List<MutableGroup> close = new ArrayList<MutableGroup>();
		// sort groups in those that must be kept and those that may be closed
		for (int i = keep.size() - 1; i >= 0 && allowClose(keep.get(i)); i--) {
			close.add(0, keep.get(i));
			keep.remove(i);
		}
		if (!close.isEmpty()) {
			// collect parents groups
			List<MutableGroup> parents = new ArrayList<MutableGroup>(close);
			parents.remove(parents.size() - 1); // remove current group
			if (!keep.isEmpty()) {
				parents.add(0, keep.get(0)); // insert top allowed parent first
												// in list
			}

			int maxDescent = close.size() - 1;

			// prototype that is copied for each parent
			List<MutableGroup> stackPrototype = new ArrayList<MutableGroup>(keep);

			LinkedList<GroupPath> level = new LinkedList<GroupPath>();
			LinkedList<GroupPath> nextLevel = new LinkedList<GroupPath>();
			for (int i = 0; i < parents.size(); i++) {
				// add existing parent
				GroupPath path = new GroupPath(new ArrayList<MutableGroup>(stackPrototype), null);
				level.addFirst(path);
				GroupProperty gp = null;

				// check for a direct match in the group
				PropertyDefinition property = determineDirectProperty(parents.get(i), propertyName,
						strict, ignoreNamespaces);
				if (property != null) {
					gp = new GroupProperty(property, path);
				}

				if (gp == null && maxDescent >= 0) { // >= 0 because also for
														// maxDescent 0 we get
														// siblings
					// check the sub-properties
					gp = determineSubProperty(level, propertyName, nextLevel, 0, strict,
							ignoreNamespaces);
				}

				if (gp != null) {
					return gp;
				}

				// XXX remove XXX add twin of parent to next level check
				// (because it was ignored)
//				List<MutableGroup> twinParents = new ArrayList<MutableGroup>(stackPrototype);
//				List<DefinitionGroup> twinChildren = new ArrayList<DefinitionGroup>();
//				twinChildren.add(parents.get(i).getDefinition());
//				GroupPath twin = new GroupPath(twinParents, twinChildren);
//				nextLevel.add(twin);

				// prepare stack prototype for next parent
				if (i + 1 < parents.size()) {
					stackPrototype.add(parents.get(i + 1));
				}
				// swap lists, clear nextLevel
				LinkedList<GroupPath> tmp = level;
				level = nextLevel;
				nextLevel = tmp;
				nextLevel.clear();
			}

			siblings = level;
		}

		// preferred 2: property of the current group
		PropertyDefinition property = determineDirectProperty(currentGroup, propertyName, strict,
				ignoreNamespaces);
		if (property != null) {
			return new GroupProperty(property, new GroupPath(groups, null));
		}

		// preferred 3: property of a sub-group, sibling group or sibling
		// sub-group
		siblings.addFirst(new GroupPath(groups, null)); // add current group
		// check the sub-properties
		GroupProperty gp = determineSubProperty(siblings, propertyName, null, -1, strict,
				ignoreNamespaces);

		if (gp != null) {
			return gp;
		}

		if (strict && allowFallback) {
			// fall-back: property in any group without validity checks
			// XXX though allowClose will still be strict
			log.warn(MessageFormat
					.format("Could not find valid property path for {0}, source data might be invalid regarding the source schema.",
							propertyName));
			return determineProperty(groups, propertyName, false, false, ignoreNamespaces);
		}

		return null;
	}

	/**
	 * Find a child definition based on the name.
	 * 
	 * @param parent the parent type or group
	 * @param propertyName the property name
	 * @param ignoreNamespaces if matches with differing namespace should be
	 *            allowed
	 * @return the child definition or <code>null</code>
	 */
	@Nullable
	static ChildDefinition<?> findChild(DefinitionGroup parent, QName propertyName,
			boolean ignoreNamespaces) {
		ChildDefinition<?> result = parent.getChild(propertyName);

		if (result == null && ignoreNamespaces) {
			// look for local name match
			for (ChildDefinition<?> candidate : DefinitionUtil.getAllChildren(parent)) {
				if (candidate.getName().getLocalPart().equals(propertyName.getLocalPart())) {
					result = candidate;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Determines if a property value for the given property name may be added
	 * to the given group and returns the corresponding property definition.
	 * 
	 * @param group the group
	 * @param propertyName the property name
	 * @param strict states if additional checks are applied apart from whether
	 *            the property exists
	 * @param ignoreNamespaces if a property with a differing namespace may be
	 *            accepted
	 * @return the property definition or <code>null</code> if none is found or
	 *         no value may be added
	 */
	private static PropertyDefinition determineDirectProperty(MutableGroup group,
			QName propertyName, boolean strict, boolean ignoreNamespaces) {
		ChildDefinition<?> child = findChild(group.getDefinition(), propertyName, ignoreNamespaces);
		if (child != null && child.asProperty() != null
				&& (!strict || allowAdd(group, null, child.asProperty().getName()))) {
			return child.asProperty();
		}

		return null;
	}

	/**
	 * Determine the property definition for the given property name in
	 * sub-groups of the given group stack.
	 * 
	 * @param paths the group paths whose children shall be checked for the
	 *            property
	 * @param propertyName the property name
	 * @param leafs the queue is populated with the leafs in the explored
	 *            definition group tree that are not processed because of the
	 *            max descent, may be <code>null</code> if no population is
	 *            needed
	 * @param maxDescent the maximum descent, -1 for no maximum descent
	 * @param strict states if additional checks are applied apart from whether
	 *            the property exists
	 * @param ignoreNamespaces if a property with a differing namespace may be
	 *            accepted
	 * @return the property definition or <code>null</code> if none is found
	 */
	private static GroupProperty determineSubProperty(Queue<GroupPath> paths, QName propertyName,
			Queue<GroupPath> leafs, int maxDescent, boolean strict, boolean ignoreNamespaces) {
		if (maxDescent != -1 && maxDescent < 0) {
			return null;
		}

		while (!paths.isEmpty()) {
			GroupPath path = paths.poll();

			DefinitionGroup lastDef = null;
			if (path.getChildren() != null && !path.getChildren().isEmpty()) {
				// check if path is a valid result
				if (path.allowAdd(propertyName, strict, ignoreNamespaces)) {
					ChildDefinition<?> property = findChild(path.getLastDefinition(), propertyName,
							ignoreNamespaces);

					if (property != null && property.asProperty() != null) {
						// return group property
						return new GroupProperty(property.asProperty(), path);
					}
					else {
						log.error("False positive for property candidate.");
					}
				}

				lastDef = path.getLastDefinition();
			}
			else {
				// the first path which must not be checked, just the children
				// must be added to the queue
				List<MutableGroup> parents = path.getParents();
				if (parents != null && !parents.isEmpty()) {
					lastDef = parents.get(parents.size() - 1).getDefinition();
				}
			}

			if (lastDef != null) {
				// add children to queue
				Collection<? extends ChildDefinition<?>> children = DefinitionUtil
						.getAllChildren(lastDef);

				for (ChildDefinition<?> child : children) {
					if (child.asGroup() != null
							&& (path.getChildren() == null || !path.getChildren().contains(
									child.asGroup()))) { // (check for
															// definition cycle)
						List<DefinitionGroup> childDefs = new ArrayList<DefinitionGroup>();
						if (path.getChildren() != null) {
							childDefs.addAll(path.getChildren());
						}
						childDefs.add(child.asGroup());

						GroupPath newPath = new GroupPath(path.getParents(), childDefs);

						// check if path is valid
						if (!strict || newPath.isValid()) {
							// check max descent
							if (maxDescent >= 0 && newPath.getChildren().size() > maxDescent) {
								if (leafs != null) {
									leafs.add(newPath);
								}
							}
							else {
								paths.add(newPath);
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Determines if the given group is valid and may be closed
	 * 
	 * @param currentGroup the current group
	 * @return if the group may be closed
	 */
	private static boolean allowClose(MutableGroup currentGroup) {
		if (currentGroup instanceof Instance) {
			return false; // instances may never be closed, they have no parent
							// in the group stack
		}

		if (currentGroup.getDefinition() instanceof GroupPropertyDefinition
				&& ((GroupPropertyDefinition) currentGroup.getDefinition()).getConstraint(
						ChoiceFlag.class).isEnabled()) {
			// group is a choice
			Iterator<QName> it = currentGroup.getPropertyNames().iterator();
			if (it.hasNext()) {
				// choice has at least on value set -> check cardinality for the
				// corresponding property
				QName name = it.next();
				return isValidCardinality(currentGroup, currentGroup.getDefinition().getChild(name));
			}
			// else check all children like below
		}

		// determine all children
		Collection<? extends ChildDefinition<?>> children = DefinitionUtil
				.getAllChildren(currentGroup.getDefinition());

		// check cardinality of children
		for (ChildDefinition<?> childDef : children) {
			if (!isValidCardinality(currentGroup, childDef)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Determines if a child is contained in a given group with a valid minimum
	 * cardinality.
	 * 
	 * @param group the group
	 * @param childDef the child definition
	 * @return if the minimum cardinality of the child definition is matched in
	 *         the group
	 */
	static boolean isValidCardinality(Group group, ChildDefinition<?> childDef) {
		Cardinality cardinality = null;
		if (childDef.asProperty() != null) {
			cardinality = childDef.asProperty().getConstraint(Cardinality.class);
		}
		else if (childDef.asGroup() != null) {
			cardinality = childDef.asGroup().getConstraint(Cardinality.class);
		}
		else {
			log.error("Unrecognized child definition.");
		}

		if (cardinality != null) {
			// check minimum
			long min = cardinality.getMinOccurs();
			if (min > 0 && min != Cardinality.UNBOUNDED) {
				Object[] values = group.getProperty(childDef.getName());
				int count = (values == null) ? (0) : (values.length);
				if (min > count) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Determines if another value of the given property may be added to the
	 * given group.
	 * 
	 * @param group the group, <code>null</code> represents an empty group
	 * @param groupDef the definition of the given group, may be
	 *            <code>null</code> if the group is not <code>null</code>
	 * @param propertyName the property name
	 * @return if another property value may be added to the group
	 */
	@SuppressWarnings("null")
	static boolean allowAdd(Group group, DefinitionGroup groupDef, QName propertyName) {
		if (group == null && groupDef == null) {
			throw new IllegalArgumentException();
		}

		final DefinitionGroup def;
		if (groupDef == null) {
			def = group.getDefinition();
		}
		else {
			def = groupDef;
		}

		if (group == null) {
			// create an empty dummy group if none is specified
			group = new Group() {

				@Override
				public Object[] getProperty(QName propertyName) {
					return null;
				}

				@Override
				public Iterable<QName> getPropertyNames() {
					return Collections.emptyList();
				}

				@Override
				public DefinitionGroup getDefinition() {
					return def;
				}
			};
		}

		if (def instanceof GroupPropertyDefinition) {
			// group property
			GroupPropertyDefinition gpdef = (GroupPropertyDefinition) def;

			if (gpdef.getConstraint(ChoiceFlag.class).isEnabled()) {
				// choice
				// a choice may only contain one of its properties
				for (QName pName : group.getPropertyNames()) {
					if (!pName.equals(propertyName)) {
						// other property is present -> may not add property
						// value
						return false;
					}
				}
				// check cardinality
				return allowAddCheckCardinality(group, propertyName);
			}
			else {
				// sequence, group(, attributeGroup)

				// check order
				if (!allowAddCheckOrder(group, propertyName, def)) {
					return false;
				}

				// check cardinality
				return allowAddCheckCardinality(group, propertyName);
			}
		}
		else if (def instanceof TypeDefinition) {
			// type
			TypeDefinition typeDef = (TypeDefinition) def;

			// check order unless IgnoreOrderFlag is set (e.g. in case of an
			// <xs:all> group)
			if (!typeDef.getConstraint(IgnoreOrderFlag.class).isEnabled()
					&& !allowAddCheckOrder(group, propertyName, typeDef)) {
				return false;
			}

			// check cardinality
			return allowAddCheckCardinality(group, propertyName);
		}

		return false;
	}

	/**
	 * Determines if another value of the given property may be added to the
	 * given group based on values available in the group and the order of the
	 * child definitions in the given definition group.
	 * 
	 * @param group the group, <code>null</code> represents an empty group
	 * @param propertyName the property name
	 * @param groupDef the definition group
	 * @return if another property value may be added to the group based on the
	 *         values and the child definition order
	 */
	private static boolean allowAddCheckOrder(Group group, QName propertyName,
			final DefinitionGroup groupDef) {
		boolean before = true;

		Collection<? extends ChildDefinition<?>> children = DefinitionUtil.getAllChildren(groupDef);

		for (ChildDefinition<?> childDef : children) {
			if (childDef.getName().equals(propertyName)) {
				before = false;
			}
			else {
				// ignore XML attributes
				if (childDef.asProperty() != null
						&& childDef.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
					continue;
				}
				// ignore groups that contain no elements
				if (childDef.asGroup() != null && !StreamGmlHelper.hasElements(childDef.asGroup())) {
					continue;
				}

				if (before) {
					// child before the property
					// the property may only be added if all children before are
					// valid in their cardinality
					if (!isValidCardinality(group, childDef)) {
						return false;
					}
				}
				else {
					// child after the property
					// the property may only be added if there are no values for
					// children after the property
					Object[] values = group.getProperty(childDef.getName());
					if (values != null && values.length > 0) {
						return false;
					}
				}
			}
		}

		// no fail -> allow add
		return true;
	}

	/**
	 * Determines if another value of the given property may be added to the
	 * given group based on the cardinality of the property.
	 * 
	 * @param group the group
	 * @param propertyName the property name
	 * @return if another property value may be added to the group based on the
	 *         property cardinality
	 */
	private static boolean allowAddCheckCardinality(Group group, QName propertyName) {
		ChildDefinition<?> child = group.getDefinition().getChild(propertyName);
		Cardinality cardinality = DefinitionUtil.getCardinality(child);

		// check maximum
		long max = cardinality.getMaxOccurs();
		if (max == Cardinality.UNBOUNDED) {
			return true; // add allowed in any case
		}
		else if (max <= 0) {
			return false; // add never allowed
		}

		Object[] values = group.getProperty(propertyName);
		if (values == null) {
			return true; // allowed because max is 1 or more
		}

		return values.length < max;
	}

}
