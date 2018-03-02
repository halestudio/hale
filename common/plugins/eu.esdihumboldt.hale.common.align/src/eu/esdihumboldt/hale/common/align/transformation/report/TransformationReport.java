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

package eu.esdihumboldt.hale.common.align.transformation.report;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Interface for transformation reports.
 * 
 * @author Simon Templer
 */
public interface TransformationReport extends Report<TransformationMessage> {

	/**
	 * Task type for transformation reports.
	 */
	public static final String TASK_TYPE = "eu.esdihumboldt.hale.transform";

}
