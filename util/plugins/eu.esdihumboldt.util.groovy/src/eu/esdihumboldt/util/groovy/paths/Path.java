/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.util.groovy.paths;

import java.util.List;

/**
 * Represents a path.
 * 
 * FIXME might have to be adapted to represent possible loops
 * 
 * @param <C> the element type
 * @author Simon Templer
 */
public interface Path<C> {

	/**
	 * Get the path elements.
	 * 
	 * @return the list of elements on the path
	 */
	public List<C> getElements();

	/**
	 * Create a sub path.
	 * 
	 * @param child the child to add at the end of the path
	 * @return the child path
	 */
	public Path<C> subPath(C child);

	/**
	 * Create a sub path.
	 * 
	 * @param append the path to append
	 * @return the child path
	 */
	public Path<C> subPath(Path<C> append);

}
