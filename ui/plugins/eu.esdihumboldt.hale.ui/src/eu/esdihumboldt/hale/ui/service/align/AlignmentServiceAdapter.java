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

package eu.esdihumboldt.hale.ui.service.align;

import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.Cell;

/**
 * Adapter for alignment service listeners
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AlignmentServiceAdapter implements AlignmentServiceListener {

	/**
	 * @see AlignmentServiceListener#alignmentCleared()
	 */
	@Override
	public void alignmentCleared() {
		// override me
	}

	/**
	 * @see AlignmentServiceListener#cellsRemoved(Iterable)
	 */
	@Override
	public void cellsRemoved(Iterable<Cell> cells) {
		// override me
	}

	/**
	 * @see AlignmentServiceListener#cellsReplaced(Map)
	 */
	@Override
	public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
		// override me
	}

	/**
	 * @see AlignmentServiceListener#cellsAdded(Iterable)
	 */
	@Override
	public void cellsAdded(Iterable<Cell> cells) {
		// override me
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellsPropertyChanged(java.lang.Iterable,
	 *      java.lang.String)
	 */
	@Override
	public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
		// override me
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#alignmentChanged()
	 */
	@Override
	public void alignmentChanged() {
		// override me
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#customFunctionsChanged()
	 */
	@Override
	public void customFunctionsChanged() {
		// override me
	}

}
