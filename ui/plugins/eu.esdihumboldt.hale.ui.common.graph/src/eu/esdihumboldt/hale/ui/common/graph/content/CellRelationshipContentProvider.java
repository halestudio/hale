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

package eu.esdihumboldt.hale.ui.common.graph.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityRelationshipContentProvider;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Graph entity relationship content provider that models entities and cells as
 * nodes. Supports an {@link Alignment}, a {@link Cell} or an {@link Iterable}
 * of {@link Cell}s as input.
 * 
 * @author Simon Templer
 */
public class CellRelationshipContentProvider extends ArrayContentProvider implements
		IGraphEntityRelationshipContentProvider {

	/**
	 * @see IGraphEntityRelationshipContentProvider#getRelationships(Object,
	 *      Object)
	 */
	@Override
	public Object[] getRelationships(Object source, Object dest) {
		List<Edge> result;
		if (dest instanceof Cell && source instanceof Entity) {
			result = new ArrayList<Edge>();
			// identify source entity relations
			ListMultimap<String, ? extends Entity> entityMap = ((Cell) dest).getSource();
			for (String key : entityMap.keySet()) {
				if (entityMap.containsEntry(key, source)) {
					result.add(new Edge(source, dest, key));
				}
			}
		}
		else if (source instanceof Cell && dest instanceof Entity) {
			result = new ArrayList<Edge>();
			// identify target entity relations
			ListMultimap<String, ? extends Entity> entityMap = ((Cell) source).getTarget();
			for (String key : entityMap.keySet()) {
				if (entityMap.containsEntry(key, dest)) {
					result.add(new Edge(source, dest, key));
				}
			}
		}
		else {
			return null;
		}

		return result.toArray();
	}

	/**
	 * @see IGraphContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object input) {
		if (input instanceof Alignment) {
			return getNodes(((Alignment) input).getCells());
		}

		if (input instanceof Cell) {
			return getNodes(Collections.singleton(input));
		}

		if (input instanceof Iterable<?>) {
			return getNodes((Iterable<?>) input);
		}

		return super.getElements(input);
	}

	/**
	 * Get all nodes for the given cells.
	 * 
	 * @param cells an iterable of {@link Cell}s, other objects will be ignored
	 * @return the array of edges
	 */
	protected Object[] getNodes(Iterable<?> cells) {
		Set<Object> nodes = new LinkedHashSet<Object>();

		for (Object object : cells) {
			if (object instanceof Cell) {
				Cell cell = (Cell) object;

				// add source entities
				for (Entity entity : cell.getSource().values()) {
					nodes.add(entity);
				}

				// add cell
				nodes.add(cell);

				// add target entities
				for (Entity entity : cell.getTarget().values()) {
					nodes.add(entity);
				}
			}
		}

		return nodes.toArray();
	}

}
