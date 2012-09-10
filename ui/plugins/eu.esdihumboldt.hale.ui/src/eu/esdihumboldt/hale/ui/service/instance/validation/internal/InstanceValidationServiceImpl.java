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

package eu.esdihumboldt.hale.ui.service.instance.validation.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instancevalidator.InstanceValidator;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationReport;
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
public class InstanceValidationServiceImpl extends InstanceServiceAdapter implements
		InstanceValidationService {

	private final InstanceService instanceService;
	private final ReportService reportService;
	private InstanceValidationJob validationJob;
	private boolean liveValidation = true; // TODO store somewhere? project?
	private final TypeSafeListenerList<InstanceValidationListener> listeners = new TypeSafeListenerList<InstanceValidationListener>();

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

		InstanceCollection instances;

		/**
		 * Default constructor.
		 * 
		 * @param instances the instances to validate
		 */
		public InstanceValidationJob(InstanceCollection instances) {
			super("Instance validation");
			this.instances = instances;
		}

		/**
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			InstanceValidationReport report = InstanceValidator.validateInstances(instances,
					monitor);
			if (!monitor.isCanceled()) {
				reportService.addReport(report);
				for (InstanceValidationListener listener : listeners)
					listener.instancesValidated(report);
			}
			else
				return Status.CANCEL_STATUS;
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
