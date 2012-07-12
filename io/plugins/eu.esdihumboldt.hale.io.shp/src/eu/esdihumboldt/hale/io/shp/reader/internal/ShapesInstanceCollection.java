/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.shp.reader.internal;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionUtil;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.InstanceResolver;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.io.shp.ShapefileIO;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class ShapesInstanceCollection implements InstanceCollection {
	
	private static final ALogger log = ALoggerFactory.getLogger(ShapesInstanceCollection.class);
	
	/**
	 * Iterates through a shape data store
	 */
	private class ShapesIterator implements ResourceIterator<Instance> {
		
		private final Iterator<Name> nameIterator;
		
		private TypeDefinition currentType;
		
		private SimpleFeatureIterator currentIterator;

		/**
		 * Create a new iterator on the data store.
		 * @throws IOException if reading the data store fails 
		 */
		public ShapesIterator() throws IOException {
			super();
			
			nameIterator = store.getNames().iterator();
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			proceedToNext();
			
			if (currentIterator != null && currentIterator.hasNext()) {
				return true;
			}
			return false;
		}

		/**
		 * Proceed to next feature iterator if necessary.
		 */
		private synchronized void proceedToNext() {
			if ((currentIterator == null || !currentIterator.hasNext()) && nameIterator.hasNext()) {
				if (currentIterator != null) {
					currentIterator.close();
				}
				
				Name name = nameIterator.next();
				try {
					currentIterator = store.getFeatureSource(name).getFeatures().features();
					
					QName typeName = new QName(ShapefileIO.SHAPEFILE_NS, name.getLocalPart());
					currentType = typeIndex.getType(typeName);
					
					if (currentType == null) {
						proceedToNext();
						log.error("Could not find type " + 
								typeName + " in source schema, corresponding instances are not created.");
					}
				} catch (IOException e) {
					log.error("Error accessing feature source " + name, e);
				}
			}
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Instance next() {
			proceedToNext();
			
			if (currentType == null) {
				throw new IllegalStateException();
			}
			
			SimpleFeature feature = currentIterator.next();
			
			Instance instance = createInstance(currentType, feature);
			if (instance != null) {
				return instance;
			}
			else {
				log.error("Could not create a data instance from a feature of type " + 
						currentType.getName());
				throw new IllegalStateException();
			}
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
							//TODO ask CRS provider (but settings can't be stored in InstanceReader configuration!)
//							crsDef = getDefaultCRS(child.asProperty());
							crsDef = null;
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
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.ResourceIterator#close()
		 */
		@Override
		public void close() {
			if (currentIterator != null) {
				currentIterator.close();
			}
		}

	}

	private final DataStore store;
	private final TypeIndex typeIndex;

	/**
	 * Data store for accessing simple features (from a Shapefile).
	 * @param store the data store
	 * @param typeIndex the type index
	 */
	public ShapesInstanceCollection(DataStore store, TypeIndex typeIndex) {
		this.store = store;
		this.typeIndex = typeIndex;
	}

	/**
	 * @see InstanceResolver#getReference(eu.esdihumboldt.hale.common.instance.model.Instance)
	 */
	@Override
	public InstanceReference getReference(Instance instance) {
		//TODO data store based instance reference?
		return new PseudoInstanceReference(instance);
	}

	/**
	 * @see InstanceResolver#getInstance(eu.esdihumboldt.hale.common.instance.model.InstanceReference)
	 */
	@Override
	public Instance getInstance(InstanceReference reference) {
		//TODO data store based instance reference?
		if (reference instanceof PseudoInstanceReference) {
			return ((PseudoInstanceReference) reference).getInstance();
		}
		
		return null;
	}

	/**
	 * @see InstanceCollection#iterator()
	 */
	@Override
	public ResourceIterator<Instance> iterator() {
		try {
			return new ShapesIterator();
		} catch (IOException e) {
			throw new IllegalStateException("Could not read shapefile", e);
		}
	}

	/**
	 * @see InstanceCollection#hasSize()
	 */
	@Override
	public boolean hasSize() {
		return false;
	}

	/**
	 * @see InstanceCollection#size()
	 */
	@Override
	public int size() {
		return UNKNOWN_SIZE;
	}

	/**
	 * @see InstanceCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		ResourceIterator<Instance> it = iterator();
		try {
			return !it.hasNext();
		} finally {
			it.close();
		}
	}

	/**
	 * @see InstanceCollection#select(Filter)
	 */
	@Override
	public InstanceCollection select(Filter filter) {
		//TODO allow applying filter on data source level?
		return new FilteredInstanceCollection(this, filter);
	}

}
