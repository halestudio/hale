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

package eu.esdihumboldt.hale.ui.function.generic.pages.internal;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.function.common.PropertyEntitySelector;

/**
 * Represents named property entities in a function
 * 
 * @author Simon Templer
 */
public class PropertyField extends Field<PropertyParameterDefinition, PropertyEntitySelector> {

	private TypeEntityDefinition parentType;

	/**
	 * Create a property field
	 * 
	 * @param definition the field definition
	 * @param ssid the schema space
	 * @param parent the parent composite
	 * @param candidates the entity candidates
	 * @param initialCell the initial cell
	 * @param parentType the parent type of the properties
	 */
	public PropertyField(PropertyParameterDefinition definition, SchemaSpaceID ssid,
			Composite parent, Set<EntityDefinition> candidates, Cell initialCell,
			TypeEntityDefinition parentType) {
		super(definition, ssid, parent, candidates, initialCell);

		// set the parent type on all added selectors
		setParentType(parentType);
	}

	/**
	 * Set the parent type
	 * 
	 * @param parentType the parentType to set
	 */
	public void setParentType(TypeEntityDefinition parentType) {
		this.parentType = parentType;

		// set the parent type on the selectors
		for (PropertyEntitySelector selector : getSelectors()) {
			selector.setParentType(parentType);
		}
	}

	/**
	 * @see Field#createEntitySelector(SchemaSpaceID, ParameterDefinition,
	 *      Composite)
	 */
	@Override
	protected PropertyEntitySelector createEntitySelector(SchemaSpaceID ssid,
			PropertyParameterDefinition field, Composite parent) {
		return new PropertyEntitySelector(ssid, field, parent, parentType);
	}

	/**
	 * @return the parentType
	 */
	public TypeDefinition getParentType() {
		return parentType.getType();
	}

}
