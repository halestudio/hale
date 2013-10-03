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

package eu.esdihumboldt.hale.ui.templates.webtemplates

import groovy.transform.CompileStatic


/**
 * Represents a web template.
 * 
 * @author Simon Templer
 */
@CompileStatic
class WebTemplate {

	/**
	 * The template identifier
	 */
	String id;

	/**
	 * The display name
	 */
	String name;

	/**
	 * The project location
	 */
	URI project

	/**
	 * The website location 
	 */
	URI site
}
