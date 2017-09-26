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

package eu.esdihumboldt.hale.common.core.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportLog;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Former default report implementation
 * 
 * @param <T> the message type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class AllInMemoryReporter<T extends Message> extends AbstractReporter<T> {

	/**
	 * The logger
	 */
	protected final SimpleLog log;

	/**
	 * The list of errors
	 */
	protected final List<T> errors = new ArrayList<T>();

	/**
	 * The list of warnings
	 */
	protected final List<T> warnings = new ArrayList<T>();

	/**
	 * The list of info messages
	 */
	protected final List<T> infos = new ArrayList<T>();

	/**
	 * The setting if entries should be logged when added
	 */
	protected final boolean doLog;

	/**
	 * Create an empty report. It is set to not successful by default. But you
	 * should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @param taskName the name of the task the report is related to
	 * @param messageType the message type
	 * @param doLog if added messages shall also be logged using the provided
	 *            logger
	 * @param log the log to use if doLog is enabled
	 */
	public AllInMemoryReporter(String taskName, Class<T> messageType, boolean doLog,
			SimpleLog log) {
		super(taskName, messageType);
		this.doLog = doLog;
		this.log = log;
	}

	/**
	 * Create an empty report. It is set to not successful by default. But you
	 * should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @param taskName the name of the task the report is related to
	 * @param messageType the message type
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public AllInMemoryReporter(String taskName, Class<T> messageType, boolean doLog) {
		this(taskName, messageType, doLog, SimpleLog
				.fromLogger(ALoggerFactory.getMaskingLogger(AllInMemoryReporter.class, null)));
	}

	/**
	 * Adds a warning to the report. If configured accordingly a log message
	 * will also be created.
	 * 
	 * @param message the warning message
	 */
	@Override
	public void warn(T message) {
		warnings.add(message);

		if (doLog) {
			log.warn(message.getMessage(), message.getThrowable());
		}
	}

	/**
	 * Adds an error to the report. If configured accordingly a log message will
	 * also be created.
	 * 
	 * @param message the error message
	 */
	@Override
	public void error(T message) {
		errors.add(message);

		if (doLog) {
			log.error(message.getMessage(), message.getThrowable());
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.ReportLog#info(eu.esdihumboldt.hale.common.core.report.Message)
	 */
	@Override
	public void info(T message) {
		infos.add(message);

		if (doLog) {
			log.info(message.getMessage(), message.getThrowable());
		}
	}

	/**
	 * @see Report#getErrors()
	 */
	@Override
	public Collection<T> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	/**
	 * @see Report#getWarnings()
	 */
	@Override
	public Collection<T> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	/**
	 * @see Report#getInfos()
	 */
	@Override
	public Collection<T> getInfos() {
		return Collections.unmodifiableList(infos);
	}

	/**
	 * Add all messages of the given report to this report. This method will
	 * never log the messages (because the original report may have logged them
	 * already.
	 * 
	 * @see ReportLog#importMessages(Report)
	 */
	@Override
	public void importMessages(Report<? extends T> report) {
		errors.addAll(report.getErrors());
		warnings.addAll(report.getWarnings());
		infos.addAll(report.getInfos());
	}

	@Override
	public void countError(int number) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void countWarning(int number) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void countInfo(int number) {
		throw new UnsupportedOperationException();
	}

}
