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

package eu.esdihumboldt.hale.ui.views.functions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.extension.category.Category;
import eu.esdihumboldt.hale.common.align.extension.category.CategoryExtension;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;

/**
 * Function content provider
 * @author Simon Templer
 */
public class FunctionContentProvider implements ITreeContentProvider {
	
	private static final Category CAT_OTHER = new Category(null, "Others", 
			"Any functions not associated to a category");
	
	private CategoryExtension categoryExtension;
	
	private TypeFunctionExtension typeFunctionExtension;
	
	private PropertyFunctionExtension propertyFunctionExtension;
	
	/**
	 * Get the category extension
	 * @return the category extension
	 */
	protected CategoryExtension getCategoryExtension() {
		if (categoryExtension == null) {
			categoryExtension = OsgiUtils.getService(CategoryExtension.class);
		}
		return categoryExtension;
	}
	
	/**
	 * Get the type function extension
	 * @return the type function extension
	 */
	protected TypeFunctionExtension getTypeFunctionExtension() {
		if (typeFunctionExtension == null) {
			typeFunctionExtension = OsgiUtils.getService(TypeFunctionExtension.class);
		}
		return typeFunctionExtension;
	}
	
	/**
	 * Get the property function extension
	 * @return the property function extension
	 */
	protected PropertyFunctionExtension getPropertyFunctionExtension() {
		if (propertyFunctionExtension == null) {
			propertyFunctionExtension = OsgiUtils.getService(PropertyFunctionExtension.class);
		}
		return propertyFunctionExtension;
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
	 * @see ITreeContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		List<Category> cats = new ArrayList<Category>(getCategoryExtension().getElements());
		cats.add(CAT_OTHER);
		return cats.toArray();
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Category) {
			Category category = (Category) parentElement;
			
			List<AbstractFunction> functions = new ArrayList<AbstractFunction>();
			functions.addAll(getTypeFunctionExtension().getFunctions(
					category.getId()));
			functions.addAll(getPropertyFunctionExtension().getFunctions(
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Category) {
			Category category = (Category) element;
			
			List<TypeFunction> typeFunctions = getTypeFunctionExtension().getFunctions(
					category.getId());
			if (!typeFunctions.isEmpty()) {
				return true;
			}
			
			List<PropertyFunction> properyFunctions = getPropertyFunctionExtension().getFunctions(
					category.getId());
			if (!properyFunctions.isEmpty()) {
				return true;
			}
		}
		
		return false;
	}

}
