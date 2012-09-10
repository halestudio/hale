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
