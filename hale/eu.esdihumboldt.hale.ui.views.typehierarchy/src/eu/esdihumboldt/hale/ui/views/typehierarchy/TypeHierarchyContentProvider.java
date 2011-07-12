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
import java.util.List;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;

/**
 * Tree content provider showing the hierarchy of a {@link TypeDefinition}
 * @author Simon Templer
 */
public class TypeHierarchyContentProvider implements ITreeContentProvider {

	/**
	 * Parent path for a type definition
	 */
	public class ParentPath {
		
		private final List<TypeDefinition> path;

		/**
		 * Create a parent path for the give type
		 * @param type the type definition
		 */
		public ParentPath(TypeDefinition type) {
			path = new ArrayList<TypeDefinition>();
			
			while (type != null) {
				path.add(0, type);
				type = type.getSuperType();
			}
		}
		
		/**
		 * Create a parent path
		 * @param path the path
		 */
		private ParentPath(List<TypeDefinition> path) {
			if (path.isEmpty()) {
				throw new IllegalArgumentException("Path may not be empty");
			}
			
			this.path = path;
		}

		/**
		 * Get the head type in the path
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
		 * @return the tail or <code>null</code>
		 */
		public ParentPath getTail() {
			if (path.isEmpty() || path.size() < 2) {
				return null;
			}
			return new ParentPath(path.subList(1, path.size()));
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
	 * @param inputElement the input element
	 * @return the parent path or <code>null</code>
	 */
	private ParentPath createPath(Object inputElement) {
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
			ParentPath tail = path.getTail();
			if (tail != null) {
				return new Object[]{tail};
			}
			else {
				parentElement = path.getHead();
			}
		}
		
		if (parentElement instanceof TypeDefinition) {
			return ((TypeDefinition) parentElement).getSubTypes().toArray();
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
		// don't know at this point if the parent should be a parent path or a type definition
		return null;
	}

}
