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

import org.eclipse.jface.viewers.TreeViewer;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Tree content provider using a {@link TypeIndex} as root, only displaying type
 * entity definitions and not their children.
 * 
 * @author Simon Templer
 */
public class EntityTypesContentProvider extends EntityTypeIndexContentProvider {

	private static final Object[] EMPTY = new Object[] {};

	/**
	 * Create a content provider.
	 * 
	 * @param tree the tree viewer
	 * @param entityDefinitionService the entity definition service
	 * @param schemaSpace the schema space
	 */
	public EntityTypesContentProvider(TreeViewer tree,
			EntityDefinitionService entityDefinitionService, SchemaSpaceID schemaSpace) {
		super(tree, entityDefinitionService, schemaSpace);
	}

	/**
	 * @see TypeIndexContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		return EMPTY;
	}

	/**
	 * @see TypeIndexContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parentElement) {
		return false;
	}

}
