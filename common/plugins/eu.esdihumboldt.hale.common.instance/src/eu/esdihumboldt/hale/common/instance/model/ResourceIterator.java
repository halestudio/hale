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

package eu.esdihumboldt.hale.common.instance.model;

import java.io.Closeable;
import java.util.Iterator;

/**
 * Extends the iterator interface with a possibility to dispose the iterator.
 * 
 * @param <T> the type of objects that can be iterated over
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface ResourceIterator<T> extends Iterator<T>, Closeable {

	/**
	 * Dispose the iterator. After calling this method {@link #next()} may not
	 * be called.
	 */
	@Override
	public void close();

}
