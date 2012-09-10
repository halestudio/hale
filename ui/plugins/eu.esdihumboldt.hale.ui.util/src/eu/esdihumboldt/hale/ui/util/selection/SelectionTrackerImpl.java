/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.util.selection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Default selection tracker
 * 
 * @author Simon Templer
 */
public class SelectionTrackerImpl implements SelectionTracker {

	/**
	 * The set of concrete selection types currently stored
	 */
	private final Set<Class<? extends ISelection>> storedTypes = new HashSet<Class<? extends ISelection>>();

	/**
	 * The list of the last selection, the latest selection is at index 0
	 */
	private final LinkedList<ISelection> lastSelections = new LinkedList<ISelection>();

	/**
	 * Creates a tracker based on a selection service
	 * 
	 * @param selectionService the selection service to track
	 */
	public SelectionTrackerImpl(ISelectionService selectionService) {
		super();

		// TODO use PostSelectionListener instead?
		selectionService.addSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				addSelection(selection);
			}
		});
	}

	/**
	 * Add the given selection
	 * 
	 * @param selection the new selection
	 */
	protected void addSelection(ISelection selection) {
		Class<? extends ISelection> type = selection.getClass();
		synchronized (this) {
			if (storedTypes.contains(type)) {
				// remove old selection of this concrete type
				Iterator<ISelection> it = lastSelections.iterator();
				while (it.hasNext() && !it.next().getClass().equals(type)) {
					// do nothing
				}
				it.remove();
			}
			else {
				storedTypes.add(type);
			}

			// add selection
			lastSelections.addFirst(selection);
		}
	}

	/**
	 * @see SelectionTracker#getSelection(Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ISelection> T getSelection(Class<T> selectionType) {
		synchronized (this) {
			for (ISelection selection : lastSelections) {
				// return the first selection found (which is the most recent)
				// that can be assigned to the given selection type
				if (selectionType.isAssignableFrom(selection.getClass())) {
					return (T) selection;
				}
			}
		}

		// none found
		return null;
	}

}
