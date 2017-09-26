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

import java.net.URI;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Provides support for saving projects
 * 
 * @author Simon Templer
 */
public interface ProjectWriter extends ExportProvider {

	/**
	 * Mode a {@link ProjectWriter} operates in
	 * 
	 * @author Florian Esser
	 */
	public enum ProjectWriterMode {
		/**
		 * The project is saved to a target location and the save configuration
		 * is updated accordingly
		 */
		SAVE,

		/**
		 * The project is exported to a target location but the save
		 * configuration is not updated
		 */
		EXPORT
	}

	/**
	 * Set the additional project files to write.
	 * 
	 * @param projectFiles the project files to write (file name mapped to
	 *            project file)
	 */
	public void setProjectFiles(Map<String, ProjectFile> projectFiles);

	/**
	 * Set the main project file.
	 * 
	 * @param project the main project file to write
	 */
	public void setProject(Project project);

	/**
	 * Get the main project file to be adapted before saving it.
	 * 
	 * @return the main project file that is to be written
	 */
	public Project getProject();

	/**
	 * Sets the previous target if the project was saved before.
	 * 
	 * @param previousTarget the previous target
	 */
	public void setPreviousTarget(URI previousTarget);

	/**
	 * @return the {@link ProjectWriterMode} that was used in the last operation
	 */
	public ProjectWriterMode getLastWriterMode();

}
