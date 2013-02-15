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
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;

/**
 * View displaying the current alignment
 * 
 * @author Simon Templer
 */
public class AlignmentView extends AbstractMappingView {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping.alignment";

	private AlignmentServiceListener alignmentListener;

	private ComboViewer typeRelations;

	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	private ISelectionListener selectionListener;

	/**
	 * @see PropertiesViewPart#getViewContext()
	 */
	@Override
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.alignment";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(GridLayoutFactory.fillDefaults().create());

		// create type relation selection control
		typeRelations = new ComboViewer(page, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeRelations.setContentProvider(ArrayContentProvider.getInstance());
		typeRelations.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				if (element instanceof Cell) {
					// use function image if possible
					Cell cell = (Cell) element;
					String functionId = cell.getTransformationIdentifier();
					AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
					if (function != null) {
						return functionLabels.getImage(function);
					}
					return null;
				}

				return super.getImage(element);
			}

			@Override
			public String getText(Object element) {
				if (element instanceof Cell) {
					Cell cell = (Cell) element;

					return CellUtil.getCellDescription(cell);
				}

				return super.getText(element);
			}

		});
		typeRelations.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateGraph();
			}
		});
		typeRelations.getControl().setLayoutData(
				GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
						.create());

		// create viewer
		Composite viewerContainer = new Composite(page, SWT.NONE);
		viewerContainer.setLayout(new FillLayout());
		viewerContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		super.createViewControl(viewerContainer);

		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);

		update();

		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				update();
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				update();
			}

			@Override
			public void cellReplaced(Cell oldCell, Cell newCell) {
				update();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				update();
			}

			@Override
			public void alignmentChanged() {
				update();
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						getViewer().refresh();
					}
				});
			}

		});

		// listen on SchemaSelections
		getSite().getWorkbenchWindow().getSelectionService()
				.addPostSelectionListener(selectionListener = new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {
						if (!(selection instanceof SchemaSelection)) {
							// only react on schema selections
							return;
						}

						if (part != AlignmentView.this) {
							updateRelation((SchemaSelection) selection);
						}
					}
				});
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void menuAboutToShow(IMenuManager manager) {
		ISelection typeSelection = typeRelations.getSelection();
		ISelection cellSelection = getViewer().getSelection();

		// is a type relation selected
		if (typeSelection.isEmpty() || !(typeSelection instanceof IStructuredSelection))
			return;

		final Cell typeCell = (Cell) ((IStructuredSelection) typeSelection).getFirstElement();

		// is a cell selected?
		if (!(cellSelection instanceof IStructuredSelection)
				|| ((IStructuredSelection) cellSelection).size() != 1
				|| !(((IStructuredSelection) cellSelection).getFirstElement() instanceof Cell))
			return;

		final Cell selectedCell = (Cell) ((IStructuredSelection) cellSelection).getFirstElement();

		// ignore type cell
		if (AlignmentUtil.isTypeCell(selectedCell))
			return;

		// check current disable status
		if (!selectedCell.getDisabledFor().contains(typeCell)) {
			manager.add(new Action("Disable") {

				/**
				 * @see org.eclipse.jface.action.Action#run()
				 */
				@Override
				public void run() {
					AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
							AlignmentService.class);
					as.setCellProperty(selectedCell.getId(), Cell.PROPERTY_DISABLE_FOR, typeCell);
				}
			});
		}
		else {
			manager.add(new Action("Enable") {

				/**
				 * @see org.eclipse.jface.action.Action#run()
				 */
				@Override
				public void run() {
					AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
							AlignmentService.class);
					as.setCellProperty(selectedCell.getId(), Cell.PROPERTY_ENABLE_FOR, typeCell);
				}

				/**
				 * @see org.eclipse.jface.action.Action#isEnabled()
				 */
				@Override
				public boolean isEnabled() {
					// Not enabled, if the cell was disabled in a base
					// alignment.
					// Still show the action for clarity.
					if (selectedCell instanceof BaseAlignmentCell)
						return !((BaseAlignmentCell) selectedCell).getBaseDisabledFor().contains(
								typeCell);
					return true;
				}
			});
		}
	}

	/**
	 * @see AbstractMappingView#createLabelProvider()
	 */
	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new GraphLabelProvider(HaleUI.getServiceProvider()) {

			private final Color cellDisabledBackgroundColor = new Color(Display.getCurrent(), 240,
					240, 240);
			private final Color cellDisabledForegroundColor = new Color(Display.getCurrent(), 109,
					109, 132);
			private final Color cellDisabledHighlightColor = new Color(Display.getCurrent(),
					(int) (getCellHighlightColor().getRed() * 0.7), (int) (getCellHighlightColor()
							.getGreen() * 0.7), (int) (getCellHighlightColor().getBlue() * 0.7));

			/**
			 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getNodeHighlightColor(java.lang.Object)
			 */
			@Override
			public Color getNodeHighlightColor(Object entity) {
				if (entity instanceof Cell && isDisabledForCurrentType((Cell) entity))
					return cellDisabledHighlightColor;

				return super.getNodeHighlightColor(entity);
			}

			/**
			 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getBackgroundColour(java.lang.Object)
			 */
			@Override
			public Color getBackgroundColour(Object entity) {
				if (entity instanceof Cell && isDisabledForCurrentType((Cell) entity))
					return cellDisabledBackgroundColor;
				return super.getBackgroundColour(entity);
			}

			/**
			 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getForegroundColour(java.lang.Object)
			 */
			@Override
			public Color getForegroundColour(Object entity) {
				if (entity instanceof Cell && isDisabledForCurrentType((Cell) entity))
					return cellDisabledForegroundColor;
				return super.getForegroundColour(entity);
			}

			/**
			 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#dispose()
			 */
			@Override
			public void dispose() {
				cellDisabledBackgroundColor.dispose();
				cellDisabledForegroundColor.dispose();
				cellDisabledHighlightColor.dispose();
				super.dispose();
			}

			private boolean isDisabledForCurrentType(Cell cell) {
				ISelection typeSelection = typeRelations.getSelection();

				if (!typeSelection.isEmpty() && typeSelection instanceof IStructuredSelection) {
					Cell typeCell = (Cell) ((IStructuredSelection) typeSelection).getFirstElement();
					if (cell.getDisabledFor().contains(typeCell))
						return true;
				}
				return false;
			}
		};
	}

	/**
	 * Update the selected type relation to a cell that is related to the given
	 * schema selection.
	 * 
	 * @param selection the schema selection
	 */
	private void updateRelation(SchemaSelection selection) {
		ISelection typeSelection = typeRelations.getSelection();

		Cell typeCell = null;
		if (!typeSelection.isEmpty() && typeSelection instanceof IStructuredSelection) {
			typeCell = (Cell) ((IStructuredSelection) typeSelection).getFirstElement();
		}

		if (typeCell != null
				&& (associatedWithType(typeCell.getSource(), selection.getSourceItems()) && associatedWithType(
						typeCell.getTarget(), selection.getTargetItems()))) {
			// type cell is associated with source and target, don't change
			return;
		}

		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);
		Alignment alignment = as.getAlignment();

		// find type cell associated with both source and target
		for (Cell cell : alignment.getTypeCells()) {
			if ((associatedWithType(cell.getSource(), selection.getSourceItems()))
					&& associatedWithType(cell.getTarget(), selection.getTargetItems())) {
				typeRelations.setSelection(new StructuredSelection(cell));
				return;
			}
		}

		if (typeCell != null
				&& (associatedWithType(typeCell.getSource(), selection.getSourceItems()) || associatedWithType(
						typeCell.getTarget(), selection.getTargetItems()))) {
			// type cell is associated with source or target, don't change
			return;
		}

		// find type cell associated with source or target
		for (Cell cell : alignment.getTypeCells()) {
			if ((associatedWithType(cell.getSource(), selection.getSourceItems()))
					|| associatedWithType(cell.getTarget(), selection.getTargetItems())) {
				typeRelations.setSelection(new StructuredSelection(cell));
				return;
			}
		}
	}

	private boolean associatedWithType(ListMultimap<String, ? extends Entity> entities,
			Set<EntityDefinition> entityDefs) {
		Set<TypeDefinition> types = new HashSet<TypeDefinition>(); // XXX must
																	// be
																	// TypeEntityDefintions
																	// when
																	// there are
																	// contexts
																	// for types
		for (EntityDefinition entityDef : entityDefs) {
			types.add(entityDef.getType());
		}

		for (Entity entity : entities.values()) {
			if (types.contains(entity.getDefinition().getType())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Set the current alignment
	 */
	private void update() {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				ISelection selection = typeRelations.getSelection();
				Cell lastSelected = null;
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					lastSelected = (Cell) ((IStructuredSelection) selection).getFirstElement();
				}

				// update type relations
				AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
						AlignmentService.class);
				Collection<? extends Cell> typeCells = as.getAlignment().getTypeCells();
				typeRelations.setInput(typeCells);

				ISelection newSelection;
				if (lastSelected != null && typeCells.contains(lastSelected)) {
					newSelection = new StructuredSelection(lastSelected);
				}
				else if (typeCells.isEmpty()) {
					newSelection = new StructuredSelection();
				}
				else {
					newSelection = new StructuredSelection(typeCells.iterator().next());
				}
				typeRelations.setSelection(newSelection);

				// call to updateGraph is done implicitly through selection
				// change
			}
		});
	}

	private void updateGraph() {
		ISelection selection = typeRelations.getSelection();

		Cell typeCell = null;
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			typeCell = (Cell) ((IStructuredSelection) selection).getFirstElement();
		}

		if (typeCell != null) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
			Collection<Cell> cells = new ArrayList<Cell>();
			cells.add(typeCell);
			cells.addAll(as.getAlignment().getPropertyCells(typeCell, true));
			getViewer().setInput(cells);
		}
		else {
			getViewer().setInput(Collections.EMPTY_LIST);
		}
		getViewer().applyLayout();
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (alignmentListener != null) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
			as.removeListener(alignmentListener);
		}

		if (selectionListener != null) {
			getSite().getWorkbenchWindow().getSelectionService()
					.removePostSelectionListener(selectionListener);
		}

		functionLabels.dispose();

		super.dispose();
	}

}
