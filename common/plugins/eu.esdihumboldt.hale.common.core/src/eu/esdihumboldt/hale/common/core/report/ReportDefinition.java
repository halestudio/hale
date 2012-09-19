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

package eu.esdihumboldt.hale.common.core.report;

import eu.esdihumboldt.util.definition.ObjectDefinition;

/**
 * String representations of {@link Report} are explicitly allowed to span
 * multiple lines. Identifiers must begin with the {@link #ID_PREFIX}.
 * 
 * @author Andreas Burchert
 * @param <T> the report type
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface ReportDefinition<T extends Report<?>> extends ObjectDefinition<T> {

	/**
	 * The common ID prefix for all message definitions
	 */
	public static final String ID_PREFIX = "!REPORT_";

	// concrete typed interface

}
