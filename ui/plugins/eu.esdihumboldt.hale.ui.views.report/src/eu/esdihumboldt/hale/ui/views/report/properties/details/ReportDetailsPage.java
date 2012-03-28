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

package eu.esdihumboldt.hale.ui.views.report.properties.details;

import java.io.PrintWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.ui.util.dialog.StackTraceErrorDialog;
import eu.esdihumboldt.hale.ui.views.report.properties.details.tree.ReportTreeContentProvider;
import eu.esdihumboldt.hale.ui.views.report.properties.details.tree.ReportTreeLabelProvider;

/**
 * Default details page for {@link Report}s.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class ReportDetailsPage extends AbstractPropertySection {
	
	/**
	 * The FilteredTree
	 */
	protected FilteredTree tree;
	
	/**
	 * Contains the report for which details should be displayed
	 */
	protected Report<?> report;
	
	/**
	 * Composite for the tabbed property page
	 */
	private Composite composite;
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(GridLayoutFactory.fillDefaults().create());
		
		// create pattern filter for FilteredTree
		PatternFilter filter = new PatternFilter();
		
		// create FilteredTree
		tree = new FilteredTree(composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL, filter, true);

		final TreeViewer viewer = tree.getViewer();
		
		// set content provider
		viewer.setContentProvider(new ReportTreeContentProvider());
		
		// set label provider
		viewer.setLabelProvider(new ReportTreeLabelProvider());

		// add menu on right-click
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(viewer.getControl());

		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
					
					Object o = selection.getFirstElement();
					
					if (o instanceof Message) {
						Message m = (Message) o;
						// check if a stacktrace exists
						if (m.getStackTrace() != null && !m.getStackTrace().equals("")) {
							// add Action to the menu
							manager.add(new ShowStackTraceAction("Show Stack Trace", null, m));
						}
					}
				}
			}
		});
		
		// remove previous menus
		menuMgr.setRemoveAllWhenShown(true);
		
		// add menu to viewer
		viewer.getControl().setMenu(menu);
		
		// open stacktrace on double click
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeSelection o = (TreeSelection)event.getSelection();
				
				if (o.getFirstElement() instanceof Message) {
					Message m = (Message) o.getFirstElement();
					// check if a stacktrace exists
					if (m.getStackTrace() != null && !m.getStackTrace().equals("")) {
						// create action and run it
						(new ShowStackTraceAction("Show Stack Trace", null, m)).run();
					}
				}
			}
		});
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#shouldUseExtraSpace()
	 */
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}
	
	/**
	 * Internal Exception wrapper.
	 */
	private class ShowException extends Exception {
		
		/**
		 * Version id.
		 */
		private static final long serialVersionUID = 4961655914112792450L;
		
		/**
		 * Stacktrace message.
		 */
		private String stackTrace;
		
		/**
		 * Constructor.
		 * @param stackTrace stackTrace
		 */
		public ShowException(String stackTrace) {
			this.stackTrace = stackTrace;
		}
		
		@Override
		public void printStackTrace(PrintWriter pw) {
			pw.append(stackTrace);
		}
	}
	
	/**
	 * Action for displaying a StackTrace(String).
	 */
	private class ShowStackTraceAction extends Action {
		
		/**
		 * Contains the message.
		 */
		private Message m;
		
		/**
		 * Constructor.
		 * 
		 * @param text the action's text, or null if there is no text
		 * @param image the action's image, or null if there is no image
		 * @param m the message
		 */
		public ShowStackTraceAction(String text, ImageDescriptor image, Message m) {
			super(text, image);
			this.m = m;
		}
		
		@Override
		public void run() {
			Status status = new Status(IStatus.ERROR, "eu.esdihumboldt.hale.ui.views.report", 
					"See details", new ShowException(m.getStackTrace()));
			StackTraceErrorDialog d = new StackTraceErrorDialog(Display.getCurrent().getActiveShell(),
					"Message Details", m.getMessage(), status, IStatus.ERROR);
			d.open();
		}
	}
}
