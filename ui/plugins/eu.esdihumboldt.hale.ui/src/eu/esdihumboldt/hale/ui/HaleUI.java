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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.UndoRedoActionGroup;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.service.ServiceManager;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;

/**
 * Hale UI utility methods.
 * 
 * @author Simon Templer
 */
public abstract class HaleUI {

	private static final ServiceProvider uiServiceProvider = new ServiceProvider() {

		private boolean initialized = false;

		/**
		 * Project scope services
		 */
		private final ServiceManager projectScope = new ServiceManager(
				ServiceManager.SCOPE_PROJECT);

		@Override
		public <T> T getService(Class<T> serviceInterface) {
			synchronized (this) {
				if (!initialized) {
					ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
					ps.addListener(new ProjectServiceAdapter() {

						@Override
						public void onClean() {
							projectScope.clear();
						}

					});
				}
				initialized = true;
			}

			// first try project scope
			T service = projectScope.getService(serviceInterface);

			// then platform services
			if (service == null) {
				service = HalePlatform.getService(serviceInterface);
			}

			// then workbench
			if (service == null) {
				service = PlatformUI.getWorkbench().getService(serviceInterface);
			}

			return service;
		}
	};

	/**
	 * Get the service provider for the HALE UI context.
	 * 
	 * @return the service provider instance
	 */
	public static ServiceProvider getServiceProvider() {
		return uiServiceProvider;
	}

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
