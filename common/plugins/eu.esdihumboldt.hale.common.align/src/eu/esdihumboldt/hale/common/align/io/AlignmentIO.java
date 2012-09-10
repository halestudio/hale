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

package eu.esdihumboldt.hale.common.align.io;

/**
 * Alignment I/O utilities and constants.
 * 
 * @author Simon Templer
 * @since 2.5
 */
public abstract class AlignmentIO {

	/**
	 * ID of the action to load an alignment. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_LOAD_ALIGNMENT = "eu.esdihumboldt.hale.io.align.read";

	/**
	 * The name of the project file containing the alignment. Reflects the file
	 * name registered in the extension.
	 */
	public static final String PROJECT_FILE_ALIGNMENT = "alignment.xml";

}
