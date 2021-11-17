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
package eu.esdihumboldt.hale.ui.service.schema;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.transformation.service.TransformationSchemas;
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
public interface SchemaService extends TransformationSchemas {

	/**
	 * The action id used for reading source schemas.
	 */
	public static final String ACTION_READ_SOURCE = "eu.esdihumboldt.hale.io.schema.read.source";
	/**
	 * The action id used for reading target schemas.
	 */
	public static final String ACTION_READ_TARGET = "eu.esdihumboldt.hale.io.schema.read.target";

	@Override
	public SchemaSpace getSchemas(SchemaSpaceID spaceID);

	/**
	 * Get schema for a specified resourceId.
	 * 
	 * @param resourceID resource Id of the schema to find.
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 * @return schema.
	 */
	public Schema getSchema(String resourceID, SchemaSpaceID spaceID);

	/**
	 * Add a schema to the source or target schema space.
	 * 
	 * @param resourceID resource id of the schema to be added.
	 * 
	 * @param schema the schema to add
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 */
	public void addSchema(String resourceID, Schema schema, SchemaSpaceID spaceID);

	/**
	 * Removes a schema to the source schema space from the project view.
	 * 
	 * @param resourceID of the schema to be removed.
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 * @return true if the schema was removed, false otherwise.
	 */
	public boolean removeSchema(String resourceID, SchemaSpaceID spaceID);

	/**
	 * Removes all schemas from the source or target schema space.
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
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

	// XXX something like this should be handled in the schema space / schema
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
