/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.xsd.reader.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;

import eu.esdihumboldt.hale.schema.model.impl.DefaultTypeIndex;

/**
 * XML schema used during schema parsing, manages {@link XmlTypeDefinition}s
 * @author Simon Templer
 */
public class XmlIndex extends DefaultTypeIndex {
	
	/**
	 * XML attribute definitions
	 */
	private final Map<QName, XmlSchemaAttribute> attributes = new HashMap<QName, XmlSchemaAttribute>();
	
	/**
	 * XML attribute group definitions
	 */
	private final Map<QName, XmlSchemaAttributeGroup> attributeGroups = new HashMap<QName, XmlSchemaAttributeGroup>();
	
	/**
	 * XML elements
	 */
	private final Map<QName, SchemaElement> elements = new HashMap<QName, SchemaElement>();

	/**
	 * Creates a new type definition if no type with the given name is found.
	 * 
	 * @see DefaultTypeIndex#getType(QName)
	 */
	@Override
	public XmlTypeDefinition getType(QName name) {
		XmlTypeDefinition type = (XmlTypeDefinition) super.getType(name);
		if (type == null) {
			type = new XmlTypeDefinition(name);
			addType(type);
		}
		return type;
	}

	/**
	 * @return the attribute definitions
	 */
	public Map<QName, XmlSchemaAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * @return the attribute group definitions
	 */
	public Map<QName, XmlSchemaAttributeGroup> getAttributeGroups() {
		return attributeGroups;
	}

	/**
	 * @return the element definitions
	 */
	public Map<QName, SchemaElement> getElements() {
		return elements;
	}

}
