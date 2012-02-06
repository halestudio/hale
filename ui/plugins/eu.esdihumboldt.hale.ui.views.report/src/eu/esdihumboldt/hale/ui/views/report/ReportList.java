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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.layout.TreeColumnLayout;
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
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportSession;
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

	private MenuItem _mntmClearReportList;
	private MenuItem _mntmDeleteLog;
	private MenuItem _mntmRestoreLog;
	private MenuItem _mntmExportLog;
	private MenuItem _mntmImportLog;

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
	@SuppressWarnings({ "rawtypes" })
	private void loadReports() {
		for (ReportSession s : this.repService.getAllSessions()) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String info = df.format(new Date(s.getId()));
			
			for (Report r : s.getAllReports().values()) {
				_treeViewer.setInput(new ReportItem(info, r));
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
				tree.setHeaderVisible(true);
				tree.setLinesVisible(true);
				formToolkit.paintBordersFor(tree);
				
				_menu = new Menu(tree);
				tree.setMenu(_menu);
				
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
						
						SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
						String info = df.format(new Date(System.currentTimeMillis()));
						fd.setFileName(info+"-"+System.currentTimeMillis());
						
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
								repService.saveCurrentReports(file);
							} catch (IOException exception) {
								_log.error("Could not save the report log.", exception.getStackTrace());
							}
						}
					}
				});
				_mntmExportLog.setText("Export Log");
				
				_mntmImportLog = new MenuItem(_menu, SWT.NONE);
				_mntmImportLog.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						FileDialog fd = new FileDialog(tree.getShell(), SWT.OPEN);
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
					}
				});
				_mntmImportLog.setText("Import Log");
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
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String info = df.format(new Date(System.currentTimeMillis()));
					
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
	}
}
