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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.xml.namespace.QName;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.InstanceResolver;
import eu.esdihumboldt.hale.common.instance.model.MetaFilter;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import eu.esdihumboldt.hale.common.instance.orient.internal.ONamespaceMap;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Instance collection based on a {@link LocalOrientDB}
 * 
 * FIXME implement instance collection fan-out (InstanceCollection2)
 * 
 * @author Simon Templer
 */
public class BrowseOrientInstanceCollection implements InstanceCollection {

	private class OrientInstanceIterator implements ResourceIterator<Instance> {

		private DatabaseReference<ODatabaseDocumentTx> ref;

		private Map<String, TypeDefinition> classTypes;

		private Queue<String> classQueue;

		private String currentClass;

		private ORecordIteratorClass<ODocument> currentIterator;

		private DatabaseHandle handle;

		private boolean allowUpdate = true;

		/**
		 * @see Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			update();

			if (currentClass == null || currentIterator == null) {
				return false;
			}

			return true;
//			return currentIterator.hasNext(); XXX Bug in OrientDB 1.0rc8: hasNext will move to the next element
		}

		private void update() {
			if (ref == null) {
				// initialize the connection and the state
				classQueue = new LinkedList<String>();
				classTypes = new HashMap<String, TypeDefinition>();
				for (TypeDefinition type : types.getMappingRelevantTypes()) {
					String className = ONamespaceMap.encode(type.getName());
					// ONameUtil.encodeName(type.getIdentifier());
					classTypes.put(className, type);
					classQueue.add(className);
				}

				ref = database.openRead();
				handle = new DatabaseHandle(ref.getDatabase());
				if (!classQueue.isEmpty()) {
					currentClass = classQueue.poll();
					if (ref.getDatabase().getMetadata().getSchema().getClass(currentClass) != null
							&& ref.getDatabase().countClass(currentClass) > 0) {
						// XXX set a fetch plan?
						currentIterator = ref.getDatabase().browseClass(currentClass);
					}
					else {
						currentIterator = null;
					}
				}
			}

			// make sure the database is associated to the current thread
			ODatabaseRecordThreadLocal.INSTANCE.set(ref.getDatabase());

			if (!allowUpdate) {
				return;
			}
			else {
				allowUpdate = false; // ensure that hasNext is only called once
										// per next on the current iterator (due
										// to the OrientDB bug)
			}

			// update class if needed
			while (currentClass != null
					&& (currentIterator == null || !currentIterator.hasNext())) {
				currentClass = classQueue.poll();
				if (ref.getDatabase().getMetadata().getSchema().getClass(currentClass) != null
						&& ref.getDatabase().countClass(currentClass) > 0) {
					// XXX set a fetch plan?
					currentIterator = ref.getDatabase().browseClass(currentClass);
					currentIterator.setFetchPlan("*:0");
				}
				else {
					currentIterator = null;
				}
			}
		}

		/**
		 * @see Iterator#next()
		 */
		@Override
		public Instance next() {
			if (hasNext()) {
				ODocument doc = currentIterator.next();
				allowUpdate = true; // allow updating in hasNext
				Instance instance = new OInstance(doc, getCurrentType(), ref.getDatabase(),
						dataSet);
				return handle.addInstance(instance);
			}
			else {
				throw new IllegalStateException(
						"No more instances available, you should have checked hasNext().");
			}
		}

		private TypeDefinition getCurrentType() {
			return classTypes.get(currentClass);
		}

		/**
		 * @see Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see ResourceIterator#close()
		 */
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

	private final TypeIndex types;

	private final DataSet dataSet;

	/**
	 * Create an instance collection based on the given database
	 * 
	 * @param database the database
	 * @param types the type index
	 * @param dataSet the data set the instances are associated to
	 */
	public BrowseOrientInstanceCollection(LocalOrientDB database, TypeIndex types,
			DataSet dataSet) {
		super();
		this.database = database;
		this.types = types;
		this.dataSet = dataSet;
	}

	/**
	 * @see InstanceCollection#iterator()
	 */
	@Override
	public ResourceIterator<Instance> iterator() {
		return new OrientInstanceIterator();
	}

	/**
	 * @see InstanceCollection#hasSize()
	 */
	@Override
	public boolean hasSize() {
		return true;
	}

	/**
	 * @see InstanceCollection#size()
	 */
	@Override
	public int size() {
		int size = 0;
		DatabaseReference<ODatabaseDocumentTx> ref = database.openRead();
		ODatabaseDocumentTx db = ref.getDatabase();
		try {
			Collection<String> classes = getMainClassNames();
			for (String clazz : classes) {
				try {
					size += db.countClass(clazz);
				} catch (IllegalArgumentException e) {
					// class not contained in the database
				}
			}
		} finally {
			ref.dispose();
		}

		return size;
	}

	/**
	 * Get the main class names
	 * 
	 * @return the main class names
	 */
	private Collection<String> getMainClassNames() {
		Collection<String> classes = new ArrayList<String>();

		for (TypeDefinition type : types.getMappingRelevantTypes()) {
			classes.add(ONamespaceMap.encode(type.getName()));
			// ONameUtil.encodeName(type.getIdentifier()));
		}

		return classes;
	}

	/**
	 * @see InstanceCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		DatabaseReference<ODatabaseDocumentTx> ref = database.openRead();
		ODatabaseDocumentTx db = ref.getDatabase();
		// make sure the database is associated to the current thread XXX not
		// sure if this is necessary
		ODatabaseRecordThreadLocal.INSTANCE.set(db);
		try {
			Collection<String> classes = getMainClassNames();
			for (String clazz : classes) {
				try {
					if (db.countClass(clazz) > 0) {
						return false;
					}
				} catch (IllegalArgumentException e) {
					// ignore
				}
			}
			return true;
		} finally {
			ref.dispose();
		}
	}

	/**
	 * @see InstanceCollection#select(Filter)
	 */
	@Override
	public InstanceCollection select(Filter filter) {
		// XXX special handling for MetaFilter (as SQL query)
		if (filter instanceof MetaFilter) {
			MetaFilter metaFilter = (MetaFilter) filter;

			if (metaFilter.getType() != null && !metaFilter.getValues().isEmpty()) {
				StringBuilder query = new StringBuilder();

				// build SQL query (XXX not injection safe)
				query.append("SELECT FROM ");
				query.append(ONamespaceMap.encode(metaFilter.getType().getName()));

				query.append(" WHERE ");
				query.append(OInstance.FIELD_METADATA);
				query.append(".");
				query.append(ONamespaceMap.encode(new QName(metaFilter.getMetadataKey())));
				query.append(" in [");
				boolean first = true;
				for (Object value : metaFilter.getValues()) {
					if (first) {
						first = false;
					}
					else {
						query.append(',');
					}
					query.append('\'');
					query.append(value);
					query.append('\'');
				}
				query.append("]");

				return new SQLQueryInstanceCollection(database, query.toString(), types, dataSet);
			}
		}

		// TODO specific support for Type filters?!
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	/**
	 * @see InstanceResolver#getReference(Instance)
	 */
	@Override
	public InstanceReference getReference(Instance instance) {
		return OrientInstanceReference.createReference(instance);
	}

	/**
	 * @see InstanceResolver#getInstance(InstanceReference)
	 */
	@Override
	public Instance getInstance(InstanceReference reference) {
		OrientInstanceReference ref = (OrientInstanceReference) reference;

		return ref.load(database, this);
	}

}
