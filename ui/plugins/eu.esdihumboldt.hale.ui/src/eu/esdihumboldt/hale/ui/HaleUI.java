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

package eu.esdihumboldt.hale.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.operations.UndoRedoActionGroup;

/**
 * Hale UI utility methods.
 * 
 * @author Simon Templer
 */
public abstract class HaleUI {

	/**
	 * Register a view site for undo/redo in workbench context.
	 * 
	 * @param site the view site
	 */
	public static void registerWorkbenchUndoRedo(IViewSite site) {
		IUndoContext undoContext = site.getWorkbenchWindow().getWorkbench().getOperationSupport()
				.getUndoContext();
		UndoRedoActionGroup undoRedoActionGroup = new UndoRedoActionGroup(site, undoContext, true);
		IActionBars actionBars = site.getActionBars();
		undoRedoActionGroup.fillActionBars(actionBars);
	}

	/**
	 * Wait for a finished flag being set to <code>true</code> by another
	 * thread. If the current thread is the display thread, display events will
	 * still be processed.
	 * 
	 * @param finishedFlag the finished flag
	 */
	public static void waitFor(AtomicBoolean finishedFlag) {
		if (Display.getCurrent() != null) {
			// current thread is a display thread
			Display display = Display.getCurrent();
			while (!finishedFlag.get()) {
				if (!display.readAndDispatch()) { // handle events
					display.sleep();
				}
			}
		}
		else {
			// some other thread
			while (!finishedFlag.get()) {
				try {
					Thread.sleep(100); // sleep for 100 milliseconds
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}

}
