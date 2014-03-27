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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIterableContentProvider;
import eu.esdihumboldt.hale.ui.service.population.UnpopulatedPropertiesFilter;
import eu.esdihumboldt.hale.ui.util.viewer.FilterAction;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;
import eu.esdihumboldt.hale.ui.views.schemas.SchemasViewTypes;
import eu.esdihumboldt.hale.ui.views.schemas.internal.SchemasViewPlugin;

/**
 * This is a Schema Explorer to hold one type. It reacts on the
 * {@link SchemasViewTypes} navigation selection.
 * 
 * @author Yasmina Kammeyer
 */
public class SingleTypeSchemaExplorer extends SchemaExplorer {

	private ITreePathContentProvider contentProvider;

	private ISelectionListener selectionListener;

	/**
	 * @see SchemaExplorer#SchemaExplorer(Composite, String, SchemaSpaceID)
	 */
	public SingleTypeSchemaExplorer(Composite parent, String title, SchemaSpaceID schemaSpace) {
		super(parent, title, schemaSpace);

		// listen on SchemaSelections
		selectionListener = (ISelectionListener) PlatformUI.getWorkbench().getService(
				SchemaSelection.class);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
				.addPostSelectionListener(selectionListener = new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {

						if (part.getSite().getId()
								.equals("eu.esdihumboldt.hale.ui.views.mapping.alignmenttypes")
								&& selection instanceof IStructuredSelection
								&& ((IStructuredSelection) selection).getFirstElement() instanceof Cell) {
							Cell dt = (Cell) ((IStructuredSelection) selection).getFirstElement();

							updateSchemaExplorerWithCell(dt);
						}
						else {
							return;
						}

					}
				});

	}

	/**
	 * Use a DefaultType parameter to set the input of the viewer
	 * 
	 * @param firstElement the DefaultType
	 */
	protected void updateSchemaExplorer(DefaultType firstElement) {
		// Update Source Explorer
		if (super.getSchemaSpace().equals(SchemaSpaceID.SOURCE)
				&& firstElement.getDefinition().getSchemaSpace().equals(SchemaSpaceID.SOURCE)) {
			Object[] element = { firstElement.getDefinition() };
			getTreeViewer().setInput(element);
		}
		// Update Target Explorer
		else if (super.getSchemaSpace().equals(SchemaSpaceID.TARGET)
				&& firstElement.getDefinition().getSchemaSpace().equals(SchemaSpaceID.TARGET)) {
			Object[] element = { firstElement.getDefinition() };
			getTreeViewer().setInput(element);
		}
		// getTreeViewer().setInput(element);
	}

	/**
	 * Use a Cell to set the input of the viewer
	 * 
	 * @param cell The {@link Cell}
	 */
	private void updateSchemaExplorerWithCell(Cell cell) {
		Collection<? extends Entity> entities;
		// Source
		if (super.getSchemaSpace().equals(SchemaSpaceID.SOURCE)) {
			entities = cell.getSource().values();
		}
		// Target
		else if (super.getSchemaSpace().equals(SchemaSpaceID.TARGET)) {
			entities = cell.getTarget().values();
		}
		else {
			return;
		}
		Collection<TypeEntityDefinition> types = new ArrayList<>();
		// Add selection to collection if it is a TypeEntityDefinition
		for (Entity entity : entities) {
			if (entity.getDefinition() instanceof TypeEntityDefinition)
				types.add((TypeEntityDefinition) entity.getDefinition());
		}
		// set the input (content of the tree viewer)
		getTreeViewer().setInput(types);
		// expand to first level
		getTreeViewer().expandToLevel(2);
	}

	/**
	 * @param selection
	 */
	private void updateFocus(SchemaSelection selection) {
		if (!selection.isEmpty()) {
			if (super.getSchemaSpace().equals(SchemaSpaceID.SOURCE)) {

				getTreeViewer().setInput(selection.getSourceItems());
			}
			if (super.getSchemaSpace().equals(SchemaSpaceID.TARGET)) {
				getTreeViewer().setInput(selection.getTargetItems());
			}
		}
		else {
			getTreeViewer().setInput(null);
		}
	}

	/**
	 * @see SchemaExplorer#createContentProvider(TreeViewer)
	 */
	@Override
	protected IContentProvider createContentProvider(TreeViewer tree) {
		EntityDefinitionService service = (EntityDefinitionService) PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);

		contentProvider = new TreePathProviderAdapter(new EntityTypeIterableContentProvider(
				service, getSchemaSpace()));

		return contentProvider;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.schemas.explorer.SchemaExplorer#setSchema(eu.esdihumboldt.hale.common.schema.model.TypeIndex)
	 */
	@Override
	public void setSchema(TypeIndex schema) {
//		SchemaSelection current = SchemaSelectionHelper.getSchemaSelection();
//		if (!current.isEmpty()) {
//			if (super.getSchemaSpace().equals(SchemaSpaceID.SOURCE)) {
//				getTreeViewer().setInput(current.getSourceItems());
//			}
//			if (super.getSchemaSpace().equals(SchemaSpaceID.TARGET)) {
//				getTreeViewer().setInput(current.getTargetItems());
//			}
//		}
//		else {
		getTreeViewer().setInput(null);
//		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.schemas.explorer.SchemaExplorer#prependToolbarActions(org.eclipse.jface.action.ToolBarManager)
	 */
	@Override
	protected void prependToolbarActions(ToolBarManager manager) {

		// XXX Add Action class?
		Action collapseTree = new Action("Collapse", Action.AS_PUSH_BUTTON) {

			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				getTreeViewer().collapseAll();
			}
		};

		collapseTree.setToolTipText("Click to collapse the tree");
		collapseTree.setImageDescriptor(SchemasViewPlugin.getImageDescriptor("icons/schema.gif"));

		manager.add(collapseTree);
		manager.add(new Separator());

		ViewerFilter unpopulated = new UnpopulatedPropertiesFilter();
		manager.add(new FilterAction("Hide unpopulated properties", "Show unpopulated properties",
				SchemasViewPlugin.getImageDescriptor("icons/empty.gif"), getTreeViewer(),
				unpopulated, true, true));

		manager.add(new Separator());
	}

	/**
	 * Dispose the action, removing any service listeners.
	 */
	@Override
	public void dispose() {
		if (selectionListener != null)
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
					.removePostSelectionListener(selectionListener);
		super.dispose();
	}
}
