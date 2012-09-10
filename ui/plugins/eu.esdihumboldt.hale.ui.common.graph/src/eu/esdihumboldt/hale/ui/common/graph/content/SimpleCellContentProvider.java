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
