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

package eu.esdihumboldt.hale.ui.views.report.properties.details.warning;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.ui.views.report.properties.details.ReportDetailsPage;
import eu.esdihumboldt.hale.ui.views.report.properties.details.extension.CustomReportDetailsPage.MessageType;

/**
 * Default details page for {@link Report}s. Does only show warnings.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class WarningDetailPage extends ReportDetailsPage {

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

		if (report instanceof Report)
			super.setReport((Report<?>) report);

		super.setInputFor(MessageType.Warning);
	}
}
