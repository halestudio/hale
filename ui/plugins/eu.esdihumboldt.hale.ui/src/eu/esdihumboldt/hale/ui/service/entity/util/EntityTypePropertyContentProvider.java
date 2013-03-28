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

package eu.esdihumboldt.hale.ui.service.entity.util;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Content provider that shows properties of an input type entity definition.
 * 
 * @author Simon Templer
 */
public class EntityTypePropertyContentProvider extends EntityTypeIndexContentProvider {

	/**
	 * @see EntityTypeIndexContentProvider#EntityTypeIndexContentProvider(EntityDefinitionService,
	 *      SchemaSpaceID)
	 */
	public EntityTypePropertyContentProvider(EntityDefinitionService entityDefinitionService,
			SchemaSpaceID schemaSpace) {
		super(entityDefinitionService, schemaSpace);
	}

	/**
	 * @see TypeIndexContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TypeEntityDefinition) {
			return getChildren(inputElement);
		}
		else {
			throw new IllegalArgumentException(
					"Content provider only applicable for type definitions.");
		}
	}

}
