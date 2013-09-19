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
 * Holds basic information on a mapping project.
 * 
 * @author Simon Templer
 */
@VertexEntity('project')
class Project {
	/**
	 * The project name.
	 */
	String name

	/**
	 * The project description.
	 */
	String description

	/**
	 * The project's owner.
	 */
	User owner
}
