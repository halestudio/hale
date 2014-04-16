/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.codelist.inspire.internal;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Content provider for code list listing.
 * 
 * @author Simon Templer
 */
public class CodeListContentProvider implements ITreeContentProvider {

	/**
	 * Other category which should appear at the end.
	 */
	public static final String OTHER = "Other";

	private final Multimap<String, CodeListRef> categorized = ArrayListMultimap.create();

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// populate map
		categorized.clear();
		if (newInput instanceof Iterable<?>) {
			for (Object item : (Iterable<?>) newInput) {
				if (item instanceof CodeListRef) {
					CodeListRef ref = (CodeListRef) item;
					categorized.put(getCategory(ref), ref);
				}
			}
		}
	}

	@Override
	public void dispose() {
		categorized.clear();
	}

	@Override
	public boolean hasChildren(Object element) {
		// categories are Strings
		return element instanceof String;
	}

	/**
	 * Get the category for a code list.
	 * 
	 * @param ref the code list
	 * @return the code list category
	 */
	protected String category(CodeListRef ref) {
		return ref.getThemeName();
	}

	private String getCategory(CodeListRef ref) {
		String cat = category(ref);
		if (cat == null) {
			return OTHER;
		}
		return cat;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof CodeListRef) {
			return getCategory((CodeListRef) element);
		}

		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return categorized.keySet().toArray();
		// return ArrayContentProvider.getInstance().getElements(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof String) {
			return categorized.get(parentElement.toString()).toArray();
		}
		return new Object[] {};
	}
}