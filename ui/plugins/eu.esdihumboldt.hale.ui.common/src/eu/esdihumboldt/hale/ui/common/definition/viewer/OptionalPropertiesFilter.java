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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Filters that hides optional properties.(Only works for
 * {@link EntityDefinition} elements).
 * 
 * @author Arun
 */
public class OptionalPropertiesFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof TreePath) {
			element = ((TreePath) element).getLastSegment();
		}
		if (element instanceof EntityDefinition) {
			EntityDefinition entityDef = (EntityDefinition) element;

			if (!entityDef.getPropertyPath().isEmpty()) {
				// property or group
				// accept if not optional
				return !isOptional(entityDef);
			}
			else {
				// accept type entity allways
				return true;
			}
		}
		return true;
	}

	/**
	 * Determines if all children of the given entity are optional.
	 * 
	 * @param entityDef the entity definition which children to check
	 * @return if the entity is optional
	 */
	private boolean isOptional(EntityDefinition entityDef) {
		return isOptional(entityDef, new HashSet<Definition<?>>());
	}

	/**
	 * Determines if an entity definition is optional.
	 * 
	 * @param entityDef the entity definition which children to check
	 * @param alreadyChecked the set of definitions that have already been
	 *            checked (excluding the given entity)
	 * @return if the entity is optional
	 */
	private boolean isOptional(EntityDefinition entityDef, Set<Definition<?>> alreadyChecked) {
		Definition<?> def = entityDef.getDefinition();

		if (alreadyChecked.contains(def)) {
			// could not decide yet if it is optional
			// means there was no clearly mandatory property
			// -> treat as optional
			return true;
		}

		if (def instanceof GroupPropertyDefinition) {
			Cardinality cardinality = ((GroupPropertyDefinition) def)
					.getConstraint(Cardinality.class);
			if (cardinality.getMinOccurs() != 0
					&& !areChildrenOptional(entityDef, alreadyChecked)) {
				// not optional if it must occur at least once and children
				// are not optional
				return false;
			}

			return true;
		}
		else if (def instanceof PropertyDefinition) {
			Cardinality cardinality = ((PropertyDefinition) def).getConstraint(Cardinality.class);

			if (cardinality.getMinOccurs() != 0) {
				// if the property must occur it could still be nillable
				if (((PropertyDefinition) def).asProperty().getConstraint(NillableFlag.class)
						.isEnabled()) {
					// if the property is nillable it is not optional if any
					// of the children is not optional
					if (!areChildrenOptional(entityDef, alreadyChecked)) {
						return false;
					}
				}
				else {
					// the property must occur and is not nillable
					return false;
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Determines if the given (nillable) entity can be considered optional in
	 * respect to its children.
	 * 
	 * @param entityDef the entity definition which children to check
	 * @param alreadyChecked the set of definitions that have already been
	 *            checked (excluding the given entity)
	 * @return if all children are optional
	 */
	private boolean areChildrenOptional(EntityDefinition entityDef,
			Set<Definition<?>> alreadyChecked) {
		// get children without contexts
		Collection<? extends EntityDefinition> children = AlignmentUtil
				.getChildrenWithoutContexts(entityDef);

		if (children == null || children.isEmpty()) {
			return true;
		}

		for (EntityDefinition child : children) {
			Set<Definition<?>> checked = new HashSet<>(alreadyChecked);
			checked.add(entityDef.getDefinition());

			ChildDefinition<?> childDef = (ChildDefinition<?>) child.getDefinition();

			/*
			 * XML: We only need to check children that are attributes, if they
			 * are optional.
			 */
			if (childDef.asProperty() != null
					&& childDef.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
				// child is an XML attribute
				// need to check if it is optional
				if (!isOptional(child, checked)) {
					return false;
				}
			}

			/*
			 * XXX does other special handling need to be done for other kinds
			 * of schemas?
			 */
		}

		return true;
	}

}
