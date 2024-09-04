/*
 * Copyright (c) 2021 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.shp.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.springframework.core.convert.ConversionService;

import com.google.common.collect.ImmutableMap;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.MultiLocationOutputSupplier;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceAccessor;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractGeoInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.io.shp.ShapefileConstants;

/**
 * Class to write features into Shapefiles.
 * 
 * @author Kapil Agnihotri
 */
public class ShapefileInstanceWriter extends AbstractGeoInstanceWriter {

	/**
	 * The identifier of the writer as registered to the I/O provider extension.
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.shp.instance.writer";

	/**
	 * Map for different bindings to use for feature type fields for encountered
	 * types.
	 */
	public static final Map<Class<?>, Class<?>> VALID_BINDING_MAP = ImmutableMap
			.<Class<?>, Class<?>> builder() //
			// Java 8 date + time
			//
			// Please note that regardless if there is a time component, Geotools will by
			// default only use the date part when writing the DBF file, see
			// https://github.com/geotools/geotools/blob/f802eb83131e1f7f346007791ce3a8bdde165ede/modules/plugin/shapefile/src/main/java/org/geotools/data/shapefile/ShapefileDataStore.java#L379
			.put(LocalDate.class, Date.class) //
			.put(LocalDateTime.class, Date.class) //
			.put(Instant.class, Date.class) //
			.build(); //

	/**
	 * Regular expression to split the camelCase, the snake_case, or the
	 * alphanumeric string.
	 */
	private final String REGEX = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_|((?<=[a-zA-Z])(?=[0-9]))";

	@Override
	public boolean isPassthrough() {
		return false;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generating Shapefile", ProgressIndicator.UNKNOWN);
		InstanceCollection instances = getInstances();
		List<String> filesWritten;
		try {
			URI location = getTarget().getLocation();
			String filePath = Paths.get(location).getParent().toString();

			filesWritten = writeInstances(instances, progress, reporter, location);

			if (filesWritten.size() > 1) {
				List<URI> uris = filesWritten.stream().map(f -> {
					File file = new File(filePath + "/" + f + ShapefileConstants.SHP_EXTENSION);
					return file.toURI();
				}).collect(Collectors.toList());

				// Reset the target property so that a caller can find out
				// which
				// files were created.
				setTarget(new MultiLocationOutputSupplier(uris));
			}

			for (String f : filesWritten) {
				String cpgFileName = filePath + "/" + f + ShapefileConstants.CPG_EXTENSION;
				writeCodePageFile(cpgFileName);
			}
			if (filesWritten.isEmpty()) {
				reporter.warn("No file has been exported because there was no geometry found.");
			}
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getMessage(), e));
			reporter.setSuccess(false);
			reporter.setSummary("Saving instances to Shapefile failed.");
		} finally {
			progress.end();
		}

		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return null;
	}

	/**
	 * Write instances to the Shapefiles. It is a 4 step process. <br>
	 * 1. create simpleFeatureType <br>
	 * 2. create Shapefile schema from the feature collection. <br>
	 * 3. create features <br>
	 * 4. write the feature data to the Shapefile.
	 * 
	 * @param instanceCollection instance to write to.
	 * @param progress the progress indicator.
	 * @param reporter the reporter.
	 * @param location file path URI.
	 * 
	 * @return List of file names that were written (without suffixes)
	 * 
	 * @throws IOException exception in any.
	 * 
	 */
	protected List<String> writeInstances(InstanceCollection instanceCollection,
			ProgressIndicator progress, IOReporter reporter, URI location) throws IOException {

		// in all the variables, outer Map is for tracking multiple schemas and
		// inner Map for multiple geometries.
		Map<String, Map<String, SimpleFeatureType>> schemaFtMap = createFeatureType(
				instanceCollection, progress, reporter);

		Map<String, Map<String, ShapefileDataStore>> schemaDataStoreMap = createSchema(location,
				schemaFtMap);

		Map<String, Map<String, List<SimpleFeature>>> schemaFeaturesMap = createFeatures(
				instanceCollection, progress, reporter, schemaFtMap);

		return writeToFile(schemaDataStoreMap, schemaFtMap, schemaFeaturesMap);
	}

	/**
	 * Step 1. Method to create feature type for the shape file. This is the
	 * first step, which creates the schema for the shape file.
	 * 
	 * shape file restrictions: <br>
	 * a single geometry column named the_geom <br>
	 * - "the_geom" is always first, and used for geometry attribute name <br>
	 * - "the_geom" must be of type Point, MultiPoint, MuiltiLineString,
	 * MultiPolygon<br>
	 * - Attribute names are limited in length<br>
	 * - Not all data types are supported (example Timestamp represented as
	 * Date)<br>
	 * 
	 * @param instances the instance to write.
	 * @param progress the progress indicator.
	 * @param reporter the reporter.
	 * @return map of SimpleFeatureType type used as a template to describe the
	 *         file contents.
	 */
	private Map<String, Map<String, SimpleFeatureType>> createFeatureType(
			InstanceCollection instances, ProgressIndicator progress, IOReporter reporter) {
		// 1. create simpleFeatureType
		Map<String, Map<String, SimpleFeatureType>> schemaSftMap = new HashMap<String, Map<String, SimpleFeatureType>>();

		Map<String, Map<String, SimpleFeatureTypeBuilder>> schemaBuilderMap = new HashMap<String, Map<String, SimpleFeatureTypeBuilder>>();

		LinkedHashSet<String> missingGeomsForSchemas = new LinkedHashSet<String>();
		try (ResourceIterator<Instance> it = instances.iterator()) {
			while (it.hasNext() && !progress.isCanceled()) {
				Instance instance = it.next();
				TypeDefinition type = instance.getDefinition();

				String localPart = type.getName().getLocalPart();
				Map<String, SimpleFeatureTypeBuilder> geometryBuilderMap = schemaBuilderMap
						.computeIfAbsent(localPart,
								k -> new HashMap<String, SimpleFeatureTypeBuilder>());

				writeGeometrySchema(instance, localPart, geometryBuilderMap,
						missingGeomsForSchemas);
				// add rest of the properties to the
				// SimpleFeatureTypeBuilder.
				writePropertiesSchema(instance, type, geometryBuilderMap);
				schemaBuilderMap.put(localPart, geometryBuilderMap);
				// else nothing to do as the schema definition is already
				// present.
			}
		}

		// create SimpleFeatureType from SimpleFeatureTypeBuilder.
		for (Entry<String, Map<String, SimpleFeatureTypeBuilder>> schemaEntry : schemaBuilderMap
				.entrySet()) {

			if (missingGeomsForSchemas.contains(schemaEntry.getKey())) {
				reporter.warn("No geometry found for " + schemaEntry.getKey());
			}

			for (Entry<String, SimpleFeatureTypeBuilder> geometryEntry : schemaEntry.getValue()
					.entrySet()) {
				SimpleFeatureType buildFeatureType = geometryEntry.getValue().buildFeatureType();
				schemaSftMap
						.computeIfAbsent(schemaEntry.getKey(),
								k -> new HashMap<String, SimpleFeatureType>())
						.put(geometryEntry.getKey(), buildFeatureType);
			}
		}
		return schemaSftMap;
	}

	/**
	 * Method to write Geometry definition to the shape file schema.
	 * 
	 * @param instance instance.
	 * @param localPart local part of <code>QName</code> which tracks multiple
	 *            schemas.
	 * @param geometryBuilderMap SimpleFeatureType to build schema definition
	 *            for the shape file.
	 * @param missingGeomsForSchemas track all the schemas with missing
	 *            geometry, so that later they can be logged in the reporter and
	 *            prevent throwing exception in case of more than one schema to
	 *            export.
	 */
	private void writeGeometrySchema(Instance instance, String localPart,
			Map<String, SimpleFeatureTypeBuilder> geometryBuilderMap,
			LinkedHashSet<String> missingGeomsForSchemas) {
		Geometry geom = null;

		List<GeometryProperty<?>> geoms = traverseInstanceForGeometries(instance);

		// add geometries to the shape SimpleFeatureTypeBuilder.
		if (geoms.size() > 1) {
			for (GeometryProperty<?> geoProp : geoms) {
				geom = geoProp.getGeometry();
				createSimpleFeatureTypeBuilderWithGeometry(localPart, geometryBuilderMap, geom,
						geoProp);
			}
		}
		else if (!geoms.isEmpty()) {
			geom = geoms.get(0).getGeometry();
			createSimpleFeatureTypeBuilderWithGeometry(localPart, geometryBuilderMap, geom,
					geoms.get(0));
		}
		else {
			missingGeomsForSchemas.add(localPart);
		}
	}

	/**
	 * Method to write schema definition for all the properties.
	 * 
	 * @param instance instance to write to.
	 * @param type type definition.
	 * @param geometryBuilderMap SimpleFeatureType to build schema definition
	 *            for the shape file.
	 */
	private void writePropertiesSchema(Instance instance, TypeDefinition type,
			Map<String, SimpleFeatureTypeBuilder> geometryBuilderMap) {
		Collection<? extends PropertyDefinition> allNonComplexProperties = getNonComplexProperties(
				type);
		for (PropertyDefinition prop : allNonComplexProperties) {
			Class<?> binding = prop.getPropertyType().getConstraint(Binding.class).getBinding();
			binding = toValidBinding(binding);

			// ignore geometry and filename properties.
			if (!prop.getPropertyType().getConstraint(GeometryType.class).isGeometry()
					&& !prop.getName().getNamespaceURI()
							.equalsIgnoreCase(ShapefileConstants.SHAPEFILE_AUGMENT_NS)) {
				// intentionally removing instance.getProperty(prop.getName())
				// != null, otherwise some properties that are null here are
				// read in step 3. This change will export all the attributes.
				Set<String> keySet = geometryBuilderMap.keySet();
				for (String key : keySet) {
					String propName = truncatePropertyName(prop.getName().getLocalPart());
					if (geometryBuilderMap.get(key).get(propName) == null) {
						geometryBuilderMap.get(key).add(propName, binding);
					}
				}
			}
		}
	}

	/**
	 * Not all kind of bindings that might be used are supported to be written. Map
	 * to a valid binding where possible / known.
	 *
	 * @param binding the original binding
	 * @return the binding to use for the original binding, or the original binding
	 *         if that is supported or no mapping known
	 */
	private Class<?> toValidBinding(Class<?> binding) {
		/*
		 * Supported binding can be found here:
		 *
		 * spotless:off
		 * https://github.com/geotools/geotools/blob/f802eb83131e1f7f346007791ce3a8bdde165ede/modules/plugin/shapefile/src/main/java/org/geotools/data/shapefile/ShapefileDataStore.java#L344
		 * spotless:on
		 */
		if (binding == null) {
			return null;
		}

		Class<?> mappedBinding = VALID_BINDING_MAP.get(binding);

		if (mappedBinding != null) {
			return mappedBinding;
		}
		return binding;
	}

	/**
	 * Method to truncate the property names up to 10 characters by splitting
	 * them from camelCase, snake_case, or alphanumeric characters. E.g. <br/>
	 * camelCase: caCa<br/>
	 * snake_case: snca <br/>
	 * alpha1234; al12 <br/>
	 * snake_camelCase: sncaCa <br/>
	 * snake_camelCase1234: sncaCa12 <br/>
	 * population: population
	 * 
	 * As the Shapefile DB doesn't allow more than 10 characters, the change is
	 * required to avoid exporting null values to the columns whose name is
	 * greater than 10 characters. This method intentionally truncates to 8
	 * characters to leave a room for the unexpected scenario(s) where the
	 * truncated names might clash, then the values will be appended with the
	 * integers by the library and the null values will be exported (should be a
	 * rare scenario).
	 * 
	 * @param propName property name to be truncated.
	 * @return unchanged property name if <= 10 chars long or truncated property
	 *         name with max 9 characters.
	 */
	private String truncatePropertyName(String propName) {
		if (propName != null && propName.length() > 10) {
			String[] split = propName.split(REGEX);
			if (split.length > 1) {
				StringBuilder propNameFormatted = new StringBuilder();

				Arrays.stream(split).forEach(s -> propNameFormatted
						.append(s.substring(0, s.length() > 2 ? 2 : s.length())));
				propName = propNameFormatted.toString();
				int stringLen = propName.length() > 8 ? 8 : propName.length();
				propName = propName.substring(0, stringLen);
			}
			else {
				// as it is greater than 10 but there is nothing to split. So
				// instead of truncating it with 2 characters, truncate it with
				// 8 characters.
				propName = propName.substring(0, 8);
			}

		}
		return propName;
	}

	/**
	 * Method to traverse instance to find geometries.
	 * 
	 * @param instance instance.
	 * @return list of geometries.
	 */
	private List<GeometryProperty<?>> traverseInstanceForGeometries(Instance instance) {
		// find geometries in the schema.
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		GeometryFinder geoFind = new GeometryFinder(null);
		traverser.traverse(instance, geoFind);

		List<GeometryProperty<?>> geoms = geoFind.getGeometries();
		return geoms;
	}

	/**
	 * Method to retrieve all the properties.
	 * 
	 * @param type type definition.
	 * @return Collection of all the properties.
	 */
	private Collection<? extends PropertyDefinition> getNonComplexProperties(TypeDefinition type) {
		Collection<? extends PropertyDefinition> allNonComplexProperties = DefinitionUtil
				.getAllProperties(type).stream().filter(p -> {
					// filter out complex properties w/o HasValue or
					// AugmentedValue
					return p.getPropertyType().getConstraint(HasValueFlag.class).isEnabled() || p
							.getPropertyType().getConstraint(AugmentedValueFlag.class).isEnabled();
				}).collect(Collectors.toList());
		return allNonComplexProperties;
	}

	/**
	 * Convenience method to create SimpleFeatureTypeBuilder with geometry and
	 * target CRS information.
	 * 
	 * @param localPart local part of <code>QName</code> which tracks multiple
	 *            schemas.
	 * @param geometryBuilderMap simpleFeatureTypeBuilder which adds schema info
	 *            for the shape file.
	 * @param geom geometry.
	 * @param geoProp GeometryProperty.
	 */
	private void createSimpleFeatureTypeBuilderWithGeometry(String localPart,
			Map<String, SimpleFeatureTypeBuilder> geometryBuilderMap, Geometry geom,
			GeometryProperty<?> geoProp) {
		SimpleFeatureTypeBuilder sftBuilder = new SimpleFeatureTypeBuilder();
		sftBuilder.setName(localPart);
		sftBuilder.setNamespaceURI(ShapefileConstants.SHAPEFILE_NS);

		CRSDefinition targetCrs = getTargetCRS();
		if (targetCrs != null) {
			sftBuilder.setCRS(targetCrs.getCRS());
		}
		else {
			sftBuilder.setCRS(geoProp.getCRSDefinition().getCRS());
		}
		sftBuilder.add(ShapefileConstants.THE_GEOM, geom.getClass());
		geometryBuilderMap.put(geom.getGeometryType(), sftBuilder);
	}

	/**
	 * Step 2. method to create schema. This method will create filename as:<br>
	 * - filename_schemaName_geometryType.shp if multiple schema and geom.<br>
	 * - filename_schemaName.shp if multiple schemas.<br>
	 * - filename_geometryType.shp if multiple geometries.<br>
	 * - filename.shp single schema and geom.
	 * 
	 * @param location location to store the shape files.
	 * 
	 * @param schemaSftMap type is used as a template to describe the file
	 *            contents.
	 * @return shape file data store.
	 * @throws IOException exception if any.
	 */
	private Map<String, Map<String, ShapefileDataStore>> createSchema(URI location,
			Map<String, Map<String, SimpleFeatureType>> schemaSftMap) throws IOException {

		Map<String, Map<String, ShapefileDataStore>> schemaDataStoreMap = new HashMap<String, Map<String, ShapefileDataStore>>();

		// logic to create file name based on the multiple schemas and/or
		// multiple geometries.
		int numberOfSchemas = schemaSftMap.keySet().size();
		for (Entry<String, Map<String, SimpleFeatureType>> schemaEntry : schemaSftMap.entrySet()) {
			int numberOfGeometries = schemaEntry.getValue().keySet().size();
			for (Entry<String, SimpleFeatureType> geometryEntry : schemaEntry.getValue()
					.entrySet()) {

				Map<String, Serializable> params = new HashMap<String, Serializable>();
				File file = createFileWithFormattedName(location, numberOfSchemas, schemaEntry,
						numberOfGeometries, geometryEntry);
				params.put(ShapefileConstants.URL_STRING, file.toURI().toURL());
				// create schema.
				ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
				ShapefileDataStore newDataStore;

				newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
				newDataStore.setCharset(getCharset());
				newDataStore.createSchema(geometryEntry.getValue());
				schemaDataStoreMap
						.computeIfAbsent(schemaEntry.getKey(),
								k -> new HashMap<String, ShapefileDataStore>())
						.put(geometryEntry.getKey(), newDataStore);
			}
		}
		return schemaDataStoreMap;
	}

	/**
	 * Method to create file name based on the number of schema and geom:<br>
	 * - filename_schemaName_geometryType.shp if multiple schema and geom.<br>
	 * - filename_schemaName.shp if multiple schemas.<br>
	 * - filename_geometryType.shp if multiple geometries.<br>
	 * - filename.shp single schema and geom.
	 * 
	 * @param location file location.
	 * @param numberOfSchemas number of schemas.
	 * @param schemaEntry current schema in process.
	 * @param numberOfGeometries number of geometries in the schema.
	 * @param geometryEntry current geometry entry in process.
	 * @return file with the formatted file name.
	 */
	private File createFileWithFormattedName(URI location, int numberOfSchemas,
			Entry<String, Map<String, SimpleFeatureType>> schemaEntry, int numberOfGeometries,
			Entry<String, SimpleFeatureType> geometryEntry) {
		String filenameWithType = location.getPath();
		String filePath = Paths.get(location).getParent().toString();
		String baseFilename = Paths.get(location).getFileName().toString();
		baseFilename = baseFilename.substring(0, baseFilename.lastIndexOf("."));
		if (numberOfSchemas > 1) {
			if (numberOfGeometries > 1) {
				filenameWithType = filePath + FileSystems.getDefault().getSeparator() + baseFilename
						+ ShapefileConstants.UNDERSCORE + schemaEntry.getKey()
						+ ShapefileConstants.UNDERSCORE + geometryEntry.getKey()
						+ ShapefileConstants.SHP_EXTENSION;
			}
			else {
				filenameWithType = filePath + FileSystems.getDefault().getSeparator() + baseFilename
						+ ShapefileConstants.UNDERSCORE + schemaEntry.getKey()
						+ ShapefileConstants.SHP_EXTENSION;
			}
		}
		else if (numberOfGeometries > 1) {
			filenameWithType = filePath + FileSystems.getDefault().getSeparator() + baseFilename
					+ ShapefileConstants.UNDERSCORE + geometryEntry.getKey()
					+ ShapefileConstants.SHP_EXTENSION;
		}
		File file;
		try {
			file = new File(filenameWithType);
		} catch (Exception e) {
			throw new IllegalArgumentException("Only files are supported as data source", e);
		}
		if (file.exists() && file.length() == 0L) {
			// convenience for overwriting to empty existing file.
			file.delete();
		}
		return file;
	}

	/**
	 * * Step 3. method to create features for the shape files and write them as
	 * per the schema definition.<br>
	 * Always the first entry should be "the_geom" then rest of the properties
	 * can be written.
	 * 
	 * @param instances instance to write.
	 * @param progress the progress indicator.
	 * @param reporter the reporter.
	 * @param schemaFtMap type is used as a template to describe the file
	 *            contents.
	 * @return list all the features to be added to the file bundled in map for
	 *         multiple schemas and multiple geometries.
	 */
	private Map<String, Map<String, List<SimpleFeature>>> createFeatures(
			InstanceCollection instances, ProgressIndicator progress, IOReporter reporter,
			Map<String, Map<String, SimpleFeatureType>> schemaFtMap) {
		// 3. create features

		Map<String, Map<String, List<SimpleFeature>>> schemaFeaturesMap = new HashMap<String, Map<String, List<SimpleFeature>>>();
		Map<String, Map<String, SimpleFeatureBuilder>> schemaFbMap = new HashMap<String, Map<String, SimpleFeatureBuilder>>();

		// initialize simple feature type builder for all the schemas and
		// geometries.
		for (Entry<String, Map<String, SimpleFeatureType>> schemaEntry : schemaFtMap.entrySet()) {
			for (Entry<String, SimpleFeatureType> geomEntry : schemaEntry.getValue().entrySet()) {
				schemaFbMap
						.computeIfAbsent(schemaEntry.getKey(),
								k -> new HashMap<String, SimpleFeatureBuilder>())
						.computeIfAbsent(geomEntry.getKey(),
								k1 -> new SimpleFeatureBuilder(geomEntry.getValue()));
			}
		}

		// write features to shape file schema.
		try (ResourceIterator<Instance> it = instances.iterator()) {
			while (it.hasNext() && !progress.isCanceled()) {

				Instance instance = it.next();
				TypeDefinition type = instance.getDefinition();
				String localPart = type.getName().getLocalPart();
				if (schemaFtMap.containsKey(localPart)) {
					writeGeometryInstanceData(reporter, schemaFbMap, instance, localPart);
					// add data for the rest of the properties.
					writePropertiesInstanceData(schemaFbMap, instance, type, localPart, reporter);

					// create list of simple features.
					// fix in case geometries have multiple geometry types but
					// single geometry in data. So, always extract geometries
					// from instance and update to schema. Otherwise the data
					// will be updated to all the geometries
					List<GeometryProperty<?>> geoms = traverseInstanceForGeometries(instance);
					for (GeometryProperty<?> geoProp : geoms) {
						String key = geoProp.getGeometry().getGeometryType();
						SimpleFeature feature = schemaFbMap.get(localPart).get(key)
								.buildFeature(null);
						schemaFeaturesMap
								.computeIfAbsent(localPart,
										k -> new HashMap<String, List<SimpleFeature>>())
								.computeIfAbsent(key, k1 -> new ArrayList<SimpleFeature>())
								.add(feature);
					}
				}
			}
		}
		return schemaFeaturesMap;
	}

	/**
	 * Method to write the geometry in the shape file schema.
	 * 
	 * @param reporter reporter.
	 * @param schemaFbMap map of feature builder to write the data to.
	 * @param instance instance
	 * @param localPart local part of <code>QName</code> which tracks multiple
	 *            schemas.
	 */
	private void writeGeometryInstanceData(IOReporter reporter,
			Map<String, Map<String, SimpleFeatureBuilder>> schemaFbMap, Instance instance,
			String localPart) {
		List<GeometryProperty<?>> geoms = traverseInstanceForGeometries(instance);

		for (GeometryProperty<?> geoProp : geoms) {
			addGeometryData(reporter, schemaFbMap, localPart, geoProp);
		}
	}

	/**
	 * Method to write all the property data in the shape file schema.
	 * 
	 * @param schemaFbMap map of feature builder to write the data to.
	 * @param instance instance.
	 * @param type type definition.
	 * @param localPart local part of <code>QName</code> which tracks multiple
	 *            schemas.
	 */
	private void writePropertiesInstanceData(
			Map<String, Map<String, SimpleFeatureBuilder>> schemaFbMap, Instance instance,
			TypeDefinition type, String localPart, SimpleLog log) {
		Collection<? extends PropertyDefinition> allNonComplexProperties = getNonComplexProperties(
				type);
		for (PropertyDefinition prop : allNonComplexProperties) {
			if (!prop.getPropertyType().getConstraint(GeometryType.class).isGeometry()
					&& !prop.getName().getNamespaceURI()
							.equalsIgnoreCase(ShapefileConstants.SHAPEFILE_AUGMENT_NS)
					&& prop.getName().getLocalPart() != null) {
				Object value = new InstanceAccessor(instance)
						.findChildren(prop.getName().getLocalPart()).value();

				if (value != null) {
					Class<?> binding = toValidBinding(
							prop.getPropertyType().getConstraint(Binding.class).getBinding());
					if (!binding.isAssignableFrom(value.getClass())) {
						// convert value
						ConversionService cs = HalePlatform.getService(ConversionService.class);
						if (cs != null) {
							try {
								value = cs.convert(value, binding);
							} catch (Exception e) {
								log.error("Could not convert value to binding {0}", binding, e);
							}
						}
						else {
							log.error(
									"Could not access conversion service to convert to binding {0}",
									binding);
						}
					}
				}

				List<GeometryProperty<?>> geoms = traverseInstanceForGeometries(instance);
				// add value by traversing geometryType from instance
				for (GeometryProperty<?> geoProp : geoms) {
					if (geoProp.getGeometry() != null) {
						String geometryType = geoProp.getGeometry().getGeometryType();
						if (schemaFbMap.get(localPart) != null
								&& schemaFbMap.get(localPart).get(geometryType) != null) {
							schemaFbMap.get(localPart).get(geometryType).add(value);
						}
					}

				}
			}
		}
	}

	/**
	 * Convenience method to convert geometry to the target CRS and add to the
	 * feature builder.
	 * 
	 * @param reporter the reporter
	 * @param schemaFbMap featureBuilder to add all the data in the shape file
	 *            schema.
	 * @param localPart local part of <code>QName</code> which tracks multiple
	 *            schemas.
	 * @param geometryProperty geometry property to extract the CRS definition
	 *            and the geometry.
	 */
	private void addGeometryData(IOReporter reporter,
			Map<String, Map<String, SimpleFeatureBuilder>> schemaFbMap, String localPart,
			GeometryProperty<?> geometryProperty) {
		Geometry geom = geometryProperty.getGeometry();
		if (getTargetCRS() != null) {
			geom = convertGeometry(geometryProperty.getGeometry(),
					geometryProperty.getCRSDefinition(), reporter).getFirst();
		}
		schemaFbMap.get(localPart).get(geom.getGeometryType()).add(geom);

	}

	/**
	 * Final step to write to the shape file using transaction.
	 * 
	 * @param schemaDataStoreMap data store for the shape file.
	 * @param schemaFtMap used as a template to describe the file contents.
	 * @param schemaFeaturesMap for each schema, each geom list of features to
	 *            be written to the shape file.
	 * @return List of file names that were written (without suffixes)
	 * @throws IOException if any.
	 */
	private List<String> writeToFile(
			Map<String, Map<String, ShapefileDataStore>> schemaDataStoreMap,
			Map<String, Map<String, SimpleFeatureType>> schemaFtMap,
			Map<String, Map<String, List<SimpleFeature>>> schemaFeaturesMap) throws IOException {

		List<String> filesWritten = new ArrayList<String>();

		// extract each schema
		for (Entry<String, Map<String, ShapefileDataStore>> schemaEntry : schemaDataStoreMap
				.entrySet()) {
			String localPart = schemaEntry.getKey();
			// extract each geometry.
			for (Entry<String, ShapefileDataStore> geomEntry : schemaEntry.getValue().entrySet()) {
				Transaction transaction = new DefaultTransaction(
						ShapefileConstants.CREATE_CONSTANT);

				ShapefileDataStore dataStore = geomEntry.getValue();
				String typeName = dataStore.getTypeNames()[0];
				for (Name name : dataStore.getNames()) {
					// The local part of the Name contains the file name of the
					// ShapefileDataStore (without suffix)
					filesWritten.add(name.getLocalPart());
				}

				SimpleFeatureSource geomSpecificFeatureSource = geomEntry.getValue()
						.getFeatureSource(typeName);
				if (geomSpecificFeatureSource instanceof SimpleFeatureStore) {
					SimpleFeatureStore geomSpecificFeatureStore = (SimpleFeatureStore) geomSpecificFeatureSource;

					// create collection to write to the shape file.
					SimpleFeatureCollection collection = new ListFeatureCollection(
							schemaFtMap.get(localPart).get(geomEntry.getKey()),
							schemaFeaturesMap.get(localPart).get(geomEntry.getKey()));
					geomSpecificFeatureStore.setTransaction(transaction);
					try {
						geomSpecificFeatureStore.addFeatures(collection);
						transaction.commit();
					} catch (IOException e) {
						transaction.rollback();
						throw e;
					} finally {
						transaction.close();
					}
				}
				else {
					// throw exception
					transaction.close();
					throw new IOException(typeName + " does not support read/write access");
				}
			}
		}

		return filesWritten;
	}

	/**
	 * Create the CPG file starting from the Shapefile
	 * 
	 * @param cpgFilePath Path of the file to be written with just one line of
	 *            the encoding
	 * @throws IOException exception in any.
	 */
	public void writeCodePageFile(String cpgFilePath) throws IOException {
		File cpgFile = new File(cpgFilePath);
		FileWriter fileWriter = new FileWriter(cpgFile);

		try {
			fileWriter.write(getCharset() != null ? getCharset().toString()
					: getDefaultCharset().toString());
		} catch (IOException e) {
			throw new IOException("An error occurred while writing the CPG file: " + cpgFilePath
					+ " " + e.getMessage());
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				throw new IOException("An error occurred while trying to close the CPG file: "
						+ cpgFilePath + " " + e.getMessage());
			}
		}
	}

}
