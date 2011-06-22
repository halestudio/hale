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

package eu.esdihumboldt.hale.ui.views.report;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import eu.esdihumboldt.hale.core.report.Message;
import eu.esdihumboldt.hale.core.report.Report;
import eu.esdihumboldt.hale.ui.service.report.ReportListener;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * The Transformation Report View.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportView extends ViewPart implements ReportListener<Report<Message>, Message> {

	/**
	 * The view ID
	 */
	public static String ID = "eu.esdihumboldt.hale.ui.views.report";
	
	/**
	 * Displays all warnings and errors which occurred in the
	 * parse process.
	 */
	private TreeViewer viewer = null;
	private Combo combo = null;
	
	@Override
	public void createPartControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		
//		ReportService reportService = (ReportService)PlatformUI.getWorkbench().getService(ReportService.class);
//		reportService.addListener(this);
		ReportService repService = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
		repService.addReportListener(this);
		
		// bar and TreeView
		page.setLayout(new GridLayout(2, true));
	
		combo = new Combo(page, SWT.READ_ONLY | SWT.FILL);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		viewer = new TreeViewer(page);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		viewer.setContentProvider(new ReportContentProvider());
		viewer.setLabelProvider(new ReportLabelProvider());
		
		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				ReportServiceImpl reportService = (ReportServiceImpl)PlatformUI.getWorkbench().getService(ReportService.class);
				
				// gets the requested ReportEntry
//				viewer.setInput(reportService.getReport(combo.getSelectionIndex()));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				/* nothing */
			}
		});
	}

	@Override
	public void setFocus() {
		/* nothing */
	}

	/*
	public void update(UpdateMessage<?> message) {
		// get reportService
		ReportService reportService = (ReportService)PlatformUI.getWorkbench().getService(ReportService.class);
		
		// set new input
		viewer.setInput(reportService.getLastReport());

		// get current time
		DateFormat date = DateFormat.getTimeInstance();
		Date time = Calendar.getInstance().getTime();
		String id = date.format(time);
		
		// get project information
		ProjectService projectService = (ProjectService)PlatformUI.getWorkbench().getService(ProjectService.class);
		
		// add entry and select it
		combo.add(reportService.getLastReport().getIdentifier()+": "+projectService.getProjectName()+" - "+id); //$NON-NLS-1$ //$NON-NLS-2$
		combo.select(combo.getItemCount()-1);
		
	}*/

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#getReportType()
	 */
	@Override
	public Class getReportType() {
		return Report.class;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#getMessageType()
	 */
	@Override
	public Class getMessageType() {
		return Message.class;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.report.ReportListener#reportAdded(eu.esdihumboldt.hale.ui.service.report.Report)
	 */
	@Override
	public void reportAdded(final Report report) {
		System.err.println(report.getTaskName()+" / "+report.getMessageType().getSimpleName());
		
		IWorkbench bench = PlatformUI.getWorkbench();
		Display display = bench.getDisplay();
		display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try{
					viewer.setInput(new ReportModel(report));
					combo.add("["+report.getTimestamp()+"] "+report.getTaskName()+" -- "+report.getSummary());
					combo.select(combo.getItemCount()-1);
					
				} catch (NullPointerException e) {
					System.err.println("NullPointer... "+report.getSummary());
					e.printStackTrace();
				}
			}
		});
	}

}
