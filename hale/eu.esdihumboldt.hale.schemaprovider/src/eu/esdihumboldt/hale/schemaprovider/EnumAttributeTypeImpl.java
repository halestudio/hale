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

package eu.esdihumboldt.hale.schemaprovider;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;


/**
 * Enumeration attribute type decorator
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class EnumAttributeTypeImpl extends AbstractAttributeTypeDecorator
		implements EnumAttributeType {

	private final Set<String> allowedValues = new LinkedHashSet<String>();
	
	private final Name name;
	
	/**
	 * Create a enumeration attribute decorator
	 * 
	 * @param type the attribute type
	 * @param values the enumeration values
	 * @param name the custom type name or <code>null</code>
	 */
	public EnumAttributeTypeImpl(AttributeType type, Collection<String> values,
			Name name) {
		super(type);
		
		this.name = name;
		
		allowedValues.addAll(values);
	}

	/**
	 * @see EnumAttributeType#getAllowedValues()
	 */
	public Set<String> getAllowedValues() {
		return allowedValues;
	}

	/**
	 * @see AbstractAttributeTypeDecorator#getName()
	 */
	@Override
	public Name getName() {
		if (name != null) {
			return name;
		}
		
		return super.getName();
	}

}
