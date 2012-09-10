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
import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.NameImpl;
import org.geotools.feature.type.PropertyDescriptorImpl;
import org.geotools.xs.XSSchema;
import org.opengis.feature.Property;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

/**
 * Hides attribute setting/getting behind {@link Property} facade, uses user
 * data to store value.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class AttributeProperty implements Property {

	/**
	 * Default property descriptor for attributes.
	 * 
	 * XXX for now always a string, at this point we don't have access to the
	 * corresponding attribute definition
	 */
	private class AttributePropertyDescriptor extends PropertyDescriptorImpl {

		/**
		 * Default constructor
		 */
		public AttributePropertyDescriptor() {
			super(XSSchema.STRING_TYPE, new NameImpl(propertyName), 0, 1, true);
		}

	}

	/**
	 * Feature user data property name for XML attributes
	 */
	public static final String XML_ATTRIBUTES = "XmlAttributes";

	private final Property parent;

	private final String propertyName;

	private final PropertyDescriptor descriptor;

	/**
	 * Create an attribute property
	 * 
	 * @param parent
	 *            the parent object
	 * @param propertyName
	 *            the property name
	 */
	public AttributeProperty(Property parent, String propertyName) {
		this.parent = parent;
		this.propertyName = propertyName;

		this.descriptor = new AttributePropertyDescriptor();
	}

	/**
	 * @see Property#getDescriptor()
	 */
	@Override
	public PropertyDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * @see org.opengis.feature.Property#getName()
	 */
	@Override
	public Name getName() {
		return getDescriptor().getName();
	}

	/**
	 * @see Property#getType()
	 */
	@Override
	public PropertyType getType() {
		return getDescriptor().getType();
	}

	/**
	 * @see Property#getUserData()
	 */
	@Override
	public Map<Object, Object> getUserData() {
		throw new UnsupportedOperationException(
				"Attribute property doesn't support user data");
	}

	/**
	 * Get the key in the attribute map
	 * 
	 * @return the key
	 */
	private String getKey() {
		return '<' + propertyName + '>';
	}

	/**
	 * @see Property#getValue()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getValue() {
		Map<String, String> attributes = (Map<String, String>) parent
				.getUserData().get(XML_ATTRIBUTES);

		if (attributes != null) {
			return attributes.get(getKey());
		} else {
			return null;
		}
	}

	/**
	 * @see Property#isNillable()
	 */
	@Override
	public boolean isNillable() {
		return getDescriptor().isNillable();
	}

	/**
	 * @see Property#setValue(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object newValue) {
		Map<String, String> attributes = (Map<String, String>) parent
				.getUserData().get(XML_ATTRIBUTES);
		if (attributes == null) {
			attributes = new HashMap<String, String>();
			parent.getUserData().put(XML_ATTRIBUTES, attributes);
		}

		attributes.put(getKey(), newValue.toString());
	}

	/**
	 * Get the existing attributes
	 * 
	 * @param parent
	 *            the parent of the attribute properties
	 * 
	 * @return the existing attribute properties
	 */
	@SuppressWarnings("unchecked")
	public static Collection<? extends Property> getAttributeProperties(
			Property parent) {
		Collection<Property> result = new ArrayList<Property>();

		Object attributes = parent.getUserData().get(XML_ATTRIBUTES);
		if (attributes instanceof Map<?, ?>) {
			Map<String, String> attMap = (Map<String, String>) attributes;
			for (String attributeName : attMap.keySet()) {
				result.add(new AttributeProperty(parent, attributeName
						.substring(1, attributeName.length() - 1)));
			}
		}

		return result;
	}

}
