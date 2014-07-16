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

package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.function.common.TypeCellSelector;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusAdapter;
import eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusService;

/**
 * Alignment View that reacts only on special type cell selections. These cell
 * selections are provided by {@link TypeCellFocusService}
 * 
 * @author Yasmina Kammeyer
 */
public class ContextualAlignmentView extends AbstractMappingView {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping.alignmentcontextsensitive";

	private AlignmentViewContentProvider contentProvider;

	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	private AlignmentServiceAdapter alignmentListener;

	private TypeCellFocusAdapter selectionListener;

	private TypeCellSelector sourceTargetSelector;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		super.createViewControl(parent);

		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);

		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						getViewer().setInput(null);
					}
				});
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				// check for removed type cell
				if (getSelectedCell() != null && Iterables.contains(cells, getSelectedCell())) {
					getViewer().setInput(null);
				}
				refreshGraph();
			}

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				if (getSelectedCell() != null && cells.keySet().contains(getSelectedCell())) {
					getViewer().setInput(null);
				}
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
						getViewer().setInput(null);
					}
				});
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				refreshGraph();
			}

		});

		// Listen on Navigation Selection
		TypeCellFocusService ts = (TypeCellFocusService) PlatformUI.getWorkbench().getService(
				TypeCellFocusService.class);

		ts.addListener(selectionListener = new TypeCellFocusAdapter() {

			@Override
			public void dataChanged(Cell cell) {
				updateRelationWithCell(cell);
			}
		});

		// listen on size changes
		getViewer().getControl().addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				updateLayout(true);
			}
		});

		// getViewer().setInput(new DefaultCell());
		// Create cell selector for select cell button
		sourceTargetSelector = new TypeCellSelector();

		// select type cell, if it is double clicked
		getViewer().addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() == 1) {
					Object selected = selection.getFirstElement();
					if (selected instanceof Cell && AlignmentUtil.isTypeCell((Cell) selected)) {
						// start the type cell selection dialog to change
						// the active selected cell
						sourceTargetSelector.performTypeCellDialog(Display.getCurrent()
								.getActiveShell());
					}
				}
			}
		});

		// Initialize the View
		updateRelationWithCell(ts.getLastSelectedTypeCell());
		refreshGraph();
	}

	/**
	 * Set the input of the viewer if a cell is selected
	 * 
	 * @param cell the input cell, can be null
	 */
	protected void updateRelationWithCell(Cell cell) {
		getViewer().setInput(cell);
	}

	@Override
	protected void refreshGraph() {

		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				getViewer().refresh();
				updateLayout(true);
			}
		});
	}

	/**
	 * Create the content provider to be used for the graph
	 * 
	 * @return the content provider
	 */
	@Override
	protected IContentProvider createContentProvider() {
		contentProvider = new AlignmentViewContentProvider();
		return contentProvider;
	}

	/**
	 * Fill the view toolbar.
	 */
	@Override
	protected void fillToolBar() {
		super.fillToolBar();
		final IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();

		manager.add(new Separator());

		// Add Button to select a type cell
		Action button = new Action(null, Action.AS_PUSH_BUTTON) {

			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				sourceTargetSelector.performTypeCellDialog(Display.getCurrent().getActiveShell());
			}
		};

		// Set the icon
		ImageDescriptor imageD = CommonSharedImages.getImageRegistry().getDescriptor(
				CommonSharedImages.IMG_DEFINITION_CONCRETE_TYPE);

		button.setImageDescriptor(imageD);

		button.setToolTipText("Click to select a type cell");

		manager.add(button);
	}

	/**
	 * @see AbstractMappingView#createLabelProvider()
	 */
	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new GraphLabelProvider(HaleUI.getServiceProvider()) {

			/**
			 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#isInherited(eu.esdihumboldt.hale.common.align.model.Cell)
			 */
			@Override
			protected boolean isInherited(Cell cell) {
				// cannot inherit type cells
				if (AlignmentUtil.isTypeCell(cell))
					return false;

				return AlignmentUtil.reparentCell(cell, getSelectedCell(), true) != cell;
			}

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
		};
	}

	/**
	 * Checks whether the cell is disabled for the selected type cell
	 * 
	 * @param cell the cell which could be disabled
	 * @return true if disabled, false if not or no type cell is selected
	 */
	protected boolean isDisabledForCurrentType(Cell cell) {
		if (getSelectedCell() != null)
			return cell.getDisabledFor().contains(getSelectedCell().getId());
		else
			return false;
	}

	/**
	 * @return the selectedCell or null
	 */
	protected Cell getSelectedCell() {
		if (getViewer().getInput() instanceof Cell)
			return (Cell) getViewer().getInput();
		else
			return null;
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
			TypeCellFocusService ts = (TypeCellFocusService) PlatformUI.getWorkbench().getService(
					TypeCellFocusService.class);
			ts.removeListener(selectionListener);
		}

		functionLabels.dispose();

		super.dispose();
	}

}
