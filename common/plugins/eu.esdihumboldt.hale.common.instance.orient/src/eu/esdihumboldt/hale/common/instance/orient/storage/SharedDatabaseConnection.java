/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.instance.orient.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Shared database connection.
 * 
 * @author Simon Templer
 */
public class SharedDatabaseConnection {

	private static final ALogger log = ALoggerFactory.getLogger(SharedDatabaseConnection.class);

	private static final ThreadLocal<Map<OwnerReference, SharedDatabaseConnection>> cachedConnections = new ThreadLocal<Map<OwnerReference, SharedDatabaseConnection>>() {

		@Override
		protected Map<OwnerReference, SharedDatabaseConnection> initialValue() {
			return new HashMap<>();
		}

	};

	/**
	 * Create a shared database connection.
	 * 
	 * @param lodb the database
	 * @param owner the connection owner
	 * @return the database connection
	 */
	public static SharedDatabaseConnection openRead(LocalOrientDB lodb, final Object owner) {
		final String ownerName = owner.getClass().getSimpleName() + '#' + Objects.hashCode(owner);
		final OwnerReference ref = new OwnerReference(owner);

		SharedDatabaseConnection connection = cachedConnections.get().get(ref);

		if (connection == null || connection.getDb().getDatabase().isClosed()) {
			DatabaseReference<ODatabaseDocumentTx> db = lodb.openRead(false);
			DatabaseHandle handle = new DatabaseHandle(db.getDatabase()) {

				@Override
				public synchronized void tryClose() {
					super.tryClose();

					if (database.isClosed()) {
						/*
						 * FIXME the handle should be removed (and thus the
						 * finalizer thread). Make the cache not thread local
						 * again?
						 * 
						 * XXX but this method (and removeReference) seems to be
						 * never called anyway.
						 */
						log.info("Closed shared database connection on " + ownerName);
					}
				}

			};
			connection = new SharedDatabaseConnection(db, handle);
			cachedConnections.get().put(ref, connection);
			log.info("Created shared database connection on " + ownerName);
		}

		return connection;
	}

	private final DatabaseReference<ODatabaseDocumentTx> db;
	private final DatabaseHandle handle;

	private SharedDatabaseConnection(DatabaseReference<ODatabaseDocumentTx> connection,
			DatabaseHandle handle) {
		super();
		this.db = connection;
		this.handle = handle;
	}

	/**
	 * @return the database connection
	 */
	public DatabaseReference<ODatabaseDocumentTx> getDb() {
		return db;
	}

	/**
	 * @return the database handle
	 */
	public DatabaseHandle getHandle() {
		return handle;
	}

}
