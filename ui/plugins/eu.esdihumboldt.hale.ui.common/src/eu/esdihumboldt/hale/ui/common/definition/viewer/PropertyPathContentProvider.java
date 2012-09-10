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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Content provider that shows properties of an input type definition or for
 * types contained in a type index and provides the properties as
 * {@link EntityDefinition} with the full property path and default contexts.
 * 
 * @author Simon Templer
 */
public class PropertyPathContentProvider implements ITreeContentProvider {

	private final SchemaSpaceID schemaSpace;

	/**
	 * Create property path content provider.
	 * 
	 * @param schemaSpace the schema space
	 */
	public PropertyPathContentProvider(SchemaSpaceID schemaSpace) {
		super();
		this.schemaSpace = schemaSpace;
	}

	/**
	 * @see TypeIndexContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TypeDefinition) {
			return getChildren(inputElement);
		}
		else if (inputElement instanceof TypeIndex) {
			return ((TypeIndex) inputElement).getMappingRelevantTypes().toArray();
		}
		else {
			throw new IllegalArgumentException(
					"Content provider only applicable for type definitions.");
		}
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TypeDefinition) {
			return getEntityChildren(
					new TypeEntityDefinition((TypeDefinition) parentElement, schemaSpace, null))
					.toArray();
		}
		if (parentElement instanceof EntityDefinition) {
			return getEntityChildren((EntityDefinition) parentElement).toArray();
		}
		else {
			throw new IllegalArgumentException(
					"Given element not supported in schema tree structure.");
		}
	}

	private Collection<EntityDefinition> getEntityChildren(EntityDefinition entity) {
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
				children = parentContext.getChild().asProperty().getPropertyType().getChildren();
			}
			else {
				throw new IllegalStateException("Illegal child definition type encountered");
			}
		}

		if (children == null || children.isEmpty()) {
			return Collections.emptyList();
		}

		Collection<EntityDefinition> result = new ArrayList<EntityDefinition>(children.size());
		for (ChildDefinition<?> child : children) {
			// add default child entity definition to result
			ChildContext context = new ChildContext(child);
			EntityDefinition defaultEntity = AlignmentUtil.createEntity(entity.getType(),
					createPath(entity.getPropertyPath(), context), entity.getSchemaSpace(),
					entity.getFilter());
			result.add(defaultEntity);
		}

		return result;
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
		if (parentElement instanceof TypeDefinition) {
			return !((TypeDefinition) parentElement).getChildren().isEmpty();
		}
		if (parentElement instanceof EntityDefinition) {
			Collection<? extends EntityDefinition> children = getEntityChildren((EntityDefinition) parentElement);
			return !children.isEmpty();
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

}
