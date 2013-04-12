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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.string;

/**
 * RegexAnalysis constants.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public interface RegexAnalysisFunction {

	/**
	 * the function Id
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.string.regexanalysisfunction";

	/**
	 * Name of the parameter specifying the regex pattern to apply.<br>
	 */
	public static final String PARAMETER_REGEX_PATTERN = "regexPattern";

	/**
	 * Name of the parameter specifying the output format to apply.<br>
	 */
	public static final String PARAMETER_OUTPUT_FORMAT = "outputFormat";
}
