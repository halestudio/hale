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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.align.model.Cell;
import eu.esdihumboldt.hale.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Represents named type entities in a function
 * @author Simon Templer
 */
public class TypeField extends Field<TypeEntitySelector> {

	/**
	 * @see Field#Field(AbstractParameter, SchemaSpaceID, Composite, Set, Cell)
	 */
	public TypeField(AbstractParameter definition, SchemaSpaceID ssid,
			Composite parent, Set<EntityDefinition> candidates, Cell initialCell) {
		super(definition, ssid, parent, candidates, initialCell);
	}

	/**
	 * @see Field#createEntitySelector(SchemaSpaceID, Set, AbstractParameter, Composite)
	 */
	@Override
	protected TypeEntitySelector createEntitySelector(SchemaSpaceID ssid,
			Set<EntityDefinition> candidates, AbstractParameter field,
			Composite parent) {
		return new TypeEntitySelector(ssid, candidates, field, parent);
	}

}
