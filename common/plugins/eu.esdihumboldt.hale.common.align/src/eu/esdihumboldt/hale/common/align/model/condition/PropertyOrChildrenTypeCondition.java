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

package eu.esdihumboldt.hale.common.align.model.condition;

import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Property condition wrapping a type condition and applying it to the property
 * type. It also accepts the property if any children of the property match the
 * condition.
 * 
 * @author Simon Templer
 */
public class PropertyOrChildrenTypeCondition implements PropertyCondition {

	private final TypeCondition typeCondition;

	/**
	 * Create a property condition based on the given type condition
	 * 
	 * @param typeCondition the type condition to apply to the property type
	 */
	public PropertyOrChildrenTypeCondition(TypeCondition typeCondition) {
		super();
		this.typeCondition = typeCondition;
	}

	/**
	 * @see EntityCondition#accept(Entity)
	 */
	@Override
	public boolean accept(Property entity) {
		PropertyEntityDefinition ped = entity.getDefinition();

		Set<TypeDefinition> tested = new HashSet<>();
		return accept(ped, tested);
	}

	private boolean accept(EntityDefinition entityDef, Set<TypeDefinition> tested) {
		Definition<?> def = entityDef.getDefinition();

		if (def instanceof PropertyDefinition) {
			// test the property definition and its property type

			TypeDefinition type = ((PropertyDefinition) def).getPropertyType();

			if (tested.contains(type)) {
				// we already tested that type
				return false;
			}

			if (accept(type, entityDef.getSchemaSpace())) {
				return true;
			}

			tested.add(type);
		}

		// test the children
		for (ChildDefinition<?> child : DefinitionUtil.getAllChildren(DefinitionUtil
				.getDefinitionGroup(def))) {
			EntityDefinition childDef = AlignmentUtil.getChild(entityDef, child.getName());
			if (accept(childDef, tested)) {
				return true;
			}
		}

		return false;
	}

	private boolean accept(TypeDefinition typeDef, SchemaSpaceID schemaSpace) {
		Type type = new DefaultType(new TypeEntityDefinition(typeDef, schemaSpace, null));
		return typeCondition.accept(type);
	}

}
