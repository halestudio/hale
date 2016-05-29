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

package eu.esdihumboldt.hale.common.core.io.project.util;

import java.net.URI;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;

/**
 * Updates locations in a project's I/O configurations that are not accessible,
 * e.g. because the project file has been moved. The updater allows correcting
 * paths to files that reside relative to the project.
 * 
 * @author Simon Templer
 */
public class LocationUpdater extends PathUpdate {

	private final Project project;

	/**
	 * Default constructor.<br>
	 * If either project, the save configuration of project or newLocation is
	 * null all calls on this object will have no effect.
	 * 
	 * @param project the project to update
	 * @param newLocation the new location of the project file
	 */
	public LocationUpdater(Project project, URI newLocation) {
		// sorry about that...
		super(project == null ? null : ((project.getSaveConfiguration() == null) ? null : URI
				.create(project.getSaveConfiguration().getProviderConfiguration()
						.get(ExportProvider.PARAM_TARGET).toString())), newLocation);

		this.project = project;
	}

	/**
	 * Update locations in the given project.
	 * 
	 * @param keepRelative whether to keep working relative URIs as is or make
	 *            them absolute
	 */
	public void updateProject(boolean keepRelative) {
		if (project == null || getOldLocation() == null || getNewLocation() == null)
			return;

		IOConfiguration saveconfig = project.getSaveConfiguration();
		// actually cannot be null here because then old location would be null
		if (saveconfig == null)
			return;

		if (!getOldLocation().equals(getNewLocation()) || !keepRelative) {
			// update save configuration
			saveconfig.getProviderConfiguration().put(ExportProvider.PARAM_TARGET,
					Value.of(getNewLocation().toString()));

			// update I/O configurations
			List<IOConfiguration> configuration = project.getResources();
			for (IOConfiguration providerconf : configuration) {
				final Map<String, Value> conf = providerconf.getProviderConfiguration();
				final URI uri = URI.create(conf.get(ImportProvider.PARAM_SOURCE).as(String.class));
				URI resolved = findLocation(uri, true, true, keepRelative);
				if (resolved != null)
					conf.put(ImportProvider.PARAM_SOURCE, Value.of(resolved.toString()));
			}

			// update project file infos
			for (ProjectFileInfo fileInfo : project.getProjectFiles()) {
				URI location = fileInfo.getLocation();
				/*
				 * Project files should always be next to the project file.
				 * 
				 * Fallback wouldn't have an effect here because as it is used
				 * currently in the project service, project files are already
				 * loaded in the DefaultProjectReader.
				 */
				URI resolved = findLocation(location, false, false, keepRelative);
				if (resolved != null)
					fileInfo.setLocation(resolved);
			}
		}
	}

	/**
	 * Updates the source location of the given configuration.
	 * 
	 * @param configuration the configuration to update
	 * @param keepRelative whether to keep working relative URI as is or make it
	 *            absolute
	 */
	public void updateIOConfiguration(IOConfiguration configuration, boolean keepRelative) {
		final Map<String, Value> conf = configuration.getProviderConfiguration();
		final URI uri = URI.create(conf.get(ImportProvider.PARAM_SOURCE).as(String.class));
		URI resolved = findLocation(uri, true, true, keepRelative);
		if (resolved != null)
			conf.put(ImportProvider.PARAM_SOURCE, Value.of(resolved.toString()));
	}

}
