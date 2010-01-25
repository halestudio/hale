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
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.tools.RobustFTKey;

/**
 * This class implements the {@link InstanceService} as a Singleton.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version {$Id}
 */
public class InstanceServiceImpl 
	implements InstanceService {
	
	//private static Logger _log = Logger.getLogger(InstanceServiceImpl.class);
	
	private static InstanceServiceImpl instance = new InstanceServiceImpl();
	
	private FeatureCollection<?, Feature> sourceReferenceFeatures = null;
	
	private FeatureCollection<?, Feature> transformedFeatures = null;
	
	private Set<HaleServiceListener> listeners;
	
	
	// Constructors ............................................................
	
	private InstanceServiceImpl() {
		this.listeners = new HashSet<HaleServiceListener>();
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
		FeatureIterator<? extends Feature> fi = features.features();
		while (fi.hasNext()) {
			Feature current_feature = (Feature) fi.next();
			RobustFTKey candidateKey = new RobustFTKey(current_feature.getType());
			if (searchKey.equals(candidateKey)) {
				result.add(current_feature);
			}
		}
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#addInstances(DatasetType, FeatureCollection)
	 */
	public boolean addInstances(DatasetType type, 
			FeatureCollection<FeatureType, Feature> featureCollection) {
		if (type.equals(DatasetType.reference)) {
			if (this.sourceReferenceFeatures == null) {
				this.sourceReferenceFeatures = featureCollection;
			}
			else {
				FeatureIterator<? extends Feature> fi = featureCollection.features();
				while (fi.hasNext()) {
					this.sourceReferenceFeatures.add(fi.next());
				}
			}
			this.updateListeners(type);
			return true;
		}
		else if (type.equals(DatasetType.transformed)) {
			if (this.transformedFeatures == null) {
				this.transformedFeatures = featureCollection;
			}
			else {
				FeatureIterator<? extends Feature> fi = featureCollection.features();
				while (fi.hasNext()) {
					this.transformedFeatures.add(fi.next());
				}
			}
			this.updateListeners(type);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#addInstances(DatasetType, FeatureCollection, FeatureFilter)
	 */
	@SuppressWarnings("unchecked")
	public boolean addInstances(DatasetType type, 
			FeatureCollection<FeatureType, Feature> featureCollection,
			FeatureFilter filter) {
		int startsize = 0;
		if (type.equals(DatasetType.reference)) {
			startsize = this.sourceReferenceFeatures.size();
			FeatureIterator fi = featureCollection.features();
			while (fi.hasNext()) {
				Feature f = fi.next();
				if (filter.filter(f)) {
					this.sourceReferenceFeatures.add(f);
				}
			}
		}
		else if (type.equals(DatasetType.transformed)) {
			startsize = this.transformedFeatures.size();
			FeatureIterator fi = featureCollection.features();
			while (fi.hasNext()) {
				Feature f = fi.next();
				if (filter.filter(f)) {
					this.transformedFeatures.add(f);
				}
			}
		}
		if (startsize != 0  || this.sourceReferenceFeatures.size() > startsize) {
			this.updateListeners(type);
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
			this.transformedFeatures = null;
		}
		if (type.equals(DatasetType.reference)) {
			this.sourceReferenceFeatures = null;
		}
		this.updateListeners(type);
		return true;
	}

	/**
	 * @see InstanceService#cleanInstances()
	 */
	@Override
	public boolean cleanInstances() {
		transformedFeatures = null;
		sourceReferenceFeatures = null;
		
		this.updateListeners(null);
		
		return true;
	}

	public boolean addListener(HaleServiceListener sl) {
		this.listeners.add(sl);
		return true;
	}
	
	/**
	 * Update the listeners
	 * 
	 * @param type the data set that was changed, <code>null</code> if both were changed
	 */
	@SuppressWarnings("unchecked")
	private void updateListeners(DatasetType type) {
		for (HaleServiceListener hsl : this.listeners) {
			if (hsl instanceof InstanceServiceListener) {
				if (type == null) {
					((InstanceServiceListener) hsl).datasetChanged(DatasetType.reference);
					((InstanceServiceListener) hsl).datasetChanged(DatasetType.transformed);
				}
				else {
					((InstanceServiceListener) hsl).datasetChanged(type);
				}
			}
			
			hsl.update(new UpdateMessage(InstanceService.class, null)); // FIXME
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#getFeatures(eu.esdihumboldt.hale.models.InstanceService.DatasetType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FeatureCollection getFeatures(DatasetType type) {
		if (DatasetType.reference.equals(type)) {
			return this.sourceReferenceFeatures;
		}
		else if (DatasetType.transformed.equals(type)) {
			return this.transformedFeatures;
		}
		else {
			return null;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#replaceInstances(eu.esdihumboldt.hale.models.InstanceService.DatasetType, org.geotools.feature.FeatureCollection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean replaceInstances(DatasetType type,
			FeatureCollection<FeatureType, Feature> newFeatures) {
		Map<RobustFTKey, Set<Feature>> mergedFeatures = 
			new HashMap<RobustFTKey, Set<Feature>>();
		FeatureCollection<?, Feature> oldFeatures = null;
		if (DatasetType.reference.equals(type)) {
			oldFeatures = this.sourceReferenceFeatures;
		}
		else {
			oldFeatures = this.transformedFeatures;
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
			this.sourceReferenceFeatures = mergedFc;
		}
		else {
			this.transformedFeatures = mergedFc;
		}
		
		return result;
	}


}
