/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.io.shp.reader.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionUtil;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.shp.ShapefileIO;
import eu.esdihumboldt.hale.io.shp.internal.Messages;

/**
 * Reads instances from a shapefile.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class ShapeInstanceReader extends AbstractInstanceReader {

	private DefaultInstanceCollection instances;

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin(Messages.getString("ShapeSchemaProvider.1"), ProgressIndicator.UNKNOWN); //$NON-NLS-1$
		
//		DataStore store = new ShapefileDataStoreFactory().createDataStore(location.toURL());
		DataStore store = FileDataStoreFinder.getDataStore(getSource().getLocation().toURL());
		
		progress.setCurrentTask("Extracting shape instances");

		instances = new DefaultInstanceCollection(new ArrayList<Instance>());
		
		// build instances based on Features
		for (Name name : store.getNames()) {
			QName typeName = new QName(ShapefileIO.SHAPEFILE_NS, name.getLocalPart());
			TypeDefinition type = getSourceSchema().getType(typeName);
			
			if (type != null) {
				Iterator<SimpleFeature> it = store.getFeatureSource(name).getFeatures().iterator();
				while (it.hasNext()) {
					SimpleFeature feature = it.next();
					
					Instance instance = createInstance(type, feature);
					if (instance != null) {
						instances.add(instance);
					}
					else {
						reporter.error(new IOMessageImpl("Could not create a data instance from a feature of type " + 
								typeName, null));
					}
				}
			}
			else {
				reporter.error(new IOMessageImpl("Could not find type " + 
						typeName + " in source schema, corresponding instances were not created.", null));
			}
		}

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Create an instance from a given feature
	 * 
	 * @param type the type definition associated to the feature/instance
	 * @param feature the feature
	 * @return the instance or <code>null</code> if it couldn't be created
	 */
	private Instance createInstance(TypeDefinition type, SimpleFeature feature) {
		MutableInstance instance = new DefaultInstance(type, null);
		
		for (Property property : feature.getProperties()) {
			Object value = property.getValue();
			QName propertyName = new QName(property.getName().getNamespaceURI(), 
					property.getName().getLocalPart());
			
			// wrap geometry
			if (value instanceof Geometry) {
				// try to determine CRS
				CoordinateReferenceSystem crs = null;
				
				// try user data of geometry
				Object userData = ((Geometry) value).getUserData();
				if (userData instanceof CoordinateReferenceSystem) {
					crs = (CoordinateReferenceSystem) userData;
				}
				
				if (crs == null) {
					// try CRS associated to geometry descriptor
					AttributeDescriptor pd = feature.getFeatureType().getDescriptor(property.getName());
					if (pd != null && pd instanceof GeometryDescriptor) {
						crs = ((GeometryDescriptor) pd).getCoordinateReferenceSystem();
					}
				}
				
				if (crs == null) {
					// try CRS associated to feature type
					crs = feature.getFeatureType().getCoordinateReferenceSystem();
				}
				
				CRSDefinition crsDef;
				if (crs != null) {
					crsDef = CRSDefinitionUtil.createDefinition(crs);
				}
				else {
					// fallback to provider configuration
					ChildDefinition<?> child = type.getChild(propertyName);
					if (child != null && child.asProperty() != null) {
						crsDef = getDefaultCRS(child.asProperty());
					}
					else {
						crsDef = null;
					}
				}
				value = new DefaultGeometryProperty<Geometry>(crsDef, (Geometry) value);
			}
			
			//TODO safe add? in respect to binding, existence of property
			instance.addProperty(propertyName, value);
		}
		
		return instance;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return ShapefileIO.DEFAULT_TYPE_NAME;
	}

	/**
	 * @see InstanceReader#getInstances()
	 */
	@Override
	public InstanceCollection getInstances() {
		return instances;
	}

}
