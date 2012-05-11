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
 * Assign function constants.
 * 
 * @author Simon Templer
 */
public interface AssignFunction {

	/**
	 * the assign function Id
	 */
	public static final String ID = "eu.esdihumboldt.hale.align.assign";

	/**
	 * Name of the parameter specifying the value to assign. See the function
	 * definition on <code>eu.esdihumboldt.hale.common.align</code>.
	 */
	public static final String PARAMETER_VALUE = "value";
	
	/**
	 * Name of the anchor source entity, that may be associated with the
	 * assignment.
	 */
	public static final String ENTITY_ANCHOR = "anchor";

}
