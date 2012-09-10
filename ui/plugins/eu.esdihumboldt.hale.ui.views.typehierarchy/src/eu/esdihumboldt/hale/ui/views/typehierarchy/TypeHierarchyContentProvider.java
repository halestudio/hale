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

package eu.esdihumboldt.hale.ui.views.typehierarchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Tree content provider showing the hierarchy of a {@link TypeDefinition}
 * 
 * @author Simon Templer
 */
public class TypeHierarchyContentProvider implements ITreeContentProvider {

	/**
	 * Parent path for a type definition
	 */
	public static class ParentPath {

		private final List<TypeDefinition> path;

		private final TypeDefinition main;

		/**
		 * Create a parent path for the given type
		 * 
		 * @param main the main type definition
		 */
		public ParentPath(TypeDefinition main) {
			this.main = main;

			path = new ArrayList<TypeDefinition>();

			while (main != null) {
				path.add(0, main);
				main = main.getSuperType();
			}
		}

		/**
		 * Create a parent path
		 * 
		 * @param path the path
		 * @param main the main type
		 */
		private ParentPath(List<TypeDefinition> path, TypeDefinition main) {
			if (path.isEmpty()) {
				throw new IllegalArgumentException("Path may not be empty");
			}

			this.main = main;
			this.path = path;
		}

		/**
		 * Get the head type in the path
		 * 
		 * @return the head type or <code>null</code>
		 */
		public TypeDefinition getHead() {
			if (path.isEmpty()) {
				return null;
			}

			return path.get(0);
		}

		/**
		 * Get the path tail
		 * 
		 * @return the tail or <code>null</code>
		 */
		public ParentPath getTail() {
			if (path.isEmpty() || path.size() < 2) {
				return null;
			}
			return new ParentPath(path.subList(1, path.size()), main);
		}

		/**
		 * Create the sub-paths of the current path
		 * 
		 * @return the sub-paths
		 */
		public List<ParentPath> createSubPaths() {
			ParentPath tail = getTail();
			if (tail != null) {
				return Collections.singletonList(tail);
			}

			List<ParentPath> paths = new ArrayList<ParentPath>();
			for (TypeDefinition subType : getHead().getSubTypes()) {
				paths.add(new ParentPath(Collections.singletonList(subType), main));
			}
			return paths;
		}

		/**
		 * Determines if this path represents the main type.
		 * 
		 * @return if this path represents the main type
		 */
		public boolean isMainType() {
			return main.equals(getHead());
		}

		/**
		 * Get the main type.
		 * 
		 * @return the main type
		 */
		public TypeDefinition getMainType() {
			return main;
		}

		/**
		 * Get the path that only represents the main type.
		 * 
		 * @return the path that represents the main type
		 */
		public ParentPath getMainPath() {
			return new ParentPath(Collections.singletonList(getMainType()), getMainType());
		}

		/**
		 * @see Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((main == null) ? 0 : main.hashCode());
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			return result;
		}

		/**
		 * @see Object#equals(Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ParentPath other = (ParentPath) obj;
			if (main == null) {
				if (other.main != null)
					return false;
			}
			else if (!main.equals(other.main))
				return false;
			if (path == null) {
				if (other.path != null)
					return false;
			}
			else if (!path.equals(other.path))
				return false;
			return true;
		}

	}

	/**
	 * @see ITreeContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		List<ParentPath> roots = new ArrayList<ParentPath>();
		if (inputElement instanceof Iterable<?>) {
			for (Object input : ((Iterable<?>) inputElement)) {
				ParentPath path = createPath(input);
				if (path != null) {
					roots.add(path);
				}
			}
		}
		else {
			ParentPath path = createPath(inputElement);
			if (path != null) {
				roots.add(path);
			}
		}

		return roots.toArray();
	}

	/**
	 * Create a parent path for the given input element if possible
	 * 
	 * @param inputElement the input element
	 * @return the parent path or <code>null</code>
	 */
	public static ParentPath createPath(Object inputElement) {
		if (inputElement instanceof TypeDefinition) {
			return new ParentPath((TypeDefinition) inputElement);
		}

		if (inputElement instanceof PropertyDefinition) {
			return new ParentPath(((PropertyDefinition) inputElement).getPropertyType());
		}

		return null;
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ParentPath) {
			ParentPath path = (ParentPath) parentElement;
			return path.createSubPaths().toArray();
		}

		throw new IllegalArgumentException("Given element not supported in type hierarchy tree.");
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof ParentPath) {
			ParentPath path = (ParentPath) parentElement;
			ParentPath tail = path.getTail();
			if (tail != null) {
				return true;
			}
			else {
				parentElement = path.getHead();
			}
		}

		if (parentElement instanceof TypeDefinition) {
			return !((TypeDefinition) parentElement).getSubTypes().isEmpty();
		}

		throw new IllegalArgumentException("Given element not supported in type hierarchy tree.");
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
		if (element instanceof ParentPath) {
			ParentPath path = (ParentPath) element;

			TypeDefinition superType = path.getHead().getSuperType();
			if (superType == null) {
				// root -> parent is child
				return path.getMainType();
			}
			else {
				// create parent path
				List<TypeDefinition> list = new ArrayList<TypeDefinition>(path.path);
				list.add(0, superType);
				return new ParentPath(list, path.getMainType());
			}
		}

		// don't know at this point if the parent should be a parent path or a
		// type definition
		return null;
	}

}
