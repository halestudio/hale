/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.csv.reader.internal;

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
	 * The separating sign for the CSV file to be read (can be '\t' or ',' or
	 * ' ')
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
	 * Name of the parameter specifying the type name
	 */
	public static String PARAM_TYPENAME = "typename";

	/**
	 * Name of the parameter specifying the geometry/coordinate system
	 */
	public static String PARAM_GEOMETRY = "geometry";

}
