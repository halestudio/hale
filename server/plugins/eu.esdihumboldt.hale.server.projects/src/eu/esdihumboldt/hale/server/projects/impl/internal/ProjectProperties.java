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

package eu.esdihumboldt.hale.server.projects.impl.internal;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import eu.esdihumboldt.util.SyncedPropertiesFile;

/**
 * Project configuration properties.
 * 
 * @author Simon Templer
 */
public class ProjectProperties extends SyncedPropertiesFile {

	/**
	 * Name of the property specifying if a project is enabled
	 */
	public static final String PROPERTY_ENABLED = "enabled";

	/**
	 * Name of the property specifying the project file name
	 */
	public static final String PROPERTY_PROJECT_FILE = "project";

	/**
	 * Default properties
	 */
	private static final Properties DEFAULT_PROPERTIES = new Properties();
	static {
		// configure properties defaults
		DEFAULT_PROPERTIES.setProperty(PROPERTY_ENABLED, "true");
	}

	/**
	 * Create project properties.
	 * 
	 * @param projectPropertiesFile the project properties file
	 * 
	 * @throws IOException if reading the properties file fails
	 */
	public ProjectProperties(File projectPropertiesFile) throws IOException {
		super(projectPropertiesFile, DEFAULT_PROPERTIES);
	}

	/**
	 * Specifies if the project is enabled.
	 * 
	 * @return if the project is enabled
	 */
	public boolean isEnabled() {
		return Boolean.parseBoolean(getPropertyQuiet(PROPERTY_ENABLED));
	}

	/**
	 * Set if the project is enabled.
	 * 
	 * @param enabled if the project is enabled
	 */
	public void setEnabled(boolean enabled) {
		setPropertyQuiet(PROPERTY_ENABLED, String.valueOf(enabled));
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
