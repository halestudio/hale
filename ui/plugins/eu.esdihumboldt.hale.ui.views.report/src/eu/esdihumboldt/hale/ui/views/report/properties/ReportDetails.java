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

package eu.esdihumboldt.hale.ui.views.report.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.io.report.IOMessage;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportDetails extends AbstractPropertySection {
	
	public Text warnings;
	
	public Text errors;
	
	public Report report;
	
	public List warningList;
	
	public List errorList;
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;
		
		warnings = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		warnings.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		warnings.setLayoutData(data);

		CLabel warningsLabel = getWidgetFactory()
				.createCLabel(composite, "Warnings:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(warnings,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(warnings, 0, SWT.CENTER);
		warningsLabel.setLayoutData(data);
		
		// list widget
		warningList = new List(composite, SWT.BORDER);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(warnings, ITabbedPropertyConstants.VSPACE);
		warningList.setLayoutData(data);
//		warningList.add("test");
		
		
		errors = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		errors.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(warningList, ITabbedPropertyConstants.VSPACE);
		errors.setLayoutData(data);

		CLabel errorsLabel = getWidgetFactory()
				.createCLabel(composite, "Errors:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(errors,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(errors, 0, SWT.CENTER);
		errorsLabel.setLayoutData(data);
		
		errorList = new List(composite, SWT.BORDER);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(errors, ITabbedPropertyConstants.VSPACE);
		errorList.setLayoutData(data);
	}
	
	/**
	 * @see AbstractPropertySection#setInput(IWorkbenchPart, ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		Object report = null;
		if (selection instanceof IStructuredSelection) {
			// overwrite element with first element from selection
			report = ((IStructuredSelection) selection).getFirstElement();
		}
		
		// set new report
		if (report instanceof Report) {
			this.report = (Report) report;
		}
		
		// clear lists
		this.warningList.removeAll();
		this.errorList.removeAll();
	}
	
	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		int warnCount = this.report.getWarnings().size();
		int errorCount = this.report.getErrors().size();
		warnings.setText(""+warnCount);
		errors.setText(""+errorCount);
		
		if (warnCount > 0) {
			for (Object o : this.report.getWarnings()) {
				Message message = (Message) o;
				
				if (message instanceof IOMessage) {
					this.warningList.add("["+((IOMessage) message).getLineNumber()+"] "+message.getMessage());
				} else {
					this.warningList.add(message.getMessage());
				}
			}
		}
		
		if (errorCount > 0) {
			for (Object o : this.report.getErrors()) {
				Message message = (Message) o;
				
				if (message instanceof IOMessage) {
					this.errorList.add("["+((IOMessage) message).getLineNumber()+"] "+message.getMessage());
				} else {
					this.errorList.add(message.getMessage());
				}
			}
		}
	}
}
