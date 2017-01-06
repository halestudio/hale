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
import eu.esdihumboldt.hale.common.codelist.service.CodeListRegistry;
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

	private final Map<String, IOConfiguration> exportTemplates = new ExportConfigurationMap();

	private final Map<String, IOConfiguration> exportPresets = new ExportConfigurationMap();

	private final Map<Class<?>, Object> customServices = new HashMap<>();

	/**
	 * Project context service provider.
	 */
	private final ServiceProvider serviceProvider = new ServiceProvider() {

		private final ServiceProvider projectScope = new ServiceManager(
				ServiceManager.SCOPE_PROJECT);

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getService(Class<T> serviceInterface) {
			if (customServices.containsKey(serviceInterface)) {
				return (T) customServices.get(serviceInterface);
			}

			// FIXME global scope not supported yet
			return projectScope.getService(serviceInterface);
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
		super();
		this.id = id;

		// load the project
		URI location = input.getLocation();
		ProjectReader reader = HaleIO.findIOProvider(ProjectReader.class, input,
				(location != null) ? (location.getPath()) : (null));
		if (reader != null) {
			// configure reader
			reader.setSource(input);

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

			init(project);
		}
		else {
			throw new IOException("Cannot load project, no corresponding I/O provider found.");
		}
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

}
