/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.common;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.ChildEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;

/**
 * Visitor for {@link EntityDefinition}s.
 * 
 * @author Simon Templer
 */
public class EntityVisitor {

	/**
	 * Apply the visitor on an entity definition.
	 * 
	 * @param ed the entity definition
	 */
	public void accept(EntityDefinition ed) {
		if (ed instanceof TypeEntityDefinition) {
			accept((TypeEntityDefinition) ed);
		}
		else if (ed instanceof PropertyEntityDefinition) {
			accept((PropertyEntityDefinition) ed);
		}
		else if (ed instanceof ChildEntityDefinition) {
			accept((ChildEntityDefinition) ed);
		}
	}

	/**
	 * Apply the visitor on a type entity definition.
	 * 
	 * @param ted the type entity definition
	 */
	public void accept(TypeEntityDefinition ted) {
		if (visit(ted)) {
			for (ChildDefinition<?> child : ted.getDefinition().getChildren()) {
				EntityDefinition ed = AlignmentUtil.getChild(ted, child.getName());

				accept(ed);
			}
		}
	}

	/**
	 * Apply the visitor on a property entity definition.
	 * 
	 * @param ped the property entity definition
	 */
	public void accept(PropertyEntityDefinition ped) {
		if (visit(ped)) {
			for (ChildDefinition<?> child : ped.getDefinition().getPropertyType().getChildren()) {
				EntityDefinition ed = AlignmentUtil.getChild(ped, child.getName());

				accept(ed);
			}
		}
	}

	/**
	 * Apply the visitor on a child entity definition.
	 * 
	 * @param ced the child entity definition
	 */
	public void accept(ChildEntityDefinition ced) {
		if (visit(ced)) {
			for (ChildDefinition<?> child : ced.getDefinition().asGroup().getDeclaredChildren()) {
				EntityDefinition ed = AlignmentUtil.getChild(ced, child.getName());

				accept(ed);
			}
		}
	}

	/**
	 * Visit a property entity definition.
	 * 
	 * @param ped the property entity definition
	 * @return if traversal should be continued with the entity's children
	 */
	protected boolean visit(PropertyEntityDefinition ped) {
		return true;
	}

	/**
	 * Visit a child entity definition.
	 * 
	 * @param ced the child entity definition
	 * @return if traversal should be continued with the entity's children
	 */
	protected boolean visit(ChildEntityDefinition ced) {
		return true;
	}

	/**
	 * Visit a type entity definition.
	 * 
	 * @param ted the type entity definition
	 * @return if traversal should be continued with the entity's children
	 */
	protected boolean visit(TypeEntityDefinition ted) {
		return true;
	}

}
