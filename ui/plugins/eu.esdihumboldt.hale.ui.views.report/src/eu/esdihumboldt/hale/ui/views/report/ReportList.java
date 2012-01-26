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

package eu.esdihumboldt.hale.ui.views.report;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.ResourceManager;

import swing2swt.layout.BorderLayout;

import com.google.common.collect.Multimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.report.ReportListener;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.views.report.properties.ReportPropertiesViewPart;

/**
 * This is the Report view.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportList extends ReportPropertiesViewPart implements ReportListener<Report<Message>, Message> {

	/**
	 * The ID for this plugin.
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.report.ReportList"; //$NON-NLS-1$
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private TreeViewer _treeViewer;
	private Menu _menu;
	private MenuItem _mntmCopy;
	private MenuItem _mntmClearReportList;
	private MenuItem _mntmDeleteLog;
	private MenuItem _mntmRestoreLog;
	private MenuItem _mntmExportLog;

	private static final ALogger _log = ALoggerFactory.getLogger(ReportList.class);
	
	private ReportService repService;
	
	/**
	 * Constructor.
	 */
	public ReportList() {
		// get ReportService and add listener
		repService = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
		repService.addReportListener(this);
	}
	
	/**
	 * Loads all added reports from ReportService and
	 * displays them for the current session.
	 */
	@SuppressWarnings("unchecked")
	private void loadReports() {
		Multimap<Class<? extends Report<?>>, Report<?>> reports = this.repService.getAllReports();
	
		for (Report<?> r : reports.values()) {
			if (r == null) { continue; }
			try {
				this.reportAdded((Report<Message>) r);
			} catch (Exception e) {
				_log.warn("Unsupported Report", e.getStackTrace());
			}
		}
	}

	/**
	 * Create contents of the view part.
	 * @param parent parent element
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new BorderLayout(0, 0));
		{
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayoutData(BorderLayout.CENTER);
			formToolkit.adapt(composite);
			formToolkit.paintBordersFor(composite);
			composite.setLayout(new TreeColumnLayout());
			{
				_treeViewer = new TreeViewer(composite, SWT.BORDER);
				final Tree tree = _treeViewer.getTree();
				tree.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						// enable some functions in the popupmenu
						_mntmCopy.setEnabled(true);
					}
				});
				tree.setHeaderVisible(true);
				tree.setLinesVisible(true);
				formToolkit.paintBordersFor(tree);
				
				_menu = new Menu(tree);
				tree.setMenu(_menu);
				
				_mntmCopy = new MenuItem(_menu, SWT.NONE);
				_mntmCopy.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						String clipboard;
						Object obj = ((IStructuredSelection) _treeViewer.getSelection()).getFirstElement();
						if (obj instanceof Project) {
							// use the name for a project
							clipboard = ((Project) obj).getName();
						} else {
							// else copy the stuff from toString()
							clipboard = _treeViewer.getSelection().toString();
						}
						
						// write text to clipboard
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(clipboard), null);
					}
				});
				_mntmCopy.setEnabled(false);
				_mntmCopy.setImage(ResourceManager.getPluginImage("eu.esdihumboldt.hale.ui.views.report", "icons/popupmenu/copy_edit.gif"));
				_mntmCopy.setText("Copy");
				
				new MenuItem(_menu, SWT.SEPARATOR);
				
				_mntmClearReportList = new MenuItem(_menu, SWT.NONE);
				_mntmClearReportList.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						clearLogView();
					}
				});
				_mntmClearReportList.setImage(ResourceManager.getPluginImage("eu.esdihumboldt.hale.ui.views.report", "icons/popupmenu/clear_co.gif"));
				_mntmClearReportList.setText("Clear Report List");
				
				_mntmDeleteLog = new MenuItem(_menu, SWT.NONE);
				_mntmDeleteLog.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						// display a yes|no box
						MessageBox messageBox = new MessageBox(tree.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						messageBox.setText("Confirm Delete");
						messageBox.setMessage("Are you sure you want to permanently delete all logged events?");
						
						if (messageBox.open() == SWT.YES) {
							// remove all entries from ReportService
							repService.deleteAllReports();
							
							// TODO delete saved reports
						}
					}
				});
				_mntmDeleteLog.setImage(ResourceManager.getPluginImage("eu.esdihumboldt.hale.ui.views.report", "icons/popupmenu/delete_obj.gif"));
				_mntmDeleteLog.setText("Delete Log");
				
				_mntmRestoreLog = new MenuItem(_menu, SWT.NONE);
				_mntmRestoreLog.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						// restore the reports from ReportService
						loadReports();
					}
				});
				_mntmRestoreLog.setImage(ResourceManager.getPluginImage("eu.esdihumboldt.hale.ui.views.report", "icons/popupmenu/restore_log.gif"));
				_mntmRestoreLog.setText("Restore Log");
				
				new MenuItem(_menu, SWT.SEPARATOR);
				
				_mntmExportLog = new MenuItem(_menu, SWT.NONE);
				_mntmExportLog.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						FileDialog fd = new FileDialog(tree.getShell(), SWT.SAVE);
						fd.setText("Export Report Log");
						String[] filterExt = { "*.log", "*.txt", "*.*" };
						fd.setFilterExtensions(filterExt);
						
						String filePath = fd.open();
						
						if (filePath != null) {
							// check file existence
							File file = new File(filePath);
							if (file.exists()) {
								MessageBox overwrite = new MessageBox(tree.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
								overwrite.setMessage(String.format("File \"%s\" already exists.\nWould you like to overwrite it?", filePath));
								int cont = overwrite.open();
								
								if (cont == SWT.NO) {
									return;
								}
							}
							
							// try to save it
							try {
								repService.saveAllReports(file);
							} catch (IOException exception) {
								_log.error("Could not save the report log.", exception.getStackTrace());
							}
						}
					}
				});
				_mntmExportLog.setText("Export Log");
			}
		}

		createActions();
		initializeToolBar();
		initializeMenu();
		
		// set label provider
		_treeViewer.setLabelProvider(new ReportListLabelProvider());
		
		// set content provider
		_treeViewer.setContentProvider(new ReportListContentProvider());
		
		// set selection provider
		getSite().setSelectionProvider(_treeViewer);
		
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
	@SuppressWarnings("unused")
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	@SuppressWarnings("unused")
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
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
		if (this._menu == null || this._menu.isDisposed()) {
			return;
		}
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try{
					/*
					 * This is the part where new reports arrive and
					 * will be added.
					 */
					ProjectService proService = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
					ProjectInfo info = proService.getProjectInfo();
					
					if (info == null) {
						Project temp = new Project();
						temp.setName("Unknown");
						info = temp;
					}
					
					_treeViewer.setInput(new ReportItem(info, report));
				} catch (NullPointerException e) {
					_log.warn("NullpointerException while adding a Report.");
					_log.trace(e.getMessage());
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
		_treeViewer.getTree().removeAll();
		
		// clear saved data
		ReportListContentProvider.data.clear();
		
		// make some functions unavailable
		_mntmCopy.setEnabled(false);
	}
}
