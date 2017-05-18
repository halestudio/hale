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

import com.google.common.collect.ImmutableList;

/**
 * Path base implementation. This path implementation does not allow
 * <code>null</code> values as part of paths.
 * 
 * @param <C> the child type
 * @author Simon Templer
 */
public class PathImpl<C> implements Path<C> {

	private final List<C> path;

	/**
	 * Create a definition path.
	 * 
	 * @param path the list of definitions defining the path
	 */
	public PathImpl(List<C> path) {
		super();
		this.path = ImmutableList.copyOf(path);
	}

	/**
	 * Create a path with one parent element.
	 * 
	 * @param parent the parent element
	 */
	public PathImpl(C parent) {
		this(ImmutableList.<C> of(parent));
	}

	/**
	 * Create an empty path.
	 */
	public PathImpl() {
		this(ImmutableList.<C> of());
	}

	@Override
	public List<C> getElements() {
		return path;
	}

	@Override
	public Path<C> subPath(C child) {
		return new PathImpl<C>(ImmutableList.<C> builder().addAll(path).add(child).build());
	}

	@Override
	public Path<C> subPath(Path<C> append) {
		return new PathImpl<C>(
				ImmutableList.<C> builder().addAll(path).addAll(append.getElements()).build());
	}

}
