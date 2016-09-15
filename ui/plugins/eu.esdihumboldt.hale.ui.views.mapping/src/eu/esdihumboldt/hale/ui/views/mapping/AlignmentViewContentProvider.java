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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.ui.common.graph.content.Edge;
import eu.esdihumboldt.hale.ui.common.graph.content.ReverseCellGraphContentProvider;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * A {@link ReverseCellGraphContentProvider} with the option to add
 * {@link ViewerFilter}s to filter cells. Also if the input is a (incomplete)
 * type cell all its property cells and matching type cells will be shown, and
 * inherited property cells will connect to the correct properties.
 * 
 * @author Kai Schwierczek
 */
public class AlignmentViewContentProvider extends ReverseCellGraphContentProvider {

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
					return select((Cell) input);
				}
				else
					return false;
			}
		}));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.content.CellGraphContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object input) {
		// check if the input is a (incomplete) type cell
		if (input instanceof Cell) {
			Cell cell = (Cell) input;
			Entity source = CellUtil.getFirstEntity(cell.getSource());
			Entity target = CellUtil.getFirstEntity(cell.getTarget());
			if ((source == null || source instanceof Type)
					&& (target == null || target instanceof Type))
				return getEdges((Cell) input);
		}

		return super.getElements(input);
	}

	/**
	 * Get all edges for the given type cell and its property cells.
	 * 
	 * @param typeCell the type cell to show
	 * @return the array of edges
	 */
	private Object[] getEdges(Cell typeCell) {
		List<Edge> edges = new ArrayList<Edge>();

		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		Alignment alignment = as.getAlignment();

		boolean dummyCell;
		if (typeCell.getId() != null) {
			// XXX really filter type cell out?
			if (select(typeCell)) {
				addEdges(typeCell, edges);
			}
			dummyCell = false;
		}
		else {
			// dummy cell, look for matching type cells
			for (Cell cell : alignment.getTypeCells(typeCell))
				if (select(cell))
					addEdges(cell, edges);
			dummyCell = true;
		}

		for (Cell cell : sortCells(as.getAlignment().getPropertyCells(typeCell, true, dummyCell))) {
			if (!select(cell))
				continue;

			Cell reparentCell = AlignmentUtil.reparentCell(cell, typeCell, false);

			// if the cell got changed connect the changed cell's entities
			// to the original cell
			if (reparentCell == cell)
				addEdges(cell, edges);
			else {
				// add edges leading to the cell for each source entity
				if (reparentCell.getSource() != null) {
					for (Entry<String, ? extends Entity> entry : reparentCell.getSource()
							.entries()) {
						edges.add(new Edge(entry.getValue(), cell, entry.getKey()));
					}
				}

				// add edges leading to the target entities from the cell
				for (Entry<String, ? extends Entity> entry : reparentCell.getTarget().entries()) {
					edges.add(new Edge(cell, entry.getValue(), entry.getKey()));
				}
			}
		}

		return edges.toArray();
	}

	/**
	 * sorts Cells alphabetically using the names of their PropertyDefinitions
	 * along the property path
	 * 
	 * @param cells the Cells to sort
	 * @return returns the Collection of sorted Cells
	 */
	private Collection<? extends Cell> sortCells(Collection<? extends Cell> cells) {
		Cell[] cellArr = cells.toArray(new Cell[cells.size()]);
		Arrays.sort(cellArr, new CellComparator());
		return Arrays.asList(cellArr);
	}

	/**
	 * Checks whether the cell makes it through all the filters.
	 * 
	 * @param cell the cell to check
	 * @return <code>true</code> if element is included in the filtered set, and
	 *         <code>false</code> if exclude
	 */
	private boolean select(Cell cell) {
		for (ViewerFilter filter : filters)
			if (!filter.select(null, null, cell))
				return false;
		return true;
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
