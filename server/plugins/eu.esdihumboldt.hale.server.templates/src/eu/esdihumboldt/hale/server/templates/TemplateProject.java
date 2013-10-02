/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.server.templates;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfigurationResource;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater;
import eu.esdihumboldt.hale.common.headless.report.ReportFile;
import eu.esdihumboldt.hale.common.headless.scavenger.ProjectReference;

/**
 * Template project reference.
 * 
 * @author Simon Templer
 */
public class TemplateProject extends ProjectReference<Void> {

	private boolean valid;

	private String notValidMessage;

	private final Multimap<String, Resource> resources = ArrayListMultimap.create();

	/**
	 * @see ProjectReference#ProjectReference(File, String, String, Properties)
	 */
	public TemplateProject(File projectFolder, String overrideProjectFile, String projectId,
			Properties defaultSettings) throws IOException {
		super(projectFolder, overrideProjectFile, projectId, defaultSettings);
	}

	/**
	 * States if the template is valid.
	 * 
	 * @return <code>true</code> if the template is valid, <code>false</code>
	 *         otherwise
	 */
	public boolean isValid() {
		return getProjectInfo() != null && valid;
	}

	/**
	 * @return the notValidMessage
	 */
	public String getNotValidMessage() {
		return notValidMessage;
	}

	@Override
	protected void onSuccess(Void context, String projectId, File projectFile, Project project,
			ReportFile reportFile) {
		super.onSuccess(context, projectId, projectFile, project, reportFile);

		// update locations in project file
		LocationUpdater updater = new LocationUpdater(project, projectFile.toURI());
		updater.updateProject(false);

		resources.clear();

		List<URI> invalidSources = new ArrayList<>();
		Path projectFolder = getProjectFolder().toPath();
		// validate resources
		for (IOConfiguration config : project.getResources()) {
			Resource resource = new IOConfigurationResource(config);

			// check if file URIs are valid and inside project folder
			URI source = resource.getSource();
			if (source != null && "file".equals(source.getScheme())) {
				// is a file URI
				Path path = Paths.get(source).normalize();
				if (!path.startsWith(projectFolder) || !Files.exists(path)) {
					// invalid source
					invalidSources.add(source);
				}
			}

			resources.put(resource.getActionId(), resource);
		}

		valid = invalidSources.isEmpty();
		if (!valid) {
			StringBuilder builder = new StringBuilder(
					"Files referenced by the project could not be found: ");

			for (int i = 0; i < invalidSources.size(); i++) {
				if (i > 0)
					builder.append(", ");
				Path path = Paths.get(invalidSources.get(i));
				builder.append(path.getFileName().toString());
			}

			notValidMessage = builder.toString();
		}
		else {
			notValidMessage = "";
		}
	}

	@Override
	protected void onFailure(Void context, String projectId) {
		super.onFailure(context, projectId);

		valid = false;
		notValidMessage = "Project could not be loaded";
		resources.clear();
	}

	@Override
	protected void onNotAvailable(Void context, String projectId) {
		super.onNotAvailable(context, projectId);

		valid = false;
		notValidMessage = "No project file found";
		resources.clear();
	}

}
