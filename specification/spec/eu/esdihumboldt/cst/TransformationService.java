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
package eu.esdihumboldt.hale.models;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.modelrepository.abstractfc.mapping.Alignment;

/**
 * This is the interface used by HALE to access schema transformation 
 * capabilities.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface TransformationService  {
	
	/**
	 * This method can be employed to transform an entire data set using an
	 * entire mapping.
	 * @param fc a {@link FeatureCollection} with {@link Feature}s to be 
	 * transformed, with source {@link FeatureType}s.
	 * @param al the {@link Alignment} to be applied
	 * @return a {@link FeatureCollection} with the transformed Features, with 
	 * target {@link FeatureType}s.
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> 
		transform(
				FeatureCollection<? extends FeatureType, ? extends Feature> fc, 
				Alignment al);
	
	/**
	 * Transform a single {@link Feature} using a single {@link Cell}.
	 * @param f a single {@link Feature} to be transformed using the given 
	 * {@link Cell}.
	 * @param c a single cell containing mappings for the {@link FeatureType} 
	 * of the provided {@link Feature}.
	 * @return the transformed {@link Feature}.
	 */
	public Feature transform(Feature f, Cell c);

}
