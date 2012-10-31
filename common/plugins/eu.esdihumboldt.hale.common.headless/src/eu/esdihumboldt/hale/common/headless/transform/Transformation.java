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

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.google.common.io.Files;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.core.io.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.orient.storage.BrowseOrientInstanceCollection;
import eu.esdihumboldt.hale.common.instance.orient.storage.LocalOrientDB;
import eu.esdihumboldt.hale.common.instance.orient.storage.StoreInstancesJob;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Utility class for handling batch transformation. Uses {@link ExportJob} and
 * {@link ValidationJob}.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class Transformation {

//	public static void transform(List<InstanceReader> sources, InstanceWriter target,
//			TransformationEnvironment environment) {
//
//	}

	/**
	 * Transform the given instances, according to the given alignment.
	 * 
	 * @param sources the collection of source instances
	 * @param targetSink the target sink
	 * @param exportJob the export job
	 * @param validationJob the validation job, may be <code>null</code>
	 * @param alignment the alignment, may not be changed outside this method
	 * @param sourceSchema the source schema
	 * @param reportHandler the report handler
	 */
	public static void transform(InstanceCollection sources, final LimboInstanceSink targetSink,
			final ExportJob exportJob, final ValidationJob validationJob,
			final Alignment alignment, SchemaSpace sourceSchema, final ReportHandler reportHandler) {
		final InstanceCollection sourceToUse;

		// Check whether to create a temporary database or not.
		// Currently do not create a temporary DB is there are Retypes only.
		boolean useTempDatabase = false;
		final LocalOrientDB db;
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
			tmpDir.deleteOnExit();

			// get instance collection
			sourceToUse = new BrowseOrientInstanceCollection(db, sourceSchema, DataSet.SOURCE);
		}
		else {
			sourceToUse = sources;
			db = null;
		}

		// create transformation job
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
				reportHandler.publishReport(report);

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

				if (db != null)
					db.delete();
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
					"Load source instances into temporary database", db, sources) {

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
	}

}
