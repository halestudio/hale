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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import eu.esdihumboldt.hale.common.align.model.impl.ChildEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * This filter will filter through the types
 * 
 * @author Yasmina Kammeyer
 */
public class SelectedTypeFilter extends ViewerFilter {

	Set<TypeEntityDefinition> types;
	Set<TypeDefinition> superTypes;

	/**
	 * Max number of parents
	 */
	public static final int PARENT_DEPTH = 6;

	/**
	 * Constructor
	 */
	public SelectedTypeFilter() {
		types = new HashSet<TypeEntityDefinition>();
		superTypes = new HashSet<TypeDefinition>();
	}

	/**
	 * Set the types which should be filtered. This could be used to limit the
	 * Schema Explorer View to selected Types.
	 * 
	 * @param selectedType The selected types
	 */
	public void setSelectedTypes(Collection<TypeEntityDefinition> selectedType) {
		types.clear();
		superTypes.clear();
		if (selectedType != null && !selectedType.isEmpty()) {
			for (TypeEntityDefinition type : selectedType) {
				types.add(type);
				superTypes.add(type.getDefinition());
				// Add all parent Type Definition
				TypeDefinition typeDef = type.getDefinition();
				while ((typeDef = typeDef.getSuperType()) != null)
					superTypes.add(typeDef);
			}// end for
		}// end if
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		// if there is no selected type, everything is filtered
		if (types.isEmpty())
			return false;

		if (element instanceof TreePath) {
			element = ((TreePath) element).getLastSegment();
		}

		// Always add properties
		if (element instanceof ChildEntityDefinition) {
			return true;
		}

		if (element instanceof TypeEntityDefinition) {
			TypeEntityDefinition typeDef = (TypeEntityDefinition) element;
			// Always add the root element
			if (typeDef.getDefinition().getSuperType() == null)
				return true;
			// Check if the element is the selected Type
			if (types.contains(typeDef)) {
				return true;
			}
			if (typeDef.getFilter() == null)
				return superTypes.contains(typeDef.getDefinition());
			// If element has SubTypes, add all root elements
//				if (!typeDef.getDefinition().getSubTypes().isEmpty()
//						&& associatedParents.contains(typeDef.getDefinition()))
//					return true;
//				// If the element is not equal to the selected type, check the
//				// element with no filter matches the name of the selected type
//				if (typeDef.equals(type)
//						|| (type.getFilter() != null && typeDef.getFilter() == null && typeDef
//								.getDefinition().getName().equals(type.getDefinition().getName())))
//					return true;
		}
		return false;
	}
}
