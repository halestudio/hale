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

package eu.esdihumboldt.cst.functions.numeric.sequentialid;

/**
 * Constants for the sequential identifier function.
 * 
 * @author Simon Templer
 */
public interface SequentialIDConstants {

	/**
	 * The function ID.
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.numeric.sequentialid";

	/**
	 * Name of the prefix parameter.
	 */
	public static final String PARAM_PREFIX = "prefix";

	/**
	 * Name of the suffix parameter.
	 */
	public static final String PARAM_SUFFIX = "suffix";

	/**
	 * Name of the sequence parameter.
	 */
	public static final String PARAM_SEQUENCE = "sequence";

	/**
	 * The start value for sequences.
	 * 
	 * TODO make this configurable?
	 */
	public static final int START_VALUE = 1;

	/**
	 * Possible sequence types for the {@value #PARAM_SEQUENCE} parameter.
	 */
	public static enum Sequence {
		/**
		 * Sequence over all instances.
		 */
		overall,
		/**
		 * Sequence over all instances of the same type.
		 */
		type
	}

}
