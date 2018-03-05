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

package eu.esdihumboldt.hale.ui.views.report;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportSession;
import eu.esdihumboldt.hale.ui.service.report.ReportListener;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.util.viewer.ViewerMenu;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;
import eu.esdihumboldt.hale.ui.views.properties.handler.OpenPropertiesHandler;

/**
 * This is the Report view.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportList extends PropertiesViewPart
		implements ReportListener<Report<Message>, Message> {

	private static final ALogger _log = ALoggerFactory.getLogger(ReportList.class);

	/**
	 * The ID for this plugin.
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.report.ReportList"; //$NON-NLS-1$

	private TreeViewer _treeViewer;
	private final ReportService repService;

	/**
	 * Constructor.
	 */
	public ReportList() {
		// get ReportService and add listener
		repService = PlatformUI.getWorkbench().getService(ReportService.class);
		repService.addReportListener(this);
	}

	/**
	 * @see PropertiesViewPart#getViewContext()
	 */
	@Override
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.report_list";
	}

	/**
	 * Loads all added reports from ReportService and displays them for the
	 * current session.
	 */
	private void loadReports() {
		_treeViewer.setInput(this.repService.getAllSessions());
	}

	/**
	 * Selects the given report if it exists, otherwise the selection is
	 * removed.
	 * 
	 * @param report the report to select
	 */
	public void selectReport(Report<? extends Message> report) {
		_treeViewer.setSelection(new StructuredSelection(report), true);
	}

	/**
	 * @see ITabbedPropertySheetPageContributor#getContributorId()
	 */
	@Override
	public String getContributorId() {
		return "eu.esdihumboldt.hale.ui.views.report.properties";
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent parent element
	 */
	@Override
	public void createViewControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		TreeColumnLayout layout = new TreeColumnLayout();
		composite.setLayout(layout);
		{
			_treeViewer = new TreeViewer(composite, SWT.BORDER);
			final Tree tree = _treeViewer.getTree();
			tree.setHeaderVisible(false);
			tree.setLinesVisible(false);

			// create column for reports
			TreeViewerColumn col1 = new TreeViewerColumn(_treeViewer, SWT.NONE);

			// add the label provider
			col1.setLabelProvider(new TreeColumnViewerLabelProvider(new ReportListLabelProvider()));

			// and layout
			layout.setColumnData(col1.getColumn(), new ColumnWeightData(3));

			// create column for reports
			TreeViewerColumn col2 = new TreeViewerColumn(_treeViewer, SWT.NONE);

			// add the label provider
			col2.setLabelProvider(
					new TreeColumnViewerLabelProvider(new ReportListLabelDateProvider()));

			// create column for reports
			layout.setColumnData(col2.getColumn(), new ColumnWeightData(1));

			new ReportListMenu(getSite(), _treeViewer);
		}

		createActions();
		initializeToolBar();
		initializeMenu();

		// set label provider
		// _treeViewer.setLabelProvider(new ReportListLabelProvider());

		// set content provider
		_treeViewer.setContentProvider(new ReportListContentProvider());

		// disable this if it uses too much memory
		// but should maintain the list much faster
		_treeViewer.setUseHashlookup(true);

		// order the sessions from latest to oldest
		_treeViewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof ReportSession && e2 instanceof ReportSession) {
					long first = ((ReportSession) e1).getId();
					long second = ((ReportSession) e2).getId();
					if (first > second) {
						return -1;
					}
					else if (first < second) {
						return 1;
					}
					else {
						return 0;
					}
				}
				else if (e1 instanceof Report<?> && e2 instanceof Report<?>) {
					Report<?> first = (Report<?>) e1;
					Report<?> second = (Report<?>) e2;
					if (first.getStartTime() == null && second.getStartTime() == null) {
						return 0;
					}
					else if (first.getStartTime() == null) {
						return 1;
					}
					else if (second.getStartTime() == null) {
						return -1;
					}
					else if (first.getStartTime().getTime() > second.getStartTime().getTime()) {
						return -1;
					}
					else if (first.getStartTime().getTime() < second.getStartTime().getTime()) {
						return 1;
					}
					else {
						return 0;
					}
				}

				return 0;
			}
		});

		// set selection provider
		getSite().setSelectionProvider(_treeViewer);

		_treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				OpenPropertiesHandler.unpinAndOpenPropertiesView(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			}
		});

		// load all added reports
		this.loadReports();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(new ActionShowCurrentSession(_treeViewer));
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
//		IMenuManager menuManager = getViewSite().getActionBars()
//				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
		_treeViewer.getControl().setFocus();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#getReportType()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getReportType() {
		return Report.class;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#getMessageType()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getMessageType() {
		return Message.class;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#reportAdded(eu.esdihumboldt.hale.common.core.report.Report)
	 */
	@Override
	public void reportAdded(final Report<Message> report) {
		// check if a widget is disposed
		if (this._treeViewer == null || this._treeViewer.getTree() == null
				|| this._treeViewer.getTree().isDisposed()) {
			return;
		}

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
					// add report to view
					_treeViewer.setInput(repService.getAllSessions());

					/*
					 * TODO expand all previous expanded items To expand all
					 * previous expanded entries: see:
					 * http://stackoverflow.com/questions
					 * /1576563/saving-treeviewer-state-before-setinput You need
					 * to make sure that your TreeViewer's content provider
					 * provides objects that have their hashset and equals
					 * methods properly defined. AbstractTreeViewer needs to be
					 * able to compare the old and new objects to determine
					 * their expansion state. If hashset and equals aren't
					 * provided, it's a simple reference check, which won't work
					 * if you've recreated your contents.
					 */
					_treeViewer.setExpandedElements(
							new Object[] { _treeViewer.getExpandedElements() });

					// select new item
					_treeViewer.setSelection(new StructuredSelection(report), true);
				} catch (NullPointerException e) {
					_log.warn("NullpointerException while adding a Report.", e);
				}
			}
		});
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#reportsDeleted()
	 */
	@Override
	public void reportsDeleted() {
		// clear the view
		clearLogView();
	}

	/**
	 * Clears the log view.
	 */
	private void clearLogView() {
		// clear the view
		if (!_treeViewer.getTree().isDisposed()) {
			_treeViewer.getTree().removeAll();
		}

		// clear saved data
		_treeViewer.setInput(null);
	}

	private class ReportListMenu extends ViewerMenu {

		/**
		 * Create a viewer context menu.
		 * 
		 * @param site the (view) site containing the viewer
		 * @param viewer the viewer
		 */
		public ReportListMenu(IWorkbenchPartSite site, Viewer viewer) {
			super(site, viewer);

		}

		@Override
		public void menuAboutToShow(IMenuManager manager) {
			manager.add(new Action("Clear Report List", AbstractUIPlugin.imageDescriptorFromPlugin(
					"eu.esdihumboldt.hale.ui.views.report", "icons/popupmenu/clear_co.gif")) {

				/**
				 * @see org.eclipse.jface.action.Action#run()
				 */
				@Override
				public void run() {
					clearLogView();
					super.run();
				}
			});

			manager.add(new Action("Delete Log", AbstractUIPlugin.imageDescriptorFromPlugin(
					"eu.esdihumboldt.hale.ui.views.report", "icons/popupmenu/delete_obj.gif")) {

				/**
				 * @see org.eclipse.jface.action.Action#run()
				 */
				@Override
				public void run() {
					// display a yes|no box
					MessageBox messageBox = new MessageBox(_treeViewer.getTree().getShell(),
							SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					messageBox.setText("Confirm Delete");
					messageBox.setMessage(
							"Are you sure you want to permanently delete all logged events?");

					if (messageBox.open() == SWT.YES) {
						// remove all entries from ReportService
						repService.deleteAllReports();
					}
					super.run();
				}
			});

			manager.add(new Action("Restore Log", AbstractUIPlugin.imageDescriptorFromPlugin(
					"eu.esdihumboldt.hale.ui.views.report", "icons/popupmenu/restore_log.gif")) {

				/**
				 * @see org.eclipse.jface.action.Action#run()
				 */
				@Override
				public void run() {
					// restore the reports from ReportService
					loadReports();
					super.run();
				}
			});

			manager.add(new Action("Export Log", null) {

				/**
				 * @see org.eclipse.jface.action.Action#run()
				 */
				@Override
				public void run() {
					FileDialog fd = new FileDialog(_treeViewer.getTree().getShell(), SWT.SAVE);
					fd.setText("Export Report Log");
					String[] filterExt = { "*.log", "*.txt", "*.*" };
					fd.setFilterExtensions(filterExt);

					SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
					String info = df.format(new Date(System.currentTimeMillis()));
					fd.setFileName(info + "-" + System.currentTimeMillis());

					String filePath = fd.open();

					if (filePath != null) {
						// check file existence
						File file = new File(filePath);
						if (file.exists()) {
							MessageBox overwrite = new MessageBox(_treeViewer.getTree().getShell(),
									SWT.ICON_QUESTION | SWT.YES | SWT.NO);
							overwrite.setMessage(String.format(
									"File \"%s\" already exists.\nWould you like to overwrite it?",
									filePath));
							int cont = overwrite.open();

							if (cont == SWT.NO) {
								return;
							}
						}

						// try to save it
						try {
							repService.saveCurrentReports(file);
						} catch (IOException exception) {
							_log.error("Could not save the report log.", exception);
						}
					}
					super.run();
				}
			});

			manager.add(new Action("Import Log", null) {

				/**
				 * @see org.eclipse.jface.action.Action#run()
				 */
				@Override
				public void run() {
					FileDialog fd = new FileDialog(_treeViewer.getTree().getShell(), SWT.OPEN);
					fd.setText("Export Report Log");
					String[] filterExt = { "*.log", "*.txt", "*.*" };
					fd.setFilterExtensions(filterExt);

					String filePath = fd.open();

					if (filePath != null) {
						// check file existence
						File file = new File(filePath);
						if (file.exists()) {
							try {
								repService.loadReport(file);
								clearLogView();
								loadReports();
							} catch (ParseException e1) {
								_log.error(e1.getMessage());
							}
						}
					}
					super.run();
				}
			});

			// call super method
			super.menuAboutToShow(manager);
		}
	}
}
