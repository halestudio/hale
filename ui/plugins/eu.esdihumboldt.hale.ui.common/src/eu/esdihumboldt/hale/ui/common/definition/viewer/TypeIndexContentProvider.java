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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Tree content provider using a {@link TypeIndex} as root, or an
 * {@link Iterable} of {@link TypeDefinition}s. XXX for now not lazy
 * 
 * @author Simon Templer
 */
public class TypeIndexContentProvider implements ITreeContentProvider {

	private final TreeViewer tree;

	/**
	 * Create a content provider based on a {@link TypeIndex} as input.
	 * 
	 * @param tree the associated tree viewer
	 */
	public TypeIndexContentProvider(TreeViewer tree) {
		super();

		this.tree = tree;
	}

	/**
	 * Get the associated tree viewer
	 * 
	 * @return the associated tree viewer
	 */
	protected TreeViewer getTree() {
		return tree;
	}

	/**
	 * @see ITreeContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TypeIndex) {
			return ((TypeIndex) inputElement).getMappingRelevantTypes().toArray();
		}
		else if (inputElement instanceof Iterable<?>) {
			return Iterables.toArray((Iterable<?>) inputElement, Object.class);
		}
		else {
			throw new IllegalArgumentException("Content provider only applicable for type indexes.");
		}
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TypeDefinition) {
			return ((TypeDefinition) parentElement).getChildren().toArray();
		}
		else if (parentElement instanceof GroupPropertyDefinition) {
			return ((GroupPropertyDefinition) parentElement).getDeclaredChildren().toArray();
		}
		else if (parentElement instanceof PropertyDefinition) {
			return ((PropertyDefinition) parentElement).getPropertyType().getChildren().toArray();
		}
		else {
			throw new IllegalArgumentException(
					"Given element not supported in schema tree structure.");
		}
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof TypeDefinition) {
			return !((TypeDefinition) parentElement).getChildren().isEmpty();
		}
		else if (parentElement instanceof GroupPropertyDefinition) {
			return !((GroupPropertyDefinition) parentElement).getDeclaredChildren().isEmpty();
		}
		else if (parentElement instanceof PropertyDefinition) {
			return !((PropertyDefinition) parentElement).getPropertyType().getChildren().isEmpty();
		}
		else {
			throw new IllegalArgumentException(
					"Given element not supported in schema tree structure.");
		}
	}

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
	 * @see ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		// parent can't be determined for sure, may be multiple possibilities
		// TODO return the "primary" parent?
		return null;
	}

}
