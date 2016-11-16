/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;

/**
 * Filters that hides optional properties.(Only works for
 * {@link EntityDefinition} elements).
 * 
 * @author Arun
 */
public class OptionalPropertiesFilter extends ViewerFilter {

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof TreePath) {
			element = ((TreePath) element).getLastSegment();
		}
		if (element instanceof EntityDefinition) {
			EntityDefinition entityDef = (EntityDefinition) element;

			Definition<?> def = entityDef.getDefinition();

			if (def instanceof PropertyDefinition) {
				Cardinality cardinality = ((PropertyDefinition) def)
						.getConstraint(Cardinality.class);
				return !(cardinality.getMinOccurs() == 0 && ((PropertyDefinition) def)
						.getConstraint(NillableFlag.class).isEnabled());
			}
		}
		return true;
	}

}
