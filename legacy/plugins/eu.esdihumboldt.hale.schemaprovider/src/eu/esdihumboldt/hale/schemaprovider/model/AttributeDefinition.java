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

package eu.esdihumboldt.hale.schemaprovider.model;

import java.util.Set;

import org.geotools.feature.AttributeTypeBuilder;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.hale.schemaprovider.Messages;

/**
 * Represents an attribute definition
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public abstract class AttributeDefinition extends AbstractDefinition implements
		Comparable<AttributeDefinition>, Definition {

	private final String name;

	private final Name typeName;

	private final Name substitutionGroup;

	private TypeDefinition attributeType;

	/**
	 * The type declaring the attribute
	 */
	private TypeDefinition declaringType;

	/**
	 * The concrete parent type of the attribute, may be equal to
	 * {@link #declaringType} or a sub type
	 */
	private TypeDefinition parentType;

	/**
	 * The attribute namespace
	 */
	private String namespace;

	private final boolean isElement;

	/**
	 * Create an attribute definition
	 * 
	 * @param name
	 *            the attribute name
	 * @param typeName
	 *            the name of the attribute type
	 * @param attributeType
	 *            the corresponding attribute type, may be <code>null</code>
	 * @param isElement
	 *            if the attribute is represented by an element
	 * @param substitutionGroup
	 *            the element substitution group, may be <code>null</code>
	 */
	public AttributeDefinition(String name, Name typeName,
			TypeDefinition attributeType, boolean isElement,
			Name substitutionGroup) {
		super();
		this.name = name;
		this.typeName = typeName;
		this.attributeType = attributeType;
		this.isElement = isElement;
		this.substitutionGroup = substitutionGroup;
	}

	/**
	 * Copy constructor
	 * 
	 * @param other
	 *            the attribute definition to copy
	 */
	protected AttributeDefinition(AttributeDefinition other) {
		this(other.getName(), other.getTypeName(), other.getAttributeType(),
				other.isElement, other.substitutionGroup);

		setDescription(other.getDescription());
		setDeclaringType(other.getDeclaringType());
		setLocation(other.getLocation());
		setNamespace(other.getNamespace());
	}

	/**
	 * Create an attribute descriptor
	 * 
	 * @param resolving
	 *            the types that are already in the process of creating a
	 *            feature type
	 * 
	 * @return the attribute descriptor
	 */
	public abstract AttributeDescriptor createAttributeDescriptor(
			Set<TypeDefinition> resolving);

	/**
	 * Init the declaring/parent type
	 * 
	 * @param declaringType
	 *            the declaring type
	 */
	void setDeclaringType(TypeDefinition declaringType) {
		this.declaringType = declaringType;
		this.parentType = declaringType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the typeName
	 */
	public Name getTypeName() {
		return typeName;
	}

	/**
	 * @return the attributeType
	 */
	public TypeDefinition getAttributeType() {
		if (attributeType == null) {
			attributeType = getDefaultAttributeType();
		}

		return attributeType;
	}

	/**
	 * Get the attribute type if none was set
	 * 
	 * @return the attribute type definition
	 */
	protected TypeDefinition getDefaultAttributeType() {
		AttributeType type = createDefaultAttributeType();
		TypeDefinition attributeType = new TypeDefinition(typeName, type, null);
		attributeType.setLocation(Messages.getString("AttributeDefinition.0")); //$NON-NLS-1$
		String desc = Messages.getString("AttributeDefinition.1"); //$NON-NLS-1$
		if (getDescription() != null) {
			setDescription(desc + "\n\n" + getDescription()); //$NON-NLS-1$
		} else {
			setDescription(desc);
		}
		// XXX log message?
		return attributeType;
	}

	/**
	 * Create an attribute type if none was set
	 * 
	 * @return the attribute type
	 */
	protected AttributeType createDefaultAttributeType() {
		AttributeTypeBuilder builder = new AttributeTypeBuilder();
		builder.setName(typeName.getLocalPart());
		builder.setNamespaceURI(typeName.getNamespaceURI());
		builder.setBinding(Object.class);
		return builder.buildType();
	}

	/**
	 * @return the declaringType
	 */
	public TypeDefinition getDeclaringType() {
		return declaringType;
	}

	/**
	 * @param attributeType
	 *            the attributeType to set
	 */
	public void setAttributeType(TypeDefinition attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((parentType == null) ? 0 : parentType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttributeDefinition other = (AttributeDefinition) obj;
		if (parentType == null) {
			if (other.parentType != null)
				return false;
		} else if (!parentType.equals(other.parentType))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(AttributeDefinition other) {
		int result = name.compareToIgnoreCase(other.name);
		if (result == 0) {
			if (parentType == null && other.parentType == null) {
				return 0;
			} else if (parentType == null) {
				return 1;
			} else if (other.parentType == null) {
				return -1;
			} else {
				return parentType.compareTo(other.parentType);
			}
		}

		return result;
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		if (parentType == null) {
			return name;
		} else {
			return parentType.getIdentifier() + "/" + name; //$NON-NLS-1$
		}
	}

	/**
	 * @return if the attribute is nillable
	 */
	public abstract boolean isNillable();

	/**
	 * @return the minOccurs
	 */
	public abstract long getMinOccurs();

	/**
	 * @return the maxOccurs
	 */
	public abstract long getMaxOccurs();

	/**
	 * @return if the attribute is represented in GML/XML as an element
	 */
	public boolean isElement() {
		return isElement;
	}

	/**
	 * @return if the attribute is represented in GML/XML as an attribute
	 */
	public boolean isAttribute() {
		return !isElement;
	}

	/**
	 * @see Definition#getEntity()
	 */
	@Override
	public Entity getEntity() {
		Name parentName;

		Set<SchemaElement> elements = getParentType().getDeclaringElements();
		if (elements.isEmpty()) {
			parentName = getParentType().getName();
		} else {
			parentName = elements.iterator().next().getElementName();
		}

		return new Property(new About(parentName.getNamespaceURI(),
				parentName.getLocalPart(), name));
	}

	/**
	 * @see Definition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getName();
	}

	/**
	 * @return the parentType
	 */
	public TypeDefinition getParentType() {
		return parentType;
	}

	/**
	 * @param parentType
	 *            the parentType to set
	 */
	protected void setParentType(TypeDefinition parentType) {
		this.parentType = parentType;
	}

	/**
	 * @return the substitution group name, may be <code>null</code>
	 */
	public Name getSubstitutionGroup() {
		return substitutionGroup;
	}

	/**
	 * Create a copy of the attribute definition, as the attribute of the given
	 * parent type
	 * 
	 * @param parentType
	 *            the parent type, must be a sub type of the declaring type of
	 *            the attribute
	 * 
	 * @return the attribute definition
	 */
	public abstract AttributeDefinition copyAttribute(TypeDefinition parentType);

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[attribute] " + getIdentifier(); //$NON-NLS-1$
	}

}
