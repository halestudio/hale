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

package eu.esdihumboldt.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.geotools.feature.ComplexAttributeImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.cst.rdf.IAbout;
import eu.esdihumboldt.goml.rdf.DetailedAbout;

/**
 * Utilities for setting/getting (nested) property values of a feature
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureInspector {
	
	private static final Logger log = Logger.getLogger(FeatureInspector.class);
	
	/**
	 * PropertyImpl that has a visible constructor 
	 */
	private static class PropertyImpl extends org.geotools.feature.PropertyImpl {

		/**
		 * @see org.geotools.feature.PropertyImpl#PropertyImpl(Object, PropertyDescriptor)
		 */
		public PropertyImpl(Object value, PropertyDescriptor descriptor) {
			super(value, descriptor);
		}

	}
	
	/**
	 * Set the value of a (nested) property of a feature
	 * 
	 * @param <T> the type of the property value
	 * @param feature the feature
	 * @param propertyAbout the about specifying the (nested) property
	 * @param value the property value to set
	 */
	public static <T> void setPropertyValue(Feature feature, IAbout propertyAbout, T value) {
		setPropertyValue(feature, 
			DetailedAbout.getDetailedAbout(propertyAbout, true).getProperties(), 
			value);
	}

	/**
	 * Set the value of a (nested) property of a feature
	 * 
	 * @param <T> the type of the property value
	 * @param feature the feature
	 * @param properties the property names specifying the (nested) property
	 * @param value the property value to set
	 */
	public static <T> void setPropertyValue(Feature feature, List<String> properties, T value) {
		Property property = getProperty(feature, properties, true);
		property.setValue(value);
	}
	
	/**
	 * Get the value of a (nested) property of a feature
	 * 
	 * @param feature the feature
	 * @param propertyAbout the about specifying the (nested) property
	 * @param defValue the default value if the property is not present
	 *  
	 * @return the property value if it is found, otherwise the default value 
	 */
	public static Object getPropertyValue(Feature feature, IAbout propertyAbout,
			Object defValue) {
		return getPropertyValue(feature, 
			DetailedAbout.getDetailedAbout(propertyAbout, true).getProperties(), 
			defValue);
	}
	
	/**
	 * Get the value of a (nested) property of a feature
	 * 
	 * @param feature the feature
	 * @param properties the property names specifying the (nested) property
	 * @param defValue the default value if the property is not present
	 *  
	 * @return the property value if it is found, otherwise the default value 
	 */
	public static Object getPropertyValue(Feature feature, List<String> properties,
			Object defValue) {
		Property property = getProperty(feature, properties, true);
		
		if (property == null) {
			return defValue;
		}
		else {
			return property.getValue();
		}
	}
	
	/**
	 * Get the (nested) property of a feature 
	 * 
	 * @param feature the feature
	 * @param propertyAbout the about specifying the (nested) property
	 * @param create if the property shall be created if it's not present
	 * 
	 * @return the property or <code>null</code> if the property is not present
	 *   and was not created
	 */
	public static Property getProperty(ComplexAttribute feature, IAbout propertyAbout,
			boolean create) {
		return getProperty(feature, 
			DetailedAbout.getDetailedAbout(propertyAbout, true).getProperties(), 
			create);
	}

	/**
	 * Get the (nested) property of a feature 
	 * 
	 * @param feature the feature
	 * @param properties the property names specifying the (nested) property
	 * @param create if the property shall be created if it's not present
	 * 
	 * @return the property or <code>null</code> if the property is not present
	 *   and was not created
	 */
	public static Property getProperty(ComplexAttribute feature, List<String> properties,
			boolean create) {
		Queue<String> propertiesQueue = new LinkedList<String>(properties);
		
		ComplexAttribute complex = feature;
		ComplexType complexType = feature.getType();
		while (!propertiesQueue.isEmpty()) {
			String propertyName = propertiesQueue.poll();
			
			Property property = complex.getProperty(propertyName);
			if (property == null) {
				// property is not present or property is attribute
				PropertyDescriptor pd = complexType.getDescriptor(propertyName);
				if (pd != null && create) {
					// create property
					property = createProperty(pd);
					
					// set property to feature type
					Collection<? extends Property> pvs = complex.getValue();
					Collection<Property> values = new ArrayList<Property>(pvs);
					values.add(property);
					complex.setValue(values);
				}
				else {
					// no property descriptor - property is attribute or invalid
					//TODO create attribute property?
				}
			}
			
			if (property == null) {
				log.debug("Could not find/create property " + propertyName + " in " + complexType.getName().getLocalPart());
				return null;
			}
			else {
				if (propertiesQueue.isEmpty()) {
					// last property
					return property;
				}
				else {
					// prepare next iteration
					Object value = property.getValue();
					if (value == null) {
						value = createValue(property.getDescriptor());
						property.setValue(value);
					}
					
					if (value instanceof ComplexAttribute) {
						complex = (ComplexAttribute) value;
						complexType = complex.getType();
					}
					else {
						// no valid property value
						log.error("Getting nested property failed: Property " + propertyName + " of type " + complexType.getName().getLocalPart() + " has no complex value");
						return  null;
					}
				}
			}
		}
		
		// no propertes were defined
		throw new IllegalArgumentException("No properties were defined");
	}

	/**
	 * Create a default value for a property with an empty value
	 * 
	 * @param pd the property descriptor
	 * 
	 * @return the value
	 */
	private static Object createValue(PropertyDescriptor pd) {
		Object value = null;
		
		if (pd.getType() instanceof ComplexType) {
			ComplexType type = (ComplexType) pd.getType();
			value = new ComplexAttributeImpl(
					new ArrayList<Property>(), 
					type, 
					new FeatureIdImpl(UUID.randomUUID().toString()));
		}
		
		return value;
	}

	/**
	 * Create a new property from a property descriptor
	 * 
	 * @param pd the property descriptor
	 * 
	 * @return the property
	 */
	private static Property createProperty(PropertyDescriptor pd) {
		Object value = createValue(pd);
		
		return new PropertyImpl(value, pd);
	}
	
}
