/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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

import eu.esdihumboldt.hale.ui.application.internal.Messages;
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
public class ApplicationWorkbenchWindowAdvisor 
	extends WorkbenchWindowAdvisor {

	/**
	 * @see WorkbenchWindowAdvisor#WorkbenchWindowAdvisor(IWorkbenchWindowConfigurer)
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	/**
	 * @see WorkbenchWindowAdvisor#preWindowOpen()
	 */
	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1280,1024));
		configurer.setTitle(Messages.ApplicationWorkbenchWindowAdvisor_0 +  //$NON-NLS-1$
				Version.parseVersion(Display.getAppVersion()));
		configurer.setShowCoolBar(true); // this reserves space for action bars on top.
		configurer.setShowPerspectiveBar(true); // this reserves space for the selection of perspectives.
        configurer.setShowMenuBar(true);
        configurer.setShowProgressIndicator(true);
        
        // show curved view tabs
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
	
		// enable heap status item
		PrefUtil.getAPIPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR, true);
		
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
			tracker = new SelectionTrackerImpl(getWindowConfigurer().getWindow().getSelectionService());
			SelectionTrackerUtil.registerTracker(tracker);
		}

		// XXX do the following somewhere else:
		// start instance validation service
		PlatformUI.getWorkbench().getService(InstanceValidationService.class);
	}

	/**
	 * @see WorkbenchWindowAdvisor#createActionBarAdvisor(IActionBarConfigurer)
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			final IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}
	
}
