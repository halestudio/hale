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

package eu.esdihumboldt.hale.server.model

import eu.esdihumboldt.util.blueprints.entities.VertexEntity


/**
 * Represents a project template.
 * 
 * @author Simon Templer
 */
@VertexEntity('template')
class Template extends Project {
	/**
	 * Reference to the template as present in the file system.
	 */
	String templateId

	/**
	 * States if the template is valid an thus can be offered for download. 
	 */
	boolean valid

	/**
	 * The last update of the template.
	 */
	Date lastUpdate

	/**
	 * The data of the initial creation of database representation.
	 */
	Date created

	/**
	 * Number of hits on the project file.
	 */
	int hits = 0

	/**
	 * Number of archive downloads.
	 */
	int downloads = 0
}
