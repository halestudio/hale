/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.core.io.report;

import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;

/**
 * Report for I/O tasks that supports changing the target
 * 
 * @author Florian Esser
 */
public interface MutableTargetIOReport extends IOReport {

	/**
	 * Change the target of this {@link IOReport}
	 * 
	 * @param target new target
	 */
	void setTarget(Locatable target);
}
