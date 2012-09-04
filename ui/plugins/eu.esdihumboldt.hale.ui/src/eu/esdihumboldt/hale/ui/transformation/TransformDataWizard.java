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

package eu.esdihumboldt.hale.ui.transformation;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import com.google.common.io.Files;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.instance.internal.orient.BrowseOrientInstanceCollection;
import eu.esdihumboldt.hale.ui.service.instance.internal.orient.LocalOrientDB;
import eu.esdihumboldt.hale.ui.service.instance.internal.orient.StoreInstancesJob;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

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
		InstanceCollection rawSources = new MultiInstanceCollection(
				sourceSelectionPage.getSourceInstances());
		final InstanceCollection sourceToUse;

		// Create a copy of the current alignment to be independent and run
		// everything in a job.
		AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench()
				.getService(AlignmentService.class);
		final Alignment alignment = new DefaultAlignment(alignmentService.getAlignment());

		// Check whether to create a temporary database or not.
		// Currently do not create a temporary DB is there are Retypes only.
		boolean useTempDatabase = false;
		LocalOrientDB db = null;
		for (Cell cell : alignment.getTypeCells())
			if (!RetypeFunction.ID.equals(cell.getTransformationIdentifier())) {
				useTempDatabase = true;
				break;
			}

		// Create temporary database if necessary.
		if (useTempDatabase) {
			// create db
			File tmpDir = Files.createTempDir();
			db = new LocalOrientDB(tmpDir);

			// get instance collection
			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(
					SchemaService.class);
			sourceToUse = new BrowseOrientInstanceCollection(db,
					ss.getSchemas(SchemaSpaceID.SOURCE), DataSet.SOURCE);
		}
		else
			sourceToUse = rawSources;

		// get / create jobs
		final Job exportJob = sourceSelectionPage.getExportJob();
		final Job validationJob = sourceSelectionPage.getValidationJob();
		final Job transformJob = new Job("Transform") {

			/**
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				TransformationService transformationService = OsgiUtils
						.getService(TransformationService.class);

				TransformationReport report = transformationService.transform(alignment,
						sourceToUse, targetSink, new ProgressMonitorIndicator(monitor));

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

		// the jobs should cancel each other
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
		// after export is done, validation should run
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

		if (useTempDatabase) {
			// run store instance job first...
			StoreInstancesJob storeJob = new StoreInstancesJob(
					"Load source instances into temporary database", db, rawSources) {

				@Override
				protected void onComplete() {
					// onComplete is also called if monitor is cancelled...
				}
			};
			// and schedule jobs on successful completion
			storeJob.addJobChangeListener(new JobChangeAdapter() {

				/**
				 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
				 */
				@Override
				public void done(IJobChangeEvent event) {
					if (event.getResult().isOK()) {
						exportJob.schedule();
						transformJob.schedule();
					}
				}
			});
			storeJob.schedule();
		}
		else {
			// otherwise schedule jobs directly
			exportJob.schedule();
			transformJob.schedule();
		}

		return true;
	}
}
