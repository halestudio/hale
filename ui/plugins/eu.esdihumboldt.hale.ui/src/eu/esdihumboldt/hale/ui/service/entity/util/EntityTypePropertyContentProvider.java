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
		else {
			throw new IllegalArgumentException(
					"Content provider only applicable for type definitions.");
		}
	}

}
