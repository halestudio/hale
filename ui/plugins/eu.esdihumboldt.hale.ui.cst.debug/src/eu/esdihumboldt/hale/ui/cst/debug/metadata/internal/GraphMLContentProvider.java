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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.cst.debug.metadata.internal;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphContentProvider;

import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

/**
 * Content Provider for zest-graph creation with GraphML
 * 
 * @author Sebastian Reinhardt
 */
public class GraphMLContentProvider extends ArrayContentProvider implements IGraphContentProvider {

	/**
	 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getSource(java.lang.Object)
	 */
	@Override
	public Object getSource(Object rel) {
		if (rel instanceof Edge) {
			Object node = ((Edge) rel).getVertex(Direction.OUT);
			if (node != null) {
				return node;
			}
			else
				return null;
		}
		else
			return null;
	}

	/**
	 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getDestination(java.lang.Object)
	 */
	@Override
	public Object getDestination(Object rel) {
		if (rel instanceof Edge) {
			Object node = ((Edge) rel).getVertex(Direction.IN);
			if (node != null) {
				return node;
			}
			else
				return null;
		}
		else
			return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ArrayContentProvider#getElements(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Iterable<?>) {
			return super.getElements(Iterables.toArray((Iterable<Edge>) inputElement, Edge.class));
		}
		else
			return new Object[0];
	}
}
