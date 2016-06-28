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

package eu.esdihumboldt.hale.ui.service.project;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import com.google.common.util.concurrent.ListenableFuture;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ComplexConfigurationService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectDescription;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;

/**
 * The {@link ProjectService} manages information on a HALE project, such as the
 * loaded schemas, instances etc.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public interface ProjectService extends ProjectInfoService {

	/**
	 * Adds a project service listener
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(ProjectServiceListener listener);

	/**
	 * Removes a project service listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(ProjectServiceListener listener);

	/**
	 * Remember I/O operations after the execution of the corresponding I/O
	 * provider for storing it in the project.
	 * 
	 * @param actionId the I/O action identifier
	 * @param providerId the I/O provider identifier
	 * @param provider the I/O provider instance used for the I/O operation
	 */
	public void rememberIO(String actionId, String providerId, IOProvider provider);

	/**
	 * Execute and remember the given I/O configuration.
	 * 
	 * @param conf the I/O configuration
	 */
	public void executeAndRemember(IOConfiguration conf);

	/**
	 * Remove all resources of the specified actionId from the project.
	 * 
	 * @see #rememberIO(String, String, IOProvider)
	 * @param actionId the I/O action identifier
	 * @return a list of removed I/O configurations
	 */
	public List<? extends Resource> removeResources(String actionId);

	/**
	 * Remove a specific resource.
	 * 
	 * @param resourceId the resource identifier
	 */
	public void removeResource(String resourceId);

	/**
	 * Get the resources associated with the current project.
	 * 
	 * @return the resources
	 */
	public Iterable<? extends Resource> getResources();

	/**
	 * Determines if there are any resources loaded for a given action.
	 * 
	 * @param actionId the action identifier
	 * @return if there are resources present for the action
	 */
	public boolean hasResources(String actionId);

	/**
	 * Get a project scoped configuration service
	 * 
	 * @return the configuration service
	 */
	public ComplexConfigurationService getConfigurationService();

	/**
	 * Get general information about the current project
	 * 
	 * @return the project info
	 */
	@Override
	public ProjectInfo getProjectInfo();

	/**
	 * Update the project information with the given description.
	 * 
	 * @param info the project information to merge with the project
	 */
	public void updateProjectInfo(ProjectDescription info);

	/**
	 * Get if the project content is changed
	 * 
	 * @return if the project content is changed
	 */
	public boolean isChanged();

	/**
	 * Inform the service about a change in the project content.
	 */
	public void setChanged();

	/**
	 * Clean the project, reset all services.
	 */
	public void clean();

	/**
	 * Load a project from a given file.
	 * 
	 * @param uri the project file
	 */
	public void load(URI uri);

	/**
	 * Load a template project.
	 * 
	 * @param project the project template
	 */
	public void loadTemplate(Project project);

	/**
	 * Open a project.
	 */
	public void open();

	/**
	 * Save the project. Calls {@link #saveAs()} if needed.
	 */
	public void save();

	/**
	 * Save the project to the given file
	 */
	public void saveAs();

	/**
	 * Export the project
	 * 
	 * @param writer the project writer to use for the export
	 * @return the future yielding the report on success
	 */
	public ListenableFuture<IOReport> export(ProjectWriter writer);

	/**
	 * Returns the location updater for the current project.
	 * 
	 * @return the location updater for the current project
	 */
	public LocationUpdater getLocationUpdater();

	/**
	 * Add the given configuration to the export configurations of the project.
	 * 
	 * @param name the configuration name
	 * @param config the I/O configuration
	 */
	public void addExportConfiguration(String name, IOConfiguration config);

	/**
	 * Remove the export configuration with the given name from the export
	 * configurations of the project.
	 * 
	 * @param name the export configuration name
	 */
	public void removeExportConfiguration(String name);

	/**
	 * Get a clone of the export configuration with the given name.
	 * 
	 * @param name the export configuration name
	 * @return the I/O configuration
	 */
	public IOConfiguration getExportConfiguration(String name);

	/**
	 * Get the names of all export configurations.
	 * 
	 * @return the export configuration names
	 */
	public Collection<String> getExportConfigurationNames();

	/**
	 * Get the names of export configurations applicable for the given provider
	 * class.
	 * 
	 * @param providerClass the provider type
	 * 
	 * @return the export configuration names
	 */
	public Collection<String> getExportConfigurationNames(
			Class<? extends IOProvider> providerClass);

	/**
	 * Returns the location the current project was loaded from. May be
	 * <code>null</code>.
	 * 
	 * @return the location the current project was loaded from
	 */
	public URI getLoadLocation();

	/**
	 * Reload the source data.
	 */
	public void reloadSourceData();

	/**
	 * Reload the project and offer options to update it.
	 */
	public void update();

}
