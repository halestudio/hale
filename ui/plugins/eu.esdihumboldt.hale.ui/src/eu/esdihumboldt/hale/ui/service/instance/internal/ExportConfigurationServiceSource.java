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

package eu.esdihumboldt.hale.ui.service.instance.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;

/**
 * Provides UI variables related to the {@link ProjectService}
 * 
 * @author Patrick Lieb
 */
public class ExportConfigurationServiceSource extends AbstractSourceProvider {

	/**
	 * The name of the variable which value is <code>true</code> if there is at
	 * least one export configuration present in the {@link ProjectService}
	 */
	public static final String HAS_EXPORT_CONFIGURATIONS = "hale.project.has_export_configurations";

	private ProjectServiceAdapter projectListener;

	/**
	 * Default Constructor
	 */
	public ExportConfigurationServiceSource() {
		super();

		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		ps.addListener(projectListener = new ProjectServiceAdapter() {

			/**
			 * @see eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter#onExportConfigurationChange()
			 */
			@Override
			public void onExportConfigurationChange() {
				fireSourceChanged(ISources.WORKBENCH, HAS_EXPORT_CONFIGURATIONS,
						hasExportConfigurations());
			}
		});

	}

	private boolean hasExportConfigurations() {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		return !ps.getExportConfigurationNames().isEmpty();
	}

	/**
	 * @see org.eclipse.ui.ISourceProvider#dispose()
	 */
	@Override
	public void dispose() {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		ps.removeListener(projectListener);
	}

	/**
	 * @see org.eclipse.ui.ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<String, Object> getCurrentState() {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(HAS_EXPORT_CONFIGURATIONS, hasExportConfigurations());
		return result;
	}

	/**
	 * @see org.eclipse.ui.ISourceProvider#getProvidedSourceNames()
	 */
	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { HAS_EXPORT_CONFIGURATIONS };
	}

}
