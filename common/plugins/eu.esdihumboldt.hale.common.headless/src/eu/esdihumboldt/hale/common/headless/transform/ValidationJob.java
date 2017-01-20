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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

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
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;

/**
 * Job for validating transformed instances.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class ValidationJob extends AbstractTransformationJob {

	private static final ALogger log = ALoggerFactory.getLogger(ValidationJob.class);

	private ReportHandler reportHandler;
	private final List<InstanceValidator> validators = new ArrayList<>();

	private final InstanceWriter writer;
	private final ServiceProvider serviceProvider;

	/**
	 * Create a job for validating transformed instances.
	 * 
	 * @param validators the validators
	 * @param reportHandler the report handler
	 * @param writer the instance writer
	 * @param serviceProvider the service provider
	 */
	public ValidationJob(Collection<InstanceValidator> validators, ReportHandler reportHandler,
			@Nullable InstanceWriter writer, ServiceProvider serviceProvider) {
		super("Validation");

		this.validators.addAll(validators);
		this.writer = writer;
		this.reportHandler = reportHandler;
		this.serviceProvider = serviceProvider;
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		boolean successful = true;

		for (InstanceValidator validator : this.validators) {
			IOReporter defaultReporter = validator.createReporter();
			defaultReporter.setSuccess(false);
			IOReport report = defaultReporter;
			try {
				ATransaction trans = log.begin(defaultReporter.getTaskName());
				try {
					if (writer != null) {
						// set validation schemas (may have been determined only
						// during writer execution)
						// set schemas
						List<? extends Locatable> schemas = writer.getValidationSchemas();
						validator.setSchemas(schemas.toArray(new Locatable[schemas.size()]));
					}
					validator.setServiceProvider(serviceProvider);

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
				log.info(report.getSummary());
			}
			else {
				// error message
				log.error(report.getSummary());
				successful = false;
			}
		}

		reset();
		if (successful) {
			log.userInfo("All validations completed successfully.");
			return Status.OK_STATUS;
		}
		else {
			log.userError("There were validation failures. Please check the reports for details.");
			return ERROR_STATUS;
		}
	}

	/**
	 * Reset the Job so no references to other objects reside.
	 * 
	 * Necessary as jobs are referenced by the job manager even after execution.
	 */
	private void reset() {
		validators.clear();
		reportHandler = null;
	}

}
