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

package eu.esdihumboldt.commons.tools;

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

import eu.esdihumboldt.commons.goml.rdf.DetailedAbout;
import eu.esdihumboldt.specification.cst.rdf.IAbout;

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
		 * @see org.geotools.feature.PropertyImpl#PropertyImpl(Object,
		 *      PropertyDescriptor)
		 */
		public PropertyImpl(Object value, PropertyDescriptor descriptor) {
			super(value, descriptor);
		}

	}

	/**
	 * Set the value of a (nested) property of a feature
	 * 
	 * @param <T>
	 *            the type of the property value
	 * @param feature
	 *            the feature
	 * @param propertyAbout
	 *            the about specifying the (nested) property
	 * @param value
	 *            the property value to set
	 */
	public static <T> void setPropertyValue(Feature feature,
			IAbout propertyAbout, T value) {
		setPropertyValue(feature,
				DetailedAbout.getDetailedAbout(propertyAbout, true)
						.getProperties(), value);
	}

	/**
	 * Set the value of a (nested) property of a feature
	 * 
	 * @param <T>
	 *            the type of the property value
	 * @param feature
	 *            the feature
	 * @param properties
	 *            the property names specifying the (nested) property
	 * @param value
	 *            the property value to set
	 */
	public static <T> void setPropertyValue(Feature feature,
			List<String> properties, T value) {
		Property property = getProperty(feature, properties, true);
		property.setValue(value);
	}

	/**
	 * Get the value of a (nested) property of a feature
	 * 
	 * @param feature
	 *            the feature
	 * @param propertyAbout
	 *            the about specifying the (nested) property
	 * @param defValue
	 *            the default value if the property is not present
	 * 
	 * @return the property value if it is found, otherwise the default value
	 */
	public static Object getPropertyValue(Feature feature,
			IAbout propertyAbout, Object defValue) {
		return getPropertyValue(feature,
				DetailedAbout.getDetailedAbout(propertyAbout, true)
						.getProperties(), defValue);
	}

	/**
	 * Get the value of a (nested) property of a feature
	 * 
	 * @param feature
	 *            the feature
	 * @param properties
	 *            the property names specifying the (nested) property
	 * @param defValue
	 *            the default value if the property is not present
	 * 
	 * @return the property value if it is found, otherwise the default value
	 */
	public static Object getPropertyValue(Feature feature,
			List<String> properties, Object defValue) {
		Property property = getProperty(feature, properties, true);

		if (property == null) {
			return defValue;
		} else {
			return property.getValue();
		}
	}

	/**
	 * Get the (nested) property of a feature
	 * 
	 * @param feature
	 *            the feature
	 * @param propertyAbout
	 *            the about specifying the (nested) property
	 * @param create
	 *            if the property shall be created if it's not present
	 * 
	 * @return the property or <code>null</code> if the property is not present
	 *         and was not created
	 */
	public static Property getProperty(ComplexAttribute feature,
			IAbout propertyAbout, boolean create) {
		return getProperty(feature,
				DetailedAbout.getDetailedAbout(propertyAbout, true)
						.getProperties(), create);
	}

	/**
	 * Get the existing properties for the given feature. To get nested
	 * properties you will have to call this method on its properties.
	 * 
	 * @param feature
	 *            the feature, complex attribute or property
	 * 
	 * @return the feature's properties
	 */
	public static Collection<Property> getProperties(Property feature) {
		Collection<Property> result = new ArrayList<Property>();

		if (feature instanceof ComplexAttribute) {
			// property is a complex attribute -> add its properties
			result.addAll(((ComplexAttribute) feature).getProperties());
		} else {
			Object propertyValue = feature.getValue();
			if (propertyValue instanceof ComplexAttribute) {
				// value is a complex property -> retrieve its properties
				result.addAll(getProperties((Property) propertyValue));
			}
		}

		// add attributes
		result.addAll(AttributeProperty.getAttributeProperties(feature));

		return result;
	}

	/**
	 * Get the (nested) property of a feature
	 * 
	 * @param feature
	 *            the feature
	 * @param properties
	 *            the property names specifying the (nested) property
	 * @param create
	 *            if the property shall be created if it's not present
	 * 
	 * @return the property or <code>null</code> if the property is not present
	 *         and was not created
	 */
	public static Property getProperty(ComplexAttribute feature,
			List<String> properties, boolean create) {
		Queue<String> propertiesQueue = new LinkedList<String>(properties);

		Property parent = feature;
		while (!propertiesQueue.isEmpty()) {
			String propertyName = propertiesQueue.poll();

			Property property = null;

			if (parent instanceof ComplexAttribute) {
				ComplexAttribute complex = (ComplexAttribute) parent;
				ComplexType complexType = complex.getType();

				property = complex.getProperty(propertyName);
				if (property == null) {
					// property is not present or property is attribute
					PropertyDescriptor pd = complexType
							.getDescriptor(propertyName);
					if (pd != null && create) {
						// create property
						property = createProperty(pd);

						// set property to feature type
						Collection<? extends Property> pvs = complex.getValue();
						Collection<Property> values = new ArrayList<Property>(
								pvs);
						values.add(property);
						complex.setValue(values);
					} else {
						// no property descriptor - property is attribute or
						// invalid
						// -> we assume it's an attribute

						// create attribute property
						if (propertiesQueue.isEmpty()) {
							return createAttributeProperty(complex,
									propertyName);
						}
					}
				}
			} else {
				// parent is a property, child can only be an attribute
				// property, but we can't descend any more
				if (propertiesQueue.isEmpty()) {
					return createAttributeProperty(parent, propertyName);
				}
			}

			if (property == null) {
				log.debug("Could not find/create property " + propertyName
						+ " in " + parent.getName().getLocalPart());
				return null;
			} else {
				if (propertiesQueue.isEmpty()) {
					// last property
					return property;
				} else {
					// prepare next iteration
					Object value = property.getValue();
					if (value == null) {
						value = createValue(property.getDescriptor());
						property.setValue(value);
					}

					// in case the value is wrapped in a collection
					if (value instanceof Collection<?>) {
						Collection<?> values = (Collection<?>) value;
						if (!values.isEmpty()) {
							value = values.iterator().next();
						}
					}

					if (value instanceof ComplexAttribute) {
						// new parent is complex attribute
						parent = (Property) value;
					} else if (propertiesQueue.size() == 1) {
						// new parent is property, attribute property may be set
						// in next iteration
						parent = property;
					} else {
						// no valid property value
						log.error("Getting nested property failed: Property "
								+ propertyName + " of type "
								+ parent.getName().getLocalPart()
								+ " has no complex value");
						return null;
					}
				}
			}
		}

		// no propertes were defined
		throw new IllegalArgumentException("No properties were defined");
	}

	/**
	 * Create an attribute property
	 * 
	 * @param parent
	 *            the parent object
	 * @param propertyName
	 *            the property name
	 * 
	 * @return the attribute property
	 */
	private static Property createAttributeProperty(Property parent,
			String propertyName) {
		return new AttributeProperty(parent, propertyName);
	}

	/**
	 * Create a default value for a property with an empty value
	 * 
	 * @param pd
	 *            the property descriptor
	 * 
	 * @return the value
	 */
	private static Object createValue(PropertyDescriptor pd) {
		Object value = null;

		if (pd.getType() instanceof ComplexType) {
			ComplexType type = (ComplexType) pd.getType();
			value = new ComplexAttributeImpl(new ArrayList<Property>(), type,
					new FeatureIdImpl(UUID.randomUUID().toString()));
		}

		Class<?> binding = pd.getType().getBinding();
		if (Collection.class.isAssignableFrom(binding)) {
			// wrap value in collection
			Collection<Object> collection = new ArrayList<Object>();
			collection.add(value);
			value = collection;
		}

		return value;
	}

	/**
	 * Create a new property from a property descriptor
	 * 
	 * @param pd
	 *            the property descriptor
	 * 
	 * @return the property
	 */
	private static Property createProperty(PropertyDescriptor pd) {
		Object value = createValue(pd);

		return new PropertyImpl(value, pd);
	}

}
