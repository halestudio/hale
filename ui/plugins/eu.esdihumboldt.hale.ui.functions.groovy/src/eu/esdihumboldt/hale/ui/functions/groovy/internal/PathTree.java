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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreePath;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Represents {@link TreePath}s in a hierarchical way.
 * 
 * @author Simon Templer
 */
public class PathTree {

	/**
	 * Create path trees from the given tree paths
	 * 
	 * @param paths the tree paths
	 * @param startIndex the start index
	 * @return the list of path trees, one per different segment on the paths at
	 *         the start index
	 */
	public static List<PathTree> createPathTrees(Iterable<TreePath> paths, int startIndex) {
		// partition paths by segment at index
		ListMultimap<Object, TreePath> partitioned = ArrayListMultimap.create();
		for (TreePath path : paths) {
			if (startIndex < path.getSegmentCount()) {
				partitioned.put(path.getSegment(startIndex), path);
			}
		}

		List<PathTree> result = new ArrayList<>();

		// for each different segment a PathTree must be created
		for (Object segment : partitioned.keySet()) {
			PathTree pt = new PathTree(segment, createPathTrees(partitioned.get(segment),
					startIndex + 1));
			result.add(pt);
		}

		return result;
	}

	private final Object segment;

	private final List<PathTree> children;

	private PathTree(Object segment, List<PathTree> children) {
		super();
		this.segment = segment;
		this.children = children;
	}

	/**
	 * @return the segment
	 */
	public Object getSegment() {
		return segment;
	}

	/**
	 * @return the children
	 */
	public List<PathTree> getChildren() {
		return children;
	}

}
