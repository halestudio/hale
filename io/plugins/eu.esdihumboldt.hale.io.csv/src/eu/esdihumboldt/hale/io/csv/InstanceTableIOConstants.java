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

package eu.esdihumboldt.hale.io.csv;

/**
 * Constants for IO of table instance files (csv and xls)
 * 
 * @author Patrick Lieb Lieb
 */
public class InstanceTableIOConstants {

	/**
	 * Parameter for IO Provider to decide whether nested properties should be
	 * solved or not
	 */
	public static final String SOLVE_NESTED_PROPERTIES = "solveNestedProperties";

	/**
	 * Parameter for IO Provider to decide whether transformed data are written
	 * following the order of columns present in the source schema
	 */
	public static final String USE_SCHEMA = "useSchema";

	/**
	 * Parameter for XLS import Provider to decide which Excel sheet should be
	 * read. Sheet indices are 0-based.
	 */
	public static final String SHEET_INDEX = "sheetIndex";

	/**
	 * Parameter for XLS/CSV Export
	 */
	public static final String EXPORT_TYPE = "selectedExportType";

	/**
	 * Parameter for exporting empty feature types to XLS Export
	 */
	public static final String EXPORT_IGNORE_EMPTY_FEATURETYPES = "ignoreEmptyFeaturetypes";

}
