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

package eu.esdihumboldt.hale.rcp.views.model;

import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.rcp.utils.FeatureTypeHelper;

/**
 * Schema item representing a feature type
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureTypeItem extends TreeParent {

	/**
	 * Creates a feature type item
	 * 
	 * @param featureType the feature type
	 */
	public FeatureTypeItem(FeatureType featureType) {
		super(
				featureType.getName().getLocalPart(), 
				featureType.getName(), 
				determineType(featureType), 
				featureType);
	}

	/**
	 * Determine the {@link TreeObject.TreeObjectType} for a feature type
	 * 
	 * @param featureType the feature type
	 * 
	 * @return the tree object type
	 */
	private static TreeObjectType determineType(FeatureType featureType) {
		TreeObjectType tot = TreeObjectType.CONCRETE_FT;
		if (FeatureTypeHelper.isPropertyType(featureType)) {
			tot = TreeObjectType.PROPERTY_TYPE;
		}
		else if (FeatureTypeHelper.isAbstract(featureType)) {
			tot = TreeObjectType.ABSTRACT_FT;
		}
		
		return tot;
	}

}
