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

package eu.esdihumboldt.hale.common.core.report.impl;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Default report implementation
 * 
 * @param <T> the message type
 * 
 * @author Simon Templer
 */
public class DefaultReporter<T extends Message> extends AllInMemoryReporter<T> {

	private static final ALogger log = ALoggerFactory.getLogger(DefaultReporter.class);

	/**
	 * Maximum number of messages per message type in a report. Negative values
	 * mean unlimited messages.
	 */
	public static final int MESSAGE_CAP = getMessageCap();

	private int totalErrors = 0;
	private int totalWarnings = 0;
	private int totalInfos = 0;

	/**
	 * Create an empty report. It is set to not successful by default. But you
	 * should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @param taskName the name of the task the report is related to
	 * @param messageType the message type
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public DefaultReporter(String taskName, Class<T> messageType, boolean doLog) {
		super(taskName, messageType, doLog);
	}

	/**
	 * Determine message cap from system property or environment variable,
	 * otherwise return a default value.
	 * 
	 * @return the message cap
	 */
	private static int getMessageCap() {
		String setting = System.getProperty("hale.reports.message_cap");

		if (setting == null) {
			setting = System.getenv("HALE_REPORTS_MESSAGE_CAP");
		}

		if (setting != null) {
			try {
				return Integer.valueOf(setting);
			} catch (Throwable e) {
				log.error("Error applying custom report message cap setting: " + setting, e);
			}
		}

		// default to fall back to
		return 1000;
	}

	@Override
	public void warn(T message) {
		if (MESSAGE_CAP < 0 || super.getTotalWarnings() < MESSAGE_CAP) {
			super.warn(message);
		}
		// should we log messages above cap? Probably not to not pollute the log
		totalWarnings++;
	}

	@Override
	public void error(T message) {
		if (MESSAGE_CAP < 0 || super.getTotalErrors() < MESSAGE_CAP) {
			super.error(message);
		}
		// should we log messages above cap? Probably not to not pollute the log
		totalErrors++;
	}

	@Override
	public void info(T message) {
		if (MESSAGE_CAP < 0 || super.getTotalInfos() < MESSAGE_CAP) {
			super.info(message);
		}
		// should we log messages above cap? Probably not to not pollute the log
		totalInfos++;
	}

	@Override
	public int getTotalWarnings() {
		return totalWarnings;
	}

	@Override
	public int getTotalErrors() {
		return totalErrors;
	}

	@Override
	public int getTotalInfos() {
		return totalInfos;
	}

	@Override
	public void importMessages(Report<? extends T> report) {
		for (T error : report.getErrors()) {
			if (MESSAGE_CAP < 0 || errors.size() < MESSAGE_CAP) {
				errors.add(error);
			}
		}
		totalErrors += report.getTotalErrors();

		for (T warn : report.getWarnings()) {
			if (MESSAGE_CAP < 0 || warnings.size() < MESSAGE_CAP) {
				warnings.add(warn);
			}
		}
		totalWarnings += report.getTotalWarnings();

		for (T info : report.getInfos()) {
			if (MESSAGE_CAP < 0 || infos.size() < MESSAGE_CAP) {
				infos.add(info);
			}
		}
		totalInfos += report.getTotalInfos();
	}

	@Override
	public void countError(int number) {
		totalErrors += number;
	}

	@Override
	public void countWarning(int number) {
		totalWarnings += number;
	}

	@Override
	public void countInfo(int number) {
		totalInfos += number;
	}

}
