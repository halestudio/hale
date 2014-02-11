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

package eu.esdihumboldt.hale.common.core.io;

/**
 * Import provider that adds the ability to cache information on execution, that
 * can be reused or updated in later executions for the same resource.
 * 
 * @author Simon Templer
 */
public interface CachingImportProvider extends ImportProvider {

	/**
	 * Sets that a cache should be provided if possible. If this method is not
	 * called a produced cache would not be used and thus should not be created.
	 */
	public void setProvideCache();

	/**
	 * Sets the previously cached information from loading the resource.
	 * 
	 * @param cache the cached information, may be the NULL value
	 * @see Value#NULL
	 */
	public void setCache(Value cache);

	/**
	 * Get the cached information from the previous execution of the provider.
	 * 
	 * @return the cached information represented as {@link Value}
	 * @see #isCacheUpdate()
	 */
	public Value getCache();

	/**
	 * States if the cache was updated during the last execution.
	 * 
	 * @return if the cache was updated
	 */
	public boolean isCacheUpdate();

}
