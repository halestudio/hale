/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.collector;

/**
 * Constants for assign references from collector function
 * 
 * @author Florian Esser
 */
public interface AssignFromCollectorFunction {

	/**
	 * the function Id
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.collector.assign";

	/**
	 * Name of the parameter specifying the name of the collector to assign
	 * values from.
	 */
	public static final String PARAMETER_COLLECTOR = "collector";

}
