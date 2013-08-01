/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.entity.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * This content provider expects an array or Iterable. It will show all type
 * entities found in its argument.
 * 
 * @author Kai Schwierczek
 */
public class EntityTypeIterableContentProvider extends EntityTypeIndexContentProvider {

	/**
	 * Create a content provider based on an {@link Iterable} or array as input.
	 * It will show the given types and their properties.
	 * 
	 * @param entityDefinitionService the entity definition service
	 * @param schemaSpace the associated schema space
	 */
	public EntityTypeIterableContentProvider(EntityDefinitionService entityDefinitionService,
			SchemaSpaceID schemaSpace) {
		super(entityDefinitionService, schemaSpace, false);
	}

	/**
	 * Create a content provider based on an {@link Iterable} or array as input.
	 * It will show the given types.
	 * 
	 * @param entityDefinitionService the entity definition service
	 * @param schemaSpace the associated schema space
	 * @param onlyTypes whether to only show types, or also their properties
	 */
	public EntityTypeIterableContentProvider(EntityDefinitionService entityDefinitionService,
			SchemaSpaceID schemaSpace, boolean onlyTypes) {
		super(entityDefinitionService, schemaSpace, false, onlyTypes);
	}

	/**
	 * @see EntityTypeIndexContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Object[])
			inputElement = Arrays.asList((Object[]) inputElement);

		if (inputElement instanceof Iterable) {
			List<TypeEntityDefinition> defs = new ArrayList<>();
			for (Object elem : (Iterable<?>) inputElement) {
				if (elem instanceof TypeEntityDefinition)
					defs.add((TypeEntityDefinition) elem);
			}
			return defs.toArray();
		}
		else
			return super.getElements(inputElement);
	}
}
