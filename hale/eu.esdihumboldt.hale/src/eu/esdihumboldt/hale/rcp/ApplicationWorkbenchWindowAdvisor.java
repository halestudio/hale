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
package eu.esdihumboldt.hale.rcp;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.util.PrefUtil;

import eu.esdihumboldt.hale.Messages;

/**
 * This is the base class for configuring the workbench window in which the 
 * {@link IPerspectiveFactory}s reside.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
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
		configurer.setTitle(Messages.getString("ApplicationWorkbenchWindowAdvisor.0") +  //$NON-NLS-1$
				HALEActivator.getDefault().getBundle().getVersion().toString());
		configurer.setShowCoolBar(true); // this reserves space for action bars on top.
		configurer.setShowPerspectiveBar(true); // this reserves space for the selection of perspectives.
        configurer.setShowMenuBar(true);
        
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
	 * @see WorkbenchWindowAdvisor#createActionBarAdvisor(IActionBarConfigurer)
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			final IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer) {
			/**
			 * @see ActionBarAdvisor#fillMenuBar(IMenuManager)
			 */
        	@Override
			protected void fillMenuBar(final IMenuManager menuBar) {
				super.fillMenuBar(menuBar);

				menuBar.add(new GroupMarker(
						IWorkbenchActionConstants.MB_ADDITIONS));

				IContributionItem item = ContributionItemFactory.VIEWS_SHORTLIST
						.create(configurer.getWindowConfigurer().getWindow());
				
				IMenuManager windowMenu = new MenuManager(Messages.getString("ApplicationWorkbenchWindowAdvisor.1"), Messages.getString("ApplicationWorkbenchWindowAdvisor.2")); //$NON-NLS-1$ //$NON-NLS-2$
				IMenuManager viewMenu = new MenuManager(Messages.getString("ApplicationWorkbenchWindowAdvisor.3")); //$NON-NLS-1$
				windowMenu.add(viewMenu);
				viewMenu.add(item);
				
				//XXX
				IWorkbenchAction perspectiveAction = ActionFactory.SAVE_PERSPECTIVE.create(configurer.getWindowConfigurer().getWindow());
				IContributionItem perspectiveItem = new ActionContributionItem(perspectiveAction);
				windowMenu.add(perspectiveItem);

				menuBar.add(windowMenu);
			}
		};
	}
	
}
