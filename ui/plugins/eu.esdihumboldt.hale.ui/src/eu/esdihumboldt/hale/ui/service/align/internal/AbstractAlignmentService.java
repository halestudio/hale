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

package eu.esdihumboldt.hale.ui.service.align.internal;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * Notification handling for {@link AlignmentService}s that support
 * {@link AlignmentServiceListener}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractAlignmentService implements AlignmentService {

	private final CopyOnWriteArraySet<AlignmentServiceListener> listeners = new CopyOnWriteArraySet<AlignmentServiceListener>();

	/**
	 * Adds a listener to the service
	 * 
	 * @param listener the listener to add
	 */
	@Override
	public void addListener(AlignmentServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a listener to the service
	 * 
	 * @param listener the listener to remove
	 */
	@Override
	public void removeListener(AlignmentServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Call when the alignment has been cleared
	 */
	protected void notifyAlignmentCleared() {
		for (AlignmentServiceListener listener : listeners) {
			listener.alignmentCleared();
		}
	}

	/**
	 * Call when cells have been added
	 * 
	 * @param cells the cells that have been added
	 */
	protected void notifyCellsAdded(Iterable<Cell> cells) {
		for (AlignmentServiceListener listener : listeners) {
			listener.cellsAdded(cells);
		}
	}

	/**
	 * Call when an existing cell has been replaced by another.
	 * 
	 * @param oldCell the old cell that has been replaced
	 * @param newCell the new cell that has replaced the other
	 */
	protected void notifyCellReplaced(Cell oldCell, Cell newCell) {
		Map<Cell, Cell> cells = Collections.singletonMap(oldCell, newCell);
		for (AlignmentServiceListener listener : listeners) {
			listener.cellsReplaced(cells);
		}
	}

	/**
	 * Call when several existing cell has been replaced by others.<br>
	 * This method will take care of the map so that listeners cannot change it.
	 * 
	 * @param cells a mapping from replaced cell to new cell
	 */
	protected void notifyCellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
		cells = Collections.unmodifiableMap(cells);
		for (AlignmentServiceListener listener : listeners) {
			listener.cellsReplaced(cells);
		}
	}

	/**
	 * Call when existing cells have been removed
	 * 
	 * @param cells the cells that have been removed
	 */
	protected void notifyCellsRemoved(Iterable<Cell> cells) {
		for (AlignmentServiceListener listener : listeners) {
			listener.cellsRemoved(cells);
		}
	}

	/**
	 * Call when cells are modified.
	 * 
	 * @param cells the cells that have been modified.
	 * @param propertyName the name of the property that changed.
	 */
	protected void notifyCellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
		for (AlignmentServiceListener listener : listeners) {
			listener.cellsPropertyChanged(cells, propertyName);
		}
	}

	/**
	 * Call when the custom function definitions have changed
	 * (added/removed/replaced).
	 */
	protected void notifyCustomFunctionsChanged() {
		for (AlignmentServiceListener listener : listeners) {
			listener.customFunctionsChanged();
		}
	}

	/**
	 * Call when the alignment had some unspecified update.
	 */
	protected void notifyAlignmentChanged() {
		for (AlignmentServiceListener listener : listeners) {
			listener.alignmentChanged();
		}
	}

}
