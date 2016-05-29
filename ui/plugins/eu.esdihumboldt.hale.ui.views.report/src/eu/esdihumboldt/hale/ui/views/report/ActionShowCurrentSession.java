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

package eu.esdihumboldt.hale.ui.views.report;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportSession;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * {@link Action} for {@link ReportList} to display only {@link Report}s for the
 * current session.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ActionShowCurrentSession extends Action {

	private final TreeViewer _treeViewer;
	private final CurrentSessionFilter sessionFilter = new CurrentSessionFilter();
	private final ReportService repService;

	/**
	 * Constructor.
	 * 
	 * @param treeViewer the related treeviwer
	 */
	public ActionShowCurrentSession(TreeViewer treeViewer) {
		// set alternate text and toggle button
		super("Show current session only", SWT.TOGGLE);

		// set tooltip
		setToolTipText("Only show reports from the current session.");

		// set icon
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				"eu.esdihumboldt.hale.ui.views.report", "icons/current_session.gif"));

		// save treeViewer
		_treeViewer = treeViewer;

		// get the report service
		repService = PlatformUI.getWorkbench().getService(ReportService.class);
	}

	@Override
	public void run() {
		if (isChecked()) {
			// activate filter
			_treeViewer.addFilter(sessionFilter);
		}
		else {
			// deactivate filter
			_treeViewer.removeFilter(sessionFilter);
		}
	}

	/**
	 * The {@link ViewerFilter} for {@link ActionShowCurrentSession}. Only
	 * display current {@link Report}s.
	 * 
	 * @author Andreas Burchert
	 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
	 */
	private class CurrentSessionFilter extends ViewerFilter {

		/**
		 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {

			long current = repService.getCurrentSessionDescription();

			if (current == 0) {
				// there are no current session so we display all
				return true;
			}

			if (element instanceof ReportSession) {
				if (((ReportSession) element).getId() == current) {
					return true;
				}
			}
			else if (parentElement instanceof ReportSession) {
				if (((ReportSession) parentElement).getId() == current) {
					return true;
				}
			}

			return false;
		}

	}
}
