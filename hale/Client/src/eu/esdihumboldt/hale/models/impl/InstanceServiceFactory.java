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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.opengis.feature.Feature;
import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.FeatureFilter;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;

/**
 * This class implements the {@link InstanceService} as a Singleton.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version {$Id}
 */
public class InstanceServiceFactory implements InstanceService {
	
	private static Logger _log = Logger.getLogger(InstanceServiceFactory.class);
	
	private static InstanceServiceFactory instance = new InstanceServiceFactory();
	
	private Map<String, Feature> referenceFeatures;
	
	private Map<String, Feature> transformedFeatures;
	
	
	// Constructors ............................................................
	
	private InstanceServiceFactory() {
		this.referenceFeatures = new TreeMap<String, Feature>();
		this.transformedFeatures = new TreeMap<String, Feature>();
	}
	
	/**
	 * @return the singleton instance of the {@link InstanceServiceFactory}.
	 */
	public static InstanceService getInstance() {
		return InstanceServiceFactory.instance;
	}

	// InstanceService methods .................................................
	
	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#getAllFeatures()
	 */
	public Collection<Feature> getAllFeatures(DatasetType type) {
		if (type.equals(DatasetType.reference)) {
			return this.referenceFeatures.values();
		}
		else if (type.equals(DatasetType.transformed)) {
			return this.transformedFeatures.values();
		}
		else { // return both.
			Collection<Feature> result = new HashSet<Feature>();
			result.addAll(this.referenceFeatures.values());
			result.addAll(this.transformedFeatures.values());
			return result;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#getFeatureByID(java.lang.String)
	 */
	public Feature getFeatureByID(String featureID) {
		Feature f = this.transformedFeatures.get(featureID);
		if (f == null) {
			f = this.referenceFeatures.get(featureID);
		}
		return f;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#getFeaturesByType(org.geotools.feature.FeatureType)
	 */
	public Collection<Feature> getFeaturesByType(FeatureType featureType) {
		Set<Feature> result = new HashSet<Feature>();
		String search_ft_id = featureType.getName().toString();
		_log.debug("search name: " + search_ft_id);
		for (Feature f : this.getAllFeatures(DatasetType.both)) {
			String candidate_ft_id = f.getType().getName().toString();
			_log.debug("candidate name: " + candidate_ft_id);
			if (candidate_ft_id.equals(search_ft_id)) {
				result.add(f);
			}
		}
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.InstanceService#addInstances(FeatureCollection)
	 */
	public boolean addInstances(DatasetType type, 
			FeatureCollection featureCollection) {
		Map<String, Feature> selected_type_map = null;
		if (type.equals(DatasetType.reference)) {
			selected_type_map = this.referenceFeatures;
		}
		else {
			selected_type_map = this.transformedFeatures;
		}
		int startsize = selected_type_map.size();
		FeatureIterator fi = featureCollection.features();
		while (fi.hasNext()) {
			Feature f = fi.next();
			selected_type_map.put(f.getIdentifier().getID(), f);
		}
		if (selected_type_map.size() > startsize) {
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
			FeatureCollection featureCollection,
			FeatureFilter filter) {
		int startsize = this.referenceFeatures.size();
		FeatureIterator fi = featureCollection.features();
		while (fi.hasNext()) {
			Feature f = fi.next();
			if (filter.filter(f)) {
				this.referenceFeatures.put(f.getIdentifier().getID(), f);
			}
		}
		if (this.referenceFeatures.size() > startsize) {
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
		if (type.equals(DatasetType.transformed) || type.equals(DatasetType.both)) {
			this.transformedFeatures = new TreeMap<String, Feature>();
		}
		if (type.equals(DatasetType.reference) || type.equals(DatasetType.both)) {
			this.referenceFeatures = new TreeMap<String, Feature>();
		}
		
		return true;
	}

	@Override
	public boolean addListener(HaleServiceListener sl) {
		// TODO Auto-generated method stub
		return false;
	}

}
