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

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.google.common.base.Objects;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;

/**
 * Filter that hides inherited properties (Only works for
 * {@link EntityDefinition} elements).
 * 
 * @author Simon Templer
 */
public class InheritedPropertiesFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof TreePath) {
			element = ((TreePath) element).getLastSegment();
		}

		if (element instanceof EntityDefinition) {
			EntityDefinition entityDef = (EntityDefinition) element;

			/*
			 * Only filter properties directly associated to a type, all nested
			 * properties must be shown at all times.
			 */
			if (entityDef.getPropertyPath().size() == 1) {
				ChildDefinition<?> child = entityDef.getPropertyPath().get(0).getChild();
				// if declaring group and parent type are the same, show it
				return Objects.equal(child.getDeclaringGroup(), entityDef.getType());
			}
		}

		return true;
	}

}
