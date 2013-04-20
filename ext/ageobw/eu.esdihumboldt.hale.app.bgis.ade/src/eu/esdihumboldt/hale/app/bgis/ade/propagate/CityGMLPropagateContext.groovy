/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.propagate


/**
 * Execution context for the {@link CityGMLPropagateApplication}.
 * 
 * @author Simon Templer
 */
class CityGMLPropagateContext {

	/**
	 * URI pointing to the mapping project containing the example mappings.
	 */
	URI project

	/**
	 * URI pointing to the CityGML source schema the generated mapping should use.
	 */
	URI sourceSchema

	/**
	 * URI pointing to the feature mapping table.
	 */
	URI config

	/**
	 * The target file to write the mapping to.
	 */
	File out
}
