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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Tree content provider using a {@link TypeIndex} as root, or an
 * {@link Iterable} of {@link TypeDefinition}s. The elements represented are
 * {@link EntityDefinition}s as defined in the schema (i.e. default contexts)
 * 
 * @author Simon Templer
 */
public class SchemaEntityTypeIndexContentProvider implements ITreeContentProvider {

	private final TreeViewer tree;
	private final SchemaSpaceID schemaSpace;

	/**
	 * Create a content provider based on a {@link TypeIndex} as input.
	 * 
	 * @param tree the associated tree viewer
	 * @param schemaSpace the schema space the types belong to
	 */
	public SchemaEntityTypeIndexContentProvider(TreeViewer tree, SchemaSpaceID schemaSpace) {
		super();

		this.tree = tree;
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
		Collection<TypeEntityDefinition> result = new ArrayList<TypeEntityDefinition>();
		if (inputElement instanceof TypeIndex) {
			for (TypeDefinition type : ((TypeIndex) inputElement).getMappingRelevantTypes()) {
				result.add(new TypeEntityDefinition(type, schemaSpace, null));
			}
		}
		else if (inputElement instanceof Iterable<?>) {
			for (Object element : ((Iterable<?>) inputElement)) {
				if (element instanceof TypeEntityDefinition) {
					result.add((TypeEntityDefinition) element);
				}
				else if (element instanceof TypeDefinition) {
					result.add(new TypeEntityDefinition((TypeDefinition) element, schemaSpace, null));
				}
			}
		}

		return result.toArray();
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof EntityDefinition) {
			EntityDefinition entity = (EntityDefinition) parentElement;
			List<ChildContext> path = entity.getPropertyPath();

			Collection<? extends ChildDefinition<?>> children;

			if (path == null || path.isEmpty()) {
				// entity is a type, children are the type children
				children = entity.getType().getChildren();
			}
			else {
				// get parent context
				ChildContext parentContext = path.get(path.size() - 1);
				if (parentContext.getChild().asGroup() != null) {
					children = parentContext.getChild().asGroup().getDeclaredChildren();
				}
				else if (parentContext.getChild().asProperty() != null) {
					children = parentContext.getChild().asProperty().getPropertyType()
							.getChildren();
				}
				else {
					throw new IllegalStateException("Illegal child definition type encountered");
				}
			}

			if (children != null && !children.isEmpty()) {
				Collection<EntityDefinition> result = new ArrayList<EntityDefinition>(
						children.size());
				for (ChildDefinition<?> child : children) {
					// add default child entity definition to result
					ChildContext context = new ChildContext(child);
					EntityDefinition defaultEntity = AlignmentUtil.createEntity(entity.getType(),
							createPath(entity.getPropertyPath(), context), entity.getSchemaSpace(),
							entity.getFilter());
					result.add(defaultEntity);
				}

				return result.toArray();
			}
		}

		return new Object[] {};
	}

	/**
	 * Create a property path
	 * 
	 * @param parentPath the parent path
	 * @param context the child context
	 * @return the property path including the child context
	 */
	private static List<ChildContext> createPath(List<ChildContext> parentPath, ChildContext context) {
		if (parentPath == null || parentPath.isEmpty()) {
			return Collections.singletonList(context);
		}
		else {
			List<ChildContext> result = new ArrayList<ChildContext>(parentPath);
			result.add(context);
			return result;
		}
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof EntityDefinition) {
			parentElement = ((EntityDefinition) parentElement).getDefinition();
		}

		if (parentElement instanceof TypeDefinition) {
			return !((TypeDefinition) parentElement).getChildren().isEmpty();
		}
		else if (parentElement instanceof GroupPropertyDefinition) {
			return !((GroupPropertyDefinition) parentElement).getDeclaredChildren().isEmpty();
		}
		else if (parentElement instanceof PropertyDefinition) {
			return !((PropertyDefinition) parentElement).getPropertyType().getChildren().isEmpty();
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
			return AlignmentUtil.getParent((EntityDefinition) element);
		}

		return null;
	}

	/**
	 * Get the schema space associated to the types.
	 * 
	 * @return the schema space
	 */
	public SchemaSpaceID getSchemaSpace() {
		return schemaSpace;
	}

}
