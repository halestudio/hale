/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.service.align.internal;

import de.cs3d.util.eclipse.TypeSafeListenerList;
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
public abstract class AbstractAlignmentService implements
		AlignmentService {
	
	private final TypeSafeListenerList<AlignmentServiceListener> listeners = 
			new TypeSafeListenerList<AlignmentServiceListener>();
	
	/**
	 * Adds a listener to the service
	 * @param listener the listener to add
	 */
	@Override
	public void addListener(AlignmentServiceListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a listener to the service
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
	 * @param cells the cells that have been added
	 */
	protected void notifyCellsAdded(Iterable<Cell> cells) {
		for (AlignmentServiceListener listener : listeners) {
			listener.cellsAdded(cells);
		}
	}
	
	/**
	 * Call when existing cells have been updated
	 * @param cells the cells that have been updated
	 */
	protected void notifyCellsUpdated(Iterable<Cell> cells) {
		for (AlignmentServiceListener listener : listeners) {
			listener.cellsUpdated(cells);
		}
	}
	
	/**
	 * Call when an existing cell has been removed
	 * @param cell the cell that has been removed
	 */
	protected void notifyCellRemoved(Cell cell) {
		for (AlignmentServiceListener listener : listeners) {
			listener.cellRemoved(cell);
		}
	}
	
}
