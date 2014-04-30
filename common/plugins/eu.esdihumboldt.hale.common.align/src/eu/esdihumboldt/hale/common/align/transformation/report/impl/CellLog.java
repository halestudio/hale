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

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportLog;

/**
 * Transformation log for a fixed cell.
 * 
 * @author Simon Templer
 */
@Immutable
public class CellLog implements TransformationLog {

	private final ReportLog<TransformationMessage> log;
	private final Cell cell;

	/**
	 * Create a transformation log based on the given cell
	 * 
	 * @param log the transformation message report log to decorate
	 * @param cell the cell the transformation messages shall be associated to
	 */
	public CellLog(ReportLog<TransformationMessage> log, Cell cell) {
		super();
		this.log = log;
		this.cell = cell;
	}

	/**
	 * @see ReportLog#warn(Message)
	 */
	@Override
	public void warn(TransformationMessage message) {
		log.warn(message);
	}

	/**
	 * @see ReportLog#error(Message)
	 */
	@Override
	public void error(TransformationMessage message) {
		log.error(message);
	}

	/**
	 * @see TransformationLog#createMessage(String, Throwable)
	 */
	@Override
	public TransformationMessage createMessage(String message, Throwable throwable) {
		return new TransformationMessageImpl(cell, message, throwable);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.ReportLog#info(eu.esdihumboldt.hale.common.core.report.Message)
	 */
	@Override
	public void info(TransformationMessage message) {
		log.info(message);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.report.ReportLog#importMessages(eu.esdihumboldt.hale.common.core.report.Report)
	 */
	@Override
	public void importMessages(Report<? extends TransformationMessage> report) {
		log.importMessages(report);
	}

}
