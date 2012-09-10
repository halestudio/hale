/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : HALE2
 * 	 
 * Classname    : eu.esdihumboldt.hale.rcp.utils/ModelNavigationViewHelper.java 
 * 
 * Author       : schneidersb
 * 
 * Created on   : Aug 31, 2009 -- 10:50:42 AM
 *
 */
package eu.esdihumboldt.hale.ui.selection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

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
