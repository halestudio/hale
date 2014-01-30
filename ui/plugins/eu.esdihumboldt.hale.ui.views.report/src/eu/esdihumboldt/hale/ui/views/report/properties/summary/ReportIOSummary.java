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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.ui.common.components.URILink;

/**
 * Extended summary for {@link IOReport}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportIOSummary extends AbstractReportSummary {

	/**
	 * Link to the file from {@link IOReport}
	 */
	private URILink link;
	private Link displayLink;
	private Text linktext;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		// urilink
		link = new URILink(composite, SWT.None, null, "<A>Open Location</A>");

		displayLink = link.getLink();

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(composite, ITabbedPropertyConstants.VSPACE);
		displayLink.setLayoutData(data);
		displayLink.setBackground(getWidgetFactory().getColors().getBackground());

		// link label
		CLabel linkLabel = getWidgetFactory().createCLabel(composite, "Link:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(composite, 0, SWT.CENTER);
		linkLabel.setLayoutData(data);

		linktext = getWidgetFactory().createText(composite, "");
		linktext.setEditable(false);

		data = new FormData();
		data.width = 100;
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(displayLink, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		linktext.setLayoutData(data);

		CLabel locationLabel = getWidgetFactory().createCLabel(composite, "Location:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(linktext, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(linktext, 0, SWT.CENTER);
		locationLabel.setLayoutData(data);
	}

	/**
	 * @see AbstractPropertySection#setInput(IWorkbenchPart, ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();

		if (linktext == null || link == null) {
			// not yet initialized
			return;
		}

		if (report != null && report instanceof IOReport) {
			IOReport ioReport = (IOReport) report;

			if (ioReport.getTarget() != null && ioReport.getTarget().getLocation() != null) {
				this.link.refresh(ioReport.getTarget().getLocation());
				this.linktext.setText(ioReport.getTarget().getLocation().toString());
				this.displayLink = link.getLink();

				displayLink.setEnabled(true);

				return;
			}
		}

		displayLink.setEnabled(false);
		linktext.setText("");
	}
}
