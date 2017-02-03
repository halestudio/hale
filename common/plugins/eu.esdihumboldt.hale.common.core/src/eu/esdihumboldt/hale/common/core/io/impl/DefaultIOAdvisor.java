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

package eu.esdihumboldt.hale.common.core.io.impl;

import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;

/**
 * Base class for {@link IOAdvisor}s that implement {@link ProjectInfoAware}.
 * 
 * @author Simon Templer
 * @param <T> the I/O provider type
 */
public abstract class DefaultIOAdvisor<T extends IOProvider> extends AbstractIOAdvisor<T> {

	/**
	 * @see AbstractIOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(T provider) {
		super.prepareProvider(provider);

		if (provider instanceof ProjectInfoAware) {
			ProjectInfoService ps = getService(ProjectInfoService.class);
			if (ps != null) {
				ProjectInfoAware pia = (ProjectInfoAware) provider;
				pia.setProjectInfo(ps.getProjectInfo());
				URI projectFile = ps.getLoadLocation();
				if (projectFile == null) {
					pia.setProjectLocation(null);
				}
				else {
					pia.setProjectLocation(projectFile);
				}
			}
		}
	}
}
