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

package eu.esdihumboldt.hale.rcp.views.report;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.project.ProjectServiceImpl;
import eu.esdihumboldt.hale.rcp.views.report.service.ReportService;
import eu.esdihumboldt.hale.rcp.views.report.service.ReportServiceImpl;

/**
 * The Transformation Report View.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ReportView extends ViewPart implements HaleServiceListener {

	/**
	 * Displays all warnings and errors which occurred in the
	 * parse process.
	 */
	private TreeViewer viewer = null;
	private Combo combo = null;
	
	@Override
	public void createPartControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		
		ReportService reportService = (ReportService)PlatformUI.getWorkbench().getService(ReportService.class);
		reportService.addListener(this);
		
		// bar and TreeView
		page.setLayout(new GridLayout(2, false));

		Composite bar = new Composite(page, SWT.FILL);
		bar.setLayout(new GridLayout(2, true));
		
		Label label = new Label(bar, SWT.NONE);
		label.setText("GML Export Report");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
	
		combo = new Combo(bar, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 1, 1));
		// TODO increase the combo size

		viewer = new TreeViewer(page);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		viewer.setContentProvider(new ReportContentProvider());
		viewer.setLabelProvider(new ReportLabelProvider());
		
		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ReportServiceImpl reportService = (ReportServiceImpl)PlatformUI.getWorkbench().getService(ReportService.class);
				
				// gets the requested ReportEntry
				viewer.setInput(reportService.getReport(combo.getSelectionIndex()));
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

	@Override
	public void update(UpdateMessage<?> message) {
		// get reportService
		ReportServiceImpl reportService = (ReportServiceImpl)PlatformUI.getWorkbench().getService(ReportService.class);
		
		// set new input
		viewer.setInput(reportService.getLastReport());

		// get current time
		// TODO remove deprecated functions Date.get*()
		Date time = Calendar.getInstance().getTime();
		@SuppressWarnings("deprecation")
		String id = time.getHours()+":"+time.getMinutes()+"."+time.getSeconds();
		
		// get project information
		ProjectServiceImpl projectService = (ProjectServiceImpl)PlatformUI.getWorkbench().getService(ProjectService.class);
		
		// add entry and select it
		combo.add(projectService.getProjectName()+" - "+id);
		combo.select(combo.getItemCount()-1);
	}

}
