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

package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.zest.core.viewers.GraphViewer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.selection.SchemaSelectionHelper;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.views.mapping.internal.MappingViewPlugin;
import eu.esdihumboldt.util.Pair;

/**
 * Mapping view.
 * 
 * @author Simon Templer
 */
public class MappingView extends AbstractMappingView {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping";

	private ISelectionListener selectionListener;
	private final Action showCellsOnChildren;

	/**
	 * Default constructor.
	 */
	public MappingView() {
		final String deactive = "Show cells on children";
		final String active = "Hide cells on children";
		showCellsOnChildren = new Action("Show cells on children", Action.AS_CHECK_BOX) {

			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				String text = isChecked() ? active : deactive;
				setText(text);
				setToolTipText(text);

				SchemaSelection current = SchemaSelectionHelper.getSchemaSelection();
				if (current != null) {
					update(current);
				}
			}
		};
		showCellsOnChildren.setChecked(false);
		showCellsOnChildren.setToolTipText(active);
		showCellsOnChildren
				.setImageDescriptor(MappingViewPlugin.getImageDescriptor("icons/sub_co.gif"));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		super.createViewControl(parent);

		getSite().getWorkbenchWindow().getSelectionService()
				.addPostSelectionListener(selectionListener = new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {
						if (!(selection instanceof SchemaSelection)) {
							// only react on schema selections
							return;
						}

						if (part != MappingView.this) {
							update((SchemaSelection) selection);
						}
					}
				});

		SchemaSelection current = SchemaSelectionHelper.getSchemaSelection();
		if (current != null) {
			update(current);
		}
	}

	@Override
	protected IBaseLabelProvider createLabelProvider(GraphViewer viewer) {
		return new GraphLabelProvider(viewer, HaleUI.getServiceProvider()) {

			@Override
			protected boolean isInherited(Cell cell) {
				// cannot inherit type cells
				if (AlignmentUtil.isTypeCell(cell))
					return false;

				SchemaSelection selection = SchemaSelectionHelper.getSchemaSelection();

				if (selection != null && !selection.isEmpty()) {
					DefaultCell dummyTypeCell = new DefaultCell();
					ListMultimap<String, Type> sources = ArrayListMultimap.create();
					ListMultimap<String, Type> targets = ArrayListMultimap.create();

					Pair<Set<EntityDefinition>, Set<EntityDefinition>> items = getDefinitionsFromSelection(
							selection);
					for (EntityDefinition def : items.getFirst())
						sources.put(null, new DefaultType(AlignmentUtil.getTypeEntity(def)));
					for (EntityDefinition def : items.getSecond())
						targets.put(null, new DefaultType(AlignmentUtil.getTypeEntity(def)));

					dummyTypeCell.setSource(sources);
					dummyTypeCell.setTarget(targets);

					return AlignmentUtil.reparentCell(cell, dummyTypeCell, true) != cell;
				}
				else
					return false;
			}
		};
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#fillToolBar()
	 */
	@Override
	protected void fillToolBar() {
		super.fillToolBar();
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.add(showCellsOnChildren);
	}

	/**
	 * Update the view
	 * 
	 * @param selection the selection
	 */
	protected void update(SchemaSelection selection) {
		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		Alignment alignment = as.getAlignment();

		List<Cell> cells = new ArrayList<Cell>();

		Pair<Set<EntityDefinition>, Set<EntityDefinition>> items = getDefinitionsFromSelection(
				selection);

		// find cells associated with the selection
		for (Cell cell : alignment.getCells()) {
			if ((cell.getSource() != null && associatedWith(items.getFirst(), cell))
					|| associatedWith(items.getSecond(), cell)) {
				cells.add(cell);
			}
		}

		getViewer().setInput(cells);
	}

	private Pair<Set<EntityDefinition>, Set<EntityDefinition>> getDefinitionsFromSelection(
			SchemaSelection selection) {
		Set<EntityDefinition> sourceItems;
		Set<EntityDefinition> targetItems;

		if (selection instanceof IStructuredSelection) {
			// prefer getting information from the IStructuredSelection, which
			// from the Schema Explorer only contains the recently selected
			// elements on one side
			sourceItems = new HashSet<EntityDefinition>();
			targetItems = new HashSet<EntityDefinition>();

			for (Object object : ((IStructuredSelection) selection).toArray()) {
				if (object instanceof EntityDefinition) {
					EntityDefinition def = (EntityDefinition) object;
					switch (def.getSchemaSpace()) {
					case TARGET:
						targetItems.add(def);
						break;
					case SOURCE:
					default:
						sourceItems.add(def);
					}
				}
			}
		}
		else {
			sourceItems = selection.getSourceItems();
			targetItems = selection.getTargetItems();
		}

		return new Pair<Set<EntityDefinition>, Set<EntityDefinition>>(sourceItems, targetItems);
	}

	private boolean associatedWith(Collection<EntityDefinition> entityDefs, Cell cell) {
		for (EntityDefinition entity : entityDefs) {
			if (AlignmentUtil.associatedWith(entity, cell, true, showCellsOnChildren.isChecked())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (selectionListener != null) {
			getSite().getWorkbenchWindow().getSelectionService()
					.removePostSelectionListener(selectionListener);
		}

		super.dispose();
	}

	/**
	 * Show the cell with the given ID if found.
	 * 
	 * @param cellId the cell identifier
	 * @return if the cell was found
	 */
	public boolean selectCell(String cellId) {
		// try to retrieve cell
		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		if (as != null) {
			Cell cell = as.getAlignment().getCell(cellId);
			if (cell != null) {
				getViewer().setInput(Collections.singletonList(cell));
				return true;
			}
		}

		return false;
	}

}
