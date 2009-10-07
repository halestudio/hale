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

import org.opengis.feature.type.Name;

import eu.esdihumboldt.goml.align.Entity;

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

}