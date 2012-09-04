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

package eu.esdihumboldt.hale.ui.transformation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * Wizard for selecting source data files and a target file for a complete
 * transformation run.
 * 
 * @author Kai Schwierczek
 */
public class TransformDataWizard extends Wizard {

	private final TransformDataInstanceSink targetSink;
	private TransformDataWizardSourcePage sourceSelectionPage;

	/**
	 * Default constructor.
	 */
	public TransformDataWizard() {
		super();

		setWindowTitle("Transform data wizard");
		setForcePreviousAndNextButtons(true);

		targetSink = new TransformDataInstanceSink();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		sourceSelectionPage = new TransformDataWizardSourcePage(getContainer(), targetSink);
		addPage(sourceSelectionPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final InstanceCollection sources = new MultiInstanceCollection(
				sourceSelectionPage.getSourceInstances());
		// TODO write sources to temporary database for performance
		// if it contains any type mapping which needs references,
		// i. e. Join or Merge.
		// Maybe do it the other way around and do NOT create a temporary
		// database if
		// only Retype is used.

		final Job exportJob = sourceSelectionPage.getExportJob();
		final Job validationJob = sourceSelectionPage.getValidationJob();
		final Job transformJob = new Job("Transform") {

			/**
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench()
						.getService(AlignmentService.class);
				TransformationService transformationService = OsgiUtils
						.getService(TransformationService.class);

				// copy the alignment to be independent
				TransformationReport report = transformationService.transform(new DefaultAlignment(
						alignmentService.getAlignment()), sources, targetSink,
						new ProgressMonitorIndicator(monitor));

				if (monitor.isCanceled()) {
					targetSink.done(true);
					return Status.CANCEL_STATUS;
				}
				else
					targetSink.done(false);

				// publish report
				ReportService rs = (ReportService) PlatformUI.getWorkbench().getService(
						ReportService.class);
				rs.addReport(report);

				// XXX return something else, if report is not successful
				return Status.OK_STATUS;
			}
		};

		exportJob.setUser(true);

		transformJob.addJobChangeListener(new JobChangeAdapter() {

			/**
			 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
			 */
			@Override
			public void done(IJobChangeEvent event) {
				if (!event.getResult().isOK())
					exportJob.cancel();
			}
		});
		exportJob.addJobChangeListener(new JobChangeAdapter() {

			/**
			 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
			 */
			@Override
			public void done(IJobChangeEvent event) {
				if (!event.getResult().isOK())
					transformJob.cancel();
				else if (validationJob != null)
					validationJob.schedule();
			}
		});

		exportJob.schedule();
		transformJob.schedule();

		return true;
	}
}
