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

import java.util.Date;

import org.osgi.framework.Version;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * General information on a project
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface ProjectInfo extends ProjectDescription {

	/**
	 * @return the haleVersion
	 */
	public Version getHaleVersion();

	/**
	 * @return the created
	 */
	public Date getCreated();

	/**
	 * @return the modified
	 */
	public Date getModified();

	/**
	 * Get the property with the given name.
	 * 
	 * @param name the property name
	 * @return the property value, may be a null value but not <code>null</code>
	 */
	public Value getProperty(String name);

}
