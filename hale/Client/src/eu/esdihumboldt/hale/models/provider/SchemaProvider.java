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
package eu.esdihumboldt.hale.models.provider;

import java.util.Collection;

import org.opengis.feature.type.FeatureType;

/**
 * A {@link SchemaProvider} generates new {@link FeatureType}s from a given 
 * input.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface SchemaProvider {

	/**
	 * Create a Collection of {@link FeatureType}s based on a given file path
	 * string.
	 * @return a {@link Collection} of {@link FeatureType}s.
	 */
	public Collection<FeatureType> createFeatureTypes(String path);
}
