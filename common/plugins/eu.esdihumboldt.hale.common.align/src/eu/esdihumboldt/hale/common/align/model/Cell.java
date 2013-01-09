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

import java.util.Set;

import com.google.common.collect.ListMultimap;

/**
 * An alignment cell represents a mapping between two entities
 * 
 * @author Simon Templer
 */
public interface Cell {

	/**
	 * Get the source entities. For each the name is mapped to the entity.
	 * Multiple entities may share the same name. The map may not be modified.
	 * 
	 * @return the source entities, may be <code>null</code>
	 */
	public ListMultimap<String, ? extends Entity> getSource();

	/**
	 * Get the target entities. For each the name is mapped to the entity.
	 * Multiple entities may share the same name. The map may not be modified.
	 * 
	 * @return the target entities
	 */
	public ListMultimap<String, ? extends Entity> getTarget();

	/**
	 * Get the transformation parameters that shall be applied to the
	 * transformation specified by {@link #getTransformationIdentifier()}.
	 * 
	 * @return the transformation parameters, parameter names are mapped to
	 *         parameter values, may be <code>null</code>
	 */
	public ListMultimap<String, ParameterValue> getTransformationParameters();

	/**
	 * Get the identifier for the transformation referenced by the cell.
	 * 
	 * @return the transformation identifier
	 */
	public String getTransformationIdentifier();

	/**
	 * Get the id for identifying the cell.
	 * 
	 * @return the id
	 */
	public String getId();

	/**
	 * Returns the cells this cell is disabled for.
	 * 
	 * @return the cells this cell is disabled for
	 */
	public Set<Cell> getDisabledFor();
}
