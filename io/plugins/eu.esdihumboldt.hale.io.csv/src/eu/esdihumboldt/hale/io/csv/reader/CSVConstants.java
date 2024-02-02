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

package eu.esdihumboldt.hale.io.csv.reader;

/**
 * Constants for the CSV Classes
 * 
 * @author Kevin Mais
 */
public interface CSVConstants {

	/**
	 * Name of the parameter specifying the separating sign
	 */
	public static final String PARAM_SEPARATOR = "separator";

	/**
	 * Name of the parameter specifying the quote sing
	 */
	public static final String PARAM_QUOTE = "quote";

	/**
	 * Name of the parameter specifying the escape sign
	 */
	public static final String PARAM_ESCAPE = "escape";

	/**
	 * Name of the parameter specifying the decimal divisor
	 */
	public static final String PARAM_DECIMAL = "decimal";

	/**
	 * The separating sign for the CSV file to be read (can be '\t' or ',' or '
	 * ')
	 */
	public static final char DEFAULT_SEPARATOR = '\t';

	/**
	 * The quote sign for the CSV file to be read
	 */
	public static final char DEFAULT_QUOTE = '\"';

	/**
	 * The escape sign for the CSV file to be read
	 */
	public static final char DEFAULT_ESCAPE = '\\';

	/**
	 * The decimal sign for the CSV file to be read
	 */
	public static final char DEFAULT_DECIMAL = '.';

	/**
	 * Parameter for the reader specifying how values imported from Date cells
	 * should be formatted.
	 */
	public static final String PARAMETER_DATE_FORMAT = "dateTimeFormatterDefault";

}
