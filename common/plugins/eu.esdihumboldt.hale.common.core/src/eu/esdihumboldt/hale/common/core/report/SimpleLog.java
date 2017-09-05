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
 * Interface for providing a simple logging interface.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public interface SimpleLog {

	void warn(String message, Throwable e);

	default void warn(String message) {
		warn(message, null);
	}

	void error(String message, Throwable e);

	default void error(String message) {
		error(message, null);
	}

	void info(String message, Throwable e);

	default void info(String message) {
		info(message, null);
	}

}
