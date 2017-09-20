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

package eu.esdihumboldt.hale.common.core.report;

/**
 * Interface for classes that support injecting an operation log.
 * 
 * @author Simon Templer
 */
public interface LogAware {

	/**
	 * Set the log.
	 * 
	 * A <code>null</code> log can be set to indicate that an operation is
	 * finished. In that case the log aware object should return to it's
	 * internal default regarding logging.
	 * 
	 * @param log the log, may be <code>null</code>
	 */
	void setLog(SimpleLog log);

}
