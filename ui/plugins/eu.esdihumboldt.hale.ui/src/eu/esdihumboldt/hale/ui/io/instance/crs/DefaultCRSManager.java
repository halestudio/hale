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

package eu.esdihumboldt.hale.ui.io.instance.crs;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.geometry.impl.AbstractCRSManager;
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

		projectService = PlatformUI.getWorkbench().getService(ProjectService.class);
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
