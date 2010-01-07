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

package eu.esdihumboldt.hale.models;

import java.util.Collection;

import org.opengis.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.rcp.views.map.MapView;

/**
 * The {@link InstanceService} provides Features out of the candidate and 
 * reference data sets to the different views in HALE, such as the 
 * {@link MapView} and the TableView. It is also used to send {@link Feature}s
 * that should be transformed to the embedded CTS.
 * 
 * @author Thorsten Reitz
 * @version {$Id}
 */
public interface InstanceService 
	extends UpdateService {
	
	/**
	 * @param type the {@link DatasetType} that indicates whether you want to
	 * retrieve the transformed instance data, the reference instance data or
	 * both.
	 * @return the entire {@link FeatureCollection} currently held in the 
	 * model.
	 */
	public FeatureCollection<FeatureType, Feature> getFeatures(DatasetType type);
	
	/**
	 * @param featureID the D of the {@link Feature} to return.
	 * @return a single {@link Feature} as identified by the given _featureID.
	 */
	public Feature getFeatureByID(String featureID);
	
	/**
	 * @param featureType the {@link FeatureType} which all returned {@link Feature}s must have.
	 * @return a new {@link Collection} containing only {@link Feature}s of the given type.
	 */
	public Collection<? extends Feature> getFeaturesByType(FeatureType featureType);
	
	/**
	 * Add the {@link Feature} in the collection to the {@link InstanceService}
	 * @param type the {@link DatasetType} that indicates whether you want to
	 * add the input {@link FeatureCollection} to the transformed instance data 
	 * or the reference instance. Adding to both is not allowed.
	 * @param featureCollection the {@link FeatureCollection} from which to add 
	 * all {@link Feature}s.
	 * @return true if the instances have been added successfully.
	 */
	public boolean addInstances(DatasetType type, FeatureCollection<FeatureType, Feature> featureCollection);
	
	/**
	 * Add the {@link Feature} in the collection to the {@link InstanceService}
	 * @param type the {@link DatasetType} that indicates whether you want to
	 * add the input {@link FeatureCollection} to the transformed instance data 
	 * or the reference instance. Adding to both is not allowed.
	 * @param featureCollection the {@link FeatureCollection} from which to add 
	 * all {@link Feature}s.
	 * @return true if the instances have been added successfully.
	 */
	public boolean addInstances(DatasetType type, FeatureCollection<FeatureType, Feature> featureCollection, FeatureFilter filter);
	
	/**
	 * Replace Features for a given FeatureType (both defined in the 
	 * featureCollection) and {@link DatasetType}.
	 * @param type the {@link DatasetType} within which to replace instances.
	 * @param featureCollection the {@link FeatureCollection} that contains the
	 * Features that should be used as replacements. Please note that this 
	 * operation is expected to work on the granularity of FeatureTypes, i.e. 
	 * all Features of a Type represented in this parameter will be replaced by 
	 * the Features in this parameter.
	 * @return true if any existing instances were replaced, false if none 
	 * were replaced.
	 */
	public boolean replaceInstances(DatasetType type, FeatureCollection<FeatureType, Feature> featureCollection);
	
	/**
	 * This will remove all instances from the service.
	 * @param type the {@link DatasetType} that indicates which instances you
	 * want to clean.
	 * @return true if the cleaning operation was successful.
	 */
	public boolean cleanInstances(DatasetType type);
	
	/**
	 * This will remove all instances from the service.
	 * @return true if the cleaning operation was successful.
	 */
	public boolean cleanInstances();
	
	/**
	 * This enum is used to identify on which data set you want to perform any
	 * of the operations that this Service offers.
	 */
	public enum DatasetType {
		reference,
		transformed
	}
	
}
