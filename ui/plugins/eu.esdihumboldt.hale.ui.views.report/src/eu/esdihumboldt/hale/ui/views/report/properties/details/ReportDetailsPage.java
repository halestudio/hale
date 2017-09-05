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

package eu.esdihumboldt.hale.ui.views.report.properties.details;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.ui.views.report.properties.details.extension.CustomReportDetailsPage;
import eu.esdihumboldt.hale.ui.views.report.properties.details.extension.CustomReportDetailsPage.MessageType;
import eu.esdihumboldt.hale.ui.views.report.properties.details.extension.CustomReportDetailsPageExtension;

/**
 * Default details page for {@link Report}s.
 * 
 * @author Andreas Burchert
 * @author Kai Schwierczek
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class ReportDetailsPage extends AbstractPropertySection {

	private CustomReportDetailsPage page;
	private Report<?> report;
	private Composite parent;
	private Control control;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		this.parent = parent;

		// create final controls when report is set
	}

	/**
	 * Set the currently selected report to the given report.
	 * 
	 * @param report the now selected report
	 */
	protected void setReport(Report<?> report) {
		if (report != this.report) {
			if (this.report == null || report.getClass() != this.report.getClass()) {
				CustomReportDetailsPage customPage = CustomReportDetailsPageExtension.getInstance()
						.getDetailPage(report.getClass());
				if (customPage == null)
					customPage = new DefaultReportDetailsPage();
				if (page == null || page.getClass() != customPage.getClass()) {
					// update shown page...
					if (page != null && control != null) {
						// dispose controls
						control.dispose();
						// cleanup
						page.dispose();
					}
					page = customPage;
					control = page.createControls(parent);
					parent.layout(true);
				}
			}
			this.report = report;
		}
	}

	/**
	 * Set the input to the page for the given {@link MessageType}.
	 * 
	 * @param type the type to set the input to
	 */
	protected void setInputFor(CustomReportDetailsPage.MessageType type) {
		if (report != null && page != null) {
			Collection<? extends Message> messages;
			int more = 0;
			switch (type) {
			case Information:
				messages = report.getInfos();
				more = report.getTotalInfos() - report.getInfos().size();
				break;
			case Warning:
				messages = report.getWarnings();
				more = report.getTotalWarnings() - report.getWarnings().size();
				break;
			case Error:
				messages = report.getErrors();
				more = report.getTotalErrors() - report.getErrors().size();
				break;
			default:
				messages = Collections.emptyList();
			}
			if (more > 0) {
				page.setMore(more); // set more first!
			}
			else {
				// remove old more
				page.setMore(0);
			}
			page.setInput(messages, type);
		}
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#shouldUseExtraSpace()
	 */
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	/**
	 * @see AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose() {
		if (page != null) {
			page.dispose();
		}
	}

}
