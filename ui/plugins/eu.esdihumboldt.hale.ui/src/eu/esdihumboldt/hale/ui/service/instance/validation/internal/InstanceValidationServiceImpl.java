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

package eu.esdihumboldt.hale.ui.service.instance.validation.internal;

import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReport;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instancevalidator.InstanceValidator;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationListener;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * Service that listens to the instance service and validates instances.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidationServiceImpl extends InstanceServiceAdapter
		implements InstanceValidationService {

	private final InstanceService instanceService;
	private final ReportService reportService;
	private InstanceValidationJob validationJob;
	private boolean liveValidation = true; // TODO store somewhere? project?
	private final CopyOnWriteArraySet<InstanceValidationListener> listeners = new CopyOnWriteArraySet<InstanceValidationListener>();

	/**
	 * Creates the instance validation service.
	 * 
	 * @param instanceService the instance service to use
	 * @param reportService the report service to use
	 */
	public InstanceValidationServiceImpl(InstanceService instanceService,
			ReportService reportService) {
		this.instanceService = instanceService;
		this.reportService = reportService;

		instanceService.addListener(this);
	}

	/**
	 * @see InstanceServiceListener#datasetChanged(DataSet)
	 */
	@Override
	public void datasetChanged(DataSet type) {
		// validate transformed instances
		if (type == DataSet.TRANSFORMED && liveValidation) {
			final InstanceCollection instances = instanceService.getInstances(DataSet.TRANSFORMED);
			if (instances.isEmpty())
				return;

			validationJob = new InstanceValidationJob(instances);
			validationJob.schedule();
			validationJob.addJobChangeListener(new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {
					validationJob = null;
				}
			});
		}
	}

	/**
	 * @see InstanceServiceAdapter#datasetAboutToChange(DataSet)
	 */
	@Override
	public void datasetAboutToChange(DataSet type) {
		if (type == DataSet.TRANSFORMED) {
			if (validationJob != null)
				validationJob.cancel();
		}
	}

	/**
	 * A Job that runs the instance validation.
	 * 
	 * @author Kai Schwierczek
	 */
	private class InstanceValidationJob extends Job {

		private InstanceCollection instances;

		private final InstanceValidator validator;

		/**
		 * Default constructor.
		 * 
		 * @param instances the instances to validate
		 */
		public InstanceValidationJob(InstanceCollection instances) {
			super("Instance validation");
			this.instances = instances;
			this.validator = InstanceValidator.createDefaultValidator(HaleUI.getServiceProvider());
		}

		/**
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			InstanceValidationReport report = validator.validateInstances(instances, monitor);
			if (!monitor.isCanceled()) {
				reportService.addReport(report);
				for (InstanceValidationListener listener : listeners) {
					listener.instancesValidated(report);
				}
			}
			else {
				return Status.CANCEL_STATUS;
			}
			instances = null;
			monitor.done();

			return Status.OK_STATUS;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService#addListener(eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationListener)
	 */
	@Override
	public void addListener(InstanceValidationListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService#removeListener(eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationListener)
	 */
	@Override
	public void removeListener(InstanceValidationListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService#isValidationEnabled()
	 */
	@Override
	public boolean isValidationEnabled() {
		return liveValidation;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService#setValidationEnabled(boolean)
	 */
	@Override
	public void setValidationEnabled(boolean enable) {
		if (!enable) {
			if (validationJob != null)
				validationJob.cancel();
		}
		liveValidation = enable;
	}
}
