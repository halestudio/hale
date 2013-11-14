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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.ui.service.instance.sample.InstanceViewService;
import eu.esdihumboldt.hale.ui.service.instance.sample.Sampler;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Instance sample view service implementation.
 * 
 * @author Simon Templer
 */
public class InstanceViewServiceImpl implements InstanceViewService {

	private final ProjectService projectService;

	/**
	 * Constructor.
	 * 
	 * @param projectService the project service
	 */
	public InstanceViewServiceImpl(ProjectService projectService) {
		super();
		this.projectService = projectService;
	}

	@Override
	public InstanceCollection sample(InstanceCollection instances) {
		if (isEnabled()) {
			String samplerId = projectService.getConfigurationService().get(
					InstanceViewPreferences.KEY_SAMPLER, InstanceViewPreferences.SAMPLER_FIRST);

			if (samplerId != null) {
				Sampler sampler = InstanceViewPreferences.SAMPLERS.get(samplerId);
				if (sampler != null) {
					Value settings = projectService.getConfigurationService().getProperty(
							InstanceViewPreferences.KEY_SETTINGS_PREFIX + samplerId);
					return sampler.sample(instances, settings);
				}
			}
		}

		return instances;
	}

	@Override
	public boolean isEnabled() {
		// enabled by default
		return projectService.getConfigurationService().getBoolean(
				InstanceViewPreferences.KEY_ENABLED, InstanceViewPreferences.ENABLED_DEFAULT);
	}

}
