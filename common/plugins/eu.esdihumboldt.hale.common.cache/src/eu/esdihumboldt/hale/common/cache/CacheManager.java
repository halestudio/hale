/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
public class CacheManager extends net.sf.ehcache.CacheManager {

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
