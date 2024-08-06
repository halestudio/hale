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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.common.collect.ImmutableMap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.helper.GeometryPropertyFinder;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionUtil;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.geometry.CRSResolveCache;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.impl.EPSGResolveCache;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.InstanceResolver;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceCollection2;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.io.shp.ShapefileConstants;

/**
 * Instance collection backed by a Shapefile data store.
 * 
 * @author Simon Templer
 */
public class ShapesInstanceCollection implements InstanceCollection2 {

	private static final ALogger log = ALoggerFactory.getLogger(ShapesInstanceCollection.class);

	/**
	 * Iterates through a shape data store
	 */
	private class ShapesIterator implements InstanceIterator {

		private final SimpleFeatureIterator currentIterator;

		private final Set<QName> missingProperties = new HashSet<QName>();

		private QName geometryPropertyName = null;

		private boolean lookedForGeometryProperty = false;

		/**
		 * Create a new iterator on the data store.
		 * 
		 * @throws IOException if reading the data store fails
		 */
		public ShapesIterator() throws IOException {
			super();

			currentIterator = source.getFeatures().features();
		}

		@Override
		public boolean hasNext() {
			return currentIterator.hasNext();
		}

		@Override
		public Instance next() {
			SimpleFeature feature = currentIterator.next();

			Instance instance = createInstance(type, feature);
			if (instance != null) {
				return instance;
			}
			else {
				log.error("Could not create a data instance from a feature of type "
						+ type.getName());
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
				boolean isGeometry = value instanceof Geometry;

				QName propertyName = new QName(property.getName().getNamespaceURI(),
						property.getName().getLocalPart());

				if (type.getChild(propertyName) == null) {
					// property with the same name does not exist

					final QName lookForPropertyName = propertyName;
					propertyName = null;

					if (isGeometry) {
						// look for geometry property

						if (!lookedForGeometryProperty) {
							geometryPropertyName = findGeometryProperty(type,
									lookForPropertyName.getLocalPart());

							lookedForGeometryProperty = true;
						}

						propertyName = geometryPropertyName;
					}

					List<? extends ChildDefinition<?>> candidates = new ArrayList<>();

					if (propertyName == null && candidates.isEmpty()) {
						// Try to guess the property name in cases where the
						// names in the source file are shortened versions of
						// the target type properties

						candidates = type.getChildren().stream()
								.filter(c -> c.getName().getLocalPart()
										.equals(lookForPropertyName.getLocalPart()))
								.collect(Collectors.toList());
					}

					if (propertyName == null && candidates.isEmpty() && matchShortPropertyNames) {
						// Try to guess the property name in cases where the
						// names in the source file are shortened versions of
						// the target type properties

						candidates = type.getChildren().stream()
								.filter(c -> c.getName().getLocalPart()
										.startsWith(lookForPropertyName.getLocalPart()))
								.collect(Collectors.toList());
					}

					if (candidates.size() == 1) {
						// unique child property found whose name starts with
						// the source property's name
						propertyName = candidates.get(0).getName();
					}

					if (propertyName == null) {
						if (!missingProperties.contains(lookForPropertyName)) {
							log.warn("Discarding values of property "
									+ lookForPropertyName.getLocalPart()
									+ " as it is not contained in the schema type.");
							missingProperties.add(lookForPropertyName);
						}
					}
				}

				boolean addValue = propertyName != null;

				ChildDefinition<?> child = type.getChild(propertyName);
				PropertyDefinition propDef = null;
				if (child != null) {
					propDef = child.asProperty();
				}

				if (value == null) {
					// special handling for null value
					// don't add if property is not nillable

					if (propDef != null) {
						boolean nillable = propDef.getConstraint(NillableFlag.class).isEnabled();
						if (!nillable) {
							addValue = false;
						}
					}
				}

				if (addValue) {
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
							AttributeDescriptor pd = feature.getFeatureType()
									.getDescriptor(property.getName());
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
							crsDef = CRSDefinitionUtil.createDefinition(crs, crsCache);

							if (crs.getIdentifiers().isEmpty()) {
								// Force CRS dialog prompt if the WKT definition
								// does not contain an EPSG code for the CRS.
								// This
								// is to prevent that a CRS definition without
								// Bursa-Wolf parameters is used here silently.
								// In
								// cases of custom CRSs that don't have an EPSG
								// code, the user can still provide the WKT
								// definition in the dialog. In case of a
								// headless
								// transformation, the WKT definition will be
								// used.

								crsDef = crsProvider.getCRS(type,
										Collections.singletonList(propertyName), crsDef);
								// Update the cache with the definition of the
								// CRSProvider
								crsCache.reviseCache(crs, crsDef);
							}
						}
						else {
							// ask CRS provider
							crsDef = crsProvider.getCRS(type,
									Collections.singletonList(propertyName));
						}
						value = new DefaultGeometryProperty<Geometry>(crsDef, (Geometry) value);
					}

					// TODO safe add? in respect to binding, existence of
					// property
					instance.addProperty(propertyName, value);
				}
			}

			// add filename augmented property
			if (fileName != null) {
				QName propertyName = new QName(ShapefileConstants.SHAPEFILE_AUGMENT_NS,
						ShapefileConstants.AUGMENTED_PROPERTY_FILENAME);
				if (type == null || type.getChild(propertyName) != null) {
					instance.addProperty(propertyName, fileName);
				}
			}

			return instance;
		}

		/**
		 * Find the geometry property of the given type.
		 * 
		 * @param type the type to check
		 * @param preferredName the preferred name for the property
		 * @return the identified property or null
		 */
		private QName findGeometryProperty(TypeDefinition type, String preferredName) {
			Function<TypeDefinition, Collection<? extends PropertyDefinition>> finder = (
					TypeDefinition t) -> {
				// FIXME OK to return properties from groups here? will they be
				// picked up correctly?
				return DefinitionUtil.getAllProperties(t);
			};
			return GeometryPropertyFinder.findGeometryProperty(type, finder, preferredName,
					SimpleLog.fromLogger(log));
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() {
			currentIterator.close();
		}

		@Override
		public TypeDefinition typePeek() {
			if (hasNext()) {
				// always the same type
				return type;
			}
			return null;
		}

		@Override
		public boolean supportsTypePeek() {
			return true;
		}

		@Override
		public void skip() {
			currentIterator.next();
		}

	}

	private final CRSProvider crsProvider;
	private final TypeDefinition type;
	private final SimpleFeatureSource source;
	private final String fileName;
	private final boolean matchShortPropertyNames;

	/**
	 * Cache for resolved CRSs
	 */
	protected final CRSResolveCache crsCache = new EPSGResolveCache();

	/**
	 * Data store for accessing simple features (from a Shapefile).
	 * 
	 * @param features the feature source
	 * @param type the type to use for instances
	 * @param crsProvider CRS provider in case no CRS is specified, may be
	 *            <code>null</code>
	 * @param fileName the file name to store in the augmented property
	 * @param matchShortPropertyNames if true try to check if Shapefile property
	 *            names are shortened versions of target type property names and
	 *            match accordingly
	 */
	public ShapesInstanceCollection(SimpleFeatureSource features, TypeDefinition type,
			CRSProvider crsProvider, String fileName, boolean matchShortPropertyNames) {
		this.source = features;
		this.type = type;
		this.crsProvider = crsProvider;
		this.fileName = fileName;
		this.matchShortPropertyNames = matchShortPropertyNames;
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
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	@Override
	public boolean supportsFanout() {
		return true;
	}

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		return ImmutableMap.<TypeDefinition, InstanceCollection> of(type, this);
	}

}
