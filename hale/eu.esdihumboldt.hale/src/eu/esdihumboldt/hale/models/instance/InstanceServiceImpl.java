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
package eu.esdihumboldt.hale.models.instance;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.FeatureFilter;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.tools.RobustFTKey;

/**
 * This class implements the {@link InstanceService} as a Singleton.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version {$Id}
 */
public class InstanceServiceImpl extends AbstractInstanceService {
	
	//private static Logger _log = Logger.getLogger(InstanceServiceImpl.class);
	
	private static InstanceServiceImpl instance = new InstanceServiceImpl();
	
	private FeatureCollection<?, Feature> sourceReferenceFeatures = null;
	
	private FeatureCollection<?, Feature> transformedFeatures = null;
	
	// Constructors ............................................................

	/**
	 * Default constructor
	 */
	private InstanceServiceImpl() {
	}
	
	/**
	 * @return the singleton instance of the {@link InstanceServiceImpl}.
	 */
	public static InstanceService getInstance() {
		return InstanceServiceImpl.instance;
	}

	// InstanceService methods .................................................

	/**
	 * TODO: Does not currently use an index.
	 * @see InstanceService#getFeatureByID(DatasetType, String)
	 */
	public Feature getFeatureByID(DatasetType type, String featureID) {
		switch (type) {
		case reference:
			return getFeatureByID(featureID, sourceReferenceFeatures);
		case transformed:
			return getFeatureByID(featureID, transformedFeatures);
		default:
			return getFeatureByID(featureID, sourceReferenceFeatures);
		}
	}
	
	private Feature getFeatureByID(String featureID, FeatureCollection<?, Feature> features) {
		Feature f = null;
		FeatureIterator<? extends Feature> fi = features.features();
		while (fi.hasNext()) {
			Feature current_feature = (Feature) fi.next();
			String current_feature_id = current_feature.getIdentifier().getID();
			if (featureID.equals(current_feature_id)) {
				return current_feature;
			}
		}
		return f;
	}

	/**
	 * TODO: Does not currently use an index.
	 * @see InstanceService#getFeaturesByType(DatasetType, FeatureType)
	 */
	public Collection<Feature> getFeaturesByType(DatasetType type, FeatureType featureType) {
		switch (type) {
		case reference:
			return getFeaturesByType(featureType, sourceReferenceFeatures);
		case transformed:
			return getFeaturesByType(featureType, transformedFeatures);
		default:
			return getFeaturesByType(featureType, sourceReferenceFeatures);
		}
	}
	
	private Collection<Feature> getFeaturesByType(FeatureType featureType, 
			FeatureCollection<?, Feature> features) {
		Set<Feature> result = new HashSet<Feature>();
		RobustFTKey searchKey = new RobustFTKey(featureType);
		if (features != null) {
			FeatureIterator<? extends Feature> fi = features.features();
			while (fi.hasNext()) {
				Feature current_feature = (Feature) fi.next();
				RobustFTKey candidateKey = new RobustFTKey(current_feature.getType());
				if (searchKey.equals(candidateKey)) {
					result.add(current_feature);
				}
			}
		}
		
		return result;
	}

	/**
	 * @see InstanceService#addInstances(DatasetType, FeatureCollection)
	 */
	public boolean addInstances(DatasetType type, 
			FeatureCollection<FeatureType, Feature> featureCollection) {
		if (type.equals(DatasetType.reference)) {
			if (sourceReferenceFeatures == null) {
				sourceReferenceFeatures = featureCollection;
			}
			else {
				FeatureIterator<? extends Feature> fi = featureCollection.features();
				while (fi.hasNext()) {
					sourceReferenceFeatures.add(fi.next());
				}
			}
			notifyDatasetChanged(type);
			return true;
		}
		else if (type.equals(DatasetType.transformed)) {
			if (transformedFeatures == null) {
				transformedFeatures = featureCollection;
			}
			else {
				FeatureIterator<? extends Feature> fi = featureCollection.features();
				while (fi.hasNext()) {
					transformedFeatures.add(fi.next());
				}
			}
			notifyDatasetChanged(type);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @see InstanceService#addInstances(DatasetType, FeatureCollection, FeatureFilter)
	 */
	@SuppressWarnings("unchecked")
	public boolean addInstances(DatasetType type, 
			FeatureCollection<FeatureType, Feature> featureCollection,
			FeatureFilter filter) {
		int startsize = 0;
		if (type.equals(DatasetType.reference)) {
			startsize = sourceReferenceFeatures.size();
			FeatureIterator fi = featureCollection.features();
			while (fi.hasNext()) {
				Feature f = fi.next();
				if (filter.filter(f)) {
					sourceReferenceFeatures.add(f);
				}
			}
		}
		else if (type.equals(DatasetType.transformed)) {
			startsize = transformedFeatures.size();
			FeatureIterator fi = featureCollection.features();
			while (fi.hasNext()) {
				Feature f = fi.next();
				if (filter.filter(f)) {
					transformedFeatures.add(f);
				}
			}
		}
		if (startsize != 0  || sourceReferenceFeatures.size() > startsize) {
			notifyDatasetChanged(type);
			return true;
		} 
		else {
			return false;
		}
	}

	/**
	 * @see InstanceService#cleanInstances()
	 */
	public boolean cleanInstances(DatasetType type) {
		if (type == null) { 
			return false;
		}
		if (type.equals(DatasetType.transformed)) {
			transformedFeatures = null;
		}
		if (type.equals(DatasetType.reference)) {
			sourceReferenceFeatures = null;
		}
		notifyDatasetChanged(type);
		return true;
	}

	/**
	 * @see InstanceService#cleanInstances()
	 */
	@Override
	public boolean cleanInstances() {
		transformedFeatures = null;
		sourceReferenceFeatures = null;
		
		notifyDatasetChanged(null);
		
		return true;
	}

	/**
	 * @see InstanceService#getFeatures(DatasetType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FeatureCollection getFeatures(DatasetType type) {
		if (DatasetType.reference.equals(type)) {
			return sourceReferenceFeatures;
		}
		else if (DatasetType.transformed.equals(type)) {
			return transformedFeatures;
		}
		else {
			return null;
		}
	}

	/**
	 * @see InstanceService#replaceInstances(DatasetType, FeatureCollection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean replaceInstances(DatasetType type,
			FeatureCollection<FeatureType, Feature> newFeatures) {
		Map<RobustFTKey, Set<Feature>> mergedFeatures = 
			new HashMap<RobustFTKey, Set<Feature>>();
		FeatureCollection<?, Feature> oldFeatures = null;
		if (DatasetType.reference.equals(type)) {
			oldFeatures = sourceReferenceFeatures;
		}
		else {
			oldFeatures = transformedFeatures;
		}
		// add original features to merged collection
		FeatureIterator fi = oldFeatures.features();
		while (fi.hasNext()) {
			Feature f = fi.next();
			RobustFTKey fkey = new RobustFTKey(f.getType());
			Set<Feature> tmp = mergedFeatures.get(fkey);
			if (tmp != null) {
				tmp.add(f);
			}
			else {
				tmp = new HashSet<Feature>();
				tmp.add(f);
				mergedFeatures.put(fkey, tmp);
			}
		}
		// add new features to merged collection
		boolean result = false;
		Set<RobustFTKey> clearedSet = new HashSet<RobustFTKey>();
		fi = newFeatures.features();
		while (fi.hasNext()) {
			Feature f = fi.next();
			RobustFTKey fkey = new RobustFTKey(f.getType());
			Set<Feature> tmp = mergedFeatures.get(fkey);
			if (tmp != null) {
				if (!clearedSet.contains(fkey)) {
					tmp.clear();
					clearedSet.add(fkey);
				}
				tmp.add(f);
			}
			else {
				tmp = new HashSet<Feature>();
				tmp.add(f);
				mergedFeatures.put(fkey, tmp);
				result = true;
			}
		}
		// recreate FeatureCollection from merged collection
		FeatureCollection mergedFc = 
			FeatureCollections.newCollection();
		
		for (Set<Feature> features : mergedFeatures.values()) {
			mergedFc.addAll(features);
		}
		
		// assign result and return...
		if (DatasetType.reference.equals(type)) {
			sourceReferenceFeatures = mergedFc;
		}
		else {
			transformedFeatures = mergedFc;
		}
		
		notifyDatasetChanged(type);
		
		return result;
	}

}
