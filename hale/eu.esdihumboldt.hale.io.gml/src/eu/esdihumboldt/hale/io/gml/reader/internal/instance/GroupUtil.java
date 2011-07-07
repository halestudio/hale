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

package eu.esdihumboldt.hale.io.gml.reader.internal.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.xml.namespace.QName;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.instance.model.Group;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.MutableGroup;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.schema.model.constraint.property.ChoiceFlag;

/**
 * Utility methods regarding group handling
 * @author Simon Templer
 */
public class GroupUtil {
	
	private static final ALogger log = ALoggerFactory.getLogger(GroupUtil.class);

	/**
	 * Determine the property definition for the given property name.
	 * The given group stack will be updated so that the parent group object of 
	 * the property will be the top (last) element on the stack. 
	 * @param groups the stack of the current group objects. The topmost element
	 *   is the current group object 
	 * @param propertyName the property name
	 * @return the property definition or <code>null</code> if none is found
	 */
	static PropertyDefinition determineProperty(
			List<MutableGroup> groups, QName propertyName) {
		if (groups.isEmpty()) {
			return null;
		}
		
		// the current group
		final MutableGroup currentGroup = groups.get(groups.size() -1);
		// the queue to collect the siblings of the current group with
		final Queue<GroupPath> siblings = new LinkedList<GroupPath>();
		
		/*
		 * Policy: find the property as high in the hierarchy as possible
		 * 
		 * This might lead to problems with some special schemas, e.g. if
		 * a group is defined that allows unbounded occurrences of an element X
		 * and the parent type allows one occurrence there will be trouble if we
		 * have more than two or three of those elements (depending on group
		 * and element cardinalities).
		 * 
		 * If this really poses a problem in the practice we might need
		 * configuration parameters to use different policies. IMHO (ST) in
		 * well designed schemas this problem will not occur.
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
			keep.remove(i--);
		}
		if (!close.isEmpty()) {
			// collect parents groups
			List<MutableGroup> parents = new ArrayList<MutableGroup>(close);
			parents.remove(parents.size() - 1); // remove current group
			if (!keep.isEmpty()) {
				parents.add(0, keep.get(0)); // insert top allowed parent first in list
			}
			
			int maxDescent = close.size() - 1;
			List<MutableGroup> stackPrototype = new ArrayList<MutableGroup>(keep); // prototype that is copied for each parent
			for (int i = 0; i < parents.size(); i++) {
				List<MutableGroup> stack = new ArrayList<MutableGroup>(stackPrototype);
				
				// check for a direct match in the group
				PropertyDefinition property = determineDirectProperty(
						parents.get(i), propertyName);
				
				if (property == null && maxDescent > 0) {
					// check the sub-properties
					MutableGroup ignore = (i + 1 < parents.size())?(parents.get(i + 1)):(currentGroup);
					property = determineSubProperty(stack, 
							propertyName, ignore, siblings, maxDescent);
				}
				
				if (property != null) {
					// use stack adapted by determineSubProperty
					groups.clear();
					groups.addAll(stack);
					return property;
				}
				
				// prepare stack prototype for next parent
				if (i + 1 < parents.size()) {
					stackPrototype.add(parents.get(i+1));
				}
				// adjust max descent for next parent
				maxDescent--;
			}
		}
		
		// preferred 2: property of the current group
		PropertyDefinition property = determineDirectProperty(currentGroup, propertyName);
		if (property != null) {
			return property;
		}
		
		// preferred 3: property of a sibling group
		//TODO
		
		// preferred 4: property of a sub-group or a sibling sub-group
		//TODO
		
		//XXX fall-back: property in any group without validity checks?
		
		return null;
	}

	/**
	 * Determines if a property value for the given property name may be added
	 * to the given group and returns the corresponding property definition.
	 * @param group the group
	 * @param propertyName the property name
	 * @return the property definition or <code>null</code> if none is found or
	 *   no value may be added
	 */
	private static PropertyDefinition determineDirectProperty(
			MutableGroup group, QName propertyName) {
		ChildDefinition<?> child = group.getDefinition().getChild(propertyName);
		if (child != null && child.asProperty() != null && 
				allowAdd(group, child.asProperty())) {
			return child.asProperty();
		}
		
		return null;
	}

	/**
	 * Determine the property definition for the given property name in 
	 * sub-groups of the given group stack.
	 * The given group stack will be updated so that the parent group object of 
	 * the property will be the top (last) element on the stack.
	 * @param groups the stack of the current group objects. The topmost element
	 *   is the current group object 
	 * @param propertyName the property name
	 * @param ignore a group representing a child to ignore, may be <code>null</code>
	 * @param leafs the queue is populated with the leafs in the explored 
	 *   definition group tree that are not processed because of the max descent
	 * @param maxDescent the maximum descent, -1 for no maximum descent
	 * @return the property definition or <code>null</code> if none is found
	 */
	private static PropertyDefinition determineSubProperty(
			List<MutableGroup> groups, QName propertyName, MutableGroup ignore,
			Queue<GroupPath> leafs, int maxDescent) {
		if (maxDescent <= 0 || groups.isEmpty()) {
			return null;
		}
		
		// set for all already checked definitions to prevent cycles with new groups
		Set<DefinitionGroup> checkedDefinitions = new HashSet<DefinitionGroup>();
		
		final List<MutableGroup> pathParents = new ArrayList<MutableGroup>(groups);
		
		Queue<GroupPath> paths = new LinkedList<GroupPath>();
		paths.add(new GroupPath(pathParents, null));
		
		while (!paths.isEmpty()) {
			GroupPath path = paths.poll();
			
			DefinitionGroup lastDef = null;
			if (path.getChildren() != null && !path.getChildren().isEmpty()) {
				// check if path is a valid result
				if (path.allowAdd(propertyName)) {
					//FIXME
					//TODO create missing groups
					//TODO update stack
					//TODO return property def
				}
				
				lastDef = path.getLast();
			}
			else {
				// the first path which must not be checked, just the children must be added to the queue
				List<MutableGroup> parents = path.getParents();
				if (parents != null && !parents.isEmpty()) {
					lastDef = parents.get(parents.size() - 1).getDefinition();
				}
			}
			
			if (lastDef != null) {
				// add children to queue
				Collection<? extends ChildDefinition<?>> children = DefinitionUtil.getAllChildren(lastDef);
				for (ChildDefinition<?> child : children) {
					if (child.asGroup() != null && !checkedDefinitions.contains(child.asGroup())) {
						List<DefinitionGroup> childDefs = new ArrayList<DefinitionGroup>();
						if (path.getChildren() != null) {
							childDefs.addAll(path.getChildren());
						}
						childDefs.add(child.asGroup());
						
						GroupPath newPath = new GroupPath(path.getParents(), childDefs);
						
						// check if path is valid
						if (newPath.isValid()) {
							// check max descent
							if (maxDescent >= 0 && newPath.getChildren().size() > maxDescent) {
								leafs.add(newPath);
							}
							else {
								paths.add(newPath);
							}
						}
						
						checkedDefinitions.add(child.asGroup()); // prevent cycles for new groups
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * Determines if the given group is valid and may be closed
	 * @param currentGroup the current group
	 * @return if the group may be closed
	 */
	private static boolean allowClose(MutableGroup currentGroup) {
		if (currentGroup instanceof Instance) {
			return false; // instances may never be closed, they have no parent in the group stack
		}
		
		// determine all children
		Collection<? extends ChildDefinition<?>> children = DefinitionUtil.getAllChildren(currentGroup.getDefinition());
	
		// check cardinality of children
		for (ChildDefinition<?> childDef : children) {
			if (isValidCardinality(currentGroup, childDef)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Determines if a child is contained in a given group with a valid minimum
	 * cardinality.
	 * @param group the group
	 * @param childDef the child definition
	 * @return if the minimum cardinality of the child definition is matched in
	 *   the group
	 */
	static boolean isValidCardinality(Group group,
			ChildDefinition<?> childDef) {
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
				int count = (values == null)?(0):(values.length);
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
	 * @param group the group
	 * @param property the property
	 * @return if another property value may be added to the group
	 */
	static boolean allowAdd(MutableGroup group,
			PropertyDefinition property) {
		DefinitionGroup def = group.getDefinition();
		
		if (def instanceof GroupPropertyDefinition) {
			// group property
			GroupPropertyDefinition groupDef = (GroupPropertyDefinition) def;
			
			if (groupDef.getConstraint(ChoiceFlag.class).isEnabled()) {
				// choice
				// a choice may only contain one of its properties
				for (QName propertyName : group.getPropertyNames()) {
					if (!propertyName.equals(property.getName())) {
						// other property is present -> may not add property value
						return false;
					}
				}
				// check cardinality
				return allowAddCheckCardinality(group, property);
			}
			else {
				// sequence, group(, attributeGroup)
				
				// check order
				if (!allowAddCheckOrder(group, property.getName(), groupDef)) {
					return false;
				}
				
				// check cardinality
				return allowAddCheckCardinality(group, property);
			}
		}
		else if (def instanceof TypeDefinition) {
			// type
			TypeDefinition typeDef = (TypeDefinition) def;
			
			// check order
			if (!allowAddCheckOrder(group, property.getName(), typeDef)) {
				return false;
			}
			
			// check cardinality
			return allowAddCheckCardinality(group, property);
		}
		
		return false;
	}

	/**
	 * Determines if another value of the given property may be added to the
	 * given group based on values available in the group and the order
	 * of the child definitions in the given definition group.
	 * @param group the group, <code>null</code> represents an empty group
	 * @param propertyName the property name
	 * @param groupDef the definition group
	 * @return if another property value may be added to the group based on the
	 *   values and the child definition order
	 */
	static boolean allowAddCheckOrder(Group group,
			QName propertyName, final DefinitionGroup groupDef) {
		boolean before = true;
		
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
					return groupDef;
				}
			};
		}
		
		Collection<? extends ChildDefinition<?>> children = DefinitionUtil.getAllChildren(groupDef);
		
		for (ChildDefinition<?> childDef : children) {
			if (childDef.getName().equals(propertyName)) {
				before = false;
			}
			else {
				// ignore XML attributes
				if (childDef.asProperty() != null && childDef.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
					continue;
				}
				// ignore groups that contain no elements
				if (childDef.asGroup() != null && !StreamGmlInstance.hasElements(childDef.asGroup())) {
					continue;
				}
				
				if (before) {
					// child before the property
					// the property may only be added if all children before are valid in their cardinality
					if (!isValidCardinality(group, childDef)) {
						return false;
					}
				}
				else {
					// child after the property
					// the property may only be added if there are no values for children after the property
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
	 * @param group the group
	 * @param property the property
	 * @return if another property value may be added to the group based on the
	 *   property cardinality
	 */
	static boolean allowAddCheckCardinality(MutableGroup group,
			PropertyDefinition property) {
		Cardinality cardinality = property.getConstraint(Cardinality.class);
		
		// check maximum
		long max = cardinality.getMaxOccurs();
		if (max == Cardinality.UNBOUNDED) {
			return true; // add allowed in any case
		}
		else if (max <= 0) {
			return false; // add never allowed
		}
		
		Object[] values = group.getProperty(property.getName());
		if (values == null) {
			return true; // allowed because max is 1 or more
		}
		
		return values.length < max;
	}

}
