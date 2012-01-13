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

package eu.esdihumboldt.hale.ui.common.function.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.common.align.extension.category.Category;
import eu.esdihumboldt.hale.common.align.extension.category.CategoryExtension;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;
import eu.esdihumboldt.hale.ui.common.internal.Messages;

/**
 * Function content provider. Expects no input and displays all 
 * available functions.
 * @author Simon Templer
 */
public class FunctionContentProvider implements ITreeContentProvider {
	
	private static final Category CAT_OTHER = new Category(null, Messages.FunctionContentProvider_others, 
			Messages.FunctionContentProvider_description);
	
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
	 * @see ITreeContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		List<Category> cats = new ArrayList<Category>(
				CategoryExtension.getInstance().getElements());
		cats.add(CAT_OTHER);
		
		// remove categories w/o functions
		ListIterator<Category> it = cats.listIterator();
		while (it.hasNext()) {
			if (!hasChildren(it.next())) {
				it.remove();
			}
		}
		
		return cats.toArray();
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Category) {
			Category category = (Category) parentElement;
			
			List<AbstractFunction<?>> functions = new ArrayList<AbstractFunction<?>>();
			functions.addAll(TypeFunctionExtension.getInstance().getFunctions(
					category.getId()));
			functions.addAll(PropertyFunctionExtension.getInstance().getFunctions(
					category.getId()));
			
			return functions.toArray();
		}
		
		return null;
	}

	/**
	 * @see ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof AbstractFunction<?>) {
			String catId = ((AbstractFunction<?>) element).getCategoryId();
			
			Category cat = (catId == null)?(null):(CategoryExtension.getInstance().get(catId));
			if (cat == null) {
				cat = CAT_OTHER;
			}
			
			return cat;
		}
		
		return null;
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Category) {
			Category category = (Category) element;
			
			List<TypeFunction> typeFunctions = TypeFunctionExtension
					.getInstance().getFunctions(category.getId());
			if (!typeFunctions.isEmpty()) {
				return true;
			}

			List<PropertyFunction> properyFunctions = PropertyFunctionExtension
					.getInstance().getFunctions(category.getId());
			if (!properyFunctions.isEmpty()) {
				return true;
			}
		}
		
		return false;
	}

}
