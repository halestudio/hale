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

package eu.esdihumboldt.hale.models.alignment;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.models.AbstractUpdateService;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.UpdateMessage;

/**
 * Notification handling for {@link AlignmentService}s that support
 * {@link AlignmentServiceListener}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractAlignmentService extends AbstractUpdateService implements
		AlignmentService {
	
	/**
	 * The default update message
	 */
	private static final UpdateMessage<?> DEF_MESSAGE = new UpdateMessage<Object>(AlignmentService.class, null);
	
	/**
	 * @see AbstractUpdateService#notifyListeners(UpdateMessage)
	 * @deprecated an {@link UnsupportedOperationException} will be thrown when
	 *   calling this method, use another event notifier instead
	 */
	@Deprecated
	@Override
	protected void notifyListeners(UpdateMessage<?> message) {
		throw new UnsupportedOperationException(); //notifyAlignmentChanged();
	}

	/**
	 * Call when the alignment has been cleared
	 */
	protected void notifyAlignmentCleared() {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof AlignmentServiceListener) {
				((AlignmentServiceListener) listener).alignmentCleared();
			}
			
			listener.update(DEF_MESSAGE);
		}
	}
	
	/**
	 * Call when the whole alignment has changed and calling lesser events
	 *   is not feasible
	 */
	/*protected void notifyAlignmentChanged() {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof AlignmentServiceListener) {
				((AlignmentServiceListener) listener).alignmentChanged();
			}
			
			listener.update(DEF_MESSAGE);
		}
	}*/
	
	/**
	 * Call when cells have been added
	 * 
	 * @param cells the cells that have been added
	 */
	protected void notifyCellsAdded(Iterable<ICell> cells) {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof AlignmentServiceListener) {
				((AlignmentServiceListener) listener).cellsAdded(cells);
			}
			
			listener.update(DEF_MESSAGE);
		}
	}
	
	/**
	 * Call when existing cells have been updated
	 * 
	 * @param cells the cells that have been updated
	 */
	protected void notifyCellsUpdated(Iterable<ICell> cells) {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof AlignmentServiceListener) {
				((AlignmentServiceListener) listener).cellsUpdated(cells);
			}
			
			listener.update(DEF_MESSAGE);
		}
	}
	
	/**
	 * Call when an existing cell has been removed
	 * 
	 * @param cell the cell that has been removed
	 */
	protected void notifyCellRemoved(ICell cell) {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof AlignmentServiceListener) {
				((AlignmentServiceListener) listener).cellRemoved(cell);
			}
			
			listener.update(DEF_MESSAGE);
		}
	}
	
}
