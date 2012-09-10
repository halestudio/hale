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

package eu.esdihumboldt.hale.io.xsd.model;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;

/**
 * Represents a XML schema element
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XmlElement extends AbstractDefinition<TypeConstraint> {

	/**
	 * The element type
	 */
	private final TypeDefinition type;

	/**
	 * The substitution group
	 */
	private final QName substitutionGroup;

	/**
	 * Create a new schema element
	 * 
	 * @param elementName the element name
	 * @param type the associated type definition
	 * @param substitutionGroup the substitution group, may be <code>null</code>
	 */
	public XmlElement(QName elementName, TypeDefinition type, QName substitutionGroup) {
		super(elementName);

		// TODO also remember index for resolving the substituted element?
		// XXX what about the other direction - finding possible substitutions

		this.type = type;
		this.substitutionGroup = substitutionGroup;
	}

	/**
	 * Get the type definition associated with the element
	 * 
	 * @return the element type
	 */
	public TypeDefinition getType() {
		return type;
	}

	/**
	 * Get the element substitution group
	 * 
	 * @return the substitution group or <code>null</code>
	 */
	public QName getSubstitutionGroup() {
		return substitutionGroup;
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getName().getNamespaceURI() + "/" + getName().getLocalPart();
	}

}
