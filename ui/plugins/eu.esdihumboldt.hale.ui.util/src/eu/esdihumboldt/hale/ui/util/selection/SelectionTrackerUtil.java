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
