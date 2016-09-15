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

package eu.esdihumboldt.hale.common.schema.persist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.CachingImportProvider;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;

/**
 * Base class for schema readers that are able to cache a loaded schema through
 * a {@link Value} representation.
 * 
 * @author Simon Templer
 */
public abstract class AbstractCachedSchemaReaderBase extends AbstractSchemaReader
		implements CachingImportProvider {

	private Value cache;

	private Schema schema;

	private boolean cacheUpdate = false;

	private boolean provideCache = false;

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		if (validCache(cache) && useCache(cache)) {
			reporter.info(new IOMessageImpl("Loading schema from cached result...", null));
			schema = loadFromCache(cache, progress, reporter);
		}
		else {
			schema = loadFromSource(progress, reporter);
			if (provideCache && schema != null && reporter.isSuccess()) {
				try {
					cache = storeInCache(schema);
					reporter.info(new IOMessageImpl("Created cached schema representation", null));
				} catch (Exception e) {
					reporter.error(new IOMessageImpl(
							"Failed to create a representation of the schema for caching", e));
					// invalidate cache
					cache = null;
				}
				cacheUpdate = true;
			}
		}
		return reporter;
	}

	@Override
	public void setProvideCache() {
		provideCache = true;
	}

	/**
	 * Store the given schema as a value to be cached.
	 * 
	 * @param schema the schema to cache
	 * @return the cache value, may be <code>null</code>
	 * @throws Exception if an error occurs storing the schema as {@link Value}
	 */
	protected abstract Value storeInCache(Schema schema) throws Exception;

	/**
	 * Load the schema from the given cache. On success the reporter must be
	 * updated accordingly.<br>
	 * <br>
	 * This method is called if both {@link #validCache(Value)} and
	 * {@link #useCache(Value)} returned <code>true</code>.<br>
	 * 
	 * @param cache the cache value
	 * @param progress the progress
	 * @param reporter the reporter
	 * @return the schema loaded from the cache value
	 */
	protected abstract Schema loadFromCache(Value cache, ProgressIndicator progress,
			IOReporter reporter);

	/**
	 * Determines if with the current configuration and source the cache should
	 * be used to load the schema or not. This method is only called after
	 * {@link #validCache(Value)} returned <code>true</code>.<br>
	 * <br>
	 * The default implementation checks if the source location/stream is
	 * readable and allows using the cache if it's not.
	 * 
	 * @param cache the cache value
	 * @return if the cache should be used to load the schema
	 */
	protected boolean useCache(Value cache) {
		// test if the source is accessible

		URI loc = getSource().getLocation();
		if (loc != null) {
			// try based on URI
			return !HaleIO.testStream(loc, true);
		}
		else {
			// try opening stream
			try (InputStream in = getSource().getInput()) {
				in.read();
				return false;
			} catch (Exception e) {
				return true;
			}
		}
	}

	/**
	 * Determines if the given value provides valid cache information that could
	 * be used in {@link #loadFromCache(Value, ProgressIndicator, IOReporter)}.
	 * <br>
	 * <br>
	 * The default implementation classifies the cache value as valid if it is
	 * not <code>null</code> or the NULL value.
	 * 
	 * @param cache the cache value
	 * @return if the cache value is valid
	 */
	protected boolean validCache(Value cache) {
		return cache != null && cache.getValue() != null;
	}

	/**
	 * Load the schema from the {@link #getSource()}. On success the reporter
	 * must be updated accordingly.
	 * 
	 * @param progress the progress indicator
	 * @param reporter the reporter
	 * @return the loaded schema
	 * @throws IOProviderConfigurationException if the I/O provider was not
	 *             configured properly
	 * @throws IOException if an I/O operation fails
	 */
	protected abstract Schema loadFromSource(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException;

	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	public void setCache(Value cache) {
		this.cache = cache;
	}

	@Override
	public Value getCache() {
		return cache;
	}

	@Override
	public boolean isCacheUpdate() {
		return cacheUpdate;
	}

}
