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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal;

import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceListener;

/**
 * Command state that represents if the instance sampling is enabled.
 * 
 * @author Simon Templer
 */
public class InstanceSamplingToggleState extends State {

	private final ProjectServiceListener projectListener;

	/**
	 * Default constructor
	 */
	public InstanceSamplingToggleState() {
		super();

		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		ps.addListener(projectListener = new ProjectServiceAdapter() {

			@Override
			public void afterLoad(final ProjectService projectService) {
				// update after the project has been loaded
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						update(projectService);
					}
				});
			}

			@Override
			public void projectSettingChanged(final String name, final Value value) {
				// update after the setting change
				if (InstanceViewPreferences.KEY_ENABLED.equals(name)) {
					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

						@Override
						public void run() {
							setValue(value.as(Boolean.class,
									InstanceViewPreferences.ENABLED_DEFAULT));
						}

					});
				}
			}

		});
		update(ps);
	}

	/**
	 * Update the value from the given project service.
	 * 
	 * @param ps the project service
	 */
	protected void update(ProjectService ps) {
		setValue(ps.getConfigurationService().getBoolean(InstanceViewPreferences.KEY_ENABLED,
				InstanceViewPreferences.ENABLED_DEFAULT));
	}

	@Override
	public void dispose() {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		ps.removeListener(projectListener);
	}

}
