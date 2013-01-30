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
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.core.ServiceProvider;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.headless.HeadlessIO;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
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

	/**
	 * Transform the instances provided through the given instance readers and
	 * supply the result to the given instance writer.
	 * 
	 * @param sources the instance readers
	 * @param target the target instance writer
	 * @param environment the transformation environment
	 * @param reportHandler the report handler
	 * @param processId the identifier for the transformation process, may be
	 *            <code>null</code> if grouping the jobs to a job family is not
	 *            necessary
	 * @return the future representing the successful completion of the
	 *         transformation (note that a successful completion doesn't
	 *         necessary mean there weren't any internal transformation errors)
	 */
	public static ListenableFuture<Boolean> transform(List<InstanceReader> sources,
			InstanceWriter target, final TransformationEnvironment environment,
			final ReportHandler reportHandler, Object processId) {
		final IOAdvisor<InstanceReader> loadDataAdvisor = new AbstractIOAdvisor<InstanceReader>() {

			/**
			 * @see IOAdvisor#prepareProvider(IOProvider)
			 */
			@Override
			public void prepareProvider(InstanceReader provider) {
				super.prepareProvider(provider);

				provider.setSourceSchema(environment.getSourceSchema());
			}

			/**
			 * @see AbstractIOAdvisor#updateConfiguration(IOProvider)
			 */
			@Override
			public void updateConfiguration(InstanceReader provider) {
				super.updateConfiguration(provider);

				if (environment instanceof ProjectTransformationEnvironment) {
					// set project CRS manager as CRS provider
					/*
					 * Resource based CRS settings will however not work, as the
					 * resource identifiers will not match
					 */
					provider.setCRSProvider(new ProjectCRSManager(provider, null,
							((ProjectTransformationEnvironment) environment).getProject()));
				}
			}
		};

		List<InstanceCollection> sourceList = Lists.transform(sources,
				new Function<InstanceReader, InstanceCollection>() {

					@Override
					public InstanceCollection apply(@Nullable InstanceReader input) {
						try {
							HeadlessIO.executeProvider(input, loadDataAdvisor, null, reportHandler);
							// XXX progress?!
						} catch (IOException e) {
							throw new IllegalStateException("Failed to load source data", e);
						}
						return input.getInstances();
					}
				});

		MultiInstanceCollection sourceCollection = new MultiInstanceCollection(sourceList);

		final LimboInstanceSink targetSink = new LimboInstanceSink();

		IOAdvisor<InstanceWriter> saveDataAdvisor = new AbstractIOAdvisor<InstanceWriter>() {

			/**
			 * @see IOAdvisor#prepareProvider(IOProvider)
			 */
			@Override
			public void prepareProvider(InstanceWriter provider) {
				super.prepareProvider(provider);

				// set target schema
				provider.setTargetSchema(environment.getTargetSchema());

				// set instances to export
				provider.setInstances(targetSink.getInstanceCollection());
			}

		};

		saveDataAdvisor.prepareProvider(target);
		saveDataAdvisor.updateConfiguration(target);

		ExportJob exportJob = new ExportJob(targetSink, target, saveDataAdvisor, reportHandler);
		ValidationJob validationJob = null; // no validation
		return transform(sourceCollection, targetSink, exportJob, validationJob,
				environment.getAlignment(), environment.getSourceSchema(), reportHandler,
				environment, processId);
	}

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
	 * @param serviceProvider the service provider in the transformation context
	 * @param processId the identifier for the transformation process, may be
	 *            <code>null</code> if grouping the jobs to a job family is not
	 *            necessary
	 * @return the future representing the successful completion of the
	 *         transformation (note that a successful completion doesn't
	 *         necessary mean there weren't any internal transformation errors)
	 */
	public static ListenableFuture<Boolean> transform(InstanceCollection sources,
			final LimboInstanceSink targetSink, final ExportJob exportJob,
			final ValidationJob validationJob, final Alignment alignment, SchemaSpace sourceSchema,
			final ReportHandler reportHandler, final ServiceProvider serviceProvider,
			final Object processId) {
		final SettableFuture<Boolean> result = SettableFuture.create();

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
		final AbstractTransformationJob transformJob = new AbstractTransformationJob(
				"Transformation") {

			/**
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				TransformationService transformationService = OsgiUtils
						.getService(TransformationService.class);

				TransformationReport report = transformationService.transform(alignment,
						sourceToUse, targetSink, serviceProvider, new ProgressMonitorIndicator(
								monitor));

				if (monitor.isCanceled()) {
					targetSink.done(true);
					return Status.CANCEL_STATUS;
				}
				else
					targetSink.done(false);

				// publish report
				reportHandler.publishReport(report);

				if (report.isSuccess()) {
					return Status.OK_STATUS;
				}
				else {
					return ERROR_STATUS;
				}
			}
		};

		// set process IDs to group jobs in a job family
		if (processId != null) {
			transformJob.setProcessId(processId);
			exportJob.setProcessId(processId);
			if (validationJob != null) {
				validationJob.setProcessId(processId);
			}
		}

		exportJob.setUser(true);

		// the jobs should cancel each other
		transformJob.addJobChangeListener(new JobChangeAdapter() {

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

			@Override
			public void done(IJobChangeEvent event) {
				if (!event.getResult().isOK()) {
					transformJob.cancel();

					// failure
					failure(result, event);
				}
				else {
					// success
					result.set(true);

					if (validationJob != null) {
						validationJob.schedule();
					}
				}
			}
		});

		if (useTempDatabase) {
			// run store instance job first...
			Job storeJob = new StoreInstancesJob("Load source instances into temporary database",
					db, sources, reportHandler) {

				@Override
				protected void onComplete() {
					// onComplete is also called if monitor is cancelled...
				}

				@Override
				public boolean belongsTo(Object family) {
					if (processId == null) {
						return super.belongsTo(family);
					}

					return AbstractTransformationJob.createFamily(processId).equals(family);
				}

			};
			// and schedule jobs on successful completion
			storeJob.addJobChangeListener(new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {
					if (event.getResult().isOK()) {
						exportJob.schedule();
						transformJob.schedule();
					}
					else {
						failure(result, event);
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

		return result;
	}

	private static void failure(SettableFuture<Boolean> result, IJobChangeEvent event) {
		// signal if was canceled
		/*
		 * XXX disabled as the transform job will cancel the export job if it
		 * fails
		 */
//		if (event.getResult().matches(IStatus.CANCEL)) {
//			result.cancel(false);
//		}

		// failed - try setting exception
		if (event.getResult() != null && event.getResult().getException() != null) {
			result.setException(event.getResult().getException());
		}

		// in case there was no exception or setting it failed, just state that
		// execution was not successful
		result.set(false);
	}
}
