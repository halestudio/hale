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

package eu.esdihumboldt.hale.ui.common.function.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nullable;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import eu.esdihumboldt.hale.common.align.extension.category.Category;
import eu.esdihumboldt.hale.common.align.extension.category.CategoryExtension;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.ui.common.internal.Messages;

/**
 * Function content provider. Expects no input and displays all available
 * functions.
 * 
 * @author Simon Templer
 */
public class FunctionContentProvider implements ITreeContentProvider,
		Predicate<FunctionDefinition<?>> {

	private static final Category CAT_OTHER = new Category(null,
			Messages.FunctionContentProvider_others, Messages.FunctionContentProvider_description);

	@Nullable
	private final ServiceProvider serviceProvider;

	/**
	 * Create a new function content provider.
	 * 
	 * @param serviceProvider the service provider, may be <code>null</code> if
	 *            none is accessible
	 */
	public FunctionContentProvider(@Nullable ServiceProvider serviceProvider) {
		super();
		this.serviceProvider = serviceProvider;
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
		List<Category> cats = new ArrayList<Category>(CategoryExtension.getInstance().getElements());
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

	@Override
	public boolean apply(FunctionDefinition<?> function) {
		return true;
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Category) {
			Category category = (Category) parentElement;

			List<FunctionDefinition<?>> functions = new ArrayList<>();
			functions.addAll(Collections2.filter(
					FunctionUtil.getTypeFunctions(category.getId(), serviceProvider), this));
			functions.addAll(Collections2.filter(
					FunctionUtil.getPropertyFunctions(category.getId(), serviceProvider), this));

			return functions.toArray();
		}

		return null;
	}

	/**
	 * @see ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof FunctionDefinition<?>) {
			String catId = ((FunctionDefinition<?>) element).getCategoryId();

			Category cat = (catId == null) ? (null) : (CategoryExtension.getInstance().get(catId));
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
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

}
