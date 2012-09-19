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

package eu.esdihumboldt.hale.common.align.model;

/**
 * References a type or property and represents it in a mapping cell.
 * 
 * @author Simon Templer
 */
public interface Entity {

	/**
	 * Get the entity definition.
	 * 
	 * @return the entity definition
	 */
	public EntityDefinition getDefinition();

	// TODO common filter/restriction stuff

}
