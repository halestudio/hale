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

package eu.esdihumboldt.hale.ui.io.instance.crs;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Default CRS Manager, configuration backed by the {@link ProjectService}.
 * 
 * @author Simon Templer
 */
public class DefaultCRSManager extends AbstractCRSManager {

	private final ProjectService projectService;

	/**
	 * @see AbstractCRSManager#AbstractCRSManager(InstanceReader, CRSProvider)
	 */
	public DefaultCRSManager(InstanceReader reader, CRSProvider provider) {
		super(reader, provider);

		projectService = (ProjectService) PlatformUI.getWorkbench()
				.getService(ProjectService.class);
	}

	/**
	 * @see AbstractCRSManager#storeValue(String, String)
	 */
	@Override
	protected void storeValue(String key, String value) {
		projectService.getConfigurationService().set(key, value);
	}

	/**
	 * @see AbstractCRSManager#loadValue(String)
	 */
	@Override
	protected String loadValue(String key) {
		return projectService.getConfigurationService().get(key);
	}

}
