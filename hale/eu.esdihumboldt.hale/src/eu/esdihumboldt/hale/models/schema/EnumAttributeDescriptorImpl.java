/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models.schema;

import java.util.List;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class EnumAttributeDescriptorImpl 
	extends AttributeDescriptorImpl {
	
	protected List<String> allowedValues = null;
	
	// constructors ............................................................
	
	/**
	 * @param type information on the attribute's binding.
	 * @param name the name of the attribute
	 * @param min the minimum number of occurrences
	 * @param max the maximum number of occurrences
	 * @param isNillable true if the attribute may also be null
	 * @param defaultValue an object describing the default value
	 * @param allowedValues a List of String values representing the allowed enumeration values
	 */
	public EnumAttributeDescriptorImpl(AttributeType type, Name name, int min,
			int max, boolean isNillable, Object defaultValue, List<String> allowedValues) {
		super(type, name, min, max, isNillable, null);
		this.allowedValues = allowedValues;
	}
	
	/**
	 * More focused constructor with some default values for min = 0, max = 1, 
	 * nillable = true and defaultValue = null
	 * @param type information on the attribute's binding.
	 * @param name the name of the attribute
	 * @param allowedValues a List of String values representing the allowed enumeration values
	 */
	public EnumAttributeDescriptorImpl(
			AttributeType type, Name name, List<String> allowedValues) {
		super(type, name, 0, 1, true, null);
		this.allowedValues = allowedValues;
	}
	
	// operations ..............................................................
	
	public List<String> getAllowedValues() {
		return this.allowedValues;
	}
	
	public static AttributeType getAttributeType(String name) {
		AttributeTypeBuilder builder = new AttributeTypeBuilder();
		builder.setBinding(String.class);
		builder.setName(name);
		builder.setNillable(true);
		return builder.buildType();
	}

}
