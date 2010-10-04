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
package eu.esdihumboldt.hale.models.utils;

import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;

/**
 * Provides access to the {@link SchemaItem} trees for the source and target
 * schemas
 * 
 * @author Simon Templer
 * @version $Id$
 */
public interface SchemaItemService { 
	
	/**
	 * Adds a listener
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(SchemaServiceListener listener);
	
	/**
	 * Removes a listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(SchemaServiceListener listener);
	
	/**
	 * Get the root schema item for the given schema type
	 * 
	 * @param schema the schema type
	 * 
	 * @return the root item
	 */
	public SchemaItem getRoot(SchemaType schema);
	
	/**
	 * Get the schema item for the given identifier
	 * 
	 * @param identifier the identifier
	 * 
	 * @return the schema item or <code>null</code>
	 */
	public SchemaItem getSchemaItem(String identifier);
	
	/**
	 * Get the schema item for the given identifier in the given schema type
	 * 
	 * @param identifier the identifier
	 * @param schemaType the schema type
	 * 
	 * @return the schema item or <code>null</code>
	 */
	public SchemaItem getSchemaItem(String identifier, SchemaType schemaType);
	
	/**
	 * Get the schema item for the given entity
	 * 
	 * @param entity an entity (may not be composed)
	 * 
	 * @return the the matching schema item or <code>null</code>
	 */
	public SchemaItem getSchemaItem(IEntity entity);
	
	/**
	 * Get the schema item for the given entity
	 * 
	 * @param entity an entity (may not be composed)
	 * @param schemaType the schema type
	 * 
	 * @return the the matching schema item or <code>null</code>
	 */
	public SchemaItem getSchemaItem(IEntity entity, SchemaType schemaType);

}
