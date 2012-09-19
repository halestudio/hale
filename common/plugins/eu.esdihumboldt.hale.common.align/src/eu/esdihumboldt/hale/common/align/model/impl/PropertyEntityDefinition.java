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

package eu.esdihumboldt.hale.common.align.model.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Entity definition for a property
 * 
 * @author Simon Templer
 */
@Immutable
public class PropertyEntityDefinition extends ChildEntityDefinition {

	/**
	 * Create an entity definition specified by the given property path. The
	 * property path must contain the property definition as last element.
	 * 
	 * @param type the topmost parent of the property
	 * @param path the property path down from the type
	 * @param schemaSpace the schema space identifier
	 * @param filter the entity filter on the type, may be <code>null</code>
	 */
	public PropertyEntityDefinition(TypeDefinition type, List<ChildContext> path,
			SchemaSpaceID schemaSpace, Filter filter) {
		super(type, path, schemaSpace, filter);

		checkArgument(path != null && !path.isEmpty() && path.size() >= 1
				&& path.get(path.size() - 1).getChild() instanceof PropertyDefinition);
	}

	/**
	 * @see EntityDefinition#getDefinition()
	 */
	@Override
	public PropertyDefinition getDefinition() {
		return (PropertyDefinition) super.getDefinition();
	}

}
