package eu.esdihumboldt.cst.transformer.service.impl;
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


import java.util.List;

import org.opengis.feature.Feature;

/**
 * Holds lists of source features and the corresponding features aggregated from 
 * those source features 
 *
 * @author Ulrich Schaeffler
 * @partner 14 / TUM
 * @version $Id$ 
 */
public class InstanceAggregateMap extends InstanceMap {

	/**
	 * Create an instance map
	 * 
	 * @param sourceFeatures the source features
	 * @param transformedFeatures the corresponding target features
	 */
	public InstanceAggregateMap(List<Feature> sourceFeatures,
			List<Feature> transformedFeatures) {
		super(sourceFeatures, transformedFeatures);
		
	}

}
