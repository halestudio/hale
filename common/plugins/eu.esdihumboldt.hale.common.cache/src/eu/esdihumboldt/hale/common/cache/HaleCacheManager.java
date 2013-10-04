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

package eu.esdihumboldt.hale.common.cache;

import net.sf.ehcache.Cache;

/**
 * This class extends the provided CacheManager from ehcache and adds useful
 * functions.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class HaleCacheManager extends net.sf.ehcache.CacheManager {

	/**
	 * @see Object#finalize()
	 */
	@Override
	protected void finalize() {
		super.shutdown();
	}

	/**
	 * Flushes cache data (memory) to disk.
	 * 
	 * @param cache cache name
	 * @see Cache#flush()
	 */
	public static void flush(String cache) {
		net.sf.ehcache.CacheManager.getInstance().getCache(cache).flush();
	}
}
