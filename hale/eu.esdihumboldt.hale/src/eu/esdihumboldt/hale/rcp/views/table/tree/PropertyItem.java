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

import java.util.Arrays;
import java.util.Collection;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import eu.esdihumboldt.hale.rcp.utils.tree.MultiColumnTreeNode;

/**
 * Tree item representing a feature type property
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PropertyItem extends MultiColumnTreeNode {
	
	/**
	 * The property name
	 */
	private final String propertyName;

	/**
	 * Create a new property item
	 * 
	 * @param propertyName the property name
	 * @param label the item label
	 */
	public PropertyItem(String propertyName, String label) {
		super(label);
		
		this.propertyName = propertyName;
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
			return "null";
		}
		else {
			if (value instanceof Collection<?>) {
				return Arrays.toString(((Collection<?>) value).toArray());
			}
			else {
				return value.toString();
			}
		}
	}
	
	private Object getValue(Feature feature) {
		if (getParent() instanceof PropertyItem) {
			// property of a property
			Object propertyValue = ((PropertyItem) getParent()).getValue(feature);
			if (propertyValue != null && propertyValue instanceof Feature) {
				Property property = ((Feature) propertyValue).getProperty(propertyName);
				if (property != null) {
					return property.getValue();
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		}
		else {
			// property of the feature
			Property property = feature.getProperty(propertyName);
			return property.getValue();
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
