/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.model.condition;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Property condition wrapping a type condition and applying it to the
 * property type.
 * @author Simon Templer
 */
public class PropertyTypeCondition implements PropertyCondition {
	
	private final TypeCondition typeCondition;

	/**
	 * Create a property condition based on the given type condition
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
		Type type = new DefaultType(new TypeEntityDefinition(propertyType, 
				entity.getDefinition().getSchemaSpace(), null));
		return typeCondition.accept(type);
	}

}
