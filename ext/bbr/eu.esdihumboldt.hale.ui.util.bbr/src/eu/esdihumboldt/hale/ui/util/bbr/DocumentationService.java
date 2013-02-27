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

package eu.esdihumboldt.hale.ui.util.bbr;

import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * BBR documentation service.
 * 
 * @author Simon Templer
 */
public interface DocumentationService {

	/**
	 * Get the documentation on a schema element.
	 * 
	 * @param def the schema element definition
	 * @return the associated documentation, <code>null</code> if there is none
	 */
	public Documentation getDocumentation(Definition<?> def);

}
