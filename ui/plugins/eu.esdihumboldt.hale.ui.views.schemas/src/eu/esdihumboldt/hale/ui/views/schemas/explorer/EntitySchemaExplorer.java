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

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.util.viewer.FilterAction;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;
import eu.esdihumboldt.hale.ui.views.schemas.internal.SchemasViewPlugin;

/**
 * Schema explorer with {@link EntityDefinition}s instead of {@link Definition}s
 * as elements.
 * 
 * @author Simon Templer
 */
public class EntitySchemaExplorer extends SchemaExplorer {

	/**
	 * Create an {@link EntityDefinition} based schema explorer
	 * 
	 * @param parent the parent composite
	 * @param title the title
	 * @param schemaSpace the associated schema space
	 */
	public EntitySchemaExplorer(Composite parent, String title, SchemaSpaceID schemaSpace) {
		super(parent, title, schemaSpace);
	}

	/**
	 * @see SchemaExplorer#createContentProvider(TreeViewer)
	 */
	@Override
	protected IContentProvider createContentProvider(TreeViewer tree) {
		EntityDefinitionService service = (EntityDefinitionService) PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);
		return new TreePathProviderAdapter(new EntityTypeIndexContentProvider(tree, service,
				getSchemaSpace()));
	}

	/**
	 * @see SchemaExplorer#prependToolbarActions(ToolBarManager)
	 */
	@Override
	protected void prependToolbarActions(ToolBarManager manager) {
		ViewerFilter filter = new UnpopulatedPropertiesFilter();
		manager.add(new FilterAction("Hide unpopulated properties", "Show unpopulated properties",
				SchemasViewPlugin.getImageDescriptor("icons/empty.gif"), getTreeViewer(), filter,
				true, true));

		manager.add(new Separator());
	}

}
