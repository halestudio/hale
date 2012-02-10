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
package eu.esdihumboldt.hale.ui.service.schema;

import java.util.Collection;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * The schema service is used internally to provide access to the currently 
 * loaded schemas.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public interface SchemaService {
	
	/**
	 * Get the source or target schema space.
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE} 
	 *   or {@link SchemaSpaceID#TARGET}
	 * @return the schema space
	 */
	public SchemaSpace getSchemas(SchemaSpaceID spaceID);
	
	/**
	 * Add a schema to the source or target schema space.
	 * 
	 * @param schema the schema to add
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE} 
	 *   or {@link SchemaSpaceID#TARGET}
	 */
	public void addSchema(Schema schema, SchemaSpaceID spaceID);
	
	/**
	 * Removes all schemas from the source or target schema space.
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE} 
	 *   or {@link SchemaSpaceID#TARGET}
	 */
	public void clearSchemas(SchemaSpaceID spaceID);
	
	/**
	 * Adds a listener for schema service events
	 * 
	 * @param listener the listener to add
	 */
	public void addSchemaServiceListener(SchemaServiceListener listener); 
	
	/**
	 * Removes a listener for schema service events
	 * 
	 * @param listener the listener to remove
	 */
	public void removeSchemaServiceListener(SchemaServiceListener listener);

	/**
	 * Toggles the mappable flag of all given types of the given schema space
	 * 
	 * @param spaceID the schema space the types are in
	 * @param types he types
	 */
	public void toggleMappable(SchemaSpaceID spaceID, Collection<? extends TypeDefinition> types);

	/**
	 * Edit which types are mappable in the given schema space.
	 * 
	 * @param spaceID the schema space to edit
	 */
	public void editMappableTypes(SchemaSpaceID spaceID);
	
	//XXX something like this should be handled in the schema space / schema
//	/**
//	 * Get the type or element definition identified by the given name
//	 * 
//	 * @param name may either consist of only a local part or of a full
//	 *         name, i.e. namespace + local name part
//	 *         
//	 * @return returns the definition identified by the given name
//	 */
//	public Definition getTypeByName(String name);
//	
//	/**
//	 * Get the definition for the given identifier if it is part of the given
//	 *   schema
//	 * 
//	 * @param identifier the identifier
//	 * @param schema the schema type
//	 * 
//	 * @return the definition (either a {@link TypeDefinition} or an
//	 *   {@link AttributeDefinition})
//	 */
//	public Definition getDefinition(String identifier, SchemaSpaceID schema);
//
//	/**
//	 * Get the definition for the given identifier
//	 * 
//	 * @param identifier the identifier
//	 * 
//	 * @return the definition (either a {@link TypeDefinition} or an
//	 *   {@link AttributeDefinition})
//	 */
//	public Definition getDefinition(String identifier);

}
