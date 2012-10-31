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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.io.AlignmentIO;
import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.extension.internal.ActionProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.headless.io.HeadlessIO;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Advisor for loading a project headless. Only loads schemas and alignment, and
 * stores them in the advisor. As such an advisor instance may be used only once
 * to load a single project.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class HeadlessProjectAdvisor extends AbstractIOAdvisor<ProjectReader> {

	/**
	 * Loads an alignment.
	 */
	public class LoadAlignment extends AbstractIOAdvisor<AlignmentReader> {

		private Alignment alignment;

		@Override
		public void prepareProvider(AlignmentReader provider) {
			super.prepareProvider(provider);

			provider.setSourceSchema(sourceSchemaAdvisor.getSchema());
			provider.setTargetSchema(targetSchemaAdvisor.getSchema());
		}

		/**
		 * @see AbstractIOAdvisor#handleResults(IOProvider)
		 */
		@Override
		public void handleResults(AlignmentReader provider) {
			alignment = provider.getAlignment();
		}

		/**
		 * Get the loaded alignment.
		 * 
		 * @return the alignment
		 */
		public Alignment getAlignment() {
			return alignment;
		}

	}

	/**
	 * Updated singleton instance (holds no state).
	 */
	private static final LocationUpdater UPDATER = new LocationUpdater();

	/**
	 * Action IDs mapped to responsible advisors.
	 */
	private final Map<String, IOAdvisor<?>> advisors;

	/**
	 * Advisor for loading source schemas.
	 */
	private final LoadSchemaAdvisor sourceSchemaAdvisor;

	/**
	 * Advisor for loading target schemas.
	 */
	private final LoadSchemaAdvisor targetSchemaAdvisor;

	/**
	 * Advisor for loading the alignment.
	 */
	private final LoadAlignment alignmentAdvisor = new LoadAlignment();

	/**
	 * The loaded project
	 */
	private Project project;

	/**
	 * The report handler
	 */
	private final ReportHandler reportHandler;

	/**
	 * Default constructor
	 * 
	 * @param reportHandler the report handler to use when executing contained
	 *            I/O configurations, may be <code>null</code>
	 */
	public HeadlessProjectAdvisor(ReportHandler reportHandler) {
		super();

		this.reportHandler = reportHandler;

		advisors = new HashMap<String, IOAdvisor<?>>();

		sourceSchemaAdvisor = new LoadSchemaAdvisor();
		advisors.put(SchemaIO.ACTION_LOAD_SOURCE_SCHEMA, sourceSchemaAdvisor);

		targetSchemaAdvisor = new LoadSchemaAdvisor();
		advisors.put(SchemaIO.ACTION_LOAD_TARGET_SCHEMA, targetSchemaAdvisor);
	}

	@Override
	public void updateConfiguration(ProjectReader provider) {
		super.updateConfiguration(provider);

//		Map<String, ProjectFile> projectFiles = ProjectIO.createDefaultProjectFiles();

		Map<String, ProjectFile> projectFiles = new HashMap<String, ProjectFile>();
		// create only alignment project file
		projectFiles.put(AlignmentIO.PROJECT_FILE_ALIGNMENT, new ActionProjectFile(
				AlignmentIO.ACTION_LOAD_ALIGNMENT, //
				null, // auto-detect provider for loading
				new HashMap<String, String>(), // no parameters givens
				null, null, null) // give null for save related parts (should
									// not be called
				{

					@Override
					protected IOAdvisor<?> getLoadAdvisor(String loadActionId) {
						return alignmentAdvisor;
					}
				});
		provider.setProjectFiles(projectFiles);
	}

	@Override
	public void handleResults(ProjectReader provider) {
		project = provider.getProject();
		UPDATER.updateProject(project, provider.getSource().getLocation());

		// FIXME inject information about mappable types into advisors!

		// execute loaded I/O configurations
		List<IOConfiguration> confs = new ArrayList<IOConfiguration>(project.getResources());
		try {
			HeadlessIO.executeConfigurations(confs, advisors, reportHandler);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		Map<String, ProjectFile> projectFiles = provider.getProjectFiles();
		// apply remaining project files
		for (ProjectFile file : projectFiles.values()) {
			file.apply();
		}
	}

	/**
	 * Get the loaded project. Can be retrieved after the project was
	 * successfully loaded.
	 * 
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Get the alignment between source and target schemas. Can be retrieved
	 * after the project was successfully loaded.
	 * 
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return alignmentAdvisor.getAlignment();
	}

	/**
	 * Get the source schemas. Can be retrieved after the project was
	 * successfully loaded.
	 * 
	 * @return the source schemas
	 */
	public SchemaSpace getSourceSchema() {
		return sourceSchemaAdvisor.getSchema();
	}

	/**
	 * Get the target schemas. Can be retrieved after the project was
	 * successfully loaded.
	 * 
	 * @return the target schemas
	 */
	public SchemaSpace getTargetSchema() {
		return targetSchemaAdvisor.getSchema();
	}

}
