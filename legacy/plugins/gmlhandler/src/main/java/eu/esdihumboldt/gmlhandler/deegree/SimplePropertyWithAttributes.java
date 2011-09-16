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

package eu.esdihumboldt.gmlhandler.deegree;

import java.util.Collection;

import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.deegree.commons.tom.primitive.PrimitiveType;
import org.deegree.feature.property.SimpleProperty;
import org.deegree.feature.types.property.SimplePropertyType;
import org.opengis.feature.Property;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SimplePropertyWithAttributes extends SimpleProperty {
	
	/**
	 * Maps attribute names to values
	 */
	private final Collection<? extends Property> attributes;

	/**
	 * @see SimpleProperty#SimpleProperty(SimplePropertyType, String, PrimitiveType)
	 */
	public SimplePropertyWithAttributes(SimplePropertyType pt, String value,
			PrimitiveType type, Collection<? extends Property> attributes) {
		super(pt, value, type);
		
		this.attributes = attributes;
	}

	/**
	 * @see SimpleProperty#SimpleProperty(SimplePropertyType, String, XSSimpleTypeDefinition) 
	 */
	public SimplePropertyWithAttributes(SimplePropertyType pt, String value,
			XSSimpleTypeDefinition xsdType, Collection<? extends Property> attributes) {
		super(pt, value, xsdType);
		
		this.attributes = attributes;
	}

	/**
	 * @return the attributes
	 */
	public Collection<? extends Property> getAttributes() {
		return attributes;
	}

}
