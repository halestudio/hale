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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Strings;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.instance.io.InstanceIO;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
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

	private final List<IOConfiguration> exportTemplates = new ArrayList<IOConfiguration>();

	private final List<IOConfiguration> exportPresets = new ArrayList<IOConfiguration>();

	/**
	 * Create a transformation environment based on a project file.
	 * 
	 * @param id the identifier for the transformation environment
	 * @param input the project file input
	 * @throws IOException if loading the project fails
	 */
	public ProjectTransformationEnvironment(String id,
			LocatableInputSupplier<? extends InputStream> input) throws IOException {
		super();
		this.id = id;

		// load the project
		URI location = input.getLocation();
		ProjectReader reader = HaleIO.findIOProvider(ProjectReader.class, input,
				(location != null) ? (location.getPath()) : (null));
		if (reader != null) {
			// configure reader
			reader.setSource(input);

			HeadlessProjectAdvisor advisor = new HeadlessProjectAdvisor();
			HeadlessIO.executeProvider(reader, advisor, null); // XXX
																// progress???!!
			project = advisor.getProject();
			sourceSchema = advisor.getSourceSchema();
			targetSchema = advisor.getTargetSchema();
			alignment = advisor.getAlignment();

			init(project);
		}
		else {
			throw new IOException("Cannot load project, no corresponding I/O provider found.");
		}
	}

	/**
	 * Initialize the environment based on the loaded project.
	 * 
	 * @param project the project
	 */
	protected void init(Project project) {
		// TODO import/export configurations for data

		// export presets
		for (IOConfiguration conf : project.getResources()) {
			if (InstanceIO.ACTION_SAVE_TRANSFORMED_DATA.equals(conf.getActionId())) {
				// configuration for data export
				IOConfiguration c = conf.clone();

				// check provider
				IOProviderDescriptor factory = HaleIO.findIOProviderFactory(InstanceWriter.class,
						null, c.getProviderId());
				if (factory != null) {
					if (Strings.isNullOrEmpty(c.getName())) {
						c.setName(factory.getDisplayName());
					}
					exportPresets.add(c);
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
				conf.setName(factory.getDisplayName());
				exportTemplates.add(conf);
			} catch (IOProviderConfigurationException e) {
				// ignore
			} catch (Exception e) {
				log.error("Error initializing instance writer for testing compatibility", e);
			}
		}
	}

	/**
	 * @see TransformationEnvironment#getExportPresets()
	 */
	@Override
	public Collection<? extends IOConfiguration> getExportPresets() {
		List<IOConfiguration> result = new ArrayList<IOConfiguration>();
		for (IOConfiguration conf : exportPresets) {
			result.add(conf.clone());
		}
		return result;
	}

	/**
	 * @see TransformationEnvironment#getExportTemplates()
	 */
	@Override
	public Collection<? extends IOConfiguration> getExportTemplates() {
		List<IOConfiguration> result = new ArrayList<IOConfiguration>();
		for (IOConfiguration conf : exportTemplates) {
			result.add(conf.clone());
		}
		return result;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ProjectInfo getProjectInfo() {
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

}
