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

import org.eclipse.jface.viewers.TreeViewer;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Tree content provider using a {@link TypeIndex} as root, only displaying
 * type entity definitions and not their children.
 * @author Simon Templer
 */
public class EntityTypesContentProvider extends EntityTypeIndexContentProvider {

	private static final Object[] EMPTY = new Object[]{};

	/**
	 * Create a content provider.
	 * @param tree the tree viewer
	 * @param entityDefinitionService the entity definition service
	 * @param schemaSpace the schema space
	 */
	public EntityTypesContentProvider(TreeViewer tree,
			EntityDefinitionService entityDefinitionService,
			SchemaSpaceID schemaSpace) {
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
