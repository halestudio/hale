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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.application.internal.Messages;
import eu.esdihumboldt.hale.ui.application.workbench.WorkbenchHook;
import eu.esdihumboldt.hale.ui.application.workbench.extension.WorkbenchHookExtension;
import eu.esdihumboldt.hale.ui.application.workbench.extension.WorkbenchHookFactory;
import eu.esdihumboldt.hale.ui.launchaction.LaunchAction;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.RecentResources;
import eu.esdihumboldt.hale.ui.util.proxy.ProxySettings;

/**
 * The {@link ApplicationWorkbenchAdvisor} controls the appearance of the
 * application (menus, toolbars, perspectives, etc).
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final ALogger _log = ALoggerFactory.getLogger(ApplicationWorkbenchAdvisor.class);

	private static final String PERSPECTIVE_ID = "eu.esdihumboldt.hale.ui.application.perspective.default"; //$NON-NLS-1$

	private final OpenDocumentEventProcessor openDocProcessor;

	private final LaunchAction action;

	private List<WorkbenchHook> hooks;

	/**
	 * Create the application workbench advisor
	 * 
	 * @param openDocProcessor the processor for {@link SWT#OpenDocument} events
	 * @param action the application launch action, may be <code>null</code>
	 */
	public ApplicationWorkbenchAdvisor(OpenDocumentEventProcessor openDocProcessor,
			LaunchAction action) {
		super();

		this.openDocProcessor = openDocProcessor;
		this.action = action;
	}

	/**
	 * @see WorkbenchAdvisor#createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer)
	 */
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer, action);
	}

	/**
	 * @see WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	/**
	 * @see WorkbenchAdvisor#initialize(IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);

		configurer.setSaveAndRestore(true);

		// set a specific theme XXX done in product configuration
//		IThemeManager tm = OsgiUtils.getService(IThemeManager.class);
//		try {
//			tm.getEngineForDisplay(Display.getCurrent()).setTheme(
//					"org.eclipse.e4.ui.css.theme.e4_default", true);
//			// "org.eclipse.e4.ui.css.theme.e4_dark", true);
//		} catch (Exception e) {
//			// ignore
//		}
	}

	/**
	 * @see WorkbenchAdvisor#eventLoopIdle(Display)
	 */
	@Override
	public void eventLoopIdle(Display display) {
		openDocProcessor.openFiles();
		super.eventLoopIdle(display);
	}

	/**
	 * @see WorkbenchAdvisor#preShutdown()
	 */
	@Override
	public boolean preShutdown() {
		// call workbench hooks
		boolean shutdownCanceled = false;
		for (WorkbenchHook hook : hooks) {
			try {
				if (!hook.preShutdown(getWorkbenchConfigurer().getWorkbench())) {
					shutdownCanceled = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (shutdownCanceled) {
			return false;
		}

		// ask for save if there are changes
		// TODO use a workbench hook for this
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		if (ps.isChanged()) {
			Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
			MessageBox mb = new MessageBox(shell,
					SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
			mb.setMessage(Messages.ApplicationWorkbenchAdvisor_1); // $NON-NLS-1$
			mb.setText(Messages.ApplicationWorkbenchAdvisor_2); // $NON-NLS-1$
			int result = mb.open();
			if (result == SWT.CANCEL) {
				return false;
			}
			else if (result == SWT.YES) {
				// try saving project
				ps.save();

				if (ps.isChanged()) {
					return false;
				}
				return true;
			}
			else {
				return true;
			}
		}
		else {
			return true;
		}
	}

	@Override
	public void postShutdown() {
		super.postShutdown();

		// call workbench hooks
		for (WorkbenchHook hook : hooks) {
			try {
				hook.postShutdown(getWorkbenchConfigurer().getWorkbench());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public IStatus restoreState(IMemento memento) {
		/*
		 * This method seems no longer to be called within the Eclipse RCP 3
		 * compatibility layer in Eclipse 4. A reason might be that the Eclipse
		 * IDE application itself does not use/override this method.
		 */
		return super.restoreState(memento);
	}

	@Override
	public void preStartup() {
		super.preStartup();

		try {
			ProxySettings.install();
		} catch (Exception ex) {
			_log.warn("Setting the Proxy configuration failed: " + ex.getMessage()); //$NON-NLS-1$
		}

		/*
		 * Initialize RecentResources so they are not loaded when the first
		 * import dialog is opened.
		 * 
		 * TODO instead use a workbench hook?!
		 */
		PlatformUI.getWorkbench().getService(RecentResources.class);

		// initialize workbench hooks
		Builder<WorkbenchHook> builder = ImmutableList.builder();
		WorkbenchHookExtension ext = new WorkbenchHookExtension();
		for (WorkbenchHookFactory fact : ext.getFactories()) {
			try {
				builder.add(fact.createExtensionObject());
			} catch (Exception e) {
				// ignore
				e.printStackTrace();
			}
		}
		hooks = builder.build();

		// call workbench hooks
		for (WorkbenchHook hook : hooks) {
			try {
				hook.preStartup(getWorkbenchConfigurer().getWorkbench());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void postStartup() {
		super.postStartup();

		// call workbench hooks
		for (WorkbenchHook hook : hooks) {
			try {
				hook.postStartup(getWorkbenchConfigurer().getWorkbench());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
