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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
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
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;
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

			/**
			 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter#cellsPropertyChanged(java.lang.Iterable,
			 *      java.lang.String)
			 */
			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
//				update();
				getViewer().refresh();
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
			cells.addAll(as.getAlignment().getPropertyCells(typeCell));
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
