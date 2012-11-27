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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.report;

import org.eclipse.jface.viewers.LabelProvider;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * TODO Type description
 * 
 * @author Patrick Lieb
 */
public class ReportListLabelDurationProvider extends LabelProvider {

//	private final SimpleDateFormat df = new SimpleDateFormat("HH:mm.ss");

	private long difference;

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Report<?> && ((Report<?>) element).getStartTime() != null) {
			difference = (((Report<?>) element).getStartTime().getTime() - ((Report<?>) element)
					.getTimestamp().getTime()) / 1000;
			return String.valueOf(difference);
		}
		return "";
	}

}
