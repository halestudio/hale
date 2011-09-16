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

package eu.esdihumboldt.hale.ui.io.util;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Stores current {@link IProgressMonitor}s used in a thread. Allows subtasking
 * w/o knowing of the parent task.
 * @author Simon Templer
 */
public class ThreadProgressMonitor {
	
	private static final ThreadLocal<LinkedList<IProgressMonitor>> threadMonitors = new ThreadLocal<LinkedList<IProgressMonitor>>();

	/**
	 * Get the monitor currently associated with the thread.
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
	 * @param monitor the progress monitor to remove
	 */
	public static void remove(IProgressMonitor monitor) {
		LinkedList<IProgressMonitor> mons = threadMonitors.get();
		if (mons == null || mons.isEmpty()) {
			return;
		}
		
		if (mons.contains(monitor)) {
			while (!mons.isEmpty() && !mons.getLast().equals(monitor)) {
				// remove all monitors that have been added after the given monitor (and should have been removed)
				mons.removeLast();
			}
		
			// remove given monitor
			mons.removeLast();
		}
	}

	/**
	 * Run the given operation in a forked thread with a progress monitor dialog 
	 * or in the current thread with a sub progress monitor if possible.
	 * @param op the operation to execute
	 * @param isCancelable if the operation can be canceled
	 * @throws Exception if any error occurs executing the operation
	 */
	public static void runWithProgressDialog(final IRunnableWithProgress op,
			final boolean isCancelable) throws Exception {
		IProgressMonitor pm = getCurrent();
		if (pm == null) {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			final AtomicReference<Exception> error = new AtomicReference<Exception>();
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						new ProgressMonitorDialog(display.getActiveShell()).run(true, 
					    		isCancelable, op);
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
			op.run(new SubProgressMonitor(pm, 0, 
					SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
		}
	}

}
