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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension.ExclusiveExtensionListener;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityModeFactory;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService;
import eu.esdihumboldt.hale.ui.function.common.SourceTargetTypeSelector;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.views.mapping.internal.MappingViewPlugin;
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

	private SourceTargetTypeSelector sourceTargetSelector;
//	private ComboViewer typeRelations;

	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	private ISelectionListener selectionListener;

	private AlignmentLayoutAlgorithm treeLayout;

	private AlignmentViewContentProvider contentProvider;

	private final ViewerFilter baseAlignmentCellFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof Cell)
				return !((Cell) element).isBaseCell();
			else
				return false;
		}
	};

	private final ViewerFilter augmentationFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof Cell) {
				Cell cell = (Cell) element;
				return !AlignmentUtil.isAugmentation(cell);
			}
			else
				return false;
		}
	};

	private final ViewerFilter deactivatedCellFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof Cell)
				return !isDisabledForCurrentType((Cell) element);
			else
				return false;
		}
	};
	private FilterCellAction deactivatedCellFilterAction;

	private final ViewerFilter inheritedCellFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof Cell) {
				Cell cell = (Cell) element;
				if (AlignmentUtil.isTypeCell(cell))
					return true;
				// If reparentCell returns the original cell no change was
				// necessary so it isn't inherited.
				// If strict is set to false, the filter would for example also
				// filter out cells with sources, if the selected cell does not
				// contain any sources.
				return AlignmentUtil.reparentCell(cell, sourceTargetSelector.getSelectedCell(),
						true) == cell;
			}
			else
				return false;
		}
	};

	private ExclusiveExtensionListener<CompatibilityMode, CompatibilityModeFactory> compListener;

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
		sourceTargetSelector = new SourceTargetTypeSelector(page);
		sourceTargetSelector.getControl().setLayoutData(GridDataFactory.swtDefaults()
				.align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		sourceTargetSelector.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getViewer().setInput(sourceTargetSelector.getSelectedCell());
				if (deactivatedCellFilterAction != null) {
					deactivatedCellFilterAction.setEnabled(sourceTargetSelector.isCellSelected());
					if (!sourceTargetSelector.isCellSelected())
						deactivatedCellFilterAction.setChecked(true);
				}
				refreshGraph();
			}
		});
//		typeRelations = new ComboViewer(page, SWT.DROP_DOWN | SWT.READ_ONLY);
//		typeRelations.setContentProvider(ArrayContentProvider.getInstance());
//		typeRelations.setLabelProvider(new LabelProvider() {
//
//			@Override
//			public Image getImage(Object element) {
//				if (element instanceof Cell) {
//					// use function image if possible
//					Cell cell = (Cell) element;
//					String functionId = cell.getTransformationIdentifier();
//					AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
//					if (function != null) {
//						return functionLabels.getImage(function);
//					}
//					return null;
//				}
//
//				return super.getImage(element);
//			}
//
//			@Override
//			public String getText(Object element) {
//				if (element instanceof Cell) {
//					Cell cell = (Cell) element;
//
//					return CellUtil.getCellDescription(cell);
//				}
//
//				return super.getText(element);
//			}
//
//		});
//		typeRelations.addSelectionChangedListener(new ISelectionChangedListener() {
//
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				updateGraph();
//			}
//		});
//		typeRelations.getControl().setLayoutData(
//				GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
//						.create());

		// create viewer
		Composite viewerContainer = new Composite(page, SWT.NONE);
		viewerContainer.setLayout(new FillLayout());
		viewerContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		super.createViewControl(viewerContainer);
		updateLayout(false);

		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);

//		update();

		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						sourceTargetSelector.setSelection(StructuredSelection.EMPTY);
					}
				});
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				if (sourceTargetSelector.isCellSelected()
						&& Iterables.contains(cells, sourceTargetSelector.getSelectedCell()))
					sourceTargetSelector.setSelection(StructuredSelection.EMPTY);
				refreshGraph();
			}

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				if (sourceTargetSelector.isCellSelected()
						&& cells.keySet().contains(sourceTargetSelector.getSelectedCell()))
					sourceTargetSelector.setSelection(StructuredSelection.EMPTY);
				refreshGraph();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				refreshGraph();
			}

			@Override
			public void alignmentChanged() {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						sourceTargetSelector.setSelection(StructuredSelection.EMPTY);
					}
				});
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				refreshGraph();
			}

			@Override
			public void customFunctionsChanged() {
				refreshGraph();
			}

		});

		// initialize compatibility checkup and display
		CompatibilityService cs = PlatformUI.getWorkbench().getService(CompatibilityService.class);

		cs.addListener(
				compListener = new ExclusiveExtensionListener<CompatibilityMode, CompatibilityModeFactory>() {

					@Override
					public void currentObjectChanged(final CompatibilityMode arg0,
							final CompatibilityModeFactory arg1) {
						refreshGraph();
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

		// select type cell, if it is double clicked
		getViewer().addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() == 1) {
					Object selected = selection.getFirstElement();
					if (selected instanceof Cell && AlignmentUtil.isTypeCell((Cell) selected))
						sourceTargetSelector.setSelection(selection);
				}
			}
		});

		// listen on size changes
		getViewer().getControl().addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				updateLayout(true);
			}
		});
		getViewer().setInput(new DefaultCell());
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void menuAboutToShow(IMenuManager manager) {
		ISelection cellSelection = getViewer().getSelection();

		final Cell typeCell = sourceTargetSelector.getSelectedCell();

		// is a type relation selected
		if (!sourceTargetSelector.isCellSelected())
			return;

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
		if (!selectedCell.getDisabledFor().contains(typeCell.getId())) {
			manager.add(new Action("Disable") {

				/**
				 * @see org.eclipse.jface.action.Action#run()
				 */
				@Override
				public void run() {
					AlignmentService as = PlatformUI.getWorkbench()
							.getService(AlignmentService.class);
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
					AlignmentService as = PlatformUI.getWorkbench()
							.getService(AlignmentService.class);
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
						return !((BaseAlignmentCell) selectedCell).getBaseDisabledFor()
								.contains(typeCell.getId());
					return true;
				}
			});
		}
	}

	/**
	 * @see AbstractMappingView#createContentProvider()
	 */
	@Override
	protected IContentProvider createContentProvider() {
		contentProvider = new AlignmentViewContentProvider();
		return contentProvider;
	}

	/**
	 * @see AbstractMappingView#createLabelProvider(GraphViewer)
	 */
	@Override
	protected IBaseLabelProvider createLabelProvider(GraphViewer viewer) {
		return new GraphLabelProvider(viewer, HaleUI.getServiceProvider()) {

			/**
			 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#isInherited(eu.esdihumboldt.hale.common.align.model.Cell)
			 */
			@Override
			protected boolean isInherited(Cell cell) {
				// cannot inherit type cells
				if (AlignmentUtil.isTypeCell(cell))
					return false;

				return AlignmentUtil.reparentCell(cell, sourceTargetSelector.getSelectedCell(),
						true) != cell;
			}

			private final Color cellDisabledBackgroundColor = new Color(Display.getCurrent(), 240,
					240, 240);
			private final Color cellDisabledForegroundColor = new Color(Display.getCurrent(), 109,
					109, 132);
			private final Color cellDisabledHighlightColor = new Color(Display.getCurrent(),
					(int) (getCellHighlightColor().getRed() * 0.7),
					(int) (getCellHighlightColor().getGreen() * 0.7),
					(int) (getCellHighlightColor().getBlue() * 0.7));

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
		};
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#fillToolBar()
	 */
	@Override
	protected void fillToolBar() {
		super.fillToolBar();
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();

		manager.add(new FilterCellAction("Hide augmentation cells", "Show augmentation cells",
				MappingViewPlugin.getImageDescriptor("icons/augmentation.gif"), getViewer(),
				contentProvider, augmentationFilter, true, true));
		manager.add(new FilterCellAction("Hide base alignment cells", "Show base alignment cells",
				MappingViewPlugin.getImageDescriptor("icons/base_alignment.gif"), getViewer(),
				contentProvider, baseAlignmentCellFilter, true, true));
		deactivatedCellFilterAction = new FilterCellAction("Hide deactivated cells",
				"Show deactivated cells",
				MappingViewPlugin.getImageDescriptor("icons/progress_rem.gif"), getViewer(),
				contentProvider, deactivatedCellFilter, true, true);
		if (sourceTargetSelector != null) {
			deactivatedCellFilterAction.setEnabled(sourceTargetSelector.isCellSelected());
			if (!sourceTargetSelector.isCellSelected())
				deactivatedCellFilterAction.setChecked(true);
		}
		manager.add(deactivatedCellFilterAction);
		manager.add(new FilterCellAction("Hide inherited cells", "Show inherited cells",
				CommonSharedImages.getImageRegistry()
						.getDescriptor(CommonSharedImages.IMG_INHERITED_ARROW),
				getViewer(), contentProvider, inheritedCellFilter, true, true));
	}

	@Override
	protected LayoutAlgorithm createLayout() {
		treeLayout = new AlignmentLayoutAlgorithm(TreeLayoutAlgorithm.RIGHT_LEFT);

		return treeLayout;
	}

	/**
	 * Update the layout to the view size.
	 * 
	 * @param triggerLayout if the layout should be applied directly
	 */
	private void updateLayout(boolean triggerLayout) {
		int width = getViewer().getControl().getSize().x;

		treeLayout.setNodeSpace(new Dimension((width - 10) / 3, 30));

		if (triggerLayout) {
			getViewer().applyLayout();
		}
	}

	/**
	 * Update the selected type relation to a cell that is related to the given
	 * schema selection.
	 * 
	 * @param selection the schema selection
	 */
	private void updateRelation(SchemaSelection selection) {
		Cell typeCell = sourceTargetSelector.getSelectedCell();

		if (typeCell != null
				&& (associatedWithType(typeCell.getSource(), selection.getSourceItems())
						&& associatedWithType(typeCell.getTarget(), selection.getTargetItems()))) {
			// type cell is associated with source and target, don't change
			return;
		}

		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		Alignment alignment = as.getAlignment();

		// find type cell associated with both source and target
		for (Cell cell : alignment.getTypeCells()) {
			if ((associatedWithType(cell.getSource(), selection.getSourceItems()))
					&& associatedWithType(cell.getTarget(), selection.getTargetItems())) {
//				typeRelations.setSelection(new StructuredSelection(cell));
				sourceTargetSelector.setSelection(new StructuredSelection(cell));
				return;
			}
		}

		if (typeCell != null
				&& (associatedWithType(typeCell.getSource(), selection.getSourceItems())
						|| associatedWithType(typeCell.getTarget(), selection.getTargetItems()))) {
			// type cell is associated with source or target, don't change
			return;
		}

		// find type cell associated with source or target
		for (Cell cell : alignment.getTypeCells()) {
			if ((associatedWithType(cell.getSource(), selection.getSourceItems()))
					|| associatedWithType(cell.getTarget(), selection.getTargetItems())) {
				sourceTargetSelector.setSelection(new StructuredSelection(cell));
//				typeRelations.setSelection(new StructuredSelection(cell));
				return;
			}
		}
	}

	private boolean associatedWithType(ListMultimap<String, ? extends Entity> entities,
			Set<EntityDefinition> entityDefs) {
		if (entities == null)
			return false;

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

	private boolean isDisabledForCurrentType(Cell cell) {
		if (sourceTargetSelector.isCellSelected())
			return cell.getDisabledFor().contains(sourceTargetSelector.getSelectedCell().getId());
		else
			return false;
	}

//	/**
//	 * Set the current alignment
//	 */
//	private void update() {
//		final Display display = PlatformUI.getWorkbench().getDisplay();
//		display.syncExec(new Runnable() {
//
//			@Override
//			public void run() {
//				ISelection selection = typeRelations.getSelection();
//				Cell lastSelected = null;
//				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
//					lastSelected = (Cell) ((IStructuredSelection) selection).getFirstElement();
//				}
//
//				// update type relations
//				AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
//						AlignmentService.class);
//				Collection<? extends Cell> typeCells = as.getAlignment().getTypeCells();
//				typeRelations.setInput(typeCells);
//
//				ISelection newSelection;
//				if (lastSelected != null && typeCells.contains(lastSelected)) {
//					newSelection = new StructuredSelection(lastSelected);
//				}
//				else if (typeCells.isEmpty()) {
//					newSelection = new StructuredSelection();
//				}
//				else {
//					newSelection = new StructuredSelection(typeCells.iterator().next());
//				}
//				typeRelations.setSelection(newSelection);
//
//				// call to updateGraph is done implicitly through selection
//				// change
//			}
//		});
//	}

	private void refreshGraph() {

		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				getViewer().refresh();
				updateLayout(true);
			}
		});
//		ISelection selection = typeRelations.getSelection();
//
//		Cell typeCell = null;
//		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
//			typeCell = (Cell) ((IStructuredSelection) selection).getFirstElement();
//		}
//
//		if (typeCell != null) {
//			getViewer().setInput(typeCell);
//		}
//		else {
//			getViewer().setInput(Collections.EMPTY_LIST);
//		}
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (alignmentListener != null) {
			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
			as.removeListener(alignmentListener);
		}

		if (compListener != null) {
			CompatibilityService cs = PlatformUI.getWorkbench()
					.getService(CompatibilityService.class);
			cs.removeListener(compListener);
		}

		if (selectionListener != null) {
			getSite().getWorkbenchWindow().getSelectionService()
					.removePostSelectionListener(selectionListener);
		}

		functionLabels.dispose();

		super.dispose();
	}

}
