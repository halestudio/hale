/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.model;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.type.Name;

import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;

/**
 * A TreeObject for TreeViewers.
 *  
 * @author cjauss, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TreeObject {
	
	private final String label;
	private TreeParent parent;
	private final TreeObjectType type;
	
	private final Name name;
	
	private Entity entity = null;
	
	/**
	 * Constructor
	 * 
	 * @param label the item label
	 * @param name the entity name
	 * @param type the entity type
	 */
	public TreeObject(String label, Name name, TreeObjectType type) {
		this.label = label;
		this.type = type;
		this.name = name;
	}
	
	/**
	 * Get the item's entity
	 * 
	 * @return the item entity, null if determining the entity fails
	 */
	public Entity getEntity() {
		if (entity == null) {
			List<String> nameparts = new ArrayList<String>();
			
			switch (getType()) {
			case ABSTRACT_FT: // fall through
			case CONCRETE_FT:
				// feature type
				if (name != null) {
					nameparts.add(name.getNamespaceURI());
					nameparts.add(name.getLocalPart());
					entity = new FeatureClass(nameparts);
				}
				break;
			case ROOT:
				// no entity
				break;
			default:
				// attributes
				if (parent != null && parent.getName() != null) {
					nameparts.add(parent.getName().getNamespaceURI());
					nameparts.add(parent.getName().getLocalPart());
					nameparts.add(name.getLocalPart());
					entity = new Property(nameparts);
				}
			}
		}
		
		return entity;
	}
	
	/**
	 * @return the type, either ROOT, 
	 */
	public TreeObjectType getType() {
		return type;
	}
	
	/**
	 * Get the item label
	 * 
	 * @return the item label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * @return the name
	 */
	public Name getName() {
		return name;
	}

	/**
	 * Set the parent tree item
	 * 
	 * @param parent the parent tree item
	 */
	public void setParent(TreeParent parent) {
		this.parent = parent;
	}
	
	/**
	 * Get the parent tree item
	 * 
	 * @return the parent tree item
	 */
	public TreeParent getParent() {
		return parent;
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getLabel();
	}
	
	/**
	 * Item types
	 */
	public enum TreeObjectType {
		/** root item */
		ROOT,
		/** abstract feature type item */
		ABSTRACT_FT,
		/** concrete feature type item **/
		CONCRETE_FT,
		/** numeric attribute item */
		NUMERIC_ATTRIBUTE,
		/** string attribute item */
		STRING_ATTRIBUTE,
		/** complex attribute item */
		COMPLEX_ATTRIBUTE,
		/** geometric attribute item */
		GEOMETRIC_ATTRIBUTE
	}

}
