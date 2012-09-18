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

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;

import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Graph content provider that models cells as edges. Supports an
 * {@link Alignment}, a {@link Cell} or an {@link Iterable} of {@link Cell}s as
 * input.
 * 
 * @author Simon Templer
 */
@Deprecated
public class SimpleCellContentProvider implements IGraphContentProvider {

	/**
	 * @see IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	/**
	 * @see IGraphContentProvider#getSource(Object)
	 */
	@Override
	public Object getSource(Object rel) {
		if (rel instanceof Cell) {
			Cell cell = (Cell) rel;

			return getEntity(cell.getSource());
		}

		return null;
	}

	/**
	 * @see IGraphContentProvider#getDestination(Object)
	 */
	@Override
	public Object getDestination(Object rel) {
		if (rel instanceof Cell) {
			Cell cell = (Cell) rel;

			return getEntity(cell.getTarget());
		}

		return null;
	}

	private Object getEntity(ListMultimap<String, ? extends Entity> entities) {
		if (entities.isEmpty()) {
			return null;
		}

		// FIXME what about the other entities?!
		return entities.values().iterator().next();
	}

	/**
	 * @see IGraphContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object input) {
		// FIXME what about cells that refer to multiple source or target
		// entities?!

		if (input instanceof Alignment) {
			return ((Alignment) input).getCells().toArray();
		}

		if (input instanceof Cell) {
			return new Object[] { input };
		}

		if (input instanceof Iterable<?>) {
			return Iterables.toArray((Iterable<?>) input, Object.class);
		}

		return null;
	}

}
