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

import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.instance.model.MutableGroup;
import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.schema.model.GroupPropertyDefinition;

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
	 * @param parents the list of parent groups
	 * @param children the list of child definition groups, may be <code>null</code>
	 */
	public GroupPath(List<MutableGroup> parents, List<DefinitionGroup> children) {
		super();
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
	 * Determines if the group path in this configuration is valid in respect
	 * to the creation of new groups based on the contained definition groups.
	 * @return if the path is valid
	 */
	public boolean isValid() {
		if (parents == null || parents.isEmpty()) {
			return false;
		}
		
		if (children == null || children.isEmpty()) {
			return true; // parents is assumed to be valid
		}
		
		MutableGroup parentGroup = parents.get(parents.size());
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
		if (parents == null || parents.isEmpty()) {
			return false;
		}
		
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
			return GroupUtil.allowAddCheckOrder(null, propertyName, child);
		}
	}
	
	/**
	 * Get the last definition group in the path
	 * @return the last definition group
	 */
	public DefinitionGroup getLast() {
		if (children != null && !children.isEmpty()) {
			return children.get(children.size() - 1);
		}
		
		if (parents != null && !parents.isEmpty()) {
			return parents.get(parents.size() - 1).getDefinition();
		}
		
		return null;
	}

}
