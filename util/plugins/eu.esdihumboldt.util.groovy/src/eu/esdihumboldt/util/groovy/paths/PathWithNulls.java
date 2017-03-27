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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Path implementation that supports .
 * 
 * @param <C> the child type
 * @author Simon Templer
 */
public class PathWithNulls<C> implements Path<C> {

	private final List<C> path;

	/**
	 * Create a definition path.
	 * 
	 * @param path the list of definitions defining the path
	 */
	public PathWithNulls(List<C> path) {
		super();
		this.path = Collections.unmodifiableList(new ArrayList<>(path));
	}

	/**
	 * Create a path with one parent element.
	 * 
	 * @param parent the parent element
	 */
	public PathWithNulls(C parent) {
		this(Collections.singletonList(parent));
	}

	/**
	 * Create an empty path.
	 */
	public PathWithNulls() {
		this(ImmutableList.<C> of());
	}

	@Override
	public List<C> getElements() {
		return path;
	}

	@Override
	public Path<C> subPath(C child) {
		List<C> list = new ArrayList<>(path);
		list.add(child);
		return new PathWithNulls<C>(list);
	}

	@Override
	public Path<C> subPath(Path<C> append) {
		List<C> list = new ArrayList<>(path);
		list.addAll(append.getElements());
		return new PathWithNulls<C>(list);
	}

}
