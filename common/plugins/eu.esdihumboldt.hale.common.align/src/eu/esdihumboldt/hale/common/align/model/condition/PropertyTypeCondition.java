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

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Property condition wrapping a type condition and applying it to the property
 * type.
 * 
 * @author Simon Templer
 */
public class PropertyTypeCondition implements PropertyCondition {

	private final TypeCondition typeCondition;

	/**
	 * Create a property condition based on the given type condition
	 * 
	 * @param typeCondition the type condition to apply to the property type
	 */
	public PropertyTypeCondition(TypeCondition typeCondition) {
		super();
		this.typeCondition = typeCondition;
	}

	/**
	 * @see EntityCondition#accept(Entity)
	 */
	@Override
	public boolean accept(Property entity) {
		TypeDefinition propertyType = entity.getDefinition().getDefinition().getPropertyType();
		Type type = new DefaultType(new TypeEntityDefinition(propertyType, entity.getDefinition()
				.getSchemaSpace(), null));
		return typeCondition.accept(type);
	}

}
