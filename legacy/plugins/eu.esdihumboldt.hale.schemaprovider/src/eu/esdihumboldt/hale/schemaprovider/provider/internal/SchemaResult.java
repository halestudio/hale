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

import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Wraps up a schema parsing result
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public class SchemaResult {

	private final Map<Name, TypeDefinition> types;

	private final Map<Name, SchemaElement> elements;

	private final Map<Name, XmlSchemaAttributeGroup> schemaAttributeGroups;

	private final Map<Name, XmlSchemaAttribute> schemaAttributes;

	/**
	 * Constructor
	 * 
	 * @param types
	 *            the type definitions
	 * @param elements
	 *            the element definitions
	 * @param schemaAttributes
	 *            the attribute definitions
	 * @param schemaAttributeGroups
	 *            the attribute group definitions
	 */
	public SchemaResult(Map<Name, TypeDefinition> types,
			Map<Name, SchemaElement> elements,
			Map<Name, XmlSchemaAttribute> schemaAttributes,
			Map<Name, XmlSchemaAttributeGroup> schemaAttributeGroups) {
		super();
		this.types = types;
		this.elements = elements;
		this.schemaAttributes = schemaAttributes;
		this.schemaAttributeGroups = schemaAttributeGroups;
	}

	/**
	 * @return the nameToTypeDefinition
	 */
	public Map<Name, TypeDefinition> getTypes() {
		return types;
	}

	/**
	 * @return the elementToTypeName
	 */
	public Map<Name, SchemaElement> getElements() {
		return elements;
	}

	/**
	 * @return the schemaAttributeGroups
	 */
	public Map<Name, XmlSchemaAttributeGroup> getSchemaAttributeGroups() {
		return schemaAttributeGroups;
	}

	/**
	 * @return the schemaAttributes
	 */
	public Map<Name, XmlSchemaAttribute> getSchemaAttributes() {
		return schemaAttributes;
	}

}
