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
package eu.esdihumboldt.hale.ui.model.schema;

import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyType;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService.SchemaType;

/**
 * A TreeObject for TreeViewers.
 *  
 * @author cjauss, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class TreeObject implements SchemaItem, Comparable<TreeObject> {
	
	private final String label;
	private TreeParent parent;
	private final TreeObjectType type;
	private final PropertyType propertyType;
	private final SchemaType schemaType;
	
	private final Name name;
	
	/**
	 * Constructor
	 * 
	 * @param label the item label
	 * @param name the entity name
	 * @param type the entity type
	 * @param propertyType the property type represented by this item, may be <code>null</code>
	 * @param schemaType the schema type
	 */
	public TreeObject(String label, Name name, TreeObjectType type,
			PropertyType propertyType, SchemaType schemaType) {
		this.label = label;
		this.type = type;
		this.name = name;
		this.propertyType = propertyType;
		this.schemaType = schemaType;
	}
	
	/**
	 * @see SchemaItem#getEntity()
	 */
	@Override
	public Entity getEntity() {
		Definition definition = getDefinition();
		
		if (definition != null) {
			return definition.getEntity();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @see SchemaItem#getSchemaType()
	 */
	@Override
	public SchemaType getSchemaType() {
		return schemaType;
	}

	/**
	 * @see SchemaItem#isAttribute()
	 */
	@Override
	public boolean isAttribute() {
		switch (type) {
		case ABSTRACT_FT:
		case CONCRETE_FT:
		case PROPERTY_TYPE:
		case ROOT:
			return false;
		default:
			return true;
		}
	}
	
	/**
	 * @see SchemaItem#isType()
	 */
	@Override
	public boolean isType() {
		return isFeatureType() || type.equals(TreeObjectType.PROPERTY_TYPE);
	}
	
	/**
	 * @see SchemaItem#isFeatureType()
	 */
	@Override
	public boolean isFeatureType() {
		switch (type) {
		case ABSTRACT_FT:
		case CONCRETE_FT:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * @return the type, either ROOT, 
	 */
	@Override
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
	 * @see SchemaItem#getName()
	 */
	@Override
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
	@Override
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
		/** property type */
		PROPERTY_TYPE,
		/** numeric attribute item */
		NUMERIC_ATTRIBUTE,
		/** string attribute item */
		STRING_ATTRIBUTE,
		/** complex attribute item */
		COMPLEX_ATTRIBUTE,
		/** geometric attribute item */
		GEOMETRIC_ATTRIBUTE,
	}

	/**
	 * @see SchemaItem#getChildren()
	 */
	@Override
	public SchemaItem[] getChildren() {
		return null;
	}

	/**
	 * @see SchemaItem#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(TreeObject other) {
		if (isType() && !other.isType()) {
			return -1;
		}
		else if (!isType() && other.isType()) {
			return 1;
		}
		else {
			return label.compareToIgnoreCase(other.label);
		}
	}

	/**
	 * @see SchemaItem#getPropertyType()
	 */
	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}

}
