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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.swt.layout.FormLayout;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * TODO Type description
 * @author andi
 */
public class ReportViewer extends FormPage {

	/**
	 * Create the form page.
	 * @param id
	 * @param title
	 */
	public ReportViewer(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public ReportViewer(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * Create contents of the form.
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Report View");
		managedForm.getForm().getBody().setLayout(new BorderLayout(0, 0));
		
		Composite leftSide = new Composite(managedForm.getForm().getBody(), SWT.NONE);
		leftSide.setLayoutData(BorderLayout.WEST);
		managedForm.getToolkit().adapt(leftSide);
		managedForm.getToolkit().paintBordersFor(leftSide);
		RowLayout rl_leftSide = new RowLayout(SWT.VERTICAL);
		rl_leftSide.marginTop = 30;
		rl_leftSide.marginRight = 0;
		rl_leftSide.center = true;
		rl_leftSide.spacing = 0;
		rl_leftSide.fill = true;
		leftSide.setLayout(rl_leftSide);
		
		Button btnSummary = new Button(leftSide, SWT.FLAT | SWT.TOGGLE | SWT.CENTER);
		btnSummary.setSelection(true);
		btnSummary.setGrayed(true);
		btnSummary.setLayoutData(new RowData(70, SWT.DEFAULT));
		managedForm.getToolkit().adapt(btnSummary, true, true);
		btnSummary.setText("Summary");
		
		Button btnDetails = new Button(leftSide, SWT.TOGGLE | SWT.CENTER);
		btnDetails.setLayoutData(new RowData(66, SWT.DEFAULT));
		managedForm.getToolkit().adapt(btnDetails, true, true);
		btnDetails.setText("Details");
		
		Combo reportCombo = new Combo(managedForm.getForm().getBody(), SWT.NONE);
		reportCombo.setLayoutData(BorderLayout.NORTH);
		managedForm.getToolkit().adapt(reportCombo);
		managedForm.getToolkit().paintBordersFor(reportCombo);
		
		Composite contentComposite = new Composite(managedForm.getForm().getBody(), SWT.NONE);
		contentComposite.setLayoutData(BorderLayout.CENTER);
		managedForm.getToolkit().adapt(contentComposite);
		managedForm.getToolkit().paintBordersFor(contentComposite);
		toolkit.decorateFormHeading(form.getForm());
	}
}
