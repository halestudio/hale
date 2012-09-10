/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.service.project;

import java.net.URI;
import java.util.List;

import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;

/**
 * The {@link ProjectService} manages information on a HALE project, such as the
 * loaded schemas, instances etc.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public interface ProjectService {

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
	 * @return a list of removed io configurations
	 */
	public List<IOConfiguration> removeResources(String actionId);

	/**
	 * Get a project scoped configuration service
	 * 
	 * @return the configuration service
	 */
	public IConfigurationService getConfigurationService();

	/**
	 * Get general information about the current project
	 * 
	 * @return the project info
	 */
	public ProjectInfo getProjectInfo();

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

}
