/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.instance.model;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance iterator with advanced capabilities.
 * 
 * @author Simon Templer
 */
public interface InstanceIterator extends ResourceIterator<Instance> {

	/**
	 * Peek at the type of the next instance. May only be called if
	 * {@link #supportsTypePeek()} yields <code>true</code>.
	 * 
	 * @return the type definition of the next instance, may be
	 *         <code>null</code> if there is no next instance
	 */
	public TypeDefinition typePeek();

	/**
	 * States if the iterator suports peeking at the type of the next instances
	 * with {@link #typePeek()}.
	 * 
	 * @return if calls to {@link #typePeek()} are allowed
	 */
	public boolean supportsTypePeek();

	/**
	 * Skip the next instance without creating it. Should only be called if
	 * {@link #hasNext()} yields true.
	 */
	public void skip();

}
