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
import org.eclipse.core.runtime.jobs.Job;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
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
public class ValidationJob extends Job {

	private static final ALogger log = ALoggerFactory.getLogger(ValidationJob.class);

	private final ReportHandler reportHandler;
	private final InstanceValidator validator;

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

		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;

		// add report to report service
		reportHandler.publishReport(report);

		// show message to user
		if (report.isSuccess()) {
			// info message
			log.userInfo(report.getSummary());
		}
		else {
			// error message
			log.userError(report.getSummary());
		}

		// XXX return something depending on report success?
		return Status.OK_STATUS;
	}

}
