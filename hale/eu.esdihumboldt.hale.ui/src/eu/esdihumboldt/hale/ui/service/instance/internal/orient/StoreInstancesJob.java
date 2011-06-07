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

package eu.esdihumboldt.hale.ui.service.instance.internal.orient;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.record.impl.ODocument;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;

/**
 * Store instances in a database
 * @author Simon Templer
 */
public abstract class StoreInstancesJob extends Job {

	private final InstanceCollection instances;
	private final LocalOrientDB database;

	/**
	 * Create a job that stores instances in a database
	 * 
	 * @param name the (human readable) job name
	 * @param instances the instances to store in the database
	 * @param database the database
	 */
	public StoreInstancesJob(String name, LocalOrientDB database, InstanceCollection instances) {
		super(name);
		
		setUser(true);
		
		this.database = database;
		this.instances = instances;
	}

	/**
	 * @see Job#run(IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		boolean exactProgress = instances.hasSize();
		monitor.beginTask("Store instances in database", 
				(exactProgress)?(instances.size()):(IProgressMonitor.UNKNOWN));

		int count = 0;
		
		// get database connection
		DatabaseReference<ODatabaseDocumentTx> ref = database.openWrite();
		ODatabaseDocumentTx db = ref.getDatabase();
		try {
			//XXX use intent?
			db.declareIntent(new OIntentMassiveInsert());
			
			ResourceIterator<Instance> it = instances.iterator();
			try {
				while (it.hasNext() && !monitor.isCanceled()) {
					Instance instance = it.next();
					
					// get/create OInstance
					OInstance conv = ((instance instanceof OInstance)?
							((OInstance) instance):(new OInstance(instance)));
					
					// configure the document
					ODocument doc = conv.configureDocument(db);
					// and save it
					doc.save();
					
					count++;
					
					if (exactProgress) {
						monitor.worked(1);
					}
				}
			} finally {
				it.dispose();
			}
		} finally {
			ref.dispose();
		}
		
		onComplete();
		
		monitor.done();
		return new Status((monitor.isCanceled())?(IStatus.CANCEL):(IStatus.OK), 
				HALEUIPlugin.PLUGIN_ID, MessageFormat.format(
						"Stored {0} instances in the database", count));
	}

	/**
	 * Called when the job has been completed
	 */
	protected abstract void onComplete();

}
