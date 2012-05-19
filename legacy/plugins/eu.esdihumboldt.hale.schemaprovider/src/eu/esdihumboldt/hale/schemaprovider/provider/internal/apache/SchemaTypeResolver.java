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

package eu.esdihumboldt.hale.schemaprovider.provider.internal.apache;

import java.util.Map;

import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Combines the information for resolving types
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@Deprecated
public class SchemaTypeResolver {
	
	private Map<Name, TypeDefinition> types;
	
	private Map<Name, TypeDefinition> importedTypes;
	
	private String schemaLocation;

	/**
	 * Constructor
	 * 
	 * @param types the type definitions
	 * @param importedTypes the imported type definitions
	 * @param schemaLocation the schema location
	 */
	public SchemaTypeResolver(Map<Name, TypeDefinition> types,
			Map<Name, TypeDefinition> importedTypes, String schemaLocation) {
		super();
		this.types = types;
		this.importedTypes = importedTypes;
		this.schemaLocation = schemaLocation;
	}

	/**
	 * @return the types
	 */
	public Map<Name, TypeDefinition> getTypes() {
		return types;
	}

	/**
	 * @return the importedTypes
	 */
	public Map<Name, TypeDefinition> getImportedTypes() {
		return importedTypes;
	}

	/**
	 * @return the schemaLocation
	 */
	public String getSchemaLocation() {
		return schemaLocation;
	}
	
	/**
	 * Resolve a type by the given name
	 * 
	 * @param name the type name
	 * @return the type definition or <code>null</code> if the corresponding
	 *   type was not found
	 */
	public TypeDefinition getSchemaType(Name name) {
		TypeDefinition type = types.get(name);
		if (type == null) {
			type = importedTypes.get(name);
		}
		return type;
	}
}
