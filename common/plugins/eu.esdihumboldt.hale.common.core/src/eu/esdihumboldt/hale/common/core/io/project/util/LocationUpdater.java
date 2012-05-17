/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.core.io.project.util;

import java.net.URI;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.util.io.IOUtils;
import eu.esdihumboldt.util.io.PathUpdate;

/**
 * Updates locations in a project's I/O configurations that are not accessible,
 * e.g. because the project file has been moved. The updater allows correcting
 * paths to files that reside relative to the project.
 * @author Simon Templer
 */
public class LocationUpdater {
	
	/**
	 * Update locations in the given project. 
	 * @param project the project object
	 * @param newProjectLoc the new project location
	 */
	public void updateProject(Project project, URI newProjectLoc) {
		// uses paths based on "/" in FilePathUpdate
		IOConfiguration saveconfig = project.getSaveConfiguration();
		if (saveconfig == null)
			return;

		URI targetLoc = URI.create(saveconfig.getProviderConfiguration().get(ExportProvider.PARAM_TARGET));
		if (!targetLoc.equals(newProjectLoc)) {
			PathUpdate update = new PathUpdate(targetLoc, newProjectLoc);

			List<IOConfiguration> configuration = project.getResources();
			for (IOConfiguration providerconf : configuration) {
				final Map<String, String> conf = providerconf.getProviderConfiguration();
				final URI uri = URI.create(conf.get(ImportProvider.PARAM_SOURCE));
				if (!IOUtils.testStream(uri, true)) {
					URI newUri = update.changePath(uri);
					if (IOUtils.testStream(newUri, true))
						conf.put(ImportProvider.PARAM_SOURCE, newUri.toString());
					else {
						// not found
						URI replacement = updatePathFallback(uri);
						if (replacement != null) {
							conf.put(ImportProvider.PARAM_SOURCE, replacement.toString());
						}
					}
				}
			}
		}
	}

	/**
	 * Update the path to a resource if automatic update fails.
	 * The default implementation returns <code>null</code>, which means
	 * the location is not updated.
	 * @param oldLocation the old resource location
	 * @return the replacement resource location or <code>null</code>
	 */
	protected URI updatePathFallback(URI oldLocation) {
		return null;
	}

}
