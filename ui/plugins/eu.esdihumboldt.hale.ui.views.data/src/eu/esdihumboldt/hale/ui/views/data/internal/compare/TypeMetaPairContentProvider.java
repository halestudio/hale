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

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;
import eu.esdihumboldt.util.Pair;

/**
 * Subclass of the tree content provider {@link TypeIndexContentProvider}, which
 * can handle metadatas of instances
 * 
 * @author Sebastian Reinhardt
 */
public class TypeMetaPairContentProvider extends TypeIndexContentProvider {

	/**
	 * @see TypeIndexContentProvider#TypeIndexContentProvider(TreeViewer)
	 */
	public TypeMetaPairContentProvider(TreeViewer tree) {
		super(tree);
	}

	/**
	 * @see TypeIndexContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof Pair<?, ?>) {

			Pair<?, ?> pair = (Pair<?, ?>) inputElement;
			// second item will be a set of metadata keys
			return new Object[] { pair.getFirst(), pair.getSecond() };

		}

		else
			return new Object[0];

	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Set<?>) {
			return ((Set<String>) parentElement).toArray();
		}
		else
			return super.getChildren(parentElement);
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof Set<?>) {
			return !((Set<String>) parentElement).isEmpty();
		}
		if (parentElement instanceof String) {
			return false;
		}
		else
			return super.hasChildren(parentElement);
	}

}
