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
package eu.esdihumboldt.hale.ui.selection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

import eu.esdihumboldt.hale.ui.selection.impl.DefaultSchemaSelection;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTracker;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTrackerUtil;

/**
 * Helper class to get current {@link SchemaSelection}.
 * 
 * @author unkown
 * @author Simon Templer
 */
public abstract class SchemaSelectionHelper {

	private static final ALogger log = ALoggerFactory.getLogger(SchemaSelectionHelper.class);

	/**
	 * Get the current schema selection
	 * 
	 * @return the current schema selection or an empty model selection if it
	 *         can't be determined
	 */
	public static SchemaSelection getSchemaSelection() {
		SchemaSelection result = null;

		try {
			ISelectionService ss = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getSelectionService();

			ISelection selection = ss.getSelection();
//			ISelection selection = ss.getSelection(ModelNavigationView.ID); //XXX this is bad!

			if (selection instanceof SchemaSelection) {
				result = (SchemaSelection) selection;
			}
		} catch (Throwable e) {
			log.warn("Could not get current selection", e);
		}

		if (result == null) {
			SelectionTracker tracker = SelectionTrackerUtil.getTracker();
			if (tracker != null) {
				result = tracker.getSelection(SchemaSelection.class);
			}
		}

		if (result == null) {
			result = new DefaultSchemaSelection();
		}

		return result;
	}

}
