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

/**
 * Selection tracker utilities
 * 
 * @author Simon Templer
 */
public abstract class SelectionTrackerUtil {

	private static volatile SelectionTracker tracker = null;

	/**
	 * Get the selection tracker previously registered using
	 * {@link #registerTracker(SelectionTracker)}
	 * 
	 * @return the registered tracker or <code>null</code>
	 */
	public static SelectionTracker getTracker() {
		return tracker;
	}

	/**
	 * Register a selection tracker that can be retrieved using
	 * {@link #getTracker()}
	 * 
	 * @param tracker the tracker to register
	 */
	public static void registerTracker(SelectionTracker tracker) {
		SelectionTrackerUtil.tracker = tracker;
	}

}
