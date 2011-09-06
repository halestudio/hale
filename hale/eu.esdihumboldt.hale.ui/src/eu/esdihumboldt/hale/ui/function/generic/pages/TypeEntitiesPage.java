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
import eu.esdihumboldt.hale.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.align.model.Cell;
import eu.esdihumboldt.hale.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Entity page for types
 * @author Simon Templer
 */
public class TypeEntitiesPage extends EntitiesPage<TypeFunction, TypeField> {

	/**
	 * @see EntitiesPage#EntitiesPage(SchemaSelection, Cell)
	 */
	public TypeEntitiesPage(SchemaSelection initialSelection, Cell initialCell) {
		super(initialSelection, initialCell);
	}

	/**
	 * @see EntitiesPage#createField(AbstractParameter, SchemaSpaceID, Composite, Set, Cell)
	 */
	@Override
	protected TypeField createField(AbstractParameter field, SchemaSpaceID ssid,
			Composite parent, Set<EntityDefinition> candidates, Cell initialCell) {
		return new TypeField(field, ssid, parent, candidates, initialCell);
	}

}
