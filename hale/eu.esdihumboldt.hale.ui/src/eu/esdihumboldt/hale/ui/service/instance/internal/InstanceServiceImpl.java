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
package eu.esdihumboldt.hale.ui.service.instance.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.commons.tools.RobustFTKey;
import eu.esdihumboldt.hale.ui.service.instance.FeatureFilter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.crs.CRSDefinition;
import eu.esdihumboldt.hale.ui.service.instance.crs.CodeDefinition;
import eu.esdihumboldt.hale.ui.service.instance.crs.WKTDefinition;
import eu.esdihumboldt.hale.ui.service.instance.crs.internal.SelectCRSDialog;

/**
 * This class implements the {@link InstanceService} as a Singleton.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 */
public class InstanceServiceImpl extends AbstractInstanceService {
	
	//private static Logger _log = Logger.getLogger(InstanceServiceImpl.class);
	
	private static InstanceServiceImpl instance = new InstanceServiceImpl();
	
	private FeatureCollection<?, Feature> sourceReferenceFeatures = null;
	
	private FeatureCollection<?, Feature> transformedFeatures = null;

	private CRSDefinition crs;
	
	// Constructors ............................................................

	/**
	 * Default constructor
	 */
	private InstanceServiceImpl() {
		super();
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
	@Override
	public Feature getFeatureByID(DatasetType type, String featureID) {
		switch (type) {
		case source:
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
			Feature current_feature = fi.next();
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
	@Override
	public Collection<Feature> getFeaturesByType(DatasetType type, FeatureType featureType) {
		switch (type) {
		case source:
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
				Feature current_feature = fi.next();
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
	@Override
	public boolean addInstances(DatasetType type, 
			FeatureCollection<FeatureType, Feature> featureCollection) {
		if (type.equals(DatasetType.source)) {
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
			// determine crs
			updateCRS(sourceReferenceFeatures);
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
	@Override
	public boolean addInstances(DatasetType type, 
			FeatureCollection<FeatureType, Feature> featureCollection,
			FeatureFilter filter) {
		int startsize = 0;
		if (type.equals(DatasetType.source)) {
			startsize = sourceReferenceFeatures.size();
			@SuppressWarnings("rawtypes")
			FeatureIterator fi = featureCollection.features();
			while (fi.hasNext()) {
				Feature f = fi.next();
				if (filter.filter(f)) {
					sourceReferenceFeatures.add(f);
				}
			}
			// determine crs
			updateCRS(sourceReferenceFeatures);
		}
		else if (type.equals(DatasetType.transformed)) {
			startsize = transformedFeatures.size();
			@SuppressWarnings("rawtypes")
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
	 * @see InstanceService#cleanInstances(DatasetType)
	 */
	@Override
	public boolean cleanInstances(DatasetType type) {
		if (type == null) { 
			return false;
		}
		if (type.equals(DatasetType.transformed)) {
			transformedFeatures = null;
		}
		if (type.equals(DatasetType.source)) {
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public FeatureCollection getFeatures(DatasetType type) {
		if (DatasetType.source.equals(type)) {
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
		if (DatasetType.source.equals(type)) {
			oldFeatures = sourceReferenceFeatures;
		}
		else {
			oldFeatures = transformedFeatures;
		}
		// add original features to merged collection
		@SuppressWarnings("rawtypes")
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
		@SuppressWarnings("rawtypes")
		FeatureCollection mergedFc = 
			FeatureCollections.newCollection();
		
		for (Set<Feature> features : mergedFeatures.values()) {
			mergedFc.addAll(features);
		}
		
		// assign result and return...
		if (DatasetType.source.equals(type)) {
			sourceReferenceFeatures = mergedFc;
			// determine crs
			updateCRS(sourceReferenceFeatures);
		}
		else {
			transformedFeatures = mergedFc;
		}
		
		notifyDatasetChanged(type);
		
		return result;
	}

	private void updateCRS(
			FeatureCollection<?, Feature> fc) {
		CRSDefinition crsDef = determineCRS(fc);
		setCRS(crsDef);
	}

	/**
	 * @see InstanceService#setCRS(CRSDefinition)
	 */
	@Override
	public void setCRS(CRSDefinition crs) {
		this.crs = crs;
		
		notifyCRSChanged(crs);
	}

	/**
	 * @see InstanceService#getCRS()
	 */
	@Override
	public CRSDefinition getCRS() {
		return crs;
	}
	
	/**
	 * Determine the coordinate reference system for a feature collection
	 * 
	 * @param fc the feature collection
	 * 
	 * @return the coordinate reference system or null
	 */
	public static CRSDefinition determineCRS(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		CoordinateReferenceSystem crs = null;
		
		// try the instance data first.
		if (fc != null && !fc.isEmpty()) {
			Feature f = fc.features().next();
			if (f.getDefaultGeometryProperty() != null) {
				GeometryAttribute gp = f.getDefaultGeometryProperty();
				crs = gp.getDescriptor().getCoordinateReferenceSystem();
				
				if (crs == null) {
					// next try - user data of value
					Object value = gp.getValue();
					if (value instanceof Geometry) {
						Object userData = ((Geometry) value).getUserData();
						if (userData instanceof CoordinateReferenceSystem) {
							crs = (CoordinateReferenceSystem) userData;
						}
					}
				}
			}
		}
		
		// then check the schema.
		if (crs == null && fc != null) {
			crs = fc.getSchema().getCoordinateReferenceSystem();
		}
		
		CRSDefinition crsDef = null;
		
		// if none is available, use a default.
		if (crs == null) {
			final Display display = Display.getCurrent();
			
			SelectCRSDialog dialog = new SelectCRSDialog(display.getActiveShell(), null);
			while (crsDef == null) {
				if (dialog.open() != SelectCRSDialog.OK) {
					break;
				}
				else {
					crsDef = dialog.getValue();
				}
			}
		}
		else {
			try {
				crsDef = new CodeDefinition(crs.getIdentifiers().iterator().next().toString(), crs);
			} catch (Exception e) {
				crsDef = new WKTDefinition(crs.toWKT(), crs);
			}
		}
		
		return crsDef;
	}

}
