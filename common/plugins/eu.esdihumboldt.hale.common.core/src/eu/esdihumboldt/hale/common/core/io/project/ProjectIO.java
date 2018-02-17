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

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

import de.fhg.igd.osgi.util.configuration.AbstractConfigurationService;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.extension.ProjectFileExtension;
import eu.esdihumboldt.hale.common.core.io.project.extension.ProjectFileFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Common utilities and constants regarding project I/O
 * 
 * @author Simon Templer
 */
public abstract class ProjectIO {

	/**
	 * Configuration service wrapper for a project.
	 */
	private static class ProjectConfigServiceWrapper extends AbstractConfigurationService
			implements ComplexConfigurationService {

		private final Project _project;

		/**
		 * Create a configuration service based on the given project.
		 * 
		 * @param project the project
		 */
		public ProjectConfigServiceWrapper(Project project) {
			_project = project;
		}

		@Override
		protected void setValue(String key, String value) {
			_project.getProperties().put(key, Value.of(value));
		}

		@Override
		protected void removeValue(String key) {
			_project.getProperties().remove(key);
		}

		@Override
		protected String getValue(String key) {
			Value value = _project.getProperties().get(key);
			return (value != null) ? (value.as(String.class)) : (null);
		}

		@Override
		public void setProperty(String name, Value value) {
			if (value == null || value.getValue() == null) {
				_project.getProperties().remove(name);
			}
			else {
				_project.getProperties().put(name, value);
			}
		}

		@Override
		public Value getProperty(String name) {
			Value value = _project.getProperties().get(name);
			return (value != null) ? (value) : (Value.NULL);
		}
	}

	/**
	 * Project file default type name
	 */
	public static final String PROJECT_TYPE_NAME = "hale project";

	/**
	 * Action ID for loading a project (Even if no such action is defined).
	 */
	public static final String ACTION_LOAD_PROJECT = "eu.esdihumboldt.hale.project.load";

	/**
	 * Action ID for saving a project (Even if no such action is defined).
	 */
	public static final String ACTION_SAVE_PROJECT = "eu.esdihumboldt.hale.project.save";

	/**
	 * Name of the internal project file
	 */
	public static final String PROJECT_FILE = "project.xml";

	/**
	 * Identifier of the HALE project archive content type
	 */
	public static final String PROJECT_ARCHIVE_CONTENT_TYPE_ID = "eu.esdihumboldt.hale.io.project.hale25.zip";

	/**
	 * Create a set of default project files for use with {@link ProjectReader}
	 * and {@link ProjectWriter}
	 * 
	 * @param serviceProvider the service provider to use for eventual I/O
	 *            advisors created
	 * @return the default project files
	 */
	public static Map<String, ProjectFile> createDefaultProjectFiles(
			ServiceProvider serviceProvider) {
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
	public static ComplexConfigurationService createProjectConfigService(final Project project) {
		return new ProjectConfigServiceWrapper(project);
	}

	/**
	 * Find the HALE project file in a directory. If there are multiple it will
	 * only find one.
	 * 
	 * @param projectDir the project directory
	 * @return the name of the project file candidate in that directory,
	 *         <code>null</code> if none was found
	 */
	public static String findProjectFile(File projectDir) {
		return findProjectFile(projectDir, null);
	}

	/**
	 * Find the HALE project file in a directory. If there are multiple it will
	 * only find one.
	 * 
	 * @param projectDir the project directory
	 * @param supportedExtensions the set of supported extensions, each with a
	 *            leading dot, or <code>null</code> if the supported extensions
	 *            should be determined automatically
	 * @return the name of the project file candidate in that directory,
	 *         <code>null</code> if none was found
	 */
	public static String findProjectFile(File projectDir, Set<String> supportedExtensions) {
		final Set<String> extensions;
		if (supportedExtensions == null) {
			extensions = getSupportedExtensions();
		}
		else {
			extensions = supportedExtensions;
		}

		File[] candidates = projectDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.isFile() && !file.isHidden()) {
					String lowerName = file.getName().toLowerCase();
					for (String extension : extensions) {
						if (lowerName.endsWith(extension.toLowerCase())) {
							return true;
						}
					}
				}
				return false;
			}

		});

		if (candidates != null) {
			if (candidates.length == 1) {
				return candidates[0].getName();
			}

			// more than one candidate, do a more thorough check
			// TODO warn that there are multiple?
			for (File candidate : candidates) {
				FileIOSupplier supplier = new FileIOSupplier(candidate);
				// find content type against stream
				IContentType contentType = HaleIO.findContentType(ProjectReader.class, supplier,
						null);
				if (contentType != null) {
					return candidate.getName();
				}
			}
		}

		// none found? check in subdirectories
		File[] subdirs = projectDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && !pathname.isHidden();
			}
		});
		if (subdirs != null) {
			for (File subdir : subdirs) {
				String name = findProjectFile(subdir, extensions);
				if (name != null) {
					return subdir.getName() + "/" + name;
				}
			}
		}

		return null;
	}

	/**
	 * Get the supported file extensions for projects.
	 * 
	 * @return the set of file extensions (with leading dot)
	 */
	private static Set<String> getSupportedExtensions() {
		Collection<IOProviderDescriptor> providers = HaleIO
				.getProviderFactories(ProjectReader.class);

		// collect supported content types
		Set<String> supportedExtensions = new HashSet<String>();
		for (IOProviderDescriptor factory : providers) {
			for (IContentType type : factory.getSupportedTypes()) {
				String[] extensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
				if (extensions != null) {
					for (String ext : extensions) {
						supportedExtensions.add('.' + ext);
					}
				}
			}
		}

		return supportedExtensions;
	}

}
