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

package eu.esdihumboldt.hale.ui.service.instance.validation;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instancevalidator.InstanceValidator;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.util.ThreadProgressMonitor;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * Service that listens to the instance service and validates instances.
 *
 * @author Kai Schwierczek
 */
public class InstanceValidationService extends InstanceServiceAdapter {
	private static final ALogger log = ALoggerFactory.getLogger(InstanceValidationService.class);

	private final InstanceService instanceService;
	private final ReportService reportService;

	/**
	 * Creates the instance validation service.
	 *
	 * @param instanceService the instance service to use
	 * @param reportService the report service to use
	 */
	public InstanceValidationService(InstanceService instanceService,
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
		if (type == DataSet.TRANSFORMED) {
			final InstanceCollection instances = instanceService.getInstances(DataSet.TRANSFORMED);
			if (instances.isEmpty())
				return;

			final AtomicBoolean validationFinished = new AtomicBoolean(false);
			IRunnableWithProgress op = new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("Instance validation", instances.size());
					try {
						Report<Message> report = InstanceValidator.validateInstances(instances, monitor);
						if (!monitor.isCanceled())
							reportService.addReport(report);
						monitor.done();
					} finally {						
						// transformation finished
						validationFinished.set(true);
					}
				}
					
			};

			try {
				ThreadProgressMonitor.runWithProgressDialog(op, true);
			} catch (Throwable e) {
				log.error("Error starting instance validation", e);
			}

			// wait for validation to complete
			HaleUI.waitFor(validationFinished);
		}
	}
}
