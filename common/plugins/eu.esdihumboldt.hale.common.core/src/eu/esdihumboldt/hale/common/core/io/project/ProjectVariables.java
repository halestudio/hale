/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.project;

import javax.annotation.Nullable;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;

/**
 * Accessor for project variables. Project variable values may be overriden by
 * environment variables or system properties.
 * 
 * @author Simon Templer
 */
public class ProjectVariables {

	private static final ALogger log = ALoggerFactory.getLogger(ProjectVariables.class);

	/**
	 * Prefix for project variables provided as system property.
	 */
	public static final String PREFIX_SYSTEM_PROPERTY = "hale.project.";

	/**
	 * Prefix for project variables provided as environment variable.
	 */
	public static final String PREFIX_ENV = "HALE_PROJECT_";

	/**
	 * Name of the project property holding the project variables.
	 */
	public static final String PROJECT_PROPERTY_VARIABLES = "variables";

	/**
	 * Helper method to store a project variable value.
	 * 
	 * @param name the variable name
	 * @param value the variable value
	 * @param projectConfiguration the project configuration service
	 */
	public static void setVariable(String name, Value value,
			ComplexConfigurationService projectConfiguration) {
		Value variables = projectConfiguration.getProperty(PROJECT_PROPERTY_VARIABLES);
		ValueProperties properties = variables.as(ValueProperties.class);
		if (properties != null) {
			properties.put(name, value);
			projectConfiguration.setProperty(PROJECT_PROPERTY_VARIABLES, Value.complex(properties));
		}
		else {
			if (variables.getValue() == null) {
				log.error("Unknown representation of project variables");
			}
		}
	}

	private final ProjectInfoService projectInfo;

	/**
	 * Create a project variables accessor.
	 * 
	 * @param projectInfo the project information service
	 */
	public ProjectVariables(@Nullable ProjectInfoService projectInfo) {
		super();
		this.projectInfo = projectInfo;
	}

	/**
	 * Get the value for a given variable name.
	 * 
	 * @param name the variable name
	 * @return the project variable value or {@link Value#NULL}
	 */
	public Value getValue(String name) {
		// 1st priority: system property
		String value = System.getProperty(PREFIX_SYSTEM_PROPERTY + name);
		if (value != null) {
			return Value.of(value);
		}

		// 2nd priority: environment variable
		value = System.getenv(PREFIX_ENV + name);
		if (value != null) {
			return Value.of(value);
		}

		// 3rd priority: use value stored in project
		if (projectInfo != null) {
			Value variables = projectInfo.getProperty(PROJECT_PROPERTY_VARIABLES);
			ValueProperties properties = variables.as(ValueProperties.class);
			if (properties != null) {
				return properties.getSafe(name);
			}
			else {
				if (variables.getValue() == null) {
					log.error("Unknown representation of project variables");
				}
			}
		}

		return Value.NULL;
	}

	/**
	 * Get the string value for a given variable name.
	 * 
	 * @param name the variable name
	 * @param def the default value if none is configured
	 * @return the project variable value if set, otherwise the provided default
	 *         value
	 */
	public String getString(String name, String def) {
		Value value = getValue(name);
		if (value.getValue() == null) {
			return def;
		}
		else {
			return value.as(String.class);
		}
	}

	/**
	 * Get the string value for a given variable name.
	 * 
	 * @param name the variable name
	 * @return the project variable value if set, otherwise <code>null</code>
	 */
	public String getStringOpt(String name) {
		return getString(name, null);
	}

	/**
	 * Get the string value for a given variable name.
	 * 
	 * @param name the variable name
	 * @return the project variable value if set
	 * @throws IllegalArgumentException if the project variable with the given
	 *             name is not set
	 */
	public String getString(String name) {
		String value = getString(name, null);
		if (value == null) {
			throw new IllegalArgumentException("Project variable " + name + " is not set.");
		}
		return value;
	}

}
