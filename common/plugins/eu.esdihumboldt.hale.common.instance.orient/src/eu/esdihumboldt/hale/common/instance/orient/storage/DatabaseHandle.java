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

	/**
	 * The database connection.
	 */
	protected final ODatabaseDocumentTx database;

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
