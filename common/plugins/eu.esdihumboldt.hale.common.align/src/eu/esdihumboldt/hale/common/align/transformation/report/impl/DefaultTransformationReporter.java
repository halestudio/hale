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

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportLog;
import eu.esdihumboldt.hale.common.core.report.impl.AbstractReporter;

/**
 * Reporter for transformation messages
 * 
 * @author Simon Templer
 */
public class DefaultTransformationReporter extends AbstractReporter<TransformationMessage>
		implements TransformationReport, TransformationReporter {

	/**
	 * Maximum number of messages per cell. Negative values mean unlimited
	 * messages.
	 */
	private static final int MESSAGE_CAP = getMessageCap();

	/**
	 * Determine message cap from system property or environment variable,
	 * otherwise return a default value.
	 * 
	 * @return the message cap
	 */
	private static int getMessageCap() {
		String setting = System.getProperty("hale.reports.message_cap.per_cell");

		if (setting == null) {
			setting = System.getenv("HALE_REPORTS_MESSAGE_CAP_PER_CELL");
		}

		if (setting != null) {
			try {
				return Integer.valueOf(setting);
			} catch (Throwable e) {
				log.error("Error applying custom report message cap setting: " + setting, e);
			}
		}

		// default to fall back to
		return 100;
	}

	/**
	 * The logger
	 */
	private static final ALogger log = ALoggerFactory
			.getMaskingLogger(DefaultTransformationReporter.class, null);

	/**
	 * Transformation message key that decides on message equality. Messages are
	 * equal if the cell ID and message are the same, the stack trace is
	 * ignored.
	 */
	public class TMessageKey {

		private final TransformationMessage message;

		/**
		 * Create a transformation message key.
		 * 
		 * @param message the original message
		 */
		public TMessageKey(TransformationMessage message) {
			this.message = message;
		}

		/**
		 * @return the original message
		 */
		public TransformationMessage getMessage() {
			return message;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((message == null || message.getCellId() == null) ? 0
					: message.getCellId().hashCode());
			result = prime * result + ((message == null || message.getMessage() == null) ? 0
					: message.getMessage().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TMessageKey other = (TMessageKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (message == null) {
				return other.message == null;
			}
			if (message.getCellId() == null) {
				if (other.message.getCellId() != null)
					return false;
			}
			else if (!message.getCellId().equals(other.message.getCellId()))
				return false;
			if (message.getMessage() == null) {
				if (other.message.getMessage() != null)
					return false;
			}
			else if (!message.getMessage().equals(other.message.getMessage()))
				return false;
			return true;
		}

		private DefaultTransformationReporter getOuterType() {
			return DefaultTransformationReporter.this;
		}

	}

	/**
	 * Organizes transformation messages.
	 */
	public class TMessages {

		/**
		 * Cell IDs mapped to message keys.
		 */
		private final Map<String, Multiset<TMessageKey>> messages = new HashMap<String, Multiset<TMessageKey>>();

		private int more = 0;

		/**
		 * Add a message.
		 * 
		 * @param message the message to add
		 */
		public void add(TransformationMessage message) {
			String cell = message.getCellId();
			Multiset<TMessageKey> msgs = messages.get(cell);

			if (msgs == null) {
				msgs = LinkedHashMultiset.create();
				messages.put(cell, msgs);
			}
			else if (MESSAGE_CAP >= 0 && msgs.elementSet().size() >= MESSAGE_CAP) {
				// max messages per cell
				// we don't store this message, just count it
				more++;
				return;
			}
			TMessageKey insert = new TMessageKey(message);
			msgs.add(insert);
		}

		/**
		 * Get the collected messages, if applicable extended with the message
		 * count.
		 * 
		 * @return the messages
		 */
		public Collection<TransformationMessage> getMessages() {
			Collection<TransformationMessage> result = new ArrayList<TransformationMessage>();

			for (Multiset<TMessageKey> msgs : messages.values()) {
				for (Entry<TMessageKey> entry : msgs.entrySet()) {
					if (entry.getCount() > 1) {
						TransformationMessage org = entry.getElement().getMessage();
						Throwable throwable = org.getThrowable();
						String stackTrace = null;
						if (throwable == null) {
							stackTrace = org.getStackTrace();
						}
						result.add(new TransformationMessageImpl(org.getCellId(),
								org.getMessage() + " (" + entry.getCount() + " times)", throwable,
								stackTrace));
					}
					else {
						result.add(entry.getElement().getMessage());
					}
				}
			}

			return result;
		}

		/**
		 * Add more messages that are only represented with the count.
		 * 
		 * @param more the number of messages to add
		 */
		public void addMore(int more) {
			more += more;
		}

		/**
		 * @return the count of messages represented
		 */
		public int getMessageCount() {
			int count = more;

			for (Multiset<TMessageKey> msgs : messages.values()) {
				// only count each key once, as the messages are represented
				count += msgs.elementSet().size();
			}

			return count;
		}

		/**
		 * Determines if there are any messages contained.
		 * 
		 * @return if there are any messages present
		 */
		public boolean hasMessages() {
			return !messages.isEmpty() || more > 0;
		}
	}

	private final boolean doLog;

	private final TMessages warn = new TMessages();

	private final TMessages error = new TMessages();

	private final TMessages info = new TMessages();

	/**
	 * Create an empty report. It is set to not successful by default. But you
	 * should call {@link #setSuccess(boolean)} nonetheless to update the
	 * timestamp after the task has finished.
	 * 
	 * @param taskName the name of the task the report is related to
	 * @param doLog if added messages shall also be logged using {@link ALogger}
	 */
	public DefaultTransformationReporter(String taskName, boolean doLog) {
		super(taskName, TASK_TYPE, TransformationMessage.class);

		this.doLog = doLog;
	}

	@Override
	public void warn(TransformationMessage message) {
		warn.add(message);
	}

	@Override
	public void error(TransformationMessage message) {
		error.add(message);
	}

	@Override
	public void info(TransformationMessage message) {
		info.add(message);
	}

	@Override
	public Collection<TransformationMessage> getWarnings() {
		return warn.getMessages();
	}

	@Override
	public Collection<TransformationMessage> getErrors() {
		return error.getMessages();
	}

	@Override
	public Collection<TransformationMessage> getInfos() {
		return info.getMessages();
	}

	@Override
	public void countError(int number) {
		error.addMore(number);
	}

	@Override
	public void countWarning(int number) {
		warn.addMore(number);
	}

	@Override
	public void countInfo(int number) {
		info.addMore(number);
	}

	@Override
	public int getTotalWarnings() {
		return warn.getMessageCount();
	}

	@Override
	public int getTotalErrors() {
		return error.getMessageCount();
	}

	@Override
	public int getTotalInfos() {
		return info.getMessageCount();
	}

	@Override
	public void setSuccess(boolean success) {
		super.setSuccess(success);

		if (doLog) {
			// print warnings
			for (TransformationMessage msg : getWarnings()) {
				log.warn(msg.getMessage(), msg.getThrowable());
			}

			// print errors
			for (TransformationMessage msg : getErrors()) {
				log.error(msg.getMessage(), msg.getThrowable());
			}

			// print summary
			String message = getTaskName() + " - " + getSummary();

			if (error.hasMessages()) {
				log.error(message);
			}
			else if (warn.hasMessages()) {
				log.warn(message);
			}
			else {
				log.info(message);
			}
		}
	}

	/**
	 * Add all messages of the given report to this report. They may the logged
	 * (again) with a call to {@link #setSuccess(boolean)}.
	 * 
	 * @see ReportLog#importMessages(Report)
	 */
	@Override
	public void importMessages(Report<? extends TransformationMessage> report) {
		for (TransformationMessage message : report.getErrors()) {
			error.add(message);
		}
		int errorMore = report.getTotalErrors() - report.getErrors().size();
		if (errorMore > 0) {
			error.addMore(errorMore);
		}
		for (TransformationMessage message : report.getWarnings()) {
			warn.add(message);
		}
		int warnMore = report.getTotalWarnings() - report.getWarnings().size();
		if (warnMore > 0) {
			warn.addMore(warnMore);
		}
		for (TransformationMessage message : report.getInfos()) {
			info.add(message);
		}
		int infoMore = report.getTotalInfos() - report.getInfos().size();
		if (infoMore > 0) {
			info.addMore(infoMore);
		}
	}
}
