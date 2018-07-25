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

package eu.esdihumboldt.hale.ui.util.io;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.util.io.internal.StatesIfDoneProgressMonitor;

/**
 * Stores current {@link IProgressMonitor}s used in a thread. Allows subtasking
 * w/o knowing of the parent task.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("deprecation")
public class ThreadProgressMonitor {

	private static final ThreadLocal<LinkedList<IProgressMonitor>> threadMonitors = new ThreadLocal<LinkedList<IProgressMonitor>>();

	/**
	 * Get the monitor currently associated with the thread.
	 * 
	 * @return the progress monitor or <code>null</code>
	 */
	public static IProgressMonitor getCurrent() {
		LinkedList<IProgressMonitor> mons = threadMonitors.get();
		if (mons == null || mons.isEmpty()) {
			return null;
		}
		return mons.getLast();
	}

	/**
	 * Register a progress monitor with the current thread. It must be removed
	 * using {@link #remove(IProgressMonitor)}.
	 * 
	 * @param monitor the progress monitor
	 */
	public static void register(IProgressMonitor monitor) {
		LinkedList<IProgressMonitor> mons = threadMonitors.get();
		if (mons == null) {
			mons = new LinkedList<IProgressMonitor>();
			threadMonitors.set(mons);
		}
		mons.add(monitor);
	}

	/**
	 * Remove a progress monitor that was previously registered. Also removes
	 * monitors that have been added after the given one.
	 * 
	 * @param monitor the progress monitor to remove
	 */
	public static void remove(IProgressMonitor monitor) {
		LinkedList<IProgressMonitor> mons = threadMonitors.get();
		if (mons == null || mons.isEmpty()) {
			return;
		}

		if (mons.contains(monitor)) {
			while (!mons.isEmpty() && !mons.getLast().equals(monitor)) {
				// remove all monitors that have been added after the given
				// monitor (and should have been removed)
				mons.removeLast();
			}

			// remove given monitor
			mons.removeLast();
		}
	}

	/**
	 * Run the given operation in a forked thread with a progress monitor dialog
	 * or in the current thread with a sub progress monitor if possible.
	 * 
	 * @param op the operation to execute
	 * @param isCancelable if the operation can be canceled
	 * @throws Exception if any error occurs executing the operation
	 */
	public static void runWithProgressDialog(final IRunnableWithProgress op,
			final boolean isCancelable) throws Exception {
		IProgressMonitor pm = getCurrent();
		if (pm == null) {
			// no current progress monitor associated to thread, so
			// in display thread, launch a new progress monitor dialog
			final Display display = PlatformUI.getWorkbench().getDisplay();
			final AtomicReference<Exception> error = new AtomicReference<Exception>();
			final IRunnableWithProgress progressOp = new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					// create a custom progress monitor to be able to decide
					// whether the progress is done
					StatesIfDoneProgressMonitor cpm = new StatesIfDoneProgressMonitor(monitor);
					// register the progress monitor
					register(cpm);
					try {
						op.run(cpm);
					} finally {
						// deregister the progress monitor
						remove(cpm);
					}
				}
			};
			display.syncExec(new Runnable() {

				@Override
				public void run() {
					try {
						new ProgressMonitorDialog(display.getActiveShell()).run(true, isCancelable,
								progressOp);
					} catch (Exception e) {
						error.set(e);
					}
				}
			});
			if (error.get() != null) {
				throw error.get();
			}
		}
		else {
			// progress monitor associated to this thread, so
			// run the operation in the same thread
			boolean useOriginalMonitor = false;
			if (pm instanceof StatesIfDoneProgressMonitor) {
				useOriginalMonitor = ((StatesIfDoneProgressMonitor) pm).isDone();
			}

			if (useOriginalMonitor) {
				// use the original monitor
				pm.subTask(""); // reset subtask name
				op.run(pm);
			}
			else {
				// use a sub progress monitor
				IProgressMonitor sm = new StatesIfDoneProgressMonitor(new SubProgressMonitor(pm, 0,
						SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
				register(sm);
				try {
					op.run(sm);
				} finally {
					remove(sm);
				}
			}
		}
	}

}
