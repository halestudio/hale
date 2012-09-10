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

package eu.esdihumboldt.hale.ui.function.generic.pages.internal;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameter;
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
public class PropertyField extends Field<PropertyParameter, PropertyEntitySelector> {

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
	public PropertyField(PropertyParameter definition, SchemaSpaceID ssid, Composite parent,
			Set<EntityDefinition> candidates, Cell initialCell, TypeEntityDefinition parentType) {
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
	 * @see Field#createEntitySelector(SchemaSpaceID, AbstractParameter,
	 *      Composite)
	 */
	@Override
	protected PropertyEntitySelector createEntitySelector(SchemaSpaceID ssid,
			PropertyParameter field, Composite parent) {
		return new PropertyEntitySelector(ssid, field, parent, parentType);
	}

	/**
	 * @return the parentType
	 */
	public TypeDefinition getParentType() {
		return parentType.getType();
	}

}
