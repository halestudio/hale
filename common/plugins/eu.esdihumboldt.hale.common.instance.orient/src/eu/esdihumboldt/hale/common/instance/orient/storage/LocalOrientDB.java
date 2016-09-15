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

import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Represents a local Orient database
 * 
 * @author Simon Templer
 */
public class LocalOrientDB {

	/**
	 * Database reference for reading holding a lock
	 */
	private class ReadReference implements DatabaseReference<ODatabaseDocumentTx> {

		private ODatabaseDocumentTx database;
		private final boolean createLock;

		/**
		 * Create a new read reference.
		 * 
		 * @param lock if a lock should be created for the reference
		 */
		public ReadReference(boolean lock) {
			this.createLock = lock;
		}

		/**
		 * @see DatabaseReference#getDatabase()
		 */
		@Override
		public ODatabaseDocumentTx getDatabase() {
			if (database == null) {
				if (createLock) {
					dbLock.readLock().lock();
				}
//				database = pool.acquire(dbURI, "reader", "reader");
				database = new ODatabaseDocumentTx(dbURI).open("reader", "reader");
			}
			return database;
		}

		/**
		 * @see DatabaseReference#dispose(boolean)
		 */
		@Override
		public void dispose(boolean closeConnection) {
			if (database != null) {
				if (closeConnection) {
//					pool.release(database)
					database.close();
				}
				if (createLock) {
					dbLock.readLock().unlock();
				}
			}
		}

		/**
		 * @see DatabaseReference#dispose()
		 */
		@Override
		public void dispose() {
			dispose(true);
		}

	}

	/**
	 * Database reference for writing holding a lock
	 */
	private class WriteReference implements DatabaseReference<ODatabaseDocumentTx> {

		private ODatabaseDocumentTx database;

		/**
		 * @see DatabaseReference#getDatabase()
		 */
		@Override
		public ODatabaseDocumentTx getDatabase() {
			if (database == null) {
				// XXX could eventually use read lock
				dbLock.writeLock().lock();
//				database = new ODatabaseDocumentTx(dbURI).open("writer", "writer");
				// writer use doesn't seem to be supported any more (as of
				// 1.0rc8)
//				database = pool.acquire(dbURI, "admin", "admin");
				database = new ODatabaseDocumentTx(dbURI).open("admin", "admin");
			}
			return database;
		}

		/**
		 * @see DatabaseReference#dispose(boolean)
		 */
		@Override
		public void dispose(boolean closeConnection) {
			if (database != null) {
				if (closeConnection) {
//					pool.release(database)
					database.close();
				}
				// XXX could eventually use read lock
				dbLock.writeLock().unlock();
			}
		}

		/**
		 * @see DatabaseReference#dispose()
		 */
		@Override
		public void dispose() {
			dispose(true);
		}

	}

	private final ReadWriteLock dbLock = new ReentrantReadWriteLock();

	private final String dbURI;

//	private final ODatabaseDocumentPool pool;

	/**
	 * Create a local Orient database. It will delete database that exists
	 * previously at the same location.
	 * 
	 * @param location the data base location
	 */
	public LocalOrientDB(File location) {
		super();

		dbURI = "local:" + location.getAbsolutePath();

//		pool = new ODatabaseDocumentPool();
//		pool.setup(1,10);
		// XXX close pool? when?

		ODatabaseDocumentTx db = new ODatabaseDocumentTx(dbURI);
		try {
			// delete the database if it already exists
			db.drop();
		} catch (Throwable e) {
			// ignore
		}
		// create the database
		db.create();
		db.close();
	}

	/**
	 * Get a database reference with read access.<br>
	 * <br>
	 * NOTE: Getting the database reference locks a read lock on the database.
	 * {@link DatabaseReference#dispose()} must be called when the database
	 * reference isn't needed any more.
	 * 
	 * @return the database reference
	 */
	public DatabaseReference<ODatabaseDocumentTx> openRead() {
		return new ReadReference(true);
	}

	/**
	 * Get a database reference with read access.<br>
	 * 
	 * @param lock if a read lock should be created,
	 *            {@link DatabaseReference#dispose()} must be called when the
	 *            database reference isn't needed any more
	 * @return the database reference
	 */
	public DatabaseReference<ODatabaseDocumentTx> openRead(boolean lock) {
		return new ReadReference(lock);
	}

	/**
	 * Get a database reference with write access.<br>
	 * <br>
	 * NOTE: Getting the database reference locks a write lock on the database.
	 * {@link DatabaseReference#dispose()} must be called when the database
	 * reference isn't needed any more.
	 * 
	 * @return the database reference
	 */
	public DatabaseReference<ODatabaseDocumentTx> openWrite() {
		return new WriteReference();
	}

	/**
	 * Delete the database and recreate it.
	 */
	public void clear() {
		dbLock.writeLock().lock();
		try {
			@SuppressWarnings("resource")
			ODatabaseDocumentTx db = new ODatabaseDocumentTx(dbURI).open("admin", "admin");
			// delete the database if it already exists
			db.drop();
			// create the database
			db.create();
			db.close();
		} finally {
			dbLock.writeLock().unlock();
		}
	}

	/**
	 * Delete the database.
	 */
	public void delete() {
		dbLock.writeLock().lock();
		try {
			@SuppressWarnings("resource")
			ODatabaseDocumentTx db = new ODatabaseDocumentTx(dbURI).open("admin", "admin");
			// delete the database if it already exists
			db.drop();
			db.close();
		} finally {
			dbLock.writeLock().unlock();
		}
	}

}
