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
package eu.esdihumboldt.hale.ui.application;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.util.PrefUtil;
import org.osgi.framework.Version;

import eu.esdihumboldt.hale.ui.launchaction.LaunchAction;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTracker;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTrackerImpl;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTrackerUtil;

/**
 * This is the base class for configuring the workbench window in which the
 * {@link IPerspectiveFactory}s reside.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private LaunchAction action;

	/**
	 * Creates a new workbench window advisor for configuring a workbench window
	 * via the given workbench window configurer.
	 * 
	 * @param configurer an object for configuring the workbench window
	 * @param action the application launch action, may be <code>null</code>
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer,
			LaunchAction action) {
		super(configurer);

		this.action = action;
	}

	/**
	 * @see WorkbenchWindowAdvisor#preWindowOpen()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1280, 1024));

		Version appVersion = Version.parseVersion(Display.getAppVersion());

		// strip RELEASE qualifier
		if ("RELEASE".equalsIgnoreCase(appVersion.getQualifier())) {
			appVersion = new Version(appVersion.getMajor(), appVersion.getMinor(),
					appVersion.getMicro());
		}

		configurer.setTitle(Display.getAppName() + " " + appVersion);
		configurer.setShowCoolBar(true); // this reserves space for action bars
											// on top.
		configurer.setShowPerspectiveBar(true); // this reserves space for the
												// selection of perspectives.
		configurer.setShowMenuBar(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowFastViewBars(true);

		// show curved view tabs
		PlatformUI.getPreferenceStore()
				.setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);

		// enable heap status item
		PrefUtil.getAPIPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR,
				true);

//      IWorkbenchWindow window = getWindowConfigurer().getWindow();
//      if (window instanceof WorkbenchWindow) {
//      	((WorkbenchWindow) window).showHeapStatus(true);
//      }
	}

	/**
	 * @see WorkbenchWindowAdvisor#postWindowOpen()
	 */
	@Override
	public void postWindowOpen() {
		// register selection tracker if none is defined yet
		SelectionTracker tracker = SelectionTrackerUtil.getTracker();
		if (tracker == null) {
			// create tracker listening on window selection service
			tracker = new SelectionTrackerImpl(
					getWindowConfigurer().getWindow().getSelectionService());
			SelectionTrackerUtil.registerTracker(tracker);
		}

		// XXX do the following somewhere else:
		// start instance validation service
		PlatformUI.getWorkbench().getService(InstanceValidationService.class);

		if (action != null) {
			action.onOpenWorkbenchWindow();
			// only do this once
			action = null;
		}
	}

	/**
	 * @see WorkbenchWindowAdvisor#createActionBarAdvisor(IActionBarConfigurer)
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

}
