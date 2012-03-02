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

package eu.esdihumboldt.hale.ui.service.entity.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
 * Tree content provider using a {@link TypeIndex} as root
 * @author Simon Templer
 */
public class EntityTypeIndexContentProvider implements ITreeContentProvider {

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
	 * Create a content provider based on a {@link TypeIndex} as input.
	 * @param tree the associated tree viewer
	 * @param entityDefinitionService the entity definition service
	 * @param schemaSpace the associated schema space
	 */
	public EntityTypeIndexContentProvider(TreeViewer tree,
			EntityDefinitionService entityDefinitionService,
			SchemaSpaceID schemaSpace) {
		super();
		
		this.tree = tree;
		this.entityDefinitionService = entityDefinitionService;
		this.schemaSpace = schemaSpace;
	}
	
	/**
	 * Get the associated tree viewer
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
			List<TypeEntityDefinition> types = new ArrayList<TypeEntityDefinition>();
			for (TypeDefinition type : ((TypeIndex) inputElement).getMappingRelevantTypes()) {
				types.add(entityDefinitionService.getTypeEntities(type, schemaSpace));
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
			Collection<? extends EntityDefinition> children = 
					entityDefinitionService.getChildren((EntityDefinition) parentElement);
			return children.toArray();
		}
		else {
			throw new IllegalArgumentException("Given element not supported in schema tree structure.");
		}
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof EntityDefinition) {
			Collection<? extends EntityDefinition> children = 
					entityDefinitionService.getChildren((EntityDefinition) parentElement);
			return !children.isEmpty();
		}
		else {
			throw new IllegalArgumentException("Given element not supported in schema tree structure.");
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
