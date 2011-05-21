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
package eu.esdihumboldt.hale.schema.helper;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;

/**
 * Commonly used {@link FeatureType} methods/definitions
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class FeatureTypeHelper {
	
	/**
	 * Determine if the given feature type is an abstract feature type
	 * 
	 * @param ft the feature type
	 * @return if the feature type is abstract
	 */
	public static boolean isAbstract(FeatureType ft) {
		return ft.isAbstract() || 
			(ft.getSuper() == null && ft.getName().getLocalPart().equalsIgnoreCase("AbstractFeatureType")); //$NON-NLS-1$
	}
	
	/**
	 * Determine if the given feature type is a property type
	 * 
	 * @param ft the feature type
	 * @return if the feature type is a property type
	 */
	public static boolean isPropertyType(FeatureType ft) {
		if (ft.getName().getLocalPart().equalsIgnoreCase("AbstractFeatureType")) { //$NON-NLS-1$
			return false;
		}
		else {
			AttributeType parent = ft.getSuper();
			if (parent != null && parent instanceof FeatureType) {
				//return isPropertyType((FeatureType) parent);
				//FIXME feature type hierarchy is inconsistent - so the above code doesn't yield the expected result
				return false;
			}
			else {
				return true;
			}
		}
	}

}
