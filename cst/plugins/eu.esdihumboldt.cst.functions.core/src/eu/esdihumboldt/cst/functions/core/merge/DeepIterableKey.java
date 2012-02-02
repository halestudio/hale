/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.functions.core.merge;

import eu.esdihumboldt.util.ObjectUtil;
import net.jcip.annotations.Immutable;

/**
 * Key that uses {@link ObjectUtil#deepIterableEquals(Object, Object)}
 * in {@link #equals(Object)} to compare the internal objects.
 * @author Simon Templer
 */
@Immutable
public class DeepIterableKey {
	
	private final Object key;

	/**
	 * Create a 
	 * @param key the key object
	 */
	public DeepIterableKey(Object key) {
		super();
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DeepIterableKey) {
			return ObjectUtil.deepIterableEquals(key, ((DeepIterableKey) obj).key);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ObjectUtil.deepIterableHashCode(key);
	}

}
