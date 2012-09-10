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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * XML property definition that doesn't know its property type on construction
 * time.
 * 
 * @author Simon Templer
 */
public abstract class LazyPropertyDefinition extends DefaultPropertyDefinition {

	/**
	 * The XML index that can be used to resolve needed objects
	 */
	protected final XmlIndex index;

	/**
	 * The resolved property type (if resolved yet)
	 */
	private TypeDefinition resolvedType;

	/**
	 * Create a lazy property definiton
	 * 
	 * @param name the property name
	 * @param declaringType the declaring type
	 * @param index the XML index
	 */
	public LazyPropertyDefinition(QName name, DefinitionGroup declaringType, XmlIndex index) {
		super(name, declaringType, null);

		this.index = index;
	}

	/**
	 * @see DefaultPropertyDefinition#getPropertyType()
	 */
	@Override
	public TypeDefinition getPropertyType() {
		if (resolvedType != null) {
			return resolvedType;
		}

		// resolve type
		resolvedType = resolvePropertyType(index);

		return resolvedType;
	}

	/**
	 * Resolve the property type using the XML index
	 * 
	 * @param index the XML index
	 * @return the resolved property type
	 */
	protected abstract TypeDefinition resolvePropertyType(XmlIndex index);

}
