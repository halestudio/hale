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

import java.util.Set;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public abstract class AbstractDefaultAttribute extends AttributeDefinition {

	/**
	 * Constructor
	 * 
	 * @param name the attribute name 
	 * @param typeName the attribute type name
	 * @param attributeType the attribute type, may be <code>null</code>
	 * @param namespace the attribute namespace
	 */
	public AbstractDefaultAttribute(String name, Name typeName,
			TypeDefinition attributeType, String namespace) {
		super(name, typeName, attributeType, false);
		
		setNamespace(namespace);
	}

	/**
	 * Copy constructor
	 * 
	 * @param other the other 
	 */
	protected AbstractDefaultAttribute(AbstractDefaultAttribute other) {
		super(other);
	}

	/**
	 * @see AttributeDefinition#createAttributeDescriptor(Set)
	 */
	@Override
	public AttributeDescriptor createAttributeDescriptor(Set<TypeDefinition> resolving) {
		//XXX no attribute descriptors are created for non-element attributes
		return null;
	}

	/**
	 * @see AttributeDefinition#getMaxOccurs()
	 */
	@Override
	public long getMaxOccurs() {
		return 1;
	}

	/**
	 * @see AttributeDefinition#getMinOccurs()
	 */
	@Override
	public long getMinOccurs() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see AttributeDefinition#isNillable()
	 */
	@Override
	public boolean isNillable() {
		// TODO Auto-generated method stub
		return false;
	}

}