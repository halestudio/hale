/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.appschema.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AppSchemaDataAccessType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.NamespacesPropertyType.Namespace;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.ObjectFactory;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore.Parameters.Parameter;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;
import eu.esdihumboldt.hale.io.appschema.model.FeatureChaining;
import eu.esdihumboldt.hale.io.appschema.model.WorkspaceConfiguration;
import eu.esdihumboldt.hale.io.appschema.writer.internal.PropertyTransformationHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.PropertyTransformationHandlerFactory;
import eu.esdihumboldt.hale.io.appschema.writer.internal.TypeTransformationHandler;
import eu.esdihumboldt.hale.io.appschema.writer.internal.TypeTransformationHandlerFactory;
import eu.esdihumboldt.hale.io.appschema.writer.internal.UnsupportedTransformationException;
import eu.esdihumboldt.hale.io.appschema.writer.internal.mapping.AppSchemaMappingContext;
import eu.esdihumboldt.hale.io.appschema.writer.internal.mapping.AppSchemaMappingWrapper;
import eu.esdihumboldt.hale.io.geoserver.AppSchemaDataStore;
import eu.esdihumboldt.hale.io.geoserver.FeatureType;
import eu.esdihumboldt.hale.io.geoserver.Layer;
import eu.esdihumboldt.hale.io.geoserver.ResourceBuilder;
import eu.esdihumboldt.hale.io.geoserver.Workspace;

/**
 * Translates a HALE alignment to an app-schema mapping configuration.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaMappingGenerator {

	private static final ALogger log = ALoggerFactory.getLogger(AppSchemaMappingGenerator.class);

	private static final String NET_OPENGIS_OGC_CONTEXT = "eu.esdihumboldt.hale.io.appschema.impl.internal.generated.net_opengis_ogc";
	private static final String APP_SCHEMA_CONTEXT = "eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema";

	private final Alignment alignment;
	private final SchemaSpace targetSchemaSpace;
	private final Schema targetSchema;
	private final DataStore dataStore;
	private final FeatureChaining chainingConf;
	private final WorkspaceConfiguration workspaceConf;
	private AppSchemaMappingWrapper mappingWrapper;
	private AppSchemaMappingContext context;
	private AppSchemaDataAccessType mainMapping;
	private AppSchemaDataAccessType includedTypesMapping;

	/**
	 * Constructor.
	 * 
	 * @param alignment the alignment to translate
	 * @param targetSchemaSpace the target schema space
	 * @param dataStore the DataStore configuration to use
	 * @param chainingConf the feature chaining configuration
	 */
	public AppSchemaMappingGenerator(Alignment alignment, SchemaSpace targetSchemaSpace,
			DataStore dataStore, FeatureChaining chainingConf, WorkspaceConfiguration workspaceConf) {
		this.alignment = alignment;
		this.targetSchemaSpace = targetSchemaSpace;
		// pick the target schemas from which interpolation variables will be
		// derived
		this.targetSchema = pickTargetSchema();
		this.dataStore = dataStore;
		this.chainingConf = chainingConf;
		this.workspaceConf = workspaceConf;
	}

	/**
	 * Generates the app-schema mapping configuration.
	 * 
	 * @param reporter status reporter
	 * @return the generated app-schema mapping configuration
	 * @throws IOException if an error occurs loading the mapping template file
	 */
	public AppSchemaMappingWrapper generateMapping(IOReporter reporter) throws IOException {
		// reset wrapper
		resetMappingState();

		try {
			AppSchemaDataAccessType mapping = loadMappingTemplate();
			mappingWrapper = new AppSchemaMappingWrapper(mapping);
			context = new AppSchemaMappingContext(mappingWrapper, alignment,
					targetSchema.getMappingRelevantTypes(), chainingConf, workspaceConf);

			// create namespace objects for all target types / properties
			// TODO: this removes all namespaces that were defined in the
			// template file, add code to cope with pre-configured namespaces
			// instead
			mapping.getNamespaces().getNamespace().clear();
			createNamespaces();

			// apply datastore configuration, if any
			// TODO: for now, only a single datastore is supported
			applyDataStoreConfig();

			// populate targetTypes element
			createTargetTypes();

			// populate typeMappings element
			createTypeMappings(context, reporter);

			// cache mainMapping and includedTypesMapping for performance
			mainMapping = mappingWrapper.getMainMapping();
			includedTypesMapping = mappingWrapper.getIncludedTypesMapping();

			return mappingWrapper;
		} catch (Exception e) {
			// making sure state is reset in case an exception is thrown
			resetMappingState();
			throw e;
		}
	}

	private void resetMappingState() {
		mappingWrapper = null;
		mainMapping = null;
		includedTypesMapping = null;
	}

	/**
	 * @return the generated mapping configuration
	 */
	public AppSchemaMappingWrapper getGeneratedMapping() {
		checkMappingGenerated();

		return mappingWrapper;
	}

	/**
	 * Generates the app-schema mapping configuration and writes it to the
	 * provided output stream.
	 * 
	 * <p>
	 * If the mapping configuration requires multiple files, only the main
	 * configuration file will be written.
	 * </p>
	 * 
	 * @param output the output stream to write to
	 * @param reporter the status reporter
	 * @throws IOException if an I/O error occurs
	 */
	public void generateMapping(OutputStream output, IOReporter reporter) throws IOException {
		generateMapping(reporter);

		writeMappingConf(output);
	}

	/**
	 * Generates the app-schema mapping configuration for the included types
	 * (non-feature types or non-top level feature types) and writes it to the
	 * provided output stream.
	 * 
	 * <p>
	 * If the mapping configuration does not require multiple files, an
	 * {@link IllegalStateException} is thrown.
	 * </p>
	 * 
	 * @param output the output stream to write to
	 * @param reporter the status reporter
	 * @throws IOException if an I/O error occurs
	 * @throws IllegalStateException if the mapping configuration does not
	 *             require multiple files
	 */
	public void generateIncludedTypesMapping(OutputStream output, IOReporter reporter)
			throws IOException {
		generateMapping(reporter);

		writeIncludedTypesMappingConf(output);
	}

	/**
	 * Updates a schema URI in the generated mapping configuration.
	 * 
	 * <p>
	 * It is used mainly by exporters that need to change the target schema
	 * location.
	 * </p>
	 * 
	 * @param oldSchemaURI the current schema URI
	 * @param newSchemaURI the updated schema URI
	 */
	public void updateSchemaURI(String oldSchemaURI, String newSchemaURI) {
		checkMappingGenerated();

		mappingWrapper.updateSchemaURI(oldSchemaURI, newSchemaURI);
		// regenerate cached mappings
		mainMapping = mappingWrapper.getMainMapping();
		includedTypesMapping = mappingWrapper.getIncludedTypesMapping();
	}

	/**
	 * Returns the generated app-schema datastore configuration.
	 * 
	 * @return the generated datastore configuration
	 * @throws IllegalStateException if no app-schema mapping configuration has
	 *             been generated yet or if no target schema is available
	 */
	public eu.esdihumboldt.hale.io.geoserver.DataStore getAppSchemaDataStore() {
		checkMappingGenerated();
		checkTargetSchemaAvailable();

		eu.esdihumboldt.hale.io.geoserver.Namespace ns = getMainNamespace();
		Workspace ws = getMainWorkspace();

		String workspaceId = (String) ws.getAttribute(Workspace.ID);
		String dataStoreName = extractSchemaName(targetSchema.getLocation());
		String dataStoreId = dataStoreName + "_datastore";
		String mappingFileName = dataStoreName + ".xml";
		Map<String, String> connectionParameters = new HashMap<String, String>();
		connectionParameters.put("uri",
				(String) ns.getAttribute(eu.esdihumboldt.hale.io.geoserver.Namespace.URI));
		connectionParameters.put("workspaceName", ws.name());
		connectionParameters.put("mappingFileName", mappingFileName);

		return ResourceBuilder
				.dataStore(dataStoreName, AppSchemaDataStore.class)
				.setAttribute(eu.esdihumboldt.hale.io.geoserver.DataStore.ID, dataStoreId)
				.setAttribute(eu.esdihumboldt.hale.io.geoserver.DataStore.WORKSPACE_ID, workspaceId)
				.setAttribute(eu.esdihumboldt.hale.io.geoserver.DataStore.CONNECTION_PARAMS,
						connectionParameters).build();
	}

	/**
	 * Returns the generated workspace configuration for the main workspace.
	 * 
	 * @return the main workspace configuration
	 * @throws IllegalStateException if the no app-schema mapping configuration
	 *             has been generated yet or if no target schema is available
	 */
	public Workspace getMainWorkspace() {
		checkMappingGenerated();
		checkTargetSchemaAvailable();

		Namespace ns = context.getOrCreateNamespace(targetSchema.getNamespace(), null);
		Workspace ws = getWorkspace(ns.getPrefix(), ns.getUri());

		return ws;
	}

	/**
	 * Returns the generated namespace configuration for the main namespace.
	 * 
	 * @return the main namespace configuration
	 * @throws IllegalStateException if no app-schema mapping configuration has
	 *             been generated yet or if no target schema is available
	 */
	public eu.esdihumboldt.hale.io.geoserver.Namespace getMainNamespace() {
		checkMappingGenerated();
		checkTargetSchemaAvailable();

		Namespace ns = context.getOrCreateNamespace(targetSchema.getNamespace(), null);
		return getNamespace(ns);
	}

	/**
	 * Returns the generated namespace configuration for secondary namespaces.
	 * 
	 * @return the secondary namespaces configuration
	 * @throws IllegalStateException if no app-schema mapping configuration has
	 *             been generated yet or if no target schema is available
	 */
	public List<eu.esdihumboldt.hale.io.geoserver.Namespace> getSecondaryNamespaces() {
		checkMappingGenerated();
		checkTargetSchemaAvailable();

		List<eu.esdihumboldt.hale.io.geoserver.Namespace> secondaryNamespaces = new ArrayList<eu.esdihumboldt.hale.io.geoserver.Namespace>();
//		for (Namespace ns : mappingWrapper.getAppSchemaMapping().getNamespaces().getNamespace()) {
		for (Namespace ns : mainMapping.getNamespaces().getNamespace()) {
			if (!ns.getUri().equals(targetSchema.getNamespace())) {
				secondaryNamespaces.add(getNamespace(ns));
			}
		}

		return secondaryNamespaces;
	}

	/**
	 * Returns the configuration of the workspace associated to the provided
	 * namespace.
	 * 
	 * @param ns the namespace
	 * @return the configuration of the workspace associated to <code>ns</code>
	 */
	public Workspace getWorkspace(eu.esdihumboldt.hale.io.geoserver.Namespace ns) {
		Object namespaceUri = ns.getAttribute(eu.esdihumboldt.hale.io.geoserver.Namespace.URI);
		Workspace ws = getWorkspace(ns.name(), String.valueOf(namespaceUri));

		return ws;
	}

	private eu.esdihumboldt.hale.io.geoserver.Namespace getNamespace(Namespace ns) {
		String prefix = ns.getPrefix();
		String uri = ns.getUri();
		String namespaceId = prefix + "_namespace";

		return ResourceBuilder
				.namespace(prefix)
				.setAttribute(eu.esdihumboldt.hale.io.geoserver.Namespace.ID, namespaceId)
				.setAttribute(eu.esdihumboldt.hale.io.geoserver.Namespace.URI, uri)
				.setAttribute(eu.esdihumboldt.hale.io.geoserver.Namespace.ISOLATED, isIsolated(uri))
				.build();
	}

	private Workspace getWorkspace(String nsPrefix, String nsUri) {
		String workspaceId = nsPrefix + "_workspace";
		String workspaceName = nsPrefix;

		return ResourceBuilder.workspace(workspaceName).setAttribute(Workspace.ID, workspaceId)
				.setAttribute(Workspace.ISOLATED, isIsolated(nsUri)).build();
	}

	/**
	 * Returns the generated feature type configuration for all mapped feature
	 * types.
	 * 
	 * @return the generated feature type configuration
	 */
	public List<FeatureType> getFeatureTypes() {
		checkMappingGenerated();

		eu.esdihumboldt.hale.io.geoserver.DataStore dataStore = getAppSchemaDataStore();

		List<FeatureType> featureTypes = new ArrayList<FeatureType>();
//		for (FeatureTypeMapping ftMapping : mappingWrapper.getAppSchemaMapping().getTypeMappings()
//				.getFeatureTypeMapping()) {
		for (FeatureTypeMapping ftMapping : mainMapping.getTypeMappings().getFeatureTypeMapping()) {
			featureTypes.add(getFeatureType(dataStore, ftMapping));
		}

		return featureTypes;
	}

	private FeatureType getFeatureType(eu.esdihumboldt.hale.io.geoserver.DataStore dataStore,
			FeatureTypeMapping ftMapping) {
		String featureTypeName = stripPrefix(ftMapping.getTargetElement());
		String featureTypeId = featureTypeName + "_featureType";
		String dataStoreId = (String) dataStore
				.getAttribute(eu.esdihumboldt.hale.io.geoserver.DataStore.ID);
		eu.esdihumboldt.hale.io.geoserver.Namespace ns = getMainNamespace();

		return ResourceBuilder
				.featureType(featureTypeName)
				.setAttribute(FeatureType.ID, featureTypeId)
				.setAttribute(FeatureType.DATASTORE_ID, dataStoreId)
				.setAttribute(FeatureType.NAMESPACE_ID,
						ns.getAttribute(eu.esdihumboldt.hale.io.geoserver.Namespace.ID)).build();
	}

	/**
	 * Returns the layer configuration for the provided feature type.
	 * 
	 * @param featureType the feature type
	 * @return the layer configuration
	 */
	public Layer getLayer(FeatureType featureType) {
		String featureTypeName = featureType.name();
		String featureTypeId = (String) featureType.getAttribute(FeatureType.ID);
		String layerName = featureTypeName;
		String layerId = layerName + "_layer";

		return ResourceBuilder.layer(layerName).setAttribute(Layer.ID, layerId)
				.setAttribute(Layer.FEATURE_TYPE_ID, featureTypeId).build();
	}

	private void checkMappingGenerated() {
		if (mappingWrapper == null || mainMapping == null
				|| (includedTypesMapping == null && mappingWrapper.requiresMultipleFiles())) {
			throw new IllegalStateException("No mapping has been generated yet");
		}
	}

	private void checkTargetSchemaAvailable() {
		if (targetSchema == null) {
			throw new IllegalStateException("Target schema not available");
		}
	}

	private Schema pickTargetSchema() {
		if (this.targetSchemaSpace == null) {
			return null;
		}

		return this.targetSchemaSpace.getSchemas().iterator().next();
	}

	private String extractSchemaName(URI schemaLocation) {
		String path = schemaLocation.getPath();
		String fragment = schemaLocation.getFragment();
		if (fragment != null && !fragment.isEmpty()) {
			path = path.replace(fragment, "");
		}
		int lastSlashIdx = path.lastIndexOf('/');
		int lastDotIdx = path.lastIndexOf('.');
		if (lastSlashIdx >= 0) {
			if (lastDotIdx >= 0) {
				return path.substring(lastSlashIdx + 1, lastDotIdx);
			}
			else {
				// no dot
				return path.substring(lastSlashIdx + 1);
			}
		}
		else {
			// no slash, no dot
			return path;
		}
	}

	private String stripPrefix(String qualifiedName) {
		if (qualifiedName == null) {
			return null;
		}

		String[] prefixAndName = qualifiedName.split(":");
		if (prefixAndName.length == 2) {
			return prefixAndName[1];
		}
		else {
			return null;
		}
	}

	private void applyDataStoreConfig() {
		if (dataStore != null && dataStore.getParameters() != null) {
			DataStore targetDS = mappingWrapper.getDefaultDataStore();

			List<Parameter> inputParameters = dataStore.getParameters().getParameter();
			List<Parameter> targetParameters = targetDS.getParameters().getParameter();
			// update destination parameters
			for (Parameter inputParam : inputParameters) {
				boolean updated = false;
				for (Parameter targetParam : targetParameters) {
					if (inputParam.getName().equals(targetParam.getName())) {
						targetParam.setValue(inputParam.getValue());
						updated = true;
						break;
					}
				}

				if (!updated) {
					// parameter was not already present: add it to the list
					targetParameters.add(inputParam);
				}
			}
		}
	}

	private void createNamespaces() {
		Collection<? extends Cell> typeCells = alignment.getTypeCells();
		for (Cell typeCell : typeCells) {
			ListMultimap<String, ? extends Entity> targetEntities = typeCell.getTarget();
			if (targetEntities != null) {
				for (Entity entity : targetEntities.values()) {
					createNamespaceForEntity(entity);
				}
			}

			Collection<? extends Cell> propertyCells = alignment.getPropertyCells(typeCell);
			for (Cell propCell : propertyCells) {
				Collection<? extends Entity> targetProperties = propCell.getTarget().values();
				if (targetProperties != null) {
					for (Entity property : targetProperties) {
						createNamespaceForEntity(property);
					}
				}
			}
		}
	}

	private void createNamespaceForEntity(Entity entity) {
		QName typeName = entity.getDefinition().getType().getName();
		String namespaceURI = typeName.getNamespaceURI();
		String prefix = typeName.getPrefix();

		context.getOrCreateNamespace(namespaceURI, prefix);

		List<ChildContext> propertyPath = entity.getDefinition().getPropertyPath();
		createNamespacesForPath(propertyPath);
	}

	private void createNamespacesForPath(List<ChildContext> propertyPath) {
		if (propertyPath != null) {
			for (ChildContext childContext : propertyPath) {
				PropertyDefinition child = childContext.getChild().asProperty();
				if (child != null) {
					String namespaceURI = child.getName().getNamespaceURI();
					String prefix = child.getName().getPrefix();

					context.getOrCreateNamespace(namespaceURI, prefix);
				}
			}
		}
	}

	private boolean isIsolated(String namespaceUri) {
		boolean isIsolated = false;

		if (workspaceConf != null && workspaceConf.hasWorkspace(namespaceUri)) {
			return workspaceConf.getWorkspace(namespaceUri).isIsolated();
		}

		return isIsolated;
	}

	private void createTargetTypes() {
		Iterable<? extends Schema> targetSchemas = targetSchemaSpace.getSchemas();
		if (targetSchemas != null) {
			for (Schema targetSchema : targetSchemas) {
				mappingWrapper.addSchemaURI(targetSchema.getLocation().toString());
			}
		}
	}

	private void createTypeMappings(AppSchemaMappingContext context, IOReporter reporter) {
		Collection<? extends Cell> typeCells = alignment.getTypeCells();
		for (Cell typeCell : typeCells) {
			String typeTransformId = typeCell.getTransformationIdentifier();
			TypeTransformationHandler typeTransformHandler = null;

			try {
				typeTransformHandler = TypeTransformationHandlerFactory.getInstance()
						.createTypeTransformationHandler(typeTransformId);
				FeatureTypeMapping ftMapping = typeTransformHandler.handleTypeTransformation(
						typeCell, context);

				if (ftMapping != null) {
					Collection<? extends Cell> propertyCells = alignment.getPropertyCells(typeCell);
					for (Cell propertyCell : propertyCells) {
						String propertyTransformId = propertyCell.getTransformationIdentifier();
						PropertyTransformationHandler propertyTransformHandler = null;

						try {
							propertyTransformHandler = PropertyTransformationHandlerFactory
									.getInstance().createPropertyTransformationHandler(
											propertyTransformId);
							propertyTransformHandler.handlePropertyTransformation(typeCell,
									propertyCell, context);
						} catch (UnsupportedTransformationException e) {
							String errMsg = MessageFormat.format(
									"Error processing property cell {0}", propertyCell.getId());
							log.warn(errMsg, e);
							if (reporter != null) {
								reporter.warn(new IOMessageImpl(errMsg, e));
							}
						}
					}
				}
			} catch (UnsupportedTransformationException e) {
				String errMsg = MessageFormat.format("Error processing type cell{0}",
						typeCell.getId());
				log.warn(errMsg, e);
				if (reporter != null) {
					reporter.warn(new IOMessageImpl(errMsg, e));
				}
			}
		}
	}

	private AppSchemaDataAccessType loadMappingTemplate() throws IOException {
		InputStream is = getClass().getResourceAsStream(AppSchemaIO.MAPPING_TEMPLATE);

		JAXBElement<AppSchemaDataAccessType> templateElement = null;
		try {
			JAXBContext context = createJaxbContext();
			Unmarshaller unmarshaller = context.createUnmarshaller();

			templateElement = unmarshaller.unmarshal(new StreamSource(is),
					AppSchemaDataAccessType.class);
		} catch (JAXBException e) {
			throw new IOException(e);
		}

		return templateElement.getValue();
	}

	/**
	 * Writes the generated app-schema mapping to the provided output stream.
	 * 
	 * <p>
	 * If the mapping configuration requires multiple files, only the main
	 * configuration file will be written.
	 * </p>
	 * 
	 * @param out the output stream to write to
	 * @throws IOException if an I/O error occurs
	 */
	public void writeMappingConf(OutputStream out) throws IOException {
		checkMappingGenerated();

		try {
			writeMapping(out, mainMapping);
		} catch (JAXBException e) {
			throw new IOException(e);
		}

	}

	/**
	 * Writes the generated app-schema mapping configuration for the included
	 * types (non-feature types or non-top level feature types) to the provided
	 * output stream.
	 * 
	 * <p>
	 * If the mapping configuration does not require multiple files, an
	 * {@link IllegalStateException} is thrown.
	 * </p>
	 * 
	 * @param out the output stream to write to
	 * @throws IOException if an I/O error occurs
	 * @throws IllegalStateException if the mapping configuration does not
	 *             require multiple files
	 */
	public void writeIncludedTypesMappingConf(OutputStream out) throws IOException {
		checkMappingGenerated();

		if (!mappingWrapper.requiresMultipleFiles()) {
			throw new IllegalStateException(
					"No included types configuration is available for the generated mapping");
		}

		try {
			writeMapping(out, includedTypesMapping);
		} catch (JAXBException e) {
			throw new IOException(e);
		}

	}

	static void writeMapping(OutputStream out, AppSchemaDataAccessType mapping)
			throws JAXBException {
		JAXBContext context = createJaxbContext();

		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		JAXBElement<AppSchemaDataAccessType> mappingConfElement = new ObjectFactory()
				.createAppSchemaDataAccess(mapping);

		marshaller.marshal(mappingConfElement, out);
	}

	private static JAXBContext createJaxbContext() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(NET_OPENGIS_OGC_CONTEXT + ":"
				+ APP_SCHEMA_CONTEXT);

		return context;
	}
}
