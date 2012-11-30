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
	 * Text for duration
	 */
	public Text durationText;

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
