/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.mapping.report;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheet;

import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.views.mapping.MappingView;
import eu.esdihumboldt.hale.ui.views.report.properties.details.DefaultReportDetailsPage;

/**
 * Transformation report page that links to the mapping view.
 * 
 * @author Simon Templer
 */
public class TransformationReportPage extends DefaultReportDetailsPage {

	@Override
	protected void onDoubleClick(Message m) {
		/*
		 * On a double click on a message the corresponding cell can be shown.
		 * XXX A better integration would be organizing the messages by cells in
		 * the tree viewer.
		 */

		if (m instanceof TransformationMessage) {
			TransformationMessage tm = (TransformationMessage) m;

			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
			if (as != null && as.getAlignment().getCell(tm.getCellId()) != null) {
				IWorkbenchWindow activeWindow = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();

				// pin the property sheet if possible
				IViewReference ref = activeWindow.getActivePage()
						.findViewReference(IPageLayout.ID_PROP_SHEET);
				if (ref != null) {
					IViewPart part = ref.getView(false);
					if (part instanceof PropertySheet) {
						PropertySheet sheet = (PropertySheet) part;
						if (!sheet.isPinned()) {
							sheet.setPinned(true);
						}
					}
				}

				// show cell in mapping view
				try {
					IViewPart part = activeWindow.getActivePage().showView(MappingView.ID);
					if (part instanceof MappingView) {
						((MappingView) part).selectCell(tm.getCellId());
					}
				} catch (PartInitException e) {
					// ignore
				}
			}
		}

		super.onDoubleClick(m);
	}

}
