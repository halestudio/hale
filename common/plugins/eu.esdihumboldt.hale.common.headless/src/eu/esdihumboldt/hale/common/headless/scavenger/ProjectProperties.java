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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.scavenger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import eu.esdihumboldt.util.SyncedPropertiesFile;

/**
 * Basic project configuration properties.
 * 
 * @author Simon Templer
 */
public class ProjectProperties extends SyncedPropertiesFile {

	/**
	 * Name of the property specifying the project file name
	 */
	public static final String PROPERTY_PROJECT_FILE = "project";

	/**
	 * Create project properties.
	 * 
	 * @param projectPropertiesFile the project properties file
	 * @param defaultProperties the properties with default settings, may be
	 *            <code>null</code>
	 * 
	 * @throws IOException if reading the properties file fails
	 */
	public ProjectProperties(File projectPropertiesFile, Properties defaultProperties)
			throws IOException {
		super(projectPropertiesFile, defaultProperties);
	}

	/**
	 * Get the project file name.
	 * 
	 * @return the project file name, may be <code>null</code>
	 */
	public String getProjectFileName() {
		return getPropertyQuiet(PROPERTY_PROJECT_FILE);
	}

	/**
	 * Set the project file name.
	 * 
	 * @param filename the project file name
	 */
	public void setProjectFileName(String filename) {
		setPropertyQuiet(PROPERTY_PROJECT_FILE, filename);
	}

}
