/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.ContentProviderAction;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIndexHierarchy;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;
import eu.esdihumboldt.hale.ui.views.schemas.internal.SchemasViewPlugin;

/**
 * Content Provider to provide types only.
 * 
 * @author Yasmina Kammeyer
 */
public class TypesSchemaExplorer extends SchemaExplorer {

	private ITreePathContentProvider listProvider;

	private ITreePathContentProvider hierarchyProvider;

	/**
	 * Create an {@link TypeEntityDefinition} based schema explorer
	 * 
	 * @param parent the parent composite
	 * @param title the title
	 * @param schemaSpace the associated schema space
	 */
	public TypesSchemaExplorer(Composite parent, String title, SchemaSpaceID schemaSpace) {
		super(parent, title, schemaSpace);
	}

	/**
	 * @see SchemaExplorer#createContentProvider(TreeViewer)
	 */
	@Override
	protected IContentProvider createContentProvider(TreeViewer tree) {
		EntityDefinitionService service = (EntityDefinitionService) PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);

		hierarchyProvider = new TreePathProviderAdapter(new EntityTypeIndexHierarchy(service,
				getSchemaSpace(), true, true));
		listProvider = new TreePathProviderAdapter(new EntityTypeIndexContentProvider(service,
				getSchemaSpace(), true, true));

		return listProvider;
	}

	/**
	 * @see SchemaExplorer#prependToolbarActions(ToolBarManager)
	 */
	@Override
	protected void prependToolbarActions(ToolBarManager manager) {
		manager.add(new ContentProviderAction("Types as list", SchemasViewPlugin
				.getImageDescriptor("icons/flat_hierarchy.png"), getTreeViewer(), listProvider,
				true));

		manager.add(new ContentProviderAction("Type hierarchy", SchemasViewPlugin
				.getImageDescriptor("icons/inheritance_hierarchy.png"), getTreeViewer(),
				hierarchyProvider, false));

	}

}
