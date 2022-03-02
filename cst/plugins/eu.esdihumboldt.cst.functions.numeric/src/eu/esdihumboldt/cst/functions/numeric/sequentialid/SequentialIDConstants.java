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
	 */
	public static final String PARAM_START_VALUE = "startValue";

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
