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

import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Represents a local Orient database 
 * @author Simon Templer
 */
public class LocalOrientDB {
	
	/**
	 * Database reference for reading holding a lock
	 */
	private class ReadReference implements
			DatabaseReference<ODatabaseDocumentTx> {
		
		private ODatabaseDocumentTx database;

		/**
		 * @see DatabaseReference#getDatabase()
		 */
		@Override
		public ODatabaseDocumentTx getDatabase() {
			if (database == null)  {
				database = new ODatabaseDocumentTx(dbURI).open("reader", "reader");
				dbLock.readLock().lock();
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
					database.close();
				}
				dbLock.readLock().unlock();
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
	private class WriteReference implements
			DatabaseReference<ODatabaseDocumentTx> {
		
		private ODatabaseDocumentTx database;

		/**
		 * @see DatabaseReference#getDatabase()
		 */
		@Override
		public ODatabaseDocumentTx getDatabase() {
			if (database == null)  {
//				database = new ODatabaseDocumentTx(dbURI).open("writer", "writer");
				// writer use doesn't seem to be supported any more (as of 1.0rc8)
				database = new ODatabaseDocumentTx(dbURI).open("admin", "admin");
				//XXX could eventually use read lock
				dbLock.writeLock().lock();
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
					database.close(); 
				}
				//XXX could eventually use read lock
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

	private ReadWriteLock dbLock = new ReentrantReadWriteLock();
	
	private final String dbURI;
	
	/**
	 * Create a local Orient database. It will delete database that exists
	 * previously at the same location.
	 * 
	 * @param location the data base location
	 */
	public LocalOrientDB(File location) {
		super();
		
		dbURI = "local:" + location.getAbsolutePath();
		
		ODatabaseDocumentTx db = new ODatabaseDocumentTx(dbURI);
		try {
			// delete the database if it already exists
			db.delete();
		} catch (Throwable e) {
			// ignore
		}
		// create the database
		db.create();
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
		return new ReadReference();
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
			ODatabaseDocumentTx db = new ODatabaseDocumentTx(dbURI);
			// delete the database if it already exists
			db.delete();
			// create the database
			db.create();
		} finally {
			dbLock.writeLock().unlock();
		}
	}

}
