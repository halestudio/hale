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

package eu.esdihumboldt.hale.common.instance.orient.storage;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.record.impl.ODocument;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.core.report.Reporter;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;
import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;

/**
 * Store instances in a database
 * 
 * @author Simon Templer
 */
public abstract class StoreInstancesJob extends Job {

	private static final ALogger log = ALoggerFactory.getLogger(StoreInstancesJob.class);

	private InstanceCollection instances;
	private final LocalOrientDB database;

	/**
	 * The job report, may be <code>null</code>.
	 */
	protected final Reporter<Message> report;

	private final ReportHandler reportHandler;

	/**
	 * Create a job that stores instances in a database
	 * 
	 * @param name the (human readable) job name
	 * @param instances the instances to store in the database
	 * @param database the database
	 * @param reportHandler the report handler, <code>null</code> if no report
	 *            should be generated
	 */
	public StoreInstancesJob(String name, LocalOrientDB database, InstanceCollection instances,
			final ReportHandler reportHandler) {
		super(name);

		setUser(true);

		this.database = database;
		this.instances = instances;
		this.reportHandler = reportHandler;

		if (reportHandler != null) {
			report = new DefaultReporter<Message>("Load data into database", Message.class, false);
		}
		else {
			report = null;
		}
	}

	/**
	 * @see Job#run(IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor) {
		boolean exactProgress = instances.hasSize();
		monitor.beginTask("Store instances in database", (exactProgress) ? (instances.size())
				: (IProgressMonitor.UNKNOWN));

		int count = 0;

		if (report != null) {
			// set the correct start time
			report.setStartTime(new Date());
		}

		// get database connection
		DatabaseReference<ODatabaseDocumentTx> ref = database.openWrite();
		ODatabaseDocumentTx db = ref.getDatabase();

		ATransaction trans = log.begin("Store instances in database");
		try {
			// use intent
			db.declareIntent(new OIntentMassiveInsert());

			// TODO decouple next() and save()?

			long lastUpdate = 0; // last count update

			ResourceIterator<Instance> it = instances.iterator();
			try {
				while (it.hasNext() && !monitor.isCanceled()) {
					Instance instance = it.next();

					// further processing before storing
					processInstance(instance);

					// get/create OInstance
					OInstance conv = ((instance instanceof OInstance) ? ((OInstance) instance)
							: (new OInstance(instance)));

					conv.setInserted(true);

					// update the instance to store, e.g. generating metadata
					updateInstance(conv);

					ODatabaseRecordThreadLocal.INSTANCE.set(db);
					// configure the document
					ODocument doc = conv.configureDocument(db);
					// and save it
					doc.save();

					count++;

					if (exactProgress) {
						monitor.worked(1);
					}

					long now = System.currentTimeMillis();
					if (now - lastUpdate > 100) { // only update every 100
													// milliseconds
						monitor.subTask(String.valueOf(count) + " instances processed");
						lastUpdate = now;
					}
				}
			} finally {
				it.close();
			}

			db.declareIntent(null);
		} catch (RuntimeException e) {
			if (report != null) {
				report.error(new MessageImpl("Error storing instances in database", e));
				report.setSuccess(false);
				reportHandler.publishReport(report);
			}
			throw e;
		} finally {
			ref.dispose();
			trans.end();

			/*
			 * Reset instances to prevent memory leak. It seems Eclipse
			 * internally holds a reference to the job (in JobInfo and/or
			 * ProgressMonitorFocusJobDialog) and this results in the instance
			 * collection not being garbage collected. This is especially bad,
			 * if an in-memory instance collection is used, e.g. a
			 * DefaultInstanceCollection that is used when loading a Shapefile.
			 */
			instances = null;
		}

		try {
			onComplete();
		} catch (RuntimeException e) {
			String message = "Error while post processing stored instances";
			if (report != null) {
				report.error(new MessageImpl(message, e));
			}
			else {
				log.error(message, e);
			}
		}

		String message = MessageFormat.format("Stored {0} instances in the database.", count);
		if (monitor.isCanceled()) {
			String warn = "Loading instances was canceled, incomplete data set in the database.";
			if (report != null) {
				report.warn(new MessageImpl(warn, null));
			}
			else {
				log.warn(warn);
			}
		}

		if (report != null) {
			report.setSuccess(true);
			report.setSummary(message);
			reportHandler.publishReport(report);
		}
		else {
			log.info(message);
		}

		monitor.done();

		return new Status((monitor.isCanceled()) ? (IStatus.CANCEL) : (IStatus.OK),
				"eu.esdihumboldt.hale.common.instance.orient", message);
	}

	/**
	 * Update an instance before it is converted and saved, e.g. adding
	 * metadata. The default implementation does nothing and may be overridden.
	 * 
	 * @param instance the instance
	 */
	protected void updateInstance(MutableInstance instance) {
		// override me
	}

	/**
	 * Process an instance before it is saved. The default implementation does
	 * nothing and may be overridden.
	 * 
	 * @param instance the instance, may not be changed in any way
	 */
	protected void processInstance(Instance instance) {
		// override me
	}

	/**
	 * Called when the job has been completed
	 */
	protected abstract void onComplete();

}
