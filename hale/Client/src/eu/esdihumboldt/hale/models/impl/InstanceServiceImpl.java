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
package eu.esdihumboldt.hale.models.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.FeatureFilter;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.rcp.views.model.RobustFTKey;

/**
 * This class implements the {@link InstanceService} as a Singleton.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version {$Id}
 */
public class InstanceServiceImpl 
	implements InstanceService {
	
	private static Logger _log = Logger.getLogger(InstanceServiceImpl.class);
	
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
	 * @see eu.esdihumboldt.hale.models.InstanceService#getFeatureByID(java.lang.String)
	 */
	public Feature getFeatureByID(String featureID) {
		Feature f = null;
		FeatureIterator<? extends Feature> fi = this.sourceReferenceFeatures.features();
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
	 * @see eu.esdihumboldt.hale.models.InstanceService#getFeaturesByType(org.geotools.feature.FeatureType)
	 */
	public Collection<Feature> getFeaturesByType(FeatureType featureType) {
		Set<Feature> result = new HashSet<Feature>();
		RobustFTKey searchKey = new RobustFTKey(featureType);
		FeatureIterator<? extends Feature> fi = this.sourceReferenceFeatures.features();
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
	 * @see eu.esdihumboldt.hale.models.InstanceService#addInstances(FeatureCollection)
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
			this.updateListeners();
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
			this.updateListeners();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#addInstances(FeatureCollection, FeatureFilter)
	 */
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
			this.updateListeners();
			return true;
		} 
		else {
			return false;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#cleanInstances()
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
		this.updateListeners();
		return true;
	}

	public boolean addListener(HaleServiceListener sl) {
		this.listeners.add(sl);
		return true;
	}
	
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update();
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#getFeatures(eu.esdihumboldt.hale.models.InstanceService.DatasetType)
	 */
	@Override
	public FeatureCollection getFeatures(DatasetType type) {
		if (DatasetType.reference.equals(type)) {
			if (this.sourceReferenceFeatures != null) {
				_log.warn(this.sourceReferenceFeatures.getSchema());
			}
			return this.sourceReferenceFeatures;
		}
		else if (DatasetType.transformed.equals(type)) {
			return this.transformedFeatures;
		}
		else {
			return null;
		}
	}


}
