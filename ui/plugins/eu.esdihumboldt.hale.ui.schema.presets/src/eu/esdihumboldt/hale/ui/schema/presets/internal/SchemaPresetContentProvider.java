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

package eu.esdihumboldt.hale.ui.schema.presets.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaCategory;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaCategoryExtension;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset;

/**
 * Content provider for the categorized schema presets.
 * 
 * @author Simon Templer
 */
public class SchemaPresetContentProvider implements ITreeContentProvider {

	private final boolean forceCategories = false;

	@Override
	public void dispose() {
		// do nothing
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (forceCategories) {
			return ArrayContentProvider.getInstance().getElements(inputElement);
		}
		else {
			// only show categories for categories with more than one child
			List<Object> result = new ArrayList<>();
			for (Object element : ArrayContentProvider.getInstance().getElements(inputElement)) {
				if (element instanceof SchemaCategory) {
					SchemaCategory cat = (SchemaCategory) element;
					if (SchemaCategoryExtension.DEFAULT_CATEGORY.equals(cat)) {
						// add all schemas w/o category
						Iterables.addAll(result, cat.getSchemas());
					}
					else {
						int numSchemas = Iterables.size(cat.getSchemas());
						if (numSchemas > 1) {
							// add category
							result.add(cat);
						}
						else if (numSchemas == 1) {
							// add schemas
							result.add(cat.getSchemas().iterator().next());
						}
					}
				}
				else
					result.add(element);
			}
			return result.toArray();
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof SchemaCategory) {
			return Iterables.toArray(((SchemaCategory) parentElement).getSchemas(), Object.class);
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (forceCategories) {
			if (element instanceof SchemaPreset) {
				return SchemaCategoryExtension.getInstance().get(
						((SchemaPreset) element).getCategoryId());
			}
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof SchemaCategory) {
			return !Iterables.isEmpty(((SchemaCategory) element).getSchemas());
		}
		return false;
	}

}
