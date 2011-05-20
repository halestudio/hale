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

package eu.esdihumboldt.hale.rcp.views.table.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeNode;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import eu.esdihumboldt.commons.tools.FeatureInspector;
import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.utils.tree.DefaultTreeNode;

/**
 * Tree item representing a feature type property
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PropertyItem extends DefaultTreeNode {
	
	private static final Logger log = Logger.getLogger(PropertyItem.class);
	
	/**
	 * Feature user data property name for XML attributes
	 */
	public static final String XML_ATTRIBUTES = "XmlAttributes"; //$NON-NLS-1$
	
	/**
	 * The property name
	 */
	private final String propertyName;
	
	/**
	 * if the property is represented as an XML attribute
	 */
	private final boolean isAttribute;

	/**
	 * Create a new property item
	 * 
	 * @param propertyName the property name
	 * @param label the item label
	 * @param isAttribute if the property is represented as an XML attribute
	 */
	public PropertyItem(String propertyName, String label, boolean isAttribute) {
		super(label);
		
		this.propertyName = propertyName;
		this.isAttribute = isAttribute;
	}
	
	/**
	 * Get the value of the property for the given feature
	 * 
	 * @param feature the feature
	 * 
	 * @return the feature's property value
	 */
	public String getText(Feature feature) {
		Object value = getValue(feature);
		if (value == null) {
			return ""; //$NON-NLS-1$
		}
		else {
			if (value instanceof Collection<?>) {
				String resultRepresentation = ""; //$NON-NLS-1$
				Collection<?> c = (Collection<?>) value;
				for (Iterator<?> iterator = c.iterator(); iterator.hasNext();) {
					Object o = iterator.next();
					if (o instanceof ComplexAttribute) {
						resultRepresentation += "+"; //$NON-NLS-1$
					}
					else {
						resultRepresentation += o.toString();
					}
				}
				return resultRepresentation;
			}
			else if (value instanceof ComplexAttribute) {
				return "+"; //$NON-NLS-1$
			}
			else {
				return value.toString();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Object getValue(Feature feature) {
		if (isAttribute) {
			// retrieve attribute value
			/*Map<String, String> attributes = (Map<String, String>) feature.getUserData().get(XML_ATTRIBUTES);
			if (attributes != null) { 
				String key = "";
				if (getParent() instanceof PropertyItem) {
					key = ((PropertyItem) getParent()).propertyName;
				}
				key += "<" + propertyName + ">";
				
				return attributes.get(key);
			}
			else {
				return null;
			}*/
			// using feature inspector
			List<String> properties = new ArrayList<String>();
			
			PropertyItem current = this;
			while (current != null) {
				properties.add(0, current.propertyName);
				
				TreeNode parent = current.getParent();
				if (parent instanceof PropertyItem) {
					current = (PropertyItem) parent;
				}
				else {
					current = null;
				}
			}
			
			return FeatureInspector.getPropertyValue(feature, properties, null);
		}
		else if (getParent() instanceof PropertyItem) {
			// property of a property
			Object propertyValue = ((PropertyItem) getParent()).getValue(feature);
			if (propertyValue != null) {
				Collection<?> propertyValues = (Collection<?>) ((propertyValue instanceof Collection<?>)?(propertyValue):(Collections.singleton(propertyValue)));
				List<Object> values = new ArrayList<Object>();
				
				for (Object pValue : propertyValues) {
					if (pValue instanceof ComplexAttribute) {
						Property property = ((ComplexAttribute) pValue).getProperty(propertyName);
						if (property != null) {
							values.add(property.getValue());
						}
					}
				}
				
				if (values.isEmpty()) {
					return null;
				}
				else if (values.size() == 1) {
					return values.get(0);
				}
				else {
					return values;
				}
			}
			else {
				return null;
			}
		}
		else {
			// property of the feature
			Property property = feature.getProperty(propertyName);
			if (property != null) {
				return property.getValue();
			}
			else {
				log.warn("Error getting property " + propertyName  //$NON-NLS-1$
						+ " from feature of type "  //$NON-NLS-1$
						+ feature.getType().getName().getLocalPart());
				return "#not defined"; //$NON-NLS-1$
			}
		}
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((propertyName == null) ? 0 : propertyName.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyItem other = (PropertyItem) obj;
		if (propertyName == null) {
			if (other.propertyName != null)
				return false;
		} else if (!propertyName.equals(other.propertyName))
			return false;
		return true;
	}

}
