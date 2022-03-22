/*
 * Copyright (c) 2020 wetransform GmbH
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

package eu.esdihumboldt.hale.io.geopackage.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import mil.nga.geopackage.features.columns.GeometryColumns;
import mil.nga.geopackage.user.UserDao;
import mil.nga.geopackage.user.UserResultSet;

/**
 * Instance collection based on a GeoPackage feature table.
 * 
 * @author Simon Templer
 */
public class GeopackageFeatureCollection implements InstanceCollection {

	private static final Logger logger = LoggerFactory.getLogger(GeopackageFeatureCollection.class);

	private class FeaturesIterator implements InstanceIterator {

		private final TableInstanceBuilder builder;

		private UserResultSet<?, ?, ?> currentResults;

		/**
		 * States if the row at the current cursor position was already
		 * consumed.
		 * 
		 * Initially the result set points to the row before the first row,
		 * which by definition is consumed.
		 */
		private boolean consumed = true;

		private boolean hasNext = false;

		private boolean done = false;

		private String where;

		/**
		 * Default constructor.
		 */
		public FeaturesIterator(/* CRSProvider crsProvider */) {
			super();
			builder = new TableInstanceBuilder(/* crsProvider, */geometryColumns, log);
		}

		/**
		 * Create filtered iterator
		 * 
		 * @param where WHERE clause to filter table data
		 */
		public FeaturesIterator(String where) {
			this();
			this.where = where;
		}

		@Override
		public TypeDefinition typePeek() {
			if (hasNext()) {
				// always the same type returned in this iterator
				return type;
			}
			return null;
		}

		@Override
		public boolean supportsTypePeek() {
			return true;
		}

		@Override
		public boolean hasNext() {
			proceedToNext();

			return hasNext;
		}

		/**
		 * Proceed to the next result
		 */
		private void proceedToNext() {
			if (done) {
				return;
			}

			try {
				if (currentResults != null) {
					// move cursor if necessary

					if (consumed) {
						hasNext = currentResults.moveToNext();
						consumed = false;
					}
				}

				if (currentResults != null && !hasNext) {
					close();
				}

				if (currentResults == null) {
					consumed = true;

					// retrieve result set
					if (StringUtils.hasText(where)) {
						currentResults = features.query(where);
					}
					else {
						currentResults = features.queryForAll();
					}

					proceedToNext();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				close();
			}
		}

		@Override
		public Instance next() {
			proceedToNext();

			if (hasNext) {
				// create instance from current cursor

				Instance instance = builder.createInstance(type, currentResults);

				consumed = true;

				return instance;
			}
			else {
				throw new IllegalStateException();
			}
		}

		@Override
		public void skip() {
			proceedToNext();

			if (hasNext) {
				// mark as consumed
				consumed = true;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() {
			if (currentResults != null) {
				currentResults.close();
			}
			currentResults = null;
			done = true;
			hasNext = false;
		}

	}

	private final UserDao<?, ?, ?, ?> features;
	private final GeometryColumns geometryColumns;
	private final TypeDefinition type;
	private final String where;
	private final SimpleLog log = SimpleLog.fromLogger(logger);

	/**
	 * Create a new instance collection for features in a GeoPackage feature
	 * table.
	 * 
	 * @param features the feature DAO
	 * @param geometryColumns the geometry columns to derive CRS information
	 *            from
	 * @param type the type associated to the feature table
	 */
	public GeopackageFeatureCollection(UserDao<?, ?, ?, ?> features,
			GeometryColumns geometryColumns, TypeDefinition type) {
		this(features, geometryColumns, type, null);
	}

	/**
	 * Create a new filtered instance collection for features in a GeoPackage
	 * feature table.
	 * 
	 * @param features the feature DAO
	 * @param geometryColumns the geometry columns to derive CRS information
	 *            from
	 * @param type the type associated to the feature table
	 * @param where WHERE clause to filter table data
	 */
	public GeopackageFeatureCollection(UserDao<?, ?, ?, ?> features,
			GeometryColumns geometryColumns, TypeDefinition type, String where) {
		// FIXME is it OK to pass the DAO? e.g. related to closing the resource
		this.features = features;
		this.geometryColumns = geometryColumns;
		this.type = type;
		this.where = where;
	}

	@Override
	public InstanceReference getReference(Instance instance) {
		// FIXME primary key based?
		return new PseudoInstanceReference(instance);
	}

	@Override
	public Instance getInstance(InstanceReference reference) {
		// FIXME
		if (reference instanceof PseudoInstanceReference) {
			return ((PseudoInstanceReference) reference).getInstance();
		}
		return null;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new FeaturesIterator(where);
	}

	@Override
	public boolean hasSize() {
		return true;
	}

	@Override
	public int size() {
		return features.count();
	}

	@Override
	public boolean isEmpty() {
		return features.count() == 0;
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

}
