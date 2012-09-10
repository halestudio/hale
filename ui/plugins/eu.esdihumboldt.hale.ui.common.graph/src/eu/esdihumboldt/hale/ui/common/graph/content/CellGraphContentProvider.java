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

package eu.esdihumboldt.hale.ui.common.graph.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphContentProvider;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Graph content provider that models entities and cells as nodes. Supports an
 * {@link Alignment}, a {@link Cell} or an {@link Iterable} of {@link Cell}s as
 * input.
 * 
 * @author Simon Templer
 */
public class CellGraphContentProvider extends ArrayContentProvider implements IGraphContentProvider {

	/**
	 * @see IGraphContentProvider#getSource(Object)
	 */
	@Override
	public Object getSource(Object rel) {
		if (rel instanceof Edge) {
			Edge edge = (Edge) rel;
			return edge.getFirst();
		}

		return null;
	}

	/**
	 * @see IGraphContentProvider#getDestination(Object)
	 */
	@Override
	public Object getDestination(Object rel) {
		if (rel instanceof Edge) {
			Edge edge = (Edge) rel;
			return edge.getSecond();
		}

		return null;
	}

	/**
	 * @see IGraphContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object input) {
		if (input instanceof Alignment) {
			return getEdges(((Alignment) input).getCells());
		}

		if (input instanceof Cell) {
			return getEdges(Collections.singleton(input));
		}

		if (input instanceof Iterable<?>) {
			return getEdges((Iterable<?>) input);
		}

		return super.getElements(input);
	}

	/**
	 * Get all edges for the given cells.
	 * 
	 * @param cells an iterable of {@link Cell}s, other objects will be ignored
	 * @return the array of edges
	 */
	protected Object[] getEdges(Iterable<?> cells) {
		List<Edge> edges = new ArrayList<Edge>();

		for (Object object : cells) {
			if (object instanceof Cell) {
				Cell cell = (Cell) object;

				// add edges leading to the cell for each source entity
				if (cell.getSource() != null) {
					for (Entry<String, ? extends Entity> entry : cell.getSource().entries()) {
						edges.add(new Edge(entry.getValue(), cell, entry.getKey()));
					}
				}

				// add edges leading to the target entities from the cell
				for (Entry<String, ? extends Entity> entry : cell.getTarget().entries()) {
					edges.add(new Edge(cell, entry.getValue(), entry.getKey()));
				}
			}
		}

		return edges.toArray();
	}

}
