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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.record.impl.ODocument;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;
import eu.esdihumboldt.hale.common.core.report.LogAware;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.core.report.ReportSimpleLogSupport;
import eu.esdihumboldt.hale.common.core.report.Reporter;
import eu.esdihumboldt.hale.common.core.report.SimpleLogContext;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;
import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexService;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.IdentifiableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import eu.esdihumboldt.hale.common.instance.processing.InstanceProcessingExtension;
import eu.esdihumboldt.hale.common.instance.processing.InstanceProcessor;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntProcedure;

/**
 * Store instances in a database
 * 
 * @author Simon Templer
 */
public abstract class StoreInstancesJob extends Job {

	private static class DefaultLog extends DefaultReporter<Message>
			implements ReportSimpleLogSupport<Message> {

		public DefaultLog(String taskName, Class<Message> messageType, boolean doLog) {
			super(taskName, messageType, doLog);
		}

		@Override
		public Message createMessage(String message, Throwable e) {
			return new MessageImpl(message, e);
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(StoreInstancesJob.class);

	private InstanceCollection instances;
	private final LocalOrientDB database;

	/**
	 * The job report, may be <code>null</code>.
	 */
	protected final DefaultLog report;

	private final ReportHandler reportHandler;

	private final ServiceProvider serviceProvider;

	private final boolean doProcessing;

	/**
	 * Create a job that stores instances in a database.
	 * 
	 * @param name the (human readable) job name
	 * @param instances the instances to store in the database
	 * @param database the database
	 * @param reportHandler the report handler, <code>null</code> if no report
	 *            should be generated
	 */
	public StoreInstancesJob(String name, LocalOrientDB database, InstanceCollection instances,
			final ReportHandler reportHandler) {
		this(name, database, instances, null, reportHandler, false);
	}

	/**
	 * Create a job that stores instances in a database
	 * 
	 * @param name the (human readable) job name
	 * @param instances the instances to store in the database
	 * @param database the database
	 * @param serviceProvider the service provider, may be <code>null</code> if
	 *            processing is disabled
	 * @param reportHandler the report handler, <code>null</code> if no report
	 *            should be generated
	 * @param doProcessing if instance processing should be done on stored
	 *            instances
	 */
	public StoreInstancesJob(String name, LocalOrientDB database, InstanceCollection instances,
			final ServiceProvider serviceProvider, final ReportHandler reportHandler,
			boolean doProcessing) {
		super(name);

		setUser(true);

		this.database = database;
		this.instances = instances;
		this.serviceProvider = serviceProvider;
		this.reportHandler = reportHandler;
		this.doProcessing = doProcessing;

		if (reportHandler != null) {
			report = new DefaultLog("Load data into database", Message.class, false);
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
		monitor.beginTask("Store instances in database",
				(exactProgress) ? (instances.size()) : (IProgressMonitor.UNKNOWN));

		AtomicInteger count = new AtomicInteger(0);
		TObjectIntHashMap<QName> typeCount = new TObjectIntHashMap<>();

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

			// Find all the InstanceProcessors to feed them the stored Instances
			final List<InstanceProcessor> processors;
			if (doProcessing) {
				final InstanceProcessingExtension ext = new InstanceProcessingExtension(
						serviceProvider);
				processors = ext.getInstanceProcessors();
			}
			else {
				processors = Collections.emptyList();
			}

			BrowseOrientInstanceCollection browser = new BrowseOrientInstanceCollection(database,
					null, DataSet.SOURCE);

			final InstanceIndexService indexService;
			if (doProcessing) {
				indexService = serviceProvider.getService(InstanceIndexService.class);
			}
			else {
				indexService = null;
			}

			// TODO decouple next() and save()?

			SimpleLogContext.withLog(report, () -> {
				if (report != null && instances instanceof LogAware) {
					((LogAware) instances).setLog(report);
				}

				ResourceIterator<Instance> it = instances.iterator();
				int size = instances.size();
				try {
					while (it.hasNext() && !monitor.isCanceled()) {
						long lastUpdate = 0; // last count update

						if (report != null && instances instanceof LogAware) {
							((LogAware) instances).setLog(report);
						}

						Instance instance = it.next();

						// further processing before storing
						processInstance(instance);

						// get/create OInstance
						OInstance conv = ((instance instanceof OInstance) ? ((OInstance) instance)
								: (new OInstance(instance)));

						conv.setInserted(true);

						// update the instance to store, e.g. generating
						// metadata
						updateInstance(conv);

						ODatabaseRecordThreadLocal.INSTANCE.set(db);
						// configure the document
						ODocument doc = conv.configureDocument(db);
						// and save it
						doc.save();

						// Create an InstanceReference for the saved instance
						// and
						// feed it to all known InstanceProcessors. The
						// decoration
						// with ResolvableInstanceReference allows the
						// InstanceProcessors to resolve the instances if
						// required.
						OrientInstanceReference oRef = new OrientInstanceReference(
								doc.getIdentity(), conv.getDataSet(), conv.getDefinition());
						IdentifiableInstanceReference idRef = new IdentifiableInstanceReference(
								oRef, doc.getIdentity());
						ResolvableInstanceReference resolvableRef = new ResolvableInstanceReference(
								idRef, browser);

						processors.forEach(p -> p.process(instance, resolvableRef));

						if (indexService != null) {
							indexService.add(instance, resolvableRef);
						}

						count.incrementAndGet();

						TypeDefinition type = instance.getDefinition();
						if (type != null) {
							typeCount.adjustOrPutValue(type.getName(), 1, 1);
						}

						if (exactProgress) {
							monitor.worked(1);
						}

						long now = System.currentTimeMillis();
						if (now - lastUpdate > 100) { // only update every 100
														// milliseconds
							monitor.subTask(MessageFormat.format("{0}{1} instances processed",
									String.valueOf(count.get()),
									size != InstanceCollection.UNKNOWN_SIZE
											? "/" + String.valueOf(size) : ""));
							lastUpdate = now;
						}
					}
				} finally {
					it.close();
					if (report != null && instances instanceof LogAware) {
						((LogAware) instances).setLog(null);
					}
				}
			});

			db.declareIntent(null);
		} catch (RuntimeException e) {
			if (report != null) {
				reportTypeCount(report, typeCount);
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
			reportTypeCount(report, typeCount);
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

	private void reportTypeCount(Reporter<Message> report, TObjectIntHashMap<QName> typeCount) {
		typeCount.forEachEntry(new TObjectIntProcedure<QName>() {

			@Override
			public boolean execute(QName typeName, int count) {
				StringBuilder msg = new StringBuilder("Stored ");
				msg.append(count);
				msg.append(" instances of type ");
				msg.append(typeName.getLocalPart());
				String ns = typeName.getNamespaceURI();
				if (ns != null && !ns.isEmpty()) {
					msg.append(" (");
					msg.append(ns);
					msg.append(")");
				}

				report.info(new MessageImpl(msg.toString(), null));

				return true;
			}
		});
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
