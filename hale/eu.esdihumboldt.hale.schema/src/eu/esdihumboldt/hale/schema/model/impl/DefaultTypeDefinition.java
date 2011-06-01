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

package eu.esdihumboldt.hale.schema.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.schema.model.Definition;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.impl.internal.ReparentProperty;

/**
 * Default {@link TypeDefinition} implementation.
 * @author Simon Templer
 */
public class DefaultTypeDefinition extends AbstractDefinition<TypeConstraint> implements TypeDefinition {
	
	/**
	 * The definition of the super type
	 */
	private DefaultTypeDefinition superType;
	
	/**
	 * The sub-types
	 */
	private SortedSet<DefaultTypeDefinition> subTypes;
	
	/**
	 * The list of declared properties (list because order must be maintained for writing)
	 */
	private final List<DefaultPropertyDefinition> declaredProperties = new ArrayList<DefaultPropertyDefinition>();
	
	/**
	 * The inherited properties
	 */
	private List<PropertyDefinition> inheritedProperties;
	
	/**
	 * Create a type definition with the given name
	 * 
	 * @param name the type name
	 */
	public DefaultTypeDefinition(QName name) {
		super(name);
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return name.getNamespaceURI() + "/" + name.getLocalPart();
	}
	
	/**
	 * Add a declared property, this is called by the
	 * {@link DefaultPropertyDefinition} constructor.
	 * 
	 * @param property the property definition
	 */
	void addDeclaredProperty(DefaultPropertyDefinition property) {
		int idx = declaredProperties.indexOf(property);
		if (idx >= 0) {
			// replace
			declaredProperties.remove(idx);
			declaredProperties.add(idx, property);
		}
		else {
			declaredProperties.add(property);
		}
	}
	
	//XXX needed?
//	/**
//	 * Removes a declared property
//	 * 
//	 * @param property the property to remove
//	 */
//	public void removeDeclaredProperty(DefaultPropertyDefinition property) {
//		property.setDeclaringType(null);
//		declaredProperties.remove(property);
//	}

	/**
	 * @see TypeDefinition#getDeclaredProperties()
	 */
	@Override
	public Collection<? extends DefaultPropertyDefinition> getDeclaredProperties() {
		return Collections.unmodifiableCollection(declaredProperties);
	}

	/**
	 * @see TypeDefinition#getProperties()
	 */
	@Override
	public Collection<? extends PropertyDefinition> getProperties() {
		Collection<PropertyDefinition> properties = new ArrayList<PropertyDefinition>();
		
		if (inheritedProperties == null) {
			inheritedProperties = new ArrayList<PropertyDefinition>();
			
			// populate inherited attributes
			DefaultTypeDefinition parent = getSuperType();
			while (parent != null) {
				for (PropertyDefinition parentProperty : parent.getDeclaredProperties()) {
					// create attribute definition copy
					PropertyDefinition reparent = new ReparentProperty(parentProperty, this);
					inheritedProperties.add(reparent);
				}
				
				parent = parent.getSuperType();
			}
		}
		
		// add inherited properties
		properties.addAll(inheritedProperties);
		
		// add declared properties afterwards - correct order for output
		properties.addAll(inheritedProperties);
		
		return properties;
	}

	/**
	 * @see TypeDefinition#getSuperType()
	 */
	@Override
	public DefaultTypeDefinition getSuperType() {
		return superType;
	}

	/**
	 * Set the type's super type. This will add this type to the super type's
	 * sub-type list and remove it from the previous super type (if any).
	 * 
	 * @param superType the super-type to set
	 */
	public void setSuperType(DefaultTypeDefinition superType) {
		if (this.superType != null) {
			this.superType.removeSubType(this);
		}
		
		this.superType = superType;
		
		superType.addSubType(this);
	}

	/**
	 * Remove a sub-type
	 * 
	 * @param subtype the sub-type to remove
	 * @see #setSuperType(DefaultTypeDefinition)
	 */
	protected void removeSubType(DefaultTypeDefinition subtype) {
		if (subTypes != null) {
			subTypes.remove(subtype);
		}
	}

	/**
	 * Add a sub-type
	 * 
	 * @param subtype the sub-type to add
	 * @see #setSuperType(DefaultTypeDefinition)
	 */
	protected void addSubType(DefaultTypeDefinition subtype) {
		if (subTypes == null) {
			subTypes = new TreeSet<DefaultTypeDefinition>();
		}
		
		subTypes.add(subtype);
	}

	/**
	 * @see TypeDefinition#getSubTypes()
	 */
	@Override
	public Collection<? extends DefaultTypeDefinition> getSubTypes() {
		if (subTypes == null) {
			return Collections.emptyList();
		}
		else {
			return Collections.unmodifiableCollection(subTypes);
		}
	}

	/**
	 * @see TypeDefinition#getProperty(QName)
	 */
	@Override
	public PropertyDefinition getProperty(QName name) {
		//TODO improve, use index?
		for (PropertyDefinition property : getProperties()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}
		
		return null;
	}

}
