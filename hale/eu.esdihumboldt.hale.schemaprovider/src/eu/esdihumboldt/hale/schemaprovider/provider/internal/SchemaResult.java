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

package eu.esdihumboldt.hale.schemaprovider.provider.internal;

import java.util.Map;

import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;


/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaResult {
	
	private final Map<Name, TypeDefinition> nameToTypeDefinitionMap;
	
	private final Map<Name, Name> elementToTypeNameMap;

	/**
	 * Constructor
	 * 
	 * @param nameToTypeDefinitionMap
	 * @param elementToTypeNameMap
	 */
	public SchemaResult(Map<Name, TypeDefinition> nameToTypeDefinitionMap,
			Map<Name, Name> elementToTypeNameMap) {
		super();
		this.nameToTypeDefinitionMap = nameToTypeDefinitionMap;
		this.elementToTypeNameMap = elementToTypeNameMap;
	}

	/**
	 * @return the nameToTypeDefinition
	 */
	public Map<Name, TypeDefinition> getNameToTypeDefinitionMap() {
		return nameToTypeDefinitionMap;
	}

	/**
	 * @return the elementToTypeName
	 */
	public Map<Name, Name> getElementToTypeNameMap() {
		return elementToTypeNameMap;
	}

}
