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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Content provider that shows properties of an input type entity definition. In
 * the case of an input {@link TypeIndex} all mappable types and their
 * properties will be shown.
 * 
 * @author Simon Templer
 */
public class EntityTypePropertyContentProvider extends EntityTypeIndexContentProvider {

	/**
	 * @see EntityTypeIndexContentProvider#EntityTypeIndexContentProvider(TreeViewer,
	 *      EntityDefinitionService, SchemaSpaceID)
	 */
	public EntityTypePropertyContentProvider(TreeViewer tree,
			EntityDefinitionService entityDefinitionService, SchemaSpaceID schemaSpace) {
		super(tree, entityDefinitionService, schemaSpace);
	}

	/**
	 * @see TypeIndexContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TypeEntityDefinition) {
			return getChildren(inputElement);
		}
		else if (inputElement instanceof TypeIndex) {
			List<TypeEntityDefinition> types = new ArrayList<TypeEntityDefinition>();
			for (TypeDefinition type : ((TypeIndex) inputElement).getTypes()) {
				if (type.getConstraint(MappableFlag.class).isEnabled())
					types.addAll(entityDefinitionService.getTypeEntities(type, schemaSpace));
			}
			return types.toArray();
		}
		else
			throw new IllegalArgumentException(
					"Content provider only applicable for type indexes or a type definition.");
	}

}
