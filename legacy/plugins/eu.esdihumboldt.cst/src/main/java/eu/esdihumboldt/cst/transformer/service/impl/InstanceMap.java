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

package eu.esdihumboldt.cst.transformer.service.impl;

import java.util.List;

import org.opengis.feature.Feature;

/**
 * Holds lists of source features and transformed features with equal indices
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class InstanceMap {
	
	private final List<Feature> sourceFeatures;
	
	private final List<Feature> transformedFeatures;

	/**
	 * Create an instance map
	 * 
	 * @param sourceFeatures the source features
	 * @param transformedFeatures the corresponding target features
	 */
	public InstanceMap(List<Feature> sourceFeatures,
			List<Feature> transformedFeatures) {
		super();
		this.sourceFeatures = sourceFeatures;
		this.transformedFeatures = transformedFeatures;
	}

	/**
	 * @return the sourceFeatures
	 */
	public List<Feature> getSourceFeatures() {
		return sourceFeatures;
	}

	/**
	 * @return the transformedFeatures
	 */
	public List<Feature> getTransformedFeatures() {
		return transformedFeatures;
	}
	
}
