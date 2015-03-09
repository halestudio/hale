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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.transform;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;

/**
 * Job for exporting transformed data supplied in a {@link TransformationSink}.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class ExportJob extends AbstractTransformationJob {

	private static final ALogger log = ALoggerFactory.getLogger(ExportJob.class);

	private TransformationSink targetSink;
	private InstanceWriter writer;
	private IOAdvisor<InstanceWriter> advisor;
	private ReportHandler reportHandler;

	/**
	 * Create a job for exporting transformed data supplied in the given target
	 * sink.
	 * 
	 * @param targetSink the target sink
	 * @param writer the instance writer
	 * @param advisor the advisor, to handle the results
	 * @param reportHandler the report handler
	 */
	public ExportJob(TransformationSink targetSink, InstanceWriter writer,
			IOAdvisor<InstanceWriter> advisor, ReportHandler reportHandler) {
		super("Encoding");

		this.targetSink = targetSink;
		this.writer = writer;
		this.advisor = advisor;
		this.reportHandler = reportHandler;
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.Job#canceling()
	 */
	@Override
	protected void canceling() {
		// must cancel sink, otherwise it may be blocking
		targetSink.done(true);
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IOReporter defaultReporter = writer.createReporter();
		defaultReporter.setSuccess(false);
		IOReport report = defaultReporter;
		try {
			ATransaction trans = log.begin(defaultReporter.getTaskName());
			try {
				IOReport result = writer.execute(new ProgressMonitorIndicator(monitor));
				if (result != null) {
					report = result;
				}
				else {
					defaultReporter.setSuccess(true);
				}
			} catch (Throwable e) {
				defaultReporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
			} finally {
				trans.end();
			}
		} catch (Throwable e) {
			defaultReporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
		}

		if (monitor.isCanceled()) {
			reset();
			return Status.CANCEL_STATUS;
		}

		// add report to report service
		reportHandler.publishReport(report);

		// show message to user
		if (report.isSuccess()) {
			// no message, we rely on the report being
			// shown/processed

			// let advisor handle results
			advisor.handleResults(writer);

			reset();

			return Status.OK_STATUS;
		}
		else {
			reset();
			log.userError(report.getSummary());
			return ERROR_STATUS;
		}
	}

	/**
	 * Reset the Job so no references to other objects reside.
	 * 
	 * Necessary as jobs are referenced by the job manager even after execution.
	 */
	private void reset() {
		writer = null;
		targetSink = null;
		advisor = null;
		reportHandler = null;
	}

}
