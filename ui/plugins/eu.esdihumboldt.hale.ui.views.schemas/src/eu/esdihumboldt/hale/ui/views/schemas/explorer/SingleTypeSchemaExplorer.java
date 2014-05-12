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
import java.util.Collections;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.definition.viewer.InheritedPropertiesFilter;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SelectedTypeFilter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusAdapter;
import eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.ContentProviderAction;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIndexHierarchy;
import eu.esdihumboldt.hale.ui.service.population.UnpopulatedPropertiesFilter;
import eu.esdihumboldt.hale.ui.util.viewer.FilterAction;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;
import eu.esdihumboldt.hale.ui.views.schemas.internal.SchemasViewPlugin;

/**
 * This is a Schema Explorer to hold one relation. It reacts on the navigation
 * selection.
 * 
 * @author Yasmina Kammeyer
 */
public class SingleTypeSchemaExplorer extends SchemaExplorer {

	private final TypeCellFocusAdapter selectionListener;

	private AlignmentServiceAdapter alignmentListener;

	private SelectedTypeFilter selected;

	private TreePathProviderAdapter hierarchyProvider;

	private TreePathProviderAdapter listProvider;

	/**
	 * @see SchemaExplorer#SchemaExplorer(Composite, String, SchemaSpaceID)
	 */
	public SingleTypeSchemaExplorer(Composite parent, String title, SchemaSpaceID schemaSpace) {
		super(parent, title, schemaSpace);

		TypeCellFocusService tc = (TypeCellFocusService) PlatformUI.getWorkbench().getService(
				TypeCellFocusService.class);
		tc.addListener(selectionListener = new TypeCellFocusAdapter() {

			@Override
			public void dataChanged(Cell cell) {
				updateFilter(cell);
				getTreeViewer().refresh();
			}
		});

		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);

//		as.addListener(alignmentListener = new AlignmentServiceAdapter() {
//
//			@Override
//			public void alignmentCleared() {
//				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
//
//					@Override
//					public void run() {
//						getTreeViewer().setInput(null);
//					}
//				});
//			}
//
//			@Override
//			public void cellsRemoved(Iterable<Cell> cells) {
//				// check for removed type cells
//				getTreeViewer().setInput(null);
//			}
//
//			@Override
//			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
//				getTreeViewer().refresh();
//			}
//
//			@Override
//			public void cellsAdded(Iterable<Cell> cells) {
//				// do nothing
//			}
//
//			@Override
//			public void alignmentChanged() {
//				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
//
//					@Override
//					public void run() {
//						getTreeViewer().setInput(null);
//					}
//				});
//			}
//
//			@Override
//			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
//				getTreeViewer().refresh();
//			}
//
//		});

	}

//	/**
//	 * Use a DefaultType parameter to set the input of the viewer
//	 * 
//	 * @param firstElement the DefaultType
//	 */
//	protected void updateSchemaExplorer(DefaultType firstElement) {
//		// Update Source Explorer
//		if (super.getSchemaSpace().equals(SchemaSpaceID.SOURCE)
//				&& firstElement.getDefinition().getSchemaSpace().equals(SchemaSpaceID.SOURCE)) {
//			Object[] element = { firstElement.getDefinition() };
//			getTreeViewer().setInput(element);
//		}
//		// Update Target Explorer
//		else if (super.getSchemaSpace().equals(SchemaSpaceID.TARGET)
//				&& firstElement.getDefinition().getSchemaSpace().equals(SchemaSpaceID.TARGET)) {
//			Object[] element = { firstElement.getDefinition() };
//			getTreeViewer().setInput(element);
//		}
//		// getTreeViewer().setInput(element);
//	}

//	/**
//	 * Use a Cell to set the input of the viewer
//	 * 
//	 * @param cell The {@link Cell}
//	 */
//	private void updateSchemaExplorerWithCell(Cell cell) {
//		Collection<? extends Entity> entities;
//		// Source
//		if (super.getSchemaSpace().equals(SchemaSpaceID.SOURCE)) {
//			entities = cell.getSource().values();
//		}
//		// Target
//		else if (super.getSchemaSpace().equals(SchemaSpaceID.TARGET)) {
//			entities = cell.getTarget().values();
//		}
//		else {
//			return;
//		}
//		Collection<TypeEntityDefinition> types = new ArrayList<>();
//		// Add selection to collection if it is a TypeEntityDefinition
//		for (Entity entity : entities) {
//			if (entity.getDefinition() instanceof TypeEntityDefinition) {
//				types.add((TypeEntityDefinition) entity.getDefinition());
//			}
//		}
//	}

	/**
	 * @see SchemaExplorer#createContentProvider(TreeViewer)
	 */
	@Override
	protected IContentProvider createContentProvider(TreeViewer tree) {

		EntityDefinitionService service = (EntityDefinitionService) PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);

		hierarchyProvider = new TreePathProviderAdapter(new EntityTypeIndexHierarchy(service,
				getSchemaSpace()));
		listProvider = new TreePathProviderAdapter(new EntityTypeIndexContentProvider(service,
				getSchemaSpace()));

		return listProvider;
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

		manager.add(new ContentProviderAction("Types as list", SchemasViewPlugin
				.getImageDescriptor("icons/flat_hierarchy.png"), getTreeViewer(), listProvider,
				true));

		manager.add(new ContentProviderAction("Type hierarchy", SchemasViewPlugin
				.getImageDescriptor("icons/inheritance_hierarchy.png"), getTreeViewer(),
				hierarchyProvider, false));

		manager.add(new Separator());

		ViewerFilter unpopulated = new UnpopulatedPropertiesFilter();
		manager.add(new FilterAction("Hide unpopulated properties", "Show unpopulated properties",
				SchemasViewPlugin.getImageDescriptor("icons/empty.gif"), getTreeViewer(),
				unpopulated, true, true));

		ViewerFilter inherited = new InheritedPropertiesFilter();
		manager.add(new FilterAction("Hide inherited properties", "Show inherited properties",
				SchemasViewPlugin.getImageDescriptor("icons/inherited.gif"), getTreeViewer(),
				inherited, true, true));

		manager.add(new Separator());

		selected = new SelectedTypeFilter();
		manager.add(new FilterAction("Focus on selected Type", "Show all", SchemasViewPlugin
				.getImageDescriptor("icons/aggregation_hierarchy.png"), getTreeViewer(), selected,
				true, false));

		manager.add(new Separator());

	}

	/**
	 * Set the (selected Cell) as the new Input of the
	 * {@link SelectedTypeFilter}
	 * 
	 * @param cell The celected Cell
	 */
	private void updateFilter(Cell cell) {
		// If no Filter exist
		if (selected == null)
			return;
		// If the cell is no type cell return
		if (cell != null && !AlignmentUtil.isTypeCell(cell))
			return;
		// No selected Cell - set Filter types empty
		if (cell == null) {
			// Set the new Filter Types
			selected.setSelectedTypes(Collections.<TypeEntityDefinition> emptyList());
			return;
		}

		// Get the Entities from the Cell
		Collection<? extends Entity> entities;

		switch (getSchemaSpace()) {
		case SOURCE:
			entities = cell.getSource().values();
			break;
		case TARGET:
			entities = cell.getTarget().values();
			break;
		default:
			return;
		}

		Collection<TypeEntityDefinition> types = new ArrayList<>();
		// Add selection to collection if it is a TypeEntityDefinition
		for (Entity entity : entities) {
			if (entity.getDefinition() instanceof TypeEntityDefinition) {
				types.add((TypeEntityDefinition) entity.getDefinition());
			}
		}
		// Set the new Filter Types
		selected.setSelectedTypes(types);
		// update the Viewer
		// getTreeViewer().removeFilter(selected);
		// getTreeViewer().addFilter(selected);
		getTreeViewer().refresh();
	}

	/**
	 * Dispose the action, removing any service listeners.
	 */
	@Override
	public void dispose() {

		if (alignmentListener != null) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
			as.removeListener(alignmentListener);
		}

		if (selectionListener != null) {
			TypeCellFocusService tc = (TypeCellFocusService) PlatformUI.getWorkbench().getService(
					TypeCellFocusService.class);
			tc.removeListener(selectionListener);
		}

		super.dispose();
	}
}
