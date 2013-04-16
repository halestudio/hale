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

import com.google.common.collect.ListMultimap;

/**
 * Mutable {@link Cell} which is used where changes to the cell are allowed.
 * 
 * @author Simon Templer
 */
public interface MutableCell extends Cell {

	/**
	 * Set the identifier for the transformation referenced by the cell.
	 * 
	 * @param transformation the transformation identifier
	 */
	public void setTransformationIdentifier(String transformation);

	/**
	 * @param parameters the parameters to set
	 */
	public void setTransformationParameters(ListMultimap<String, ParameterValue> parameters);

	/**
	 * @param source the source to set
	 */
	public void setSource(ListMultimap<String, ? extends Entity> source);

	/**
	 * @param target the target to set
	 */
	public void setTarget(ListMultimap<String, ? extends Entity> target);

	/**
	 * @param id the id to set
	 */
	public void setId(String id);

	/**
	 * @param priority the {@link Priority priority} to set
	 */
	public void setPriority(Priority priority);

	/**
	 * Set the cell transformation mode. Only applicable for type cells.
	 * 
	 * @param mode the transformation mode to set
	 */
	public void setTransformatioMode(TransformationMode mode);
}
