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
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.util.io.IOUtils;
import eu.esdihumboldt.util.io.PathUpdate;

/**
 * Updates locations in a project's I/O configurations that are not accessible,
 * e.g. because the project file has been moved. The updater allows correcting
 * paths to files that reside relative to the project.
 * 
 * @author Simon Templer
 */
public class LocationUpdater {

	/**
	 * Update locations in the given project.
	 * 
	 * @param project the project object
	 * @param newProjectLoc the new project location
	 */
	public void updateProject(Project project, URI newProjectLoc) {
		// uses paths based on "/" in FilePathUpdate
		IOConfiguration saveconfig = project.getSaveConfiguration();
		if (saveconfig == null)
			return;

		// old project location
		URI targetLoc = URI.create(saveconfig.getProviderConfiguration()
				.get(ExportProvider.PARAM_TARGET).toString());

		if (!targetLoc.equals(newProjectLoc)) {
			// update save configuration
			saveconfig.getProviderConfiguration().put(ExportProvider.PARAM_TARGET,
					Value.of(newProjectLoc.toString()));

			PathUpdate update = new PathUpdate(targetLoc, newProjectLoc);

			// update I/O configurations
			List<IOConfiguration> configuration = project.getResources();
			for (IOConfiguration providerconf : configuration) {
				final Map<String, Value> conf = providerconf.getProviderConfiguration();
				final URI uri = URI.create(conf.get(ImportProvider.PARAM_SOURCE)
						.getAs(String.class));
				if (!IOUtils.testStream(uri, true)) {
					URI newUri = update.changePath(uri);
					if (IOUtils.testStream(newUri, true))
						conf.put(ImportProvider.PARAM_SOURCE, Value.of(newUri.toString()));
					else {
						// not found
						URI replacement = updatePathFallback(uri);
						if (replacement != null) {
							conf.put(ImportProvider.PARAM_SOURCE,
									Value.of(replacement.toString()));
						}
					}
				}
			}

			// update project file infos
			for (ProjectFileInfo fileInfo : project.getProjectFiles()) {
				URI location = fileInfo.getLocation();
				if (!IOUtils.testStream(fileInfo.getLocation(), false)) {
					location = update.changePath(location);
					fileInfo.setLocation(location);
					/*
					 * For this the fallback method is not called intentionally,
					 * as in the project service, this update has no effect, as
					 * the project files are already loaded in the
					 * DefaultProjectReader.
					 */
				}
			}
		}
	}

	/**
	 * Update the path to a resource if automatic update fails. The default
	 * implementation returns <code>null</code>, which means the location is not
	 * updated.
	 * 
	 * @param oldLocation the old resource location
	 * @return the replacement resource location or <code>null</code>
	 */
	protected URI updatePathFallback(URI oldLocation) {
		return null;
	}

}
