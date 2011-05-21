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

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyType;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.ui.model.schema.TreeObject.TreeObjectType;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public interface SchemaItem {

	/**
	 * Get the item's entity
	 * 
	 * @return the item entity, null if determining the entity fails
	 */
	public abstract Entity getEntity();
	
	/**
	 * Get the schema type
	 * 
	 * @return the schema type (source or target)
	 */
	public SchemaType getSchemaType();

	/**
	 * @return if the tree object represents an attribute
	 */
	public abstract boolean isAttribute();

	/**
	 * @return if the tree object represents a type (feature type or property type)
	 */
	public abstract boolean isType();

	/**
	 * @return if the tree object represents a feature type
	 */
	public abstract boolean isFeatureType();

	/**
	 * @return the name
	 */
	public abstract Name getName();
	
	/**
	 * Get the corresponding definition
	 * 
	 * @return the definition
	 */
	public Definition getDefinition();
	
	/**
	 * Get the item's children
	 * 
	 * @return an array of the item's children
	 */
	public SchemaItem[] getChildren();
	
	/**
	 * Determine if the item has any children
	 * 
	 * @return if the item has any children
	 */
	public boolean hasChildren();
	
	/**
	 * Get the parent schema item
	 * 
	 * @return the parent schema item, may be <code>null</code>
	 */
	public abstract SchemaItem getParent();
	
	/**
	 * @return type of the SchemaItem as defined
	 * @see eu.esdihumboldt.hale.ui.views.schemas.TreeObject.TreeObjectType
	 * 
	 */
	public abstract TreeObjectType getType();
	
	/**
	 * Get the property type represented by this item.
	 * This may for example be a {@link FeatureType} or {@link AttributeType}.
	 * 
	 * @return the property type represented by this item, may be <code>null</code>
	 *   if this item doesn't represent a type
	 */
	public abstract PropertyType getPropertyType();

}