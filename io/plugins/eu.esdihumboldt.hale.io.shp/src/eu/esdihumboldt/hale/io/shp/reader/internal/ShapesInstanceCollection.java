/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.shp.reader.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
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
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.io.shp.ShapefileConstants;

/**
 * Instance collection backed by a Shapefile data store.
 * 
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

		private final Set<QName> missingProperties = new HashSet<QName>();

		private String currentName;

		/**
		 * Create a new iterator on the data store.
		 * 
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
					currentName = name.getLocalPart();

					if (defaultType != null) {
						currentType = defaultType;
					}
					else {
						QName typeName = new QName(ShapefileConstants.SHAPEFILE_NS,
								name.getLocalPart());
						currentType = typeIndex.getType(typeName);
					}

					if (currentType == null) {
						proceedToNext();
						log.error("Could not find type " + name.getLocalPart()
								+ " in source schema, corresponding instances are not created.");
					}
				} catch (IOException e) {
					log.error("Error accessing feature source " + name, e);
				}
			}
		}

		/**
		 * @see Iterator#next()
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
				log.error("Could not create a data instance from a feature of type "
						+ currentType.getName());
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
				QName propertyName = new QName(property.getName().getNamespaceURI(), property
						.getName().getLocalPart());

				if (type.getChild(propertyName) == null) {
					if (!missingProperties.contains(propertyName)) {
						log.warn("Discarding values of property " + propertyName.getLocalPart()
								+ " as it is not contained in the schema type.");
						missingProperties.add(propertyName);
					}
					// only add values for properties contained in the type
					continue;
				}

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
						AttributeDescriptor pd = feature.getFeatureType().getDescriptor(
								property.getName());
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
						// ask CRS provider
						crsDef = crsProvider.getCRS(type, Collections.singletonList(propertyName));
					}
					value = new DefaultGeometryProperty<Geometry>(crsDef, (Geometry) value);
				}

				// TODO safe add? in respect to binding, existence of property
				instance.addProperty(propertyName, value);
			}

			// add filename augmented property
			if (currentName != null) {
				instance.addProperty(new QName(ShapefileConstants.SHAPEFILE_AUGMENT_NS,
						ShapefileConstants.AUGMENTED_PROPERTY_FILENAME), currentName);
			}

			return instance;
		}

		/**
		 * @see Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see ResourceIterator#close()
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
	private final CRSProvider crsProvider;
	private final TypeDefinition defaultType;

	/**
	 * Data store for accessing simple features (from a Shapefile).
	 * 
	 * @param store the data store
	 * @param defaultType the default type to use for instances, may be
	 *            <code>null</code>
	 * @param typeIndex the type index
	 * @param crsProvider CRS provider in case no CRS is specified, may be
	 *            <code>null</code>
	 */
	public ShapesInstanceCollection(DataStore store, TypeDefinition defaultType,
			TypeIndex typeIndex, CRSProvider crsProvider) {
		this.store = store;
		this.typeIndex = typeIndex;
		this.crsProvider = crsProvider;
		this.defaultType = defaultType;
	}

	/**
	 * @see InstanceResolver#getReference(Instance)
	 */
	@Override
	public InstanceReference getReference(Instance instance) {
		// TODO data store based instance reference?
		return new PseudoInstanceReference(instance);
	}

	/**
	 * @see InstanceResolver#getInstance(InstanceReference)
	 */
	@Override
	public Instance getInstance(InstanceReference reference) {
		// TODO data store based instance reference?
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
		// TODO allow applying filter on data source level?
		return new FilteredInstanceCollection(this, filter);
	}

}
