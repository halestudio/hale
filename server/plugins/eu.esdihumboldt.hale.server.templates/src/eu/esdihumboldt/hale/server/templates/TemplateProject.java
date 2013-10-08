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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.io.AlignmentIO;
import eu.esdihumboldt.hale.common.align.io.impl.internal.JaxbToAlignment;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.CellType;
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
@SuppressWarnings("restriction")
public class TemplateProject extends ProjectReference<Void> {

	private boolean valid;

	private String notValidMessage;

	private int definedRelations = 0;

	private final Multimap<String, Resource> resources = ArrayListMultimap.create();

	/**
	 * @see ProjectReference#ProjectReference(File, String, String, Properties)
	 */
	public TemplateProject(File projectFolder, String overrideProjectFile, String projectId,
			Properties defaultSettings) throws IOException {
		super(projectFolder, overrideProjectFile, projectId, defaultSettings);
	}

	/**
	 * @return the resources
	 */
	public Multimap<String, Resource> getResources() {
		return ImmutableMultimap.copyOf(resources);
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

	/**
	 * @return the number defined relations, not necessarily exact, zero may
	 *         mean unknown
	 */
	public int getDefinedRelations() {
		return definedRelations;
	}

	@Override
	protected void onSuccess(Void context, String projectId, File projectFile, Project project,
			ReportFile reportFile) {
		super.onSuccess(context, projectId, projectFile, project, reportFile);

		// update locations in project file
		LocationUpdater updater = new LocationUpdater(project, projectFile.toURI());
		updater.updateProject(false);

		resources.clear();

		List<Path> invalidSources = new ArrayList<>();
		Path projectFolder = getProjectFolder().toPath();
		// validate resources
		for (IOConfiguration config : project.getResources()) {
			Resource resource = new IOConfigurationResource(config);

			// check if file URIs are valid and inside project folder
			URI source = resource.getSource();
			if (source != null) {
				Path path = null;

				if (source.getScheme() == null) {
					// is a relative URI
					path = projectFile.toPath().resolve(source.toString()).normalize();
				}
				else if ("file".equals(source.getScheme())) {
					// is a file URI
					path = Paths.get(source).normalize();
				}

				if (path != null) {
					// only file references are validated

					if (!path.startsWith(projectFolder) || !Files.exists(path)) {
						// invalid source
						invalidSources.add(path);
					}
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
				Path path = invalidSources.get(i);
				builder.append(path.getFileName().toString());
			}

			notValidMessage = builder.toString();
		}
		else {
			notValidMessage = "";
		}

		// additionally, try to find out cell count
		definedRelations = 0;

		// check if default alignment file exists
		try {
			File defAlignmentFile = new File(URI.create(projectFile.toURI().toASCIIString() + "."
					+ AlignmentIO.PROJECT_FILE_ALIGNMENT));
			if (defAlignmentFile.exists()) {
				// check alignment size

				try (InputStream in = new BufferedInputStream(new FileInputStream(defAlignmentFile))) {
					/*
					 * Try loading the file with JAXB - only supports 2.6+
					 * projects.
					 */
					AlignmentType alignment = JaxbToAlignment.load(in, null);

					// XXX ignoring base alignments

					int count = 0;
					for (Object element : alignment.getCellOrModifier()) {
						if (element instanceof CellType) {
							count++;
						}
					}
					definedRelations = count;
				} catch (Exception e) {
					// ignore
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

	@Override
	protected void onFailure(Void context, String projectId) {
		super.onFailure(context, projectId);

		valid = false;
		notValidMessage = "Project could not be loaded";
		definedRelations = 0;
		resources.clear();
	}

	@Override
	protected void onNotAvailable(Void context, String projectId) {
		super.onNotAvailable(context, projectId);

		valid = false;
		notValidMessage = "No project file found";
		definedRelations = 0;
		resources.clear();
	}

}
