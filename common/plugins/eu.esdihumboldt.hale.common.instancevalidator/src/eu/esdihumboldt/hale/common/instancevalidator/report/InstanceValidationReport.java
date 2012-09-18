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

package eu.esdihumboldt.hale.common.instancevalidator.report;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * {@link Report} with {@link InstanceValidationMessage}s.
 * 
 * @author Kai Schwierczek
 */
public interface InstanceValidationReport extends Report<InstanceValidationMessage> {
	// nothing to add, just for concrete type...
}
