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
