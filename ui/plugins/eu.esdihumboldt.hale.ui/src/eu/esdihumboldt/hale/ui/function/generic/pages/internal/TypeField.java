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
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.function.common.TypeEntitySelector;

/**
 * Represents named type entities in a function
 * 
 * @author Simon Templer
 */
public class TypeField extends Field<TypeParameter, TypeEntitySelector> {

	/**
	 * @see Field#Field(AbstractParameter, SchemaSpaceID, Composite, Set, Cell)
	 */
	public TypeField(TypeParameter definition, SchemaSpaceID ssid, Composite parent,
			Set<EntityDefinition> candidates, Cell initialCell) {
		super(definition, ssid, parent, candidates, initialCell);
	}

	/**
	 * @see Field#createEntitySelector(SchemaSpaceID, AbstractParameter,
	 *      Composite)
	 */
	@Override
	protected TypeEntitySelector createEntitySelector(SchemaSpaceID ssid, TypeParameter field,
			Composite parent) {
		return new TypeEntitySelector(ssid, field, parent);
	}

}
