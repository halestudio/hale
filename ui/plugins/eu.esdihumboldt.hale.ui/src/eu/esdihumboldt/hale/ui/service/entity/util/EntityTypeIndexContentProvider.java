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
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Tree content provider using a {@link TypeIndex} as root
 * 
 * @author Simon Templer
 */
public class EntityTypeIndexContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY = new Object[] {};

	/**
	 * Whether to only show mapping relevant types, or all mappable types.
	 * 
	 * @see MappingRelevantFlag
	 * @see MappableFlag
	 */
	private final boolean onlyMappingRelevant;

	/**
	 * Whether to only show types, or also their properties.
	 */
	private final boolean onlyTypes;

	/**
	 * The entity definition service instance
	 */
	protected final EntityDefinitionService entityDefinitionService;

	/**
	 * The identifier of the schema space associated to the entities
	 */
	protected final SchemaSpaceID schemaSpace;

	/**
	 * Create a content provider based on a {@link TypeIndex} as input. It will
	 * only show mapping relevant types and their properties.
	 * 
	 * @param entityDefinitionService the entity definition service
	 * @param schemaSpace the associated schema space
	 */
	public EntityTypeIndexContentProvider(EntityDefinitionService entityDefinitionService,
			SchemaSpaceID schemaSpace) {
		this(entityDefinitionService, schemaSpace, true, false);
	}

	/**
	 * Create a content provider based on a {@link TypeIndex} as input. It will
	 * show the given choice of types and their properties.
	 * 
	 * @param entityDefinitionService the entity definition service
	 * @param schemaSpace the associated schema space
	 * @param onlyMappingRelevant whether to only show mapping relevant types
	 */
	public EntityTypeIndexContentProvider(EntityDefinitionService entityDefinitionService,
			SchemaSpaceID schemaSpace, boolean onlyMappingRelevant) {
		this(entityDefinitionService, schemaSpace, onlyMappingRelevant, false);
	}

	/**
	 * Create a content provider based on a {@link TypeIndex} as input.
	 * 
	 * @param entityDefinitionService the entity definition service
	 * @param schemaSpace the associated schema space
	 * @param onlyMappingRelevant whether to only show mapping relevant types
	 * @param onlyTypes whether to only show types, or also their properties
	 */
	public EntityTypeIndexContentProvider(EntityDefinitionService entityDefinitionService,
			SchemaSpaceID schemaSpace, boolean onlyMappingRelevant, boolean onlyTypes) {
		this.entityDefinitionService = entityDefinitionService;
		this.schemaSpace = schemaSpace;
		this.onlyMappingRelevant = onlyMappingRelevant;
		this.onlyTypes = onlyTypes;
	}

	/**
	 * @see ITreeContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TypeIndex) {
			List<TypeEntityDefinition> types = new ArrayList<TypeEntityDefinition>();
			if (onlyMappingRelevant) {
				for (TypeDefinition type : ((TypeIndex) inputElement).getMappingRelevantTypes())
					types.addAll(entityDefinitionService.getTypeEntities(type, schemaSpace));
			}
			else {
				for (TypeDefinition type : ((TypeIndex) inputElement).getTypes())
					if (type.getConstraint(MappableFlag.class).isEnabled())
						types.addAll(entityDefinitionService.getTypeEntities(type, schemaSpace));
			}
			return types.toArray();
		}
		else {
			throw new IllegalArgumentException("Content provider only applicable for type indexes.");
		}
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof EntityDefinition) {
			if (onlyTypes)
				return EMPTY;
			else {
				Collection<? extends EntityDefinition> children = entityDefinitionService
						.getChildren((EntityDefinition) parentElement);
				return children.toArray();
			}
		}
		else {
			throw new IllegalArgumentException(
					"Given element not supported in schema tree structure.");
		}
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof EntityDefinition) {
			if (onlyTypes)
				return false;
			else {
				Collection<? extends EntityDefinition> children = entityDefinitionService
						.getChildren((EntityDefinition) parentElement);
				return !children.isEmpty();
			}
		}
		else {
			throw new IllegalArgumentException(
					"Given element not supported in schema tree structure.");
		}
	}

	/**
	 * @see IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	/**
	 * @see ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof EntityDefinition) {
			return entityDefinitionService.getParent((EntityDefinition) element);
		}
		return null;
	}

}
