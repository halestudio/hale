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
					if (page != null && control != null)
						control.dispose();
					page = customPage;
					control = page.createControls(parent);
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
			switch (type) {
			case Information:
				messages = report.getInfos();
				break;
			case Warning:
				messages = report.getWarnings();
				break;
			case Error:
				messages = report.getErrors();
				break;
			default:
				messages = Collections.emptyList();
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
}
