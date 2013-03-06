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

package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ViewerFilter;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.align.model.Cell;

/**
 * A {@link ReverseCellGraphContentProvider} with the option to add
 * {@link ViewerFilter}s to filter cells.
 * 
 * @author Kai Schwierczek
 */
public class FilteredReverseCellGraphContentProvider extends ReverseCellGraphContentProvider {

	private final List<ViewerFilter> filters = new ArrayList<ViewerFilter>();

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.content.CellGraphContentProvider#getEdges(java.lang.Iterable)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object[] getEdges(Iterable<?> cells) {
		return super.getEdges(Iterables.filter(cells, new Predicate() {

			@Override
			public boolean apply(Object input) {
				if (input instanceof Cell) {
					for (ViewerFilter filter : filters)
						if (!filter.select(null, null, input))
							return false;
					return true;
				}
				else
					return false;
			}
		}));
	}

	/**
	 * Adds the given filter to the list of registered filters. The filters
	 * {@link ViewerFilter#select(org.eclipse.jface.viewers.Viewer, Object, Object)}
	 * method will be used with null, null and the cell which is in question.
	 * 
	 * @param filter the filter to add
	 */
	public void addFilter(ViewerFilter filter) {
		filters.add(filter);
	}

	/**
	 * Removes one occurrence of <code>filter</code> from the registered filters
	 * if possible.
	 * 
	 * @param filter the filter to be removed
	 */
	public void removeFilter(ViewerFilter filter) {
		filters.remove(filter);
	}
}
