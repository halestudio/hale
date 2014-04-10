/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.internal;

import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Wrapper for {@link TransformationLog} for Groovy scripts.
 * 
 * @author Kai Schwierczek
 */
public class TransformationLogWrapper {

	private final TransformationLog log;

	/**
	 * Constructor.
	 * 
	 * @param log the cell log to use
	 */
	public TransformationLogWrapper(TransformationLog log) {
		this.log = log;
	}

	/**
	 * Adds an info to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the message string
	 */
	public void info(String message) {
		info(message, null);
	}

	/**
	 * Adds an info to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the message string
	 * @param throwable a throwable associated to the message, may be null
	 */
	public void info(String message, Throwable throwable) {
		log.info(log.createMessage(message, throwable));
	}

	/**
	 * Adds a warning to the report. If configured accordingly a log message
	 * will also be created.
	 * 
	 * @param message the message string
	 */
	public void warn(String message) {
		warn(message, null);
	}

	/**
	 * Adds a warning to the report. If configured accordingly a log message
	 * will also be created.
	 * 
	 * @param message the message string
	 * @param throwable a throwable associated to the message, may be null
	 */
	public void warn(String message, Throwable throwable) {
		log.warn(log.createMessage(message, throwable));
	}

	/**
	 * Adds an error to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the message string
	 */
	public void error(String message) {
		error(message, null);
	}

	/**
	 * Adds an error to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the message string
	 * @param throwable a throwable associated to the message, may be null
	 */
	public void error(String message, Throwable throwable) {
		log.error(log.createMessage(message, throwable));
	}
}
