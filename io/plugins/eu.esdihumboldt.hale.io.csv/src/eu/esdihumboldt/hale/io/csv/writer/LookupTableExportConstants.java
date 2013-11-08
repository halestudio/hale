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

package eu.esdihumboldt.hale.io.csv.writer;

/**
 * Constant parameters for the lookup table export
 * 
 * @author Patrick Lieb
 */
public interface LookupTableExportConstants {

	/**
	 * Parameter for first column (source, respectively keys of lookup table)
	 */
	public static final String PARAM_SOURCE_COLUMN = "SOURCE_COLUMN";

	/**
	 * Parameter for second column (target, respectively values of lookup table)
	 */
	public static final String PARAM_TARGET_COLUMN = "TARGET_COLUMN";

	/**
	 * Name of the parameter specifying the reader setting
	 */
	public static final String PARAM_SKIP_FIRST_LINE = "skip";

	/**
	 * Name of the parameter specifiying the columns to match
	 */
	public static final String LOOKUP_KEY_COLUMN = "keyColumn";

	/**
	 * Name of the parameter specifiying the columns to match
	 */
	public static final String LOOKUP_VALUE_COLUMN = "valueColumn";

}
