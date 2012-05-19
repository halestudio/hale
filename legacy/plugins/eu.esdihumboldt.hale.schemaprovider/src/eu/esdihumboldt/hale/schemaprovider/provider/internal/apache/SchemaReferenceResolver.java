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

import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * Combines the information for resolving schema elements, attributes and
 * attribute groups
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@Deprecated
public class SchemaReferenceResolver {

	private final Map<Name, SchemaElement> elements;
	private final Map<Name, SchemaElement> importedElements;
	private final Map<Name, XmlSchemaAttribute> schemaAttributes;
	private final Map<Name, XmlSchemaAttribute> importedSchemaAttributes;
	private final Map<Name, XmlSchemaAttributeGroup> schemaAttributeGroups;
	private final Map<Name, XmlSchemaAttributeGroup> importedSchemaAttributeGroups;

	/**
	 * Constructor
	 * 
	 * @param elements the schema elements
	 * @param importedElements the imported schema elements
	 * @param schemaAttributes the schema attributes
	 * @param importedSchemaAttributes the imported schema attributes
	 * @param schemaAttributeGroups the schema attribute groups
	 * @param importedSchemaAttributeGroups the imported schema attribute groups
	 */
	public SchemaReferenceResolver(Map<Name, SchemaElement> elements,
			Map<Name, SchemaElement> importedElements,
			Map<Name, XmlSchemaAttribute> schemaAttributes,
			Map<Name, XmlSchemaAttribute> importedSchemaAttributes,
			Map<Name, XmlSchemaAttributeGroup> schemaAttributeGroups,
			Map<Name, XmlSchemaAttributeGroup> importedSchemaAttributeGroups) {
		this.elements = elements;
		this.importedElements = importedElements;
		this.schemaAttributes = schemaAttributes;
		this.importedSchemaAttributes = importedSchemaAttributes;
		this.schemaAttributeGroups = schemaAttributeGroups;
		this.importedSchemaAttributeGroups = importedSchemaAttributeGroups;
	}
	
	/**
	 * Resolve a schema element by the given name
	 * 
	 * @param name the element name
	 * @return the schema element or <code>null</code> if the corresponding
	 *   element was not found
	 */
	public SchemaElement getSchemaElement(Name name) {
		SchemaElement element = elements.get(name);
		if (element == null) {
			element = importedElements.get(name);
		}
		return element;
	}
	
	/**
	 * Resolve a schema attribute by the given name
	 * 
	 * @param name the attribute name
	 * @return the schema attribute or <code>null</code> if the corresponding
	 *   attribute was not found
	 */
	public XmlSchemaAttribute getSchemaAttribute(Name name) {
		XmlSchemaAttribute attribute = schemaAttributes.get(name);
		if (attribute == null) {
			attribute = importedSchemaAttributes.get(name);
		}
		return attribute;
	}
	
	/**
	 * Resolve a schema attribute group by the given name
	 * 
	 * @param name the attribute group name
	 * @return the schema attribute group or <code>null</code> if the 
	 * corresponding attribute group was not found
	 */
	public XmlSchemaAttributeGroup getSchemaAttributeGroup(Name name) {
		XmlSchemaAttributeGroup group = schemaAttributeGroups.get(name);
		if (group == null) {
			group = importedSchemaAttributeGroups.get(name);
		}
		return group;
	}

	/**
	 * @return the elements
	 */
	public Map<Name, SchemaElement> getElements() {
		return elements;
	}

	/**
	 * @return the importedElements
	 */
	public Map<Name, SchemaElement> getImportedElements() {
		return importedElements;
	}

	/**
	 * @return the schemaAttributes
	 */
	public Map<Name, XmlSchemaAttribute> getSchemaAttributes() {
		return schemaAttributes;
	}

	/**
	 * @return the importedSchemaAttributes
	 */
	public Map<Name, XmlSchemaAttribute> getImportedSchemaAttributes() {
		return importedSchemaAttributes;
	}

	/**
	 * @return the schemaAttributeGroups
	 */
	public Map<Name, XmlSchemaAttributeGroup> getSchemaAttributeGroups() {
		return schemaAttributeGroups;
	}

	/**
	 * @return the importedSchemaAttributeGroups
	 */
	public Map<Name, XmlSchemaAttributeGroup> getImportedSchemaAttributeGroups() {
		return importedSchemaAttributeGroups;
	}

}
