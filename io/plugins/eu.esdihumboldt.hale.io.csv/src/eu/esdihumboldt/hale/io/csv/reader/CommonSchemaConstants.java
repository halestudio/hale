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

package eu.esdihumboldt.hale.io.csv.reader;

/**
 * Constants for schema IO
 * 
 * @author Patrick Lieb
 */
public interface CommonSchemaConstants {

	/**
	 * the parameter specifying the reader setting
	 */
	public static final String PARAM_SKIP_N_LINES = "skip";

	/**
	 * Name of the parameter specifying the type name
	 */
	public static String PARAM_TYPENAME = "typename";

	/**
	 * Name of the parameter specifying the geometry/coordinate system
	 */
	public static String PARAM_GEOMETRY = "geometry";

}
