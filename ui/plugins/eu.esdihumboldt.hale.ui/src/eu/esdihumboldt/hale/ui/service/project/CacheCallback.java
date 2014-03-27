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

package eu.esdihumboldt.hale.ui.service.project;

import eu.esdihumboldt.hale.common.core.io.CachingImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Call-back that notifies about a cache update for a
 * {@link CachingImportProvider}.
 * 
 * @author Simon Templer
 */
public interface CacheCallback {

	/**
	 * Update the cache value with the given one.
	 * 
	 * @param cache the new cache value
	 */
	public void update(Value cache);

}
