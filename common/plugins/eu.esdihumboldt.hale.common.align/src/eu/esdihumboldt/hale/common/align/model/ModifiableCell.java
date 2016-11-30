/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model;

/**
 * Interface for cell that may be adapted through cell modifiers.
 * 
 * @author Simon Templer
 */
public interface ModifiableCell extends Cell {

	/**
	 * @param cell the cell to disable/enable this cell for
	 * @param disabled whether the cell should be disabled or not
	 */
	public default void setDisabledFor(Cell cell, boolean disabled) {
		setDisabledFor(cell.getId(), disabled);
	}

	/**
	 * @param cellId the ID of the cell to disable/enable this cell for
	 * @param disabled whether the cell should be disabled or not
	 */
	public void setDisabledFor(String cellId, boolean disabled);

	/**
	 * Set the cell transformation mode. Only applicable for type cells.
	 * 
	 * @param mode the transformation mode to set
	 */
	public void setTransformationMode(TransformationMode mode);

}
