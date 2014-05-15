/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.server.webtransform.war.pages;

import java.io.Serializable;

import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;

/**
 * Named I/O configuration.
 * 
 * @author Simon Templer
 */
public class NamedIOConfiguration implements Serializable {

	private static final long serialVersionUID = -844360211151734453L;

	private final String name;

	private final IOConfiguration config;

	/**
	 * Creates a new named I/O configuration.
	 * 
	 * @param name the name
	 * @param config the I/O configuration
	 */
	public NamedIOConfiguration(String name, IOConfiguration config) {
		super();
		this.name = name;
		this.config = config;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the I/O configuration
	 */
	public IOConfiguration getConfig() {
		return config;
	}

}
