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

import java.io.Closeable;
import java.io.IOException;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;

/**
 * Instance sink based on a {@link LocalOrientDB}
 * 
 * @author Simon Templer
 */
public class OrientInstanceSink implements InstanceSink, Closeable {

	private final LocalOrientDB database;
	private DatabaseReference<ODatabaseDocumentTx> ref;

	/**
	 * Create an instance sink based on a {@link LocalOrientDB}
	 * 
	 * @param database the sink database
	 * @param lockNow if the database should be locked now
	 */
	public OrientInstanceSink(LocalOrientDB database, boolean lockNow) {
		super();
		this.database = database;
		if (lockNow) {
			// ensure the lock is acquired now (in this thread)
			ref = database.openWrite();
			ref.getDatabase();
		}
	}

	/**
	 * @see InstanceSink#addInstance(Instance)
	 */
	@Override
	public synchronized void addInstance(Instance instance) {
		if (ref == null) {
			ref = database.openWrite();
		}

		ODatabaseDocumentTx db = ref.getDatabase();

		// further processing before saving
		processInstance(instance);

		// get/create OInstance
		OInstance conv = ((instance instanceof OInstance) ? ((OInstance) instance)
				: (new OInstance(instance)));

		conv.setInserted(true);

		ODatabaseRecordThreadLocal.INSTANCE.set(db);
		// configure the document
		ODocument doc = conv.configureDocument(db);
		// and save it
		doc.save();
	}

	/**
	 * Process an instance before it is converted and saved. The default
	 * implementation does nothing and may be overridden.
	 * 
	 * @param instance the instance
	 */
	protected void processInstance(Instance instance) {
		// override me
	}

	/**
	 * @see Closeable#close()
	 */
	@Override
	public synchronized void close() throws IOException {
		if (ref != null) {
			ODatabaseRecordThreadLocal.INSTANCE.set(ref.getDatabase());
			ref.dispose();
		}
	}

}
