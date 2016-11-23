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

import java.util.Collection;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
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

			if (def instanceof GroupPropertyDefinition) {
				Cardinality cardinality = ((GroupPropertyDefinition) def)
						.getConstraint(Cardinality.class);
				if (cardinality.getMinOccurs() == 0) {
					// optional group, don't accept
					return false;
				}
				else {
					// mandatory group, check if children are optional
					// accept if there are children that are not optional
					return !areChildrenOptional(entityDef);
				}

			}
			else if (def instanceof PropertyDefinition) {
				Cardinality cardinality = ((PropertyDefinition) def)
						.getConstraint(Cardinality.class);
				if (cardinality.getMinOccurs() == 0) {
					// optional property, don't accept
					return false;
				}

				// property must occur, but maybe it is nillable
				if (((PropertyDefinition) def).getConstraint(NillableFlag.class).isEnabled()) {
					// property is nillable
					// only accept if there are children that are not optional
					return !areChildrenOptional(entityDef);
				}
			}
		}
		return true;
	}

	/**
	 * Determines if all children of the given entity are optional.
	 * 
	 * @param entityDef the entity definition which children to check
	 * @return if all children are optional
	 */
	private boolean areChildrenOptional(EntityDefinition entityDef) {
		// XXX do we have to check which definitions we already visited to be
		// avoid problems in loops?

		// get children without contexts
		Collection<? extends EntityDefinition> children = AlignmentUtil
				.getChildrenWithoutContexts(entityDef);

		if (children == null || children.isEmpty()) {
			return true;
		}

		for (EntityDefinition child : children) {

			Definition<?> def = child.getDefinition();

			if (def instanceof GroupPropertyDefinition) {
				Cardinality cardinality = ((GroupPropertyDefinition) def)
						.getConstraint(Cardinality.class);
				if (cardinality.getMinOccurs() != 0 && !areChildrenOptional(entityDef)) {
					// not optional if it must occur at least once and children
					// are not optional
					return false;
				}
			}
			else if (def instanceof PropertyDefinition) {
				Cardinality cardinality = ((PropertyDefinition) def)
						.getConstraint(Cardinality.class);

				if (cardinality.getMinOccurs() != 0) {
					// if the property must occur it could still be nillable
					if (((PropertyDefinition) def).asProperty().getConstraint(NillableFlag.class)
							.isEnabled()) {
						// if the property is nillable it is not optional if any
						// of the children is not optional
						if (!areChildrenOptional(child)) {
							return false;
						}
					}
					else {
						// the property must occur and is not nillable
						return false;
					}
				}
			}
		}

		return true;
	}

}
