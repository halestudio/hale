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

package eu.esdihumboldt.cst.transformer;

import java.util.List;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.capabilities.CstServiceCapabilities;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;

/**
 * This is the interface used by HALE to access schema transformation 
 * capabilities.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface CstService {
	
	/**
	 * This method can be employed to transform an entire data set using an
	 * entire mapping.
	 * @param fc a {@link FeatureCollection} with {@link Feature}s to be 
	 * transformed, with source {@link FeatureType}s.
	 * @param al the {@link Alignment} to be applied
	 * @param targetSchema a Set of {@link FeatureType}s that make up the target
	 * schema to use.
	 * @return a {@link FeatureCollection} with the transformed Features, with 
	 * target {@link FeatureType}s.
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> 
		transform(
				FeatureCollection<? extends FeatureType, ? extends Feature> fc, 
				IAlignment al, Set<FeatureType> targetSchema);
	
	/**
	 * Transform a single {@link Feature} using a single {@link Cell}.
	 * @param f a single {@link Feature} to be transformed using the given 
	 * {@link Cell}.
	 * @param c a single cell containing mappings for the {@link FeatureType} 
	 * of the provided {@link Feature}.
	 * @return the transformed {@link Feature}.
	 */
	public Feature transform(Feature f, ICell c);
	
	/**
	 * Get a description of the Capabilities of this Transformation Service.
	 * @return a CstServiceCapabilities object describing all the 
	 * functions implemented by this {@link CstService}.
	 */
	public CstServiceCapabilities getCapabilities();
	
	/**
	 * @param packageName the name of the package from which to register 
	 * {@link CstFunction} implementations
	 * @return a List of with the qualified class names of the functions that
	 * have been registered
	 */
	public List<String> registerCstFunctions(String packageName);

}
