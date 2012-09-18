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
