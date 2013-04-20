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

package eu.esdihumboldt.hale.app.bgis.ade.duplicate


/**
 * Execution context for the {@link GenerateFromExamplesApplication}.
 * 
 * @author Simon Templer
 */
class GenerateDuplicatesContext {

	/**
	 * URI pointing to the mapping project containing the example mappings.
	 */
	URI project

	/**
	 * The target file to write the mapping to.
	 */
	File out
}
