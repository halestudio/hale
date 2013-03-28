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
		return new TypeEntitySelector(ssid, field, parent, true);
	}

}
