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
import java.util.Arrays;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.base.FinalizablePhantomReference;
import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.impl.GroupDecorator;
import eu.esdihumboldt.hale.common.instance.model.impl.InstanceDecorator;

/**
 * Database handle that manages objects referencing the database object. It will
 * release the connection when all those objects have been garbage collected.
 * 
 * @author Simon Templer
 */
public class DatabaseHandle {

	@SuppressWarnings("javadoc")
	public final class InstanceHandle extends InstanceDecorator {

		public InstanceHandle(Instance instance) {
			super(instance);
		}

		@Override
		public Object[] getProperty(QName propertyName) {
			return augmentValues(getOriginalInstance().getProperty(propertyName));
		}
	}

	@SuppressWarnings("javadoc")
	public final class GroupHandle extends GroupDecorator {

		public GroupHandle(Group group) {
			super(group);
		}

		@Override
		public Object[] getProperty(QName propertyName) {
			return augmentValues(getOriginalGroup().getProperty(propertyName));
		}
	}

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
	 * Add an object that references the database connection.
	 * 
	 * It is preferred to use {@link #addInstance(Instance)} or
	 * {@link #addGroup(Group)} instead.
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

	/**
	 * Augment an instance and add a reference for the database connection.
	 * Makes sure child instances or groups also reference the database
	 * connection.
	 * 
	 * @param instance the instance to augment
	 * @return the augmented instance
	 */
	public Instance addInstance(Instance instance) {
		if (instance == null) {
			return null;
		}

		Instance result = new InstanceHandle(instance);
		addReference(result);
		return result;
	}

	/**
	 * Augment a group and add a reference for the database connection. Makes
	 * sure child instances or groups also reference the database connection.
	 * 
	 * @param group the group to augment
	 * @return the augmented group
	 */
	public Group addGroup(Group group) {
		if (group == null) {
			return null;
		}

		Group result = new GroupHandle(group);
		addReference(result);
		return result;
	}

	/**
	 * Augment an array of values.
	 * 
	 * @param values the values to augment
	 * @return the augmented objects
	 */
	protected Object[] augmentValues(Object[] values) {
		if (values == null) {
			return null;
		}

		return Arrays.stream(values).map(value -> {
			if (value instanceof Instance) {
				return addInstance((Instance) value);
			}
			if (value instanceof Group) {
				return addGroup((Group) value);
			}
			return value;
		}).toArray();
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
