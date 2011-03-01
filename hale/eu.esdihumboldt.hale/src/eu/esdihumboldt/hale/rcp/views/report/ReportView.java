package eu.esdihumboldt.hale.rcp.views.report;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.report.service.ReportService;
import eu.esdihumboldt.hale.rcp.views.report.service.ReportServiceImpl;

public class ReportView extends ViewPart implements HaleServiceListener {

	private TreeViewer viewer = null;
	
	@Override
	public void createPartControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		
		ReportService reportService = (ReportService)PlatformUI.getWorkbench().getService(ReportService.class);
		reportService.addListener(this);
		
		// bar and TreeView
		page.setLayout(new GridLayout(2, false));
		
		Image reload = AbstractUIPlugin.imageDescriptorFromPlugin(HALEActivator.PLUGIN_ID, "icons/refresh.gif").createImage();
		
		Composite bar = new Composite(page, SWT.NONE);
		bar.setLayout(new GridLayout(3, true));
		
		Label label = new Label(bar, SWT.NONE);
		label.setText("Error Report");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
	
		Combo combo = new Combo(bar, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 1));
		combo.add("last");
		
		Button button = new Button(bar, SWT.NONE);
		button.setImage(reload);
		button.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 1));
		
		
		
		// 
		viewer = new TreeViewer(page);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		viewer.setContentProvider(new ReportContentProvider());
		viewer.setLabelProvider(new ReportLabelProvider());
		
		
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.refresh();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void setFocus() {		
	}

	@Override
	public void update(UpdateMessage<?> message) {
		ReportServiceImpl reportService = (ReportServiceImpl)PlatformUI.getWorkbench().getService(ReportService.class);
		viewer.setInput(reportService.getLastReport());
		
		// TODO add a new ComboItem
	}

}
