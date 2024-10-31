/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.headless.orient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.headless.transform.AbstractTransformationSink;
import eu.esdihumboldt.hale.common.headless.transform.LimboInstanceSink;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.InstanceDecorator;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import eu.esdihumboldt.hale.common.instance.orient.storage.BrowseOrientInstanceCollection;
import eu.esdihumboldt.hale.common.instance.orient.storage.LocalOrientDB;
import eu.esdihumboldt.hale.common.instance.orient.storage.OrientInstanceReference;
import eu.esdihumboldt.hale.common.instance.orient.storage.OrientInstanceSink;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Reiterable transformation sink based on OrientDB.
 * 
 * @author Simon Templer
 */
public class OrientTransformationSink extends AbstractTransformationSink {

	private class OrientLimboCollection implements InstanceCollection {

		private boolean firstIterator = true;

		private boolean limboOpen = false;

		@Override
		public InstanceReference getReference(Instance instance) {
			if (instance instanceof InstanceWithReference) {
				// instance served by limbo sink collection
				return ((InstanceWithReference) instance).getReference();
			}
			// served by Orient instance collection
			return OrientInstanceReference.createReference(instance);
		}

		@Override
		public Instance getInstance(InstanceReference reference) {
			OrientInstanceReference ref = (OrientInstanceReference) reference;
			return ref.load(database, this);
		}

		@Override
		public ResourceIterator<Instance> iterator() {
			if (!complete.get() && !skipLimbo.get() && firstIterator) {
				firstIterator = false;
				limboOpen = true;
				return limboSink.getInstanceCollection().iterator();
			}
			else {
				waitToComplete();

				return new BrowseOrientInstanceCollection(database, types, DataSet.TRANSFORMED)
						.iterator();
			}
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
			// XXX have to return false, even if it actually may be empty
			return false;
		}

		@Override
		public InstanceCollection select(Filter filter) {
			waitToComplete();

			// delegate to orient instance collection
			return new BrowseOrientInstanceCollection(database, types, DataSet.TRANSFORMED)
					.select(filter);
		}

		/**
		 * Blocks until the database is completely populated.
		 */
		private void waitToComplete() {
			if (!limboOpen) {
				skipLimbo.set(true); // skip limbo sink (prevent blocking when
										// adding to limbo sink)
				// consume limbo sink, make sure it does not block any more
				limboSink.done(true);
			}

			// block until complete
			while (!complete.get()) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					log.error("Waiting for transformation completion interrupted", e);
				}
			}

			dbThread.shutdown();
			try {
				dbThread.awaitTermination(100, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				log.warn("Waiting for termination of database thread interupted", e);
			}
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(OrientTransformationSink.class);

	private final Path tmpLocation;
	private final LocalOrientDB database;

	private final OrientInstanceSink orientSink;
	private final LimboInstanceSink limboSink;

	private final OrientLimboCollection collection;

	private TypeIndex types;

	private final AtomicBoolean complete = new AtomicBoolean();
	private final AtomicBoolean skipLimbo = new AtomicBoolean();

	private final ExecutorService dbThread = Executors.newSingleThreadExecutor();

	/**
	 * Default constructor.
	 */
	public OrientTransformationSink() {
		super();

		// create temporary database
		try {
			this.tmpLocation = Files.createTempDirectory("transformationSink");
		} catch (IOException e) {
			throw new IllegalStateException("Cannot create temporary database location", e);
		}
		this.database = new LocalOrientDB(tmpLocation.toFile());

		// create orient sink
		this.orientSink = new OrientInstanceSink(database, false);

		/*
		 * create limbo sink (for first iteration while data it still stored in
		 * the Orient database)
		 */
		this.limboSink = new LimboInstanceSink();

		this.collection = new OrientLimboCollection();
	}

	@Override
	public void setTypes(TypeIndex types) {
		this.types = types;
	}

	@Override
	protected void internalDone(boolean cancel) {
		dbThread.execute(new Runnable() {

			@Override
			public void run() {
				try {
					orientSink.close();
				} catch (IOException e) {
					log.error("Failed to close OrientDB instance sink", e);
				}
			}
		});
		limboSink.done(cancel);
		complete.set(true);
		dbThread.shutdown();
	}

	@Override
	public void dispose() {
		dbThread.shutdown();
		try {
			dbThread.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.warn("Waiting for termination of database thread interupted", e);
		}
		database.delete();
		try {
			Files.deleteIfExists(tmpLocation);
		} catch (IOException e) {
			log.warn("Could not delete database directory", e);
		}

		super.dispose();
	}

	@Override
	protected void internalAddInstance(final Instance instance) {
		var future = dbThread.submit(new Runnable() {

			@Override
			public void run() {
				// add to database
				InstanceReference ref = orientSink.putInstance(instance);
				// add to limbo sink
				if (!skipLimbo.get()) {
					// possible problem: limbo sink may block
					limboSink.addInstance(new InstanceWithReference(instance, ref));
				}
			}
		});

		// wait for adding instance to database to complete, otherwise the
		// transformation might complete before the instance is actually added
		try {
			future.get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InstanceCollection getInstanceCollection() {
		return FilteredInstanceCollection.applyFilter(collection, new Filter() {

			@Override
			public boolean match(Instance instance) {
				// If instance is an InstanceDecorator, it can't be checked
				// whether the instance was actually inserted.
				Instance originalInstance = instance;
				while (originalInstance instanceof InstanceDecorator) {
					originalInstance = ((InstanceDecorator) originalInstance).getOriginalInstance();
				}

				if (originalInstance instanceof OInstance) {
					return ((OInstance) originalInstance).isInserted();
				}

				return true;
			}
		});
	}

}
