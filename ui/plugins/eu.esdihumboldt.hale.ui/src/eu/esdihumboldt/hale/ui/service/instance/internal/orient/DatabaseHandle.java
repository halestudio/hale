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

import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.base.FinalizableWeakReference;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Database handle that manages objects referencing the database object. It will
 * release the connection when all those objects have been garbage collected.
 * 
 * @author Simon Templer
 */
public class DatabaseHandle {

	private final ODatabaseDocumentTx database;

	private long count = 0;

	private final FinalizableReferenceQueue referenceQueue = new FinalizableReferenceQueue();

	/**
	 * Create a database handle
	 * 
	 * @param database the database connection
	 */
	public DatabaseHandle(ODatabaseDocumentTx database) {
		super();

		this.database = database;
	}

	/**
	 * Add an object that references the database connection
	 * 
	 * @param object the object referencing the database
	 */
	public synchronized void addReference(Object object) {
		count++;
		new FinalizableWeakReference<Object>(object, referenceQueue) {

			@Override
			public void finalizeReferent() {
				removeReference();
			}
		};
	}

	private synchronized void removeReference() {
		count--;
		tryClose();
	}

	/**
	 * Try closing the database connection
	 */
	public synchronized void tryClose() {
		if (count <= 0) {
			database.close();
		}
	}

	/**
	 * @return the database
	 */
	public ODatabaseDocumentTx getDatabase() {
		return database;
	}

}
