/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.orient.storage;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.commons.codec.DecoderException;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import eu.esdihumboldt.hale.common.instance.orient.internal.ONamespaceMap;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * OrientDB instance collection based on a SQL query.
 * 
 * @author Simon Templer
 */
public class SQLQueryInstanceCollection implements InstanceCollection {

	private class QueryIterator implements ResourceIterator<Instance> {

		private DatabaseReference<ODatabaseDocumentTx> ref;

		private DatabaseHandle handle;

		private Iterator<ODocument> iterator;

		private final int limit;

		public QueryIterator(int limit) {
			super();
			this.limit = limit;
		}

		@Override
		public boolean hasNext() {
			update();

			return iterator.hasNext();
		}

		@SuppressWarnings("unchecked")
		private void update() {
			if (ref == null) {
				ref = database.openRead();
				handle = new DatabaseHandle(ref.getDatabase());

				/*
				 * FIXME use asynchronous query instead, to be able to provide
				 * instances iteratively instead of retrieving all at once (as
				 * the query below actually yields a list)
				 */
				iterator = (Iterator<ODocument>) ref.getDatabase()
						.query(new OSQLSynchQuery<ODocument>(sqlQuery, limit)).iterator();
			}

			// make sure the database is associated to the current thread
			ODatabaseRecordThreadLocal.INSTANCE.set(ref.getDatabase());
		}

		@Override
		public Instance next() {
			if (hasNext()) {
				ODocument doc = iterator.next();

				try {
					// find associated type
					QName typeName = ONamespaceMap.decode(doc.getSchemaClass().getName());
					TypeDefinition type = types.getType(typeName);
					// TODO react in case it is not found?
					Instance instance = new OInstance(doc, type, ref.getDatabase(), dataSet);
					return handle.addInstance(instance);
				} catch (DecoderException e) {
					throw new IllegalStateException("Failed to decode instance type", e);
				}
			}
			else {
				throw new IllegalStateException(
						"No more instances available, you should have checked hasNext().");
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() {
			if (ref != null) {
				// connection is closed in DatabaseHandle
				ref.dispose(false);
				// try closing the database handle (e.g. if no objects were
				// added)
				handle.tryClose();
			}

		}

	}

	private final LocalOrientDB database;
	private final String sqlQuery;
	private final TypeIndex types;
	private final DataSet dataSet;

	/**
	 * Create a new instance collection based on the given SQL query.
	 * 
	 * @param database the database to query
	 * @param sqlQuery the SQL query string (make sure type names are properly
	 *            encoded using {@link ONamespaceMap})
	 * @param types the type index where type definitions to associate are
	 *            retrieved from
	 * @param dataSet the data set to associated to the instances
	 */
	public SQLQueryInstanceCollection(LocalOrientDB database, String sqlQuery, TypeIndex types,
			DataSet dataSet) {
		this.database = database;
		this.sqlQuery = sqlQuery;
		this.types = types;
		this.dataSet = dataSet;
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	@Override
	public InstanceReference getReference(Instance instance) {
		return OrientInstanceReference.createReference(instance);
	}

	@Override
	public Instance getInstance(InstanceReference reference) {
		OrientInstanceReference ref = (OrientInstanceReference) reference;

		return ref.load(database, this);
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return iterator(-1);
	}

	private ResourceIterator<Instance> iterator(int limit) {
		return new QueryIterator(limit);
	}

	@Override
	public boolean hasSize() {
		return false;
	}

	@Override
	public int size() {
		return UNKNOWN_SIZE;
	}

	@Override
	public boolean isEmpty() {
		try (ResourceIterator<Instance> it = iterator(1)) {
			return !it.hasNext();
		}
	}

}
