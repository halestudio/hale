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

package eu.esdihumboldt.hale.ui.views.report.deprecated;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportSummary extends Composite {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Text _text;
	private Text _text_1;
	private Text _text_2;
	private Text _text_3;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ReportSummary(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		
		Form frmNewForm = toolkit.createForm(this);
		frmNewForm.setBounds(37, 22, 347, 239);
		toolkit.paintBordersFor(frmNewForm);
		frmNewForm.setText("Report Summary");
		
		Label lblNewLabel = toolkit.createLabel(frmNewForm.getBody(), "Task name", SWT.NONE);
		lblNewLabel.setBounds(36, 20, 109, 15);
		
		Label lblNewLabel_1 = toolkit.createLabel(frmNewForm.getBody(), "Status", SWT.NONE);
		lblNewLabel_1.setBounds(36, 47, 109, 15);
		
		Label lblNewLabel_2 = toolkit.createLabel(frmNewForm.getBody(), "Errors / Warnings", SWT.NONE);
		lblNewLabel_2.setBounds(36, 74, 109, 15);
		
		Label lblNewLabel_3 = toolkit.createLabel(frmNewForm.getBody(), "Time", SWT.NONE);
		lblNewLabel_3.setBounds(36, 101, 109, 15);
		
		Label label = new Label(frmNewForm.getBody(), SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(151, 20, 2, 96);
		toolkit.adapt(label, true, true);
		
		_text = new Text(frmNewForm.getBody(), SWT.BORDER);
		_text.setBounds(159, 17, 178, 21);
		toolkit.adapt(_text, true, true);
		
		_text_1 = new Text(frmNewForm.getBody(), SWT.BORDER);
		_text_1.setBounds(159, 44, 178, 21);
		toolkit.adapt(_text_1, true, true);
		
		_text_2 = new Text(frmNewForm.getBody(), SWT.BORDER);
		_text_2.setBounds(159, 71, 178, 21);
		toolkit.adapt(_text_2, true, true);
		
		_text_3 = new Text(frmNewForm.getBody(), SWT.BORDER);
		_text_3.setBounds(159, 98, 178, 21);
		toolkit.adapt(_text_3, true, true);

	}
}
