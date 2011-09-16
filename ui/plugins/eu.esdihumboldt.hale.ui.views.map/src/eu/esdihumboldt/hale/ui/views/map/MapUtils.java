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
package eu.esdihumboldt.hale.ui.views.map;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.ui.PlatformUI;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.gml3.GMLSchema;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.ui.service.instance.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.style.helper.StyleHelper;

/**
 * Utility methods for rendering the map
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class MapUtils {
	
//	private static final ALogger log = ALoggerFactory.getLogger(FeatureTileRenderer.class);
	
	/**
	 * 
	 */
	public static final String GEOMETRY_PROPERTY = "geometry";

	/**
	 * 
	 */
	public static final String REFERENCE_PROPERTY = "reference";

	private static SimpleFeatureType geometryFeatureType;
	
	private static final AttributeType REFERENCE_TYPE = new AttributeTypeImpl(
        new NameImpl("http://www.esdi-humboldt.eu/hale","InstanceReference"), 
        InstanceReference.class, false,
        false, Collections.<Filter>emptyList(), null, null
    );
	
	/**
	 * @param crs the {@link CoordinateReferenceSystem} to use.
	 * @param dataSet the {@link DataSet} to render.
	 * @param status the paint status
	 * @param selection the feature IDs of the selected features 
	 * @param selectionOnly if only the selection shall be rendered
	 * @return a {@link MapContext} with the given CRS and the 
	 * {@link FeatureCollection} identified by the given {@link DataSet}.
	 */
	public static MapContext buildMapContext(CoordinateReferenceSystem crs, 
			DataSet dataSet, FeaturePaintStatus status, Set<InstanceReference> selection, 
			boolean selectionOnly) {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
//		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		
		int failed = 0;
		MapContext mc;
		
		InstanceCollection ic = is.getInstances(dataSet);
		
		if (!ic.isEmpty()) {
			FeatureCollection<SimpleFeatureType, SimpleFeature> groupedFeatures = new MemoryFeatureCollection(getGeometryFeatureType());
			FeatureCollection<SimpleFeatureType, SimpleFeature> selectedFeatures = new MemoryFeatureCollection(getGeometryFeatureType());
			
			ResourceIterator<Instance> it = ic.iterator();
			try {
				while (it.hasNext()) {
					SimpleFeature feature = createGeometryFeature(it.next(), is, dataSet);
					if (feature != null) {
						FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
						if (selectionOnly && selection.contains(feature.getProperty(REFERENCE_PROPERTY).getValue())) {
							collection = selectedFeatures;
						}
						else if (!selectionOnly) {
							collection = groupedFeatures;
						}
						else {
							collection = null;
						}
						
						if (collection != null) {
							collection.add(feature);
						}
						
						if (crs == null) { //XXX grabs CRS from the first feature
							Object userData = ((Geometry) feature.getProperty(GEOMETRY_PROPERTY).getValue()).getUserData();
							if (userData instanceof CoordinateReferenceSystem) {
								crs = (CoordinateReferenceSystem) userData;
							}
						}
					}
					else {
						failed++;
					}
				}
			} finally {
				it.close();
			}
			
			mc = new DefaultMapContext(crs);
			// add normal features
			Style style = StyleHelper.getStyle(getGeometryFeatureType()); //XXX ss.getStyle(dataSet);
			mc.addLayer(groupedFeatures, style);
			
			// add selected features
			style = StyleHelper.getStyle(getGeometryFeatureType()); //XXX ss.getSelectionStyle(dataSet);
			mc.addLayer(selectedFeatures, style);
		}
		else {
			mc = new DefaultMapContext(crs);
		}
		
		switch (dataSet) {
		case SOURCE:
			status.setReferenceFailed(failed);
			break;
		case TRANSFORMED:
			status.setTransformedFailed(failed);
			break;
		}
		
		return mc;
	}
	
	private static SimpleFeature createGeometryFeature(Instance instance, 
			InstanceService instanceService, DataSet dataSet) {
		GeometryProperty<?> geometry = findGeometry(instance);
		
		if (geometry != null) {
			SimpleFeatureBuilder builder = new SimpleFeatureBuilder(getGeometryFeatureType());
			
			//FIXME add CRS as user data to geometry (this is not good as it changes the original object)
			geometry.getGeometry().setUserData(geometry.getCRSDefinition().getCRS());
			
			builder.add(geometry.getGeometry());
			builder.add(instanceService.getReference(instance, dataSet));
			
			return builder.buildFeature(null);
		}
		
		return null;
	}

	private static GeometryProperty<?> findGeometry(Instance instance) {
		//FIXME improve! only very simple implementation for testing purposes (with shape files)
		//XXX should be based on TypeDefinition and the path should also be cached per TypeDefinition
		
		for (QName name : instance.getPropertyNames()) {
			Object[] values = instance.getProperty(name);
			if (values != null && values.length > 0 && values[0] instanceof GeometryProperty<?>) {
				return (GeometryProperty<?>) values[0];
			}
		}
		
		return null;
	}

	/**
	 * Get the feature type containing only a geometry and an {@link InstanceReference}
	 * @return the feature type
	 */
	public static SimpleFeatureType getGeometryFeatureType() {
		if (geometryFeatureType == null) {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

			// geometry
			Name geometryName = new NameImpl(null, GEOMETRY_PROPERTY); // must be null namespace because otherwise the StreamingRenderer and some other Geotools components choke on it
			AttributeType type = GMLSchema.ABSTRACTGEOMETRYTYPE_TYPE;
			AttributeDescriptor desc =  new AttributeDescriptorImpl(
					type,
					geometryName, 
					1,
					1,
					true, // always nillable, else creating the features fails
					null);
			builder.add(desc);
			builder.setDefaultGeometry(geometryName.getLocalPart());
			
			// reference
			Name referenceName = new NameImpl(null, REFERENCE_PROPERTY);
			desc =  new AttributeDescriptorImpl(
					REFERENCE_TYPE,
					referenceName, 
					1,
					1,
					true, // always nillable, else creating the features fails
					null);
			builder.add(desc);
			
			// other properties
			builder.setAbstract(false);
			builder.setName(new NameImpl("http://www.esdi-humboldt.eu/hale", "GeometryFeature"));
			
			geometryFeatureType = builder.buildFeatureType();
		}
		
		return geometryFeatureType;
	}
	
	/**
	 * Get the geometry features for the given data set
	 * @param dataSet the data set
	 * @return the feature collection
	 */
	public static FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures(DataSet dataSet) {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		
		InstanceCollection ic = is.getInstances(dataSet);
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = new MemoryFeatureCollection(getGeometryFeatureType());
		
		if (!ic.isEmpty()) {
			ResourceIterator<Instance> it = ic.iterator();
			try {
				while (it.hasNext()) {
					SimpleFeature feature = createGeometryFeature(it.next(), is, dataSet);
					if (feature != null) {
						features.add(feature);
					}
				}
			} finally {
				it.close();
			}
		}
		
		return features;
	}

	/**
	 * Determine the CRS from a set of geometry features
	 * @param features the features
	 * @return the coordinate reference system or <code>null</code>
	 */
	public static CoordinateReferenceSystem determineCRS(
			FeatureCollection<SimpleFeatureType, SimpleFeature> features) {
		Iterator<SimpleFeature> it = features.iterator();
		while (it.hasNext()) {
			Object userData = ((Geometry) it.next().getProperty(GEOMETRY_PROPERTY).getValue()).getUserData();
			if (userData instanceof CoordinateReferenceSystem) {
				return (CoordinateReferenceSystem) userData;
			}
		}
		
		return null;
	}

}
