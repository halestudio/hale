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
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import static com.google.common.base.Preconditions.*;

import eu.esdihumboldt.hale.instance.model.MutableGroup;
import eu.esdihumboldt.hale.instance.model.impl.OGroup;
import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.schema.model.constraint.property.ChoiceFlag;

/**
 * Represents a path of groups in two parts, the existing parent 
 * {@link MutableGroup}s and the non-existent children represented by
 * a {@link DefinitionGroup}
 * @author Simon Templer
 */
public class GroupPath {
	
	private final List<MutableGroup> parents;
	
	private final List<DefinitionGroup> children;

	/**
	 * Create a group path
	 * @param parents the list of parent groups, may neither be <code>null</code>
	 *   nor empty
	 * @param children the list of child definition groups, may be <code>null</code>
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
	 * Create groups for the children in the path (which are only represented
	 * as definitions). May only be called if the path is valid. This will also
	 * update the path to include the groups instead of the definitions.
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
			MutableGroup group = new OGroup(child);
			
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
	 * Determines if the group path in this configuration is valid in respect
	 * to the creation of new groups based on the contained definition groups.
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
				if (!GroupUtil.allowAddCheckOrder(parentGroup, 
						((GroupPropertyDefinition) child).getName(), parentDef)) {
					return false;
				}
			}
			else {
				//XXX TypeDefinitions not supported in path
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
	 * @param propertyName the property name
	 * @return if adding the property value to the last element in the path is
	 *   allowed
	 */
	public boolean allowAdd(QName propertyName) {
		if (children == null || children.isEmpty()) {
			// check last parent
			MutableGroup parent = parents.get(parents.size() - 1);
			ChildDefinition<?> child = parent.getDefinition().getChild(propertyName);
			if (child.asProperty() != null) {
				return GroupUtil.allowAdd(parent, child.asProperty());
			}
			else {
				return false;
			}
		}
		else {
			// check last child
			DefinitionGroup child = children.get(children.size() - 1);
			
			if (child instanceof GroupPropertyDefinition && 
					((GroupPropertyDefinition) child).getConstraint(ChoiceFlag.class).isEnabled()) {
				// group is a choice
				return true;
			}
			
			return GroupUtil.allowAddCheckOrder(null, propertyName, child);
		}
	}
	
	/**
	 * Get the last definition group in the path
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
	 * Will create the child groups for which
	 * only definitions are present and update the path accordingly before
	 * getting the last group object.
	 * @return the last group in the path
	 * @throws IllegalStateException if the path is not valid
	 */
	public MutableGroup getLast() throws IllegalStateException {
		//XXX possibly introduce a strict parameter (regarding validation)
		return getAllGroups().peek();
	}

	/**
	 * Get all groups in the path.<br>
	 * <br>
	 * Will create the child groups for which
	 * only definitions are present and update the path accordingly before
	 * returning all group objects.
	 * @return the all groups in the path
	 * @throws IllegalStateException if the path is not valid
	 */
	public Stack<MutableGroup> getAllGroups() throws IllegalStateException {
		//XXX possibly introduce a strict parameter (regarding validation)
		if (children != null && !children.isEmpty()) {
			if (!isValid()) {
				throw new IllegalStateException("Attempt to create groups in an invalid path.");
			}
			createChildGroups();
		}
		
		Stack<MutableGroup> result = new Stack<MutableGroup>();
		result.addAll(parents);
		return result ;
	}

}
