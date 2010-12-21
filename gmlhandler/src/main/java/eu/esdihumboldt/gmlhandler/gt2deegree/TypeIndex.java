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

package eu.esdihumboldt.gmlhandler.gt2deegree;

import java.util.HashMap;
import java.util.Map;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Index of {@link TypeDefinition}s. Allows relating {@link TypeDefinition} to
 * {@link FeatureType}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TypeIndex {
	
	/**
	 * Maps {@link FeatureType} names to {@link TypeDefinition}
	 */
	private final Map<Name, TypeDefinition> index = new HashMap<Name, TypeDefinition>();
	
	/**
	 * Add a type to the index
	 * 
	 * @param type the type to add
	 */
	public void addType(TypeDefinition type) {
		index.put(type.getFeatureType().getName(), type);
	}
	
	/**
	 * Get the type definition for the given feature type
	 * 
	 * @param ft the feature type
	 * 
	 * @return the type definition or <code>null</code>
	 */
	public TypeDefinition getType(FeatureType ft) {
		return index.get(ft.getName());
	}

}
