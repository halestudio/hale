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

import org.eclipse.swt.widgets.Text;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.ui.views.properties.AbstractSimpleTextSection;

/**
 * Report duration section.
 * 
 * @author Simon Templer
 */
public class DurationSection extends AbstractSimpleTextSection {

	@Override
	protected void updateControl(Object input, Text control) {
		Report<?> report = (Report<?>) input;
		control.setText(PeriodFormat.wordBased().print(
				new Period(report.getStartTime().getTime(), report.getTimestamp().getTime())));
	}

	@Override
	protected String getPropertyName() {
		return "Duration";
	}

}
