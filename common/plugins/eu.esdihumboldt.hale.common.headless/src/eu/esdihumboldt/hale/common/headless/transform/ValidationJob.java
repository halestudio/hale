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
import eu.esdihumboldt.hale.common.core.io.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;

/**
 * Job for validating transformed instances.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class ValidationJob extends AbstractTransformationJob {

	private static final ALogger log = ALoggerFactory.getLogger(ValidationJob.class);

	private ReportHandler reportHandler;
	private InstanceValidator validator;

	/**
	 * Create a job for validating transformed instances.
	 * 
	 * @param validator the validator
	 * @param reportHandler the report handler
	 */
	public ValidationJob(InstanceValidator validator, ReportHandler reportHandler) {
		super("Validation");

		this.validator = validator;
		this.reportHandler = reportHandler;
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IOReporter defaultReporter = validator.createReporter();
		defaultReporter.setSuccess(false);
		IOReport report = defaultReporter;
		try {
			ATransaction trans = log.begin(defaultReporter.getTaskName());
			try {
				IOReport result = validator.execute(new ProgressMonitorIndicator(monitor));
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
			// info message
			reset();
			log.userInfo(report.getSummary());
			return Status.OK_STATUS;
		}
		else {
			// error message
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
		validator = null;
		reportHandler = null;
	}

}
