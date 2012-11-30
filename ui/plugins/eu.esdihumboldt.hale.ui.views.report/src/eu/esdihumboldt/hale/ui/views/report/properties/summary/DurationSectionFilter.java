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

package eu.esdihumboldt.hale.ui.views.report.properties.summary;

import org.eclipse.jface.viewers.IFilter;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Filter for report duration. Section should only be shown if a start time is
 * present in the report.
 * 
 * @author Simon Templer
 */
public class DurationSectionFilter implements IFilter {

	@Override
	public boolean select(Object toTest) {
		return toTest instanceof Report<?> && ((Report<?>) toTest).getStartTime() != null;
	}

}
