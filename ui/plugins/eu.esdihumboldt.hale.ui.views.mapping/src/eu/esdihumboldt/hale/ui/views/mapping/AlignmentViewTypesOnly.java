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
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.graph.content.DummyCellGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.StringGraphLabelProvider;
import eu.esdihumboldt.hale.ui.function.common.TypeCellSelector;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusListener;
import eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusService;

/**
 * An Alignment View for Types, overview and navigation.
 * 
 * @author Yasmina Kammeyer
 */
public class AlignmentViewTypesOnly extends AbstractMappingView {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping.navigation";

	private AlignmentServiceListener alignmentListener;

	private TypeCellSelector sourceTargetSelector;

	private TypeCellFocusListener selectionListener;

	private DummyCellGraphContentProvider contentProvider;

	private ISelectionListener mouseClickListener;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		super.createViewControl(parent);

		TypeCellFocusService tc = (TypeCellFocusService) PlatformUI.getWorkbench().getService(
				TypeCellFocusService.class);

		tc.addListener(selectionListener = new TypeCellFocusListener() {

			@Override
			public void dataChanged(Cell cell) {
				if (cell == null) {
					// getViewer().setInput(new
					DefaultCell newCell = new DefaultCell();
					// newCell.setTransformationIdentifier("None");
					getViewer().setInput(newCell);
					// StructuredSelection("Double Click to open dialog."));
				}
				else {
					getViewer().setInput(cell);
				}
				refreshGraph();
			}

		});

		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);

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
			}

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				if (sourceTargetSelector.isCellSelected()
						&& cells.keySet().contains(sourceTargetSelector.getSelectedCell()))
					sourceTargetSelector.setSelection(StructuredSelection.EMPTY);
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

		});

		// listen on size changes
		getViewer().getControl().addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				updateLayout(true);
			}
		});

		// Open the selection Dialog, if the user click on this view
		getSite().getWorkbenchWindow().getSelectionService()
				.addPostSelectionListener(mouseClickListener = new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {
						// only react to own selections
						if (part != AlignmentViewTypesOnly.this)
							return;
						// view doesn't react, if an active cell is selected
						if (sourceTargetSelector.isCellSelected()
								|| selection.equals(StructuredSelection.EMPTY)) {
							return;
						}

						getViewer().setSelection(StructuredSelection.EMPTY);
						sourceTargetSelector.performTypeCellDialog(Display.getCurrent()
								.getActiveShell());
						// System.out.println("Selection" + selection);
					}
				});

		getViewer().setInput(new DefaultCell());
	}

	/**
	 * @return the last selected Cell
	 */
	protected Cell getSelectedCell() {
		return sourceTargetSelector.getSelectedCell();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#refreshGraph()
	 */
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
	 * @see AbstractMappingView#createLabelProvider()
	 */
	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new StringGraphLabelProvider(HaleUI.getServiceProvider());
	}

	/**
	 * @see AbstractMappingView#createContentProvider()
	 */
	@Override
	protected IContentProvider createContentProvider() {
		contentProvider = new DummyCellGraphContentProvider("Select type:",
				"Click here to select a type cell.", "Or Click 'Select Cell' Button'.");
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

		// Create a new item; here create the button.
//		IContributionItem item = new ControlContribution("Select Cell") {
//
//			@Override
//			protected Control createControl(Composite parent) {
//
//				sourceTargetSelector = new TypeCellSelector(parent);
//
//				return sourceTargetSelector.getControl();
//			}
//
//		};
		sourceTargetSelector = new TypeCellSelector();

		Action item = new Action("Select Cell", Action.AS_PUSH_BUTTON) {

			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				sourceTargetSelector.performTypeCellDialog(Display.getCurrent().getActiveShell());
			}
		};

		manager.add(item);
	}

	/**
	 * Checks whether the cell is disabled for the selected type cell
	 * 
	 * @param cell the cell which could be disabled
	 * @return true if disabled, false if not or no type cell is selected
	 */
	protected boolean isDisabledForCurrentType(Cell cell) {
		if (getSelectedCell() != null)
			return cell.getDisabledFor().contains(sourceTargetSelector.getSelectedCell().getId());
		else
			return false;
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
			TypeCellFocusService as = (TypeCellFocusService) PlatformUI.getWorkbench().getService(
					TypeCellFocusService.class);
			as.removeListener(selectionListener);
		}

		if (mouseClickListener != null) {
			getSite().getWorkbenchWindow().getSelectionService()
					.removePostSelectionListener(mouseClickListener);
		}

		sourceTargetSelector.setSelection(StructuredSelection.EMPTY);
		super.dispose();
	}

}
