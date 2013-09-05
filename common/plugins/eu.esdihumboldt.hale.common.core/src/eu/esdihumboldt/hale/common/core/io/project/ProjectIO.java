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

package eu.esdihumboldt.hale.common.core.io.project;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.fhg.igd.osgi.util.configuration.AbstractConfigurationService;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.extension.ProjectFileExtension;
import eu.esdihumboldt.hale.common.core.io.project.extension.ProjectFileFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Common utilities and constants regarding project I/O
 * 
 * @author Simon Templer
 */
public abstract class ProjectIO {

	/**
	 * Project file default type name
	 */
	public static final String PROJECT_TYPE_NAME = "HALE project";

	/**
	 * Name of the internal project file
	 */
	public static final String PROJECT_FILE = "project.xml";

	/**
	 * Create a set of default project files for use with {@link ProjectReader}
	 * and {@link ProjectWriter}
	 * 
	 * @param serviceProvider the service provider to use for eventual I/O
	 *            advisors created
	 * @return the default project files
	 */
	public static Map<String, ProjectFile> createDefaultProjectFiles(ServiceProvider serviceProvider) {
		Map<String, ProjectFile> result = new HashMap<String, ProjectFile>();

		Collection<ProjectFileFactory> elements = new ProjectFileExtension(serviceProvider)
				.getElements();
		for (ProjectFileFactory element : elements) {
			result.put(element.getId(), element.createProjectFile());
		}

		return result;
	}

	/**
	 * Create an {@link IConfigurationService} from a given project.
	 * 
	 * @param project the project
	 * @return the configuration service to access the project's properties
	 */
	public static IConfigurationService createProjectConfigService(final Project project) {
		return new AbstractConfigurationService() {

			@Override
			protected void setValue(String key, String value) {
				project.getProperties().put(key, Value.of(value));
			}

			@Override
			protected void removeValue(String key) {
				project.getProperties().remove(key);
			}

			@Override
			protected String getValue(String key) {
				Value value = project.getProperties().get(key);
				return (value != null) ? (value.as(String.class)) : (null);
			}
		};
	}

}
