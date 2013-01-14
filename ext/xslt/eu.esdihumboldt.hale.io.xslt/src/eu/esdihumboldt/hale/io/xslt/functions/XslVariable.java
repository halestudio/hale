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

package eu.esdihumboldt.hale.io.xslt.functions;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * A function variable for XSL functions. Additionally to the entity it holds
 * the XPath expression through that the variable can be accessed in the current
 * context.
 * 
 * @author Simon Templer
 */
public interface XslVariable {

	/*
	 * NOTE: EntityDefinition instead if Entity is used here, as in the
	 * transformation tree/graph only the EntityDefinition is stored.
	 */
	/**
	 * Get the associated entity as specified in the associated {@link Cell}.
	 * 
	 * @return the variable entity
	 */
	public EntityDefinition getEntity();

	/**
	 * Get the XPath expression that can be used to access the variable in the
	 * current context.
	 * 
	 * @return the XPath expression to referencing the variable
	 */
	public String getXPath();

}
