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

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * A {@link ReportSession} contains all {@link Report}s from a session, which is
 * currently based on a date.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class ReportSession {

	/**
	 * Contains the session id.
	 */
	private long id;

	/**
	 * Contains all reports.
	 */
	private Map<Class<? extends Message>, Multimap<Class<? extends Report<?>>, Report<?>>> reports = new HashMap<Class<? extends Message>, Multimap<Class<? extends Report<?>>, Report<?>>>();

	/**
	 * Constructor. The timestamp is used as an identifier.
	 * 
	 * @param timestamp the timestamp
	 */
	public ReportSession(long timestamp) {
		this.id = timestamp;
	}

	/**
	 * Returns the the id of this session
	 * 
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Add a {@link Report} to this session.
	 * 
	 * @param report the report
	 */
	@SuppressWarnings("unchecked")
	public <M extends Message, R extends Report<M>> void addReport(R report) {
		// get all reports for this messageType
		Multimap<Class<? extends Report<?>>, Report<?>> reportMap = getReports(report
				.getMessageType());

		// add the report to temporary map
		reportMap.put((Class<? extends Report<?>>) report.getClass(), report);

		// add them to internal storage
		this.reports.put(report.getMessageType(), reportMap);
	}

	/**
	 * Get all reports matching the given message type
	 * 
	 * @param messageType the message type
	 * 
	 * @return report types mapped to reports
	 */
	public Multimap<Class<? extends Report<?>>, Report<?>> getReports(
			Class<? extends Message> messageType) {
		// get a map
		Multimap<Class<? extends Report<?>>, Report<?>> map = this.reports.get(messageType);

		// if does not exists: create and add it
		if (map == null) {
			map = HashMultimap.create();
			this.reports.put(messageType, map);
		}
		return map;
	}

	/**
	 * Get all reports.
	 * 
	 * @return reports
	 */
	public Multimap<Class<? extends Report<?>>, Report<?>> getAllReports() {
		// create a map
		Multimap<Class<? extends Report<?>>, Report<?>> reportMap = HashMultimap.create();

		// iterate through all reports
		for (Multimap<Class<? extends Report<?>>, Report<?>> map : this.reports.values()) {
			reportMap.putAll(map);
		}

		return reportMap;
	}
}
