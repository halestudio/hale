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

package eu.esdihumboldt.hale.ui.views.report.properties.summary;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Summary for {@link Report}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportSummary extends AbstractReportSummary {

	/**
	 * Text for success message
	 */
	public Text successText;

	/**
	 * Text for summary
	 */
	public Text summaryText;

	/**
	 * Text for timestamp
	 */
	public Text timeText;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		// success
		successText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		successText.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		successText.setLayoutData(data);

		CLabel successLabel = getWidgetFactory().createCLabel(composite, "Success:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(successText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(successText, 0, SWT.CENTER);
		successLabel.setLayoutData(data);

		// summary
		summaryText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		summaryText.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(successText, ITabbedPropertyConstants.VSPACE);
		summaryText.setLayoutData(data);

		CLabel summaryLabe = getWidgetFactory().createCLabel(composite, "Summary:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(summaryText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(summaryText, 0, SWT.CENTER);
		summaryLabe.setLayoutData(data);

		// timestamp and time related stuff
		// TODO calculate duration if possible
		timeText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		timeText.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(summaryText, ITabbedPropertyConstants.VSPACE);
		timeText.setLayoutData(data);

		CLabel timeLabel = getWidgetFactory().createCLabel(composite, "Time:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(timeText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(timeText, 0, SWT.CENTER);
		timeLabel.setLayoutData(data);
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		successText.setText(report.isSuccess() + "");
		summaryText.setText(report.getSummary());
		timeText.setText(report.getTimestamp() + "");
	}
}
