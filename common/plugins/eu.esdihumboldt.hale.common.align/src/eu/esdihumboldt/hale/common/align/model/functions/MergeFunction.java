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

package eu.esdihumboldt.hale.common.align.model.functions;

/**
 * Merge function constants.
 * 
 * @author Simon Templer
 */
public interface MergeFunction {

	/**
	 * Name of the parameter specifying a property path (key property)
	 */
	public static final String PARAMETER_PROPERTY = "property";

	/**
	 * Name of the parameter specifying a property path (no key property)
	 */
	public static final String PARAMETER_ADDITIONAL_PROPERTY = "additional_property";

	/**
	 * Name of the parameter specifying whether auto detection of other equal
	 * properties
	 */
	public static final String PARAMETER_AUTO_DETECT = "auto_detect";

	/**
	 * the merge function Id
	 */
	public static final String ID = "eu.esdihumboldt.hale.align.merge";

}
