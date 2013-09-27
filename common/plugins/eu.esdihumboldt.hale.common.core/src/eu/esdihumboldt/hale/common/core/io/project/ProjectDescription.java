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

package eu.esdihumboldt.hale.common.core.io.project;

/**
 * Basic project description that may be edited by the user.
 * 
 * @author Simon Templer
 * @since 2.7
 */
public interface ProjectDescription {

	/**
	 * Get the project name
	 * 
	 * @return the project name, may be <code>null</code> if not set
	 */
	public String getName();

	/**
	 * @return the author
	 */
	public String getAuthor();

	/**
	 * @return the description
	 */
	public String getDescription();

}
