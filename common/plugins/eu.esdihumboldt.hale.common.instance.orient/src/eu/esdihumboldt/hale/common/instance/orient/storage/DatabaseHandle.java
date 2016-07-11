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

import java.lang.ref.Reference;
import java.util.Set;

import com.google.common.base.FinalizablePhantomReference;
import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Database handle that manages objects referencing the database object. It will
 * release the connection when all those objects have been garbage collected.
 * 
 * @author Simon Templer
 */
public class DatabaseHandle {

	private static final ALogger log = ALoggerFactory.getLogger(DatabaseHandle.class);

	/**
	 * The database connection.
	 */
	protected final ODatabaseDocumentTx database;

	private long count = 0;

	private static final FinalizableReferenceQueue referenceQueue = new FinalizableReferenceQueue();

	private final Set<Reference<?>> references = Sets.newConcurrentHashSet();
	// This ensures that the FinalizablePhantomReference itself is not
	// garbage-collected.

	private static final Set<Reference<?>> handleReferences = Sets.newConcurrentHashSet();

	/**
	 * Create a database handle
	 * 
	 * @param database the database connection
	 */
	public DatabaseHandle(final ODatabaseDocumentTx database) {
		super();

		this.database = database;

		handleReferences.add(new FinalizablePhantomReference<DatabaseHandle>(this, referenceQueue) {

			@Override
			public void finalizeReferent() {
				handleReferences.remove(this);
				try {
					if (!database.isClosed()) {
						database.close();
					}
				} catch (Exception e) {
					// ignore
				}
				log.info("Closed garbage collected database handle");
			}
		});
	}

	/**
	 * Add an object that references the database connection
	 * 
	 * @param object the object referencing the database
	 */
	public synchronized void addReference(Object object) {
		FinalizablePhantomReference<?> ref = new FinalizablePhantomReference<Object>(object,
				referenceQueue) {

			@Override
			public void finalizeReferent() {
				references.remove(this);
				removeReference();
			}
		};
		references.add(ref);
		count++;
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
			onClose();
		}
	}

	/**
	 * Called when the database connection was closed.
	 */
	protected void onClose() {
		// override me
	}

	/**
	 * @return the database
	 */
	public ODatabaseDocumentTx getDatabase() {
		return database;
	}

}
