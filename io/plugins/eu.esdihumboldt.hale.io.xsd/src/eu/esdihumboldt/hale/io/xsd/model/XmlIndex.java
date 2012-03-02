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

package eu.esdihumboldt.hale.io.xsd.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.google.common.base.Preconditions;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeDefinition;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeUtil;

/**
 * XML schema used during schema parsing, manages {@link XmlTypeDefinition}s
 * @author Simon Templer
 */
public class XmlIndex extends DefaultSchema {
	
	/**
	 * XML attribute definitions
	 */
	private final Map<QName, XmlAttribute> attributes = new HashMap<QName, XmlAttribute>();
	
	/**
	 * XML attribute group definitions
	 */
	private final Map<QName, XmlAttributeGroup> attributeGroups = new HashMap<QName, XmlAttributeGroup>();
	
	/**
	 * XML group definitions
	 */
	private final Map<QName, XmlGroup> groups = new HashMap<QName, XmlGroup>();
	
	/**
	 * XML elements
	 */
	private final Map<QName, XmlElement> elements = new HashMap<QName, XmlElement>();
	
	/**
	 * Namespaces mapped to prefixes
	 */
	private final Map<String, String> prefixes = new HashMap<String, String>();
	
	/**
	 * @see DefaultSchema#DefaultSchema(String, URI)
	 */
	public XmlIndex(String namespace, URI location) {
		super(namespace, location);
	}

	/**
	 * Get the type definition with the given name. If the type doesn't exist a
	 * new type definition will be created.
	 * @param name the type name
	 * @return the type definition
	 */
	public XmlTypeDefinition getOrCreateType(QName name) {
		XmlTypeDefinition type = (XmlTypeDefinition) super.getType(name);
		if (type == null) {
			type = new XmlTypeDefinition(name);
			
			XmlTypeUtil.configureType(type);
			
			if (name.equals(XmlTypeUtil.NAME_ANY_TYPE)) {
				type.setConstraint(AbstractFlag.ENABLED);
				type.setConstraint(MappableFlag.DISABLED);
			}
			else {
				// set anyType as default super type
				type.setSuperType(getOrCreateType(XmlTypeUtil.NAME_ANY_TYPE));
			}
			
			addType(type);
		}
		return type;
	}

	/**
	 * @see DefaultTypeIndex#addType(TypeDefinition)
	 */
	@Override
	public void addType(TypeDefinition type) {
		Preconditions.checkArgument(type instanceof XmlTypeDefinition,
				"Only XML type definitions may be added to the index");
		
		super.addType(type);
	}

	/**
	 * @return the attribute definitions
	 */
	public Map<QName, XmlAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * @return the attribute group definitions
	 */
	public Map<QName, XmlAttributeGroup> getAttributeGroups() {
		return attributeGroups;
	}

	/**
	 * @return the element definitions
	 */
	public Map<QName, XmlElement> getElements() {
		return elements;
	}

	/**
	 * @return the group definitions
	 */
	public Map<QName, XmlGroup> getGroups() {
		return groups;
	}

	/**
	 * @return the prefixes, namespaces mapped to prefix
	 */
	public Map<String, String> getPrefixes() {
		return prefixes;
	}

}
