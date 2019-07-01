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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Strings;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.service.FunctionService;
import eu.esdihumboldt.hale.common.align.service.TransformationFunctionService;
import eu.esdihumboldt.hale.common.align.service.impl.AlignmentFunctionService;
import eu.esdihumboldt.hale.common.align.service.impl.AlignmentTransformationFunctionService;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationSchemas;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.codelist.service.CodeListRegistry;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.FixedProjectInfoService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.ExportConfigurationMap;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.core.service.ServiceManager;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.headless.HeadlessIO;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.instance.io.InstanceIO;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUrnBuilder;
import eu.esdihumboldt.hale.io.haleconnect.project.SimpleProjectReader;

/**
 * Transformation environment based on a {@link Project}.
 * 
 * @author Simon Templer
 */
public class ProjectTransformationEnvironment implements TransformationEnvironment {

	private static final ALogger log = ALoggerFactory
			.getLogger(ProjectTransformationEnvironment.class);

	private final Project project;

	private final String id;

	private final SchemaSpace sourceSchema;

	private final SchemaSpace targetSchema;

	private final Alignment alignment;

	private final URI loadLocation;

	private final Map<String, IOConfiguration> exportTemplates = new ExportConfigurationMap();

	private final Map<String, IOConfiguration> exportPresets = new ExportConfigurationMap();

	private final Map<Class<?>, Object> customServices = new HashMap<>();

	/**
	 * Project context service provider.
	 */
	private final ServiceProvider serviceProvider = new ServiceProvider() {

		private final ServiceProvider projectScope = new ServiceManager(
				ServiceManager.SCOPE_PROJECT) {

			@Override
			protected ServiceProvider getServiceLocator() {
				return serviceProvider;
			}

		};

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getService(Class<T> serviceInterface) {
			if (customServices.containsKey(serviceInterface)) {
				return (T) customServices.get(serviceInterface);
			}

			T service = projectScope.getService(serviceInterface);
			if (service == null) {
				// try global via HalePlatform
				return HalePlatform.getService(serviceInterface);
			}
			return service;
		}
	};

	/**
	 * Create a transformation environment based on a project file.
	 * 
	 * @param id the identifier for the transformation environment
	 * @param input the project file input
	 * @param reportHandler the report handler for the reports during project
	 *            loading, may be <code>null</code>
	 * @throws IOException if loading the project fails
	 */
	public ProjectTransformationEnvironment(String id,
			LocatableInputSupplier<? extends InputStream> input, ReportHandler reportHandler)
			throws IOException {
		this(id, input, reportHandler, null);
	}

	/**
	 * Create a transformation environment based on a project file.
	 * 
	 * @param id the identifier for the transformation environment
	 * @param input the project file input
	 * @param reportHandler the report handler for the reports during project
	 *            loading, may be <code>null</code>
	 * @param additionalAdvisors a map with additional I/O advisors, action ID
	 *            mapped to advisor, may be <code>null</code>
	 * @throws IOException if loading the project fails
	 */
	public ProjectTransformationEnvironment(String id,
			LocatableInputSupplier<? extends InputStream> input, ReportHandler reportHandler,
			Map<String, IOAdvisor<?>> additionalAdvisors) throws IOException {
		this(id, input, reportHandler, additionalAdvisors, null);
	}

	/**
	 * Create a transformation environment based on a project file.
	 * 
	 * @param id the identifier for the transformation environment
	 * @param input the project file input
	 * @param reportHandler the report handler for the reports during project
	 *            loading, may be <code>null</code>
	 * @param additionalAdvisors a map with additional I/O advisors, action ID
	 *            mapped to advisor, may be <code>null</code>
	 * @param additionalServices a map with additional services to be provided
	 *            before loading the project, may be <code>null</code>
	 * @throws IOException if loading the project fails
	 */
	public ProjectTransformationEnvironment(String id,
			LocatableInputSupplier<? extends InputStream> input, ReportHandler reportHandler,
			Map<String, IOAdvisor<?>> additionalAdvisors, Map<Class<?>, Object> additionalServices)
			throws IOException {
		super();
		this.id = id;
		this.loadLocation = input.getLocation();

		// load the project
		URI location = input.getLocation();
		ProjectReader reader;
		if (location != null
				&& HaleConnectUrnBuilder.SCHEME_HALECONNECT.equals(location.getScheme())) {
			// load from hale connect
			reader = new SimpleProjectReader();
		}
		else {
			// try to find reader automatically
			reader = HaleIO.findIOProvider(ProjectReader.class, input,
					(location != null) ? (location.getPath()) : (null));
		}
		if (reader != null) {
			// configure reader
			reader.setSource(input);

			if (additionalServices != null) {
				for (Entry<Class<?>, Object> entry : additionalServices.entrySet()) {
					customServices.put(entry.getKey(), entry.getKey().cast(entry.getValue()));
				}
			}

			HeadlessProjectAdvisor advisor = new HeadlessProjectAdvisor(reportHandler,
					serviceProvider, additionalAdvisors);
			HeadlessIO.executeProvider(reader, advisor, null, reportHandler);
			// XXX progress???!!

			project = advisor.getProject();
			sourceSchema = advisor.getSourceSchema();
			targetSchema = advisor.getTargetSchema();
			alignment = advisor.getAlignment();

			addService(FunctionService.class, new AlignmentFunctionService(alignment));
			addService(TransformationFunctionService.class,
					new AlignmentTransformationFunctionService(alignment));
			// make TransformationSchemas service available
			addService(TransformationSchemas.class, new TransformationSchemas() {

				@Override
				public SchemaSpace getSchemas(SchemaSpaceID spaceID) {
					switch (spaceID) {
					case SOURCE:
						return sourceSchema;
					case TARGET:
						return targetSchema;
					default:
						return null;
					}
				}
			});
			// make project information available
			addService(ProjectInfoService.class, new FixedProjectInfoService(project, location));
			// make code lists available
			addService(CodeListRegistry.class, advisor.getCodeListRegistry());
			// Make transformation service available, e.g. for inline
			// transformations
			addService(TransformationService.class,
					HalePlatform.getService(TransformationService.class));

			init(project);
		}
		else {
			throw new IOException("Cannot load project, no corresponding I/O provider found.");
		}
	}

	/**
	 * Copy constructor.
	 * 
	 * @param project the project
	 * @param id the identifier
	 * @param sourceSchema the source schema
	 * @param targetSchema the target schema
	 * @param alignment the alignment
	 * @param exportTemplates the export templates
	 * @param exportPresets the export presets
	 * @param customServices the custom services
	 * @param loadLocation the project load location
	 */
	protected ProjectTransformationEnvironment(Project project, String id, SchemaSpace sourceSchema,
			SchemaSpace targetSchema, Alignment alignment,
			Map<String, IOConfiguration> exportTemplates,
			Map<String, IOConfiguration> exportPresets, Map<Class<?>, Object> customServices,
			URI loadLocation) {
		this.project = project;
		this.id = id;
		this.targetSchema = targetSchema;
		this.sourceSchema = sourceSchema;
		this.alignment = alignment;
		this.loadLocation = loadLocation;

		this.exportTemplates.putAll(exportTemplates);
		this.exportPresets.putAll(exportPresets);
		this.customServices.putAll(customServices);
	}

	/**
	 * Create a copy of the transformation environment with the alignment
	 * replaced by the given alignment.
	 * 
	 * @param alignment the alignment to use for the copy
	 * @return the transformation environment
	 */
	public ProjectTransformationEnvironment copy(Alignment alignment) {
		return new ProjectTransformationEnvironment(project, id, sourceSchema, targetSchema,
				alignment, exportTemplates, exportPresets, customServices, loadLocation);
	}

	/**
	 * Create a copy of the transformation environment.
	 * 
	 * @return the transformation environment
	 */
	public ProjectTransformationEnvironment copy() {
		return new ProjectTransformationEnvironment(project, id, sourceSchema, targetSchema,
				alignment, exportTemplates, exportPresets, customServices, loadLocation);
	}

	/**
	 * Add a custom service to the transformation environment.
	 * 
	 * @param serviceInterface the service interface
	 * @param service the service
	 */
	public <T> void addService(Class<T> serviceInterface, T service) {
		customServices.put(serviceInterface, service);
	}

	/**
	 * Initialize the environment based on the loaded project.
	 * 
	 * @param project the project
	 */
	protected void init(Project project) {
		// TODO import/export configurations for data

		// export presets
		for (Entry<String, IOConfiguration> preset : project.getExportConfigurations().entrySet()) {
			IOConfiguration conf = preset.getValue();
			if (InstanceIO.ACTION_SAVE_TRANSFORMED_DATA.equals(conf.getActionId())) {
				// configuration for data export
				IOConfiguration c = conf.clone();

				// check provider
				IOProviderDescriptor factory = HaleIO.findIOProviderFactory(InstanceWriter.class,
						null, c.getProviderId());
				if (factory != null) {
					String name = preset.getKey();
					if (Strings.isNullOrEmpty(name)) {
						name = factory.getDisplayName();
					}
					exportPresets.put(name, c);
				}
				else {
					log.error("I/O provider for export preset not found.");
				}
			}
		}

		// export templates
		Collection<IOProviderDescriptor> writerFactories = HaleIO
				.getProviderFactories(InstanceWriter.class);
		for (IOProviderDescriptor factory : writerFactories) {
			try {
				InstanceWriter writer = (InstanceWriter) factory.createExtensionObject();
				writer.setTargetSchema(getTargetSchema());

				writer.checkCompatibility();

				IOConfiguration conf = new IOConfiguration();
				conf.setActionId(InstanceIO.ACTION_SAVE_TRANSFORMED_DATA);
				conf.setProviderId(factory.getIdentifier());
				exportTemplates.put(factory.getDisplayName(), conf);
			} catch (IOProviderConfigurationException e) {
				// ignore
			} catch (Exception e) {
				log.error("Error initializing instance writer for testing compatibility", e);
			}
		}
	}

	@Override
	public Map<String, ? extends IOConfiguration> getExportPresets() {
		return Collections.unmodifiableMap(exportPresets);
	}

	@Override
	public Map<String, ? extends IOConfiguration> getExportTemplates() {
		return Collections.unmodifiableMap(exportTemplates);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ProjectInfo getProjectInfo() {
		return project;
	}

	/**
	 * Get the associated project.
	 * 
	 * @return the project, must not be changed
	 */
	public Project getProject() {
		return project;
	}

	@Override
	public Alignment getAlignment() {
		return alignment;
	}

	@Override
	public SchemaSpace getSourceSchema() {
		return sourceSchema;
	}

	@Override
	public SchemaSpace getTargetSchema() {
		return targetSchema;
	}

	@Override
	public <T> T getService(Class<T> serviceInterface) {
		return serviceProvider.getService(serviceInterface);
	}

	/**
	 * @return the location the project was loaded from
	 */
	public URI getLoadLocation() {
		return loadLocation;
	}

}
