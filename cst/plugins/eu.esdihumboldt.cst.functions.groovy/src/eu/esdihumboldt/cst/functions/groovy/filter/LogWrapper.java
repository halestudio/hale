/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.cst.functions.groovy.filter;

import org.slf4j.Logger;
import org.slf4j.helpers.SubstituteLogger;

/**
 * Simple log delegating class.
 * 
 * @author Simon Templer
 */
public class LogWrapper extends SubstituteLogger {

	/**
	 * Create a new logger wrapper.
	 * 
	 * @param name the wrapper name
	 * @param logger the logger to wrap
	 */
	public LogWrapper(String name, Logger logger) {
		super(name);
		setDelegate(logger);
	}

}
