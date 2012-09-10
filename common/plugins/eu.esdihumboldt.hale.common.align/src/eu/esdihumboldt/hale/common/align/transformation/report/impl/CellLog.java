/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.transformation.report.impl;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationMessage;
import eu.esdihumboldt.hale.common.core.report.Message;
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

}
