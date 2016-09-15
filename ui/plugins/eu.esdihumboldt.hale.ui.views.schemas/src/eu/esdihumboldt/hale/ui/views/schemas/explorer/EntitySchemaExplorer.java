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

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.InheritedPropertiesFilter;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.ContentProviderAction;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIndexHierarchy;
import eu.esdihumboldt.hale.ui.service.population.UnpopulatedPropertiesFilter;
import eu.esdihumboldt.hale.ui.service.population.UnpopulatedTypesFilter;
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

	private ITreePathContentProvider listProvider;

	private ITreePathContentProvider hierarchyProvider;

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
		EntityDefinitionService service = PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);

		hierarchyProvider = new TreePathProviderAdapter(
				new EntityTypeIndexHierarchy(service, getSchemaSpace()));
		listProvider = new TreePathProviderAdapter(
				new EntityTypeIndexContentProvider(service, getSchemaSpace()));

		return listProvider;
	}

	/**
	 * @see SchemaExplorer#prependToolbarActions(ToolBarManager)
	 */
	@Override
	protected void prependToolbarActions(ToolBarManager manager) {
		// structure

		manager.add(new ContentProviderAction("Types as list",
				SchemasViewPlugin.getImageDescriptor("icons/flat_hierarchy.png"), getTreeViewer(),
				listProvider, true));

		manager.add(new ContentProviderAction("Type hierarchy",
				SchemasViewPlugin.getImageDescriptor("icons/inheritance_hierarchy.png"),
				getTreeViewer(), hierarchyProvider, false));

		manager.add(new Separator());

		// type filters

		ViewerFilter typeUnpopulated = new UnpopulatedTypesFilter();
		manager.add(new FilterAction("Hide unpopulated types", "Show unpopulated types",
				SchemasViewPlugin.getImageDescriptor("icons/type_empty.gif"), getTreeViewer(),
				typeUnpopulated, true, true));

		manager.add(new Separator());

		// properties filters

		ViewerFilter unpopulated = new UnpopulatedPropertiesFilter();
		manager.add(new FilterAction("Hide unpopulated properties", "Show unpopulated properties",
				SchemasViewPlugin.getImageDescriptor("icons/empty.gif"), getTreeViewer(),
				unpopulated, true, true));

		ViewerFilter inherited = new InheritedPropertiesFilter();
		manager.add(new FilterAction("Hide inherited properties", "Show inherited properties",
				SchemasViewPlugin.getImageDescriptor("icons/inherited.gif"), getTreeViewer(),
				inherited, true, true));

		manager.add(new Separator());
	}

}
