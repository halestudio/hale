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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * This class is the base for report summary pages.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class AbstractReportSummary extends AbstractPropertySection {

	/**
	 * Contains the report to display.
	 */
	public Report<?> report;

	/**
	 * Composite for the tabbed property page
	 */
	public Composite composite;

	/**
	 * Contains the formdata.
	 */
	public FormData data;

	/**
	 * 
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		composite = getWidgetFactory().createFlatFormComposite(parent);
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
			this.report = (Report<?>) report;
		}
	}
}
