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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Tree content provider using a {@link TypeIndex} as root and organizing types
 * by inheritance.
 * 
 * @author Simon Templer
 */
public class EntityTypeIndexHierarchy implements ITreeContentProvider {

	private final TreeViewer tree;

	/**
	 * The entity definition service instance
	 */
	protected final EntityDefinitionService entityDefinitionService;

	/**
	 * The identifier of the schema space associated to the entities
	 */
	protected final SchemaSpaceID schemaSpace;

	/**
	 * The collected valid types to be visible.
	 */
	private Set<TypeDefinition> validTypes;

	/**
	 * Create a content provider based on a {@link TypeIndex} as input.
	 * 
	 * @param tree the associated tree viewer
	 * @param entityDefinitionService the entity definition service
	 * @param schemaSpace the associated schema space
	 */
	public EntityTypeIndexHierarchy(TreeViewer tree,
			EntityDefinitionService entityDefinitionService, SchemaSpaceID schemaSpace) {
		super();

		this.tree = tree;
		this.entityDefinitionService = entityDefinitionService;
		this.schemaSpace = schemaSpace;
	}

	/**
	 * Get the associated tree viewer
	 * 
	 * @return the associated tree viewer
	 */
	protected TreeViewer getTree() {
		return tree;
	}

	/**
	 * @see ITreeContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TypeIndex) {
			validTypes = new HashSet<TypeDefinition>();
			List<TypeEntityDefinition> roots = new ArrayList<TypeEntityDefinition>();

			Queue<TypeDefinition> types = new LinkedList<TypeDefinition>();
			types.addAll(((TypeIndex) inputElement).getMappingRelevantTypes());

			// collect types and super types in valid types set
			while (!types.isEmpty()) {
				TypeDefinition type = types.poll();
				if (!validTypes.contains(type)) {
					validTypes.add(type);
					TypeDefinition superType = type.getSuperType();
					if (superType != null && !validTypes.contains(superType)) {
						types.add(superType);
					}
					if (superType == null) {
						// add default type as root
						roots.add(new TypeEntityDefinition(type, schemaSpace, null));
					}
				}
			}

//			for (TypeDefinition type : ) {
//				types.addAll(entityDefinitionService.getTypeEntities(type, schemaSpace));
//			}
			return roots.toArray();
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
			EntityDefinition parent = (EntityDefinition) parentElement;
			List<EntityDefinition> children = new ArrayList<EntityDefinition>();

			// add valid sub types and alternative type entities
			if (parent.getPropertyPath().isEmpty() && parent.getFilter() == null) {
				// represents a type w/o filter

				// add valid sub types w/o filter
				for (TypeDefinition subtype : parent.getType().getSubTypes()) {
					if (validTypes.contains(subtype)) {
						children.add(new TypeEntityDefinition(subtype, parent.getSchemaSpace(),
								null));
					}
				}

				// add type contexts
				for (TypeEntityDefinition typeEntity : entityDefinitionService.getTypeEntities(
						parent.getType(), parent.getSchemaSpace())) {
					if (typeEntity.getFilter() != null) {
						// only use type entities with filter defined
						children.add(typeEntity);
					}
				}
			}

			// add regular children
			children.addAll(entityDefinitionService.getChildren(parent));

			return children.toArray();
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
			// check regular children
			Collection<? extends EntityDefinition> children = entityDefinitionService
					.getChildren((EntityDefinition) parentElement);
			if (!children.isEmpty()) {
				return true;
			}

			// for sub types and alternative type entities check getChildren
			return getChildren(parentElement).length > 0;
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
			EntityDefinition def = (EntityDefinition) element;

			if (def.getPropertyPath().isEmpty()) {
				// represents a type

				if (def.getFilter() == null) {
					// parent is the super type
					if (def.getType().getSuperType() == null) {
						return null;
					}
					else {
						return new TypeEntityDefinition(def.getType().getSuperType(),
								def.getSchemaSpace(), null);
					}
				}
				else {
					// parent is the 'pure' type w/o filter
					return new TypeEntityDefinition(def.getType(), def.getSchemaSpace(), null);
				}
			}

			// return the property parent
			return entityDefinitionService.getParent((EntityDefinition) element);
		}
		return null;
	}

}
