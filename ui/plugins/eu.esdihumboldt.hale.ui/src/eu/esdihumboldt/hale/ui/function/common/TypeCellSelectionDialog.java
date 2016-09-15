/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.function.common;

import org.eclipse.draw2d.SWTEventDispatcher;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.graph.content.ReverseCellGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * A dialog to select a type cell.
 * 
 * @author Kai Schwierczek
 */
public class TypeCellSelectionDialog extends AbstractViewerSelectionDialog<Cell, GraphViewer> {

	/**
	 * Create a type cell selection dialog.
	 * 
	 * @param parentShell the parent shell
	 * @param title the dialog title
	 * @param initialSelection the type cell to select initially (if possible),
	 *            may be <code>null</code>
	 */
	public TypeCellSelectionDialog(Shell parentShell, String title, Cell initialSelection) {
		super(parentShell, title, initialSelection);
		setFilters(new ViewerFilter[] { new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof Cell;
			}
		} });
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog#createViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected GraphViewer createViewer(Composite parent) {
		GraphViewer viewer = new GraphViewer(parent, SWT.BORDER);
		// disable node movement
		viewer.getGraphControl().getLightweightSystem()
				.setEventDispatcher(new SWTEventDispatcher() {

					/**
					 * @see org.eclipse.draw2d.SWTEventDispatcher#dispatchMouseMoved(org.eclipse.swt.events.MouseEvent)
					 */
					@Override
					public void dispatchMouseMoved(MouseEvent me) {
						// ignore
					}
				});
		return viewer;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog#setupViewer(org.eclipse.jface.viewers.StructuredViewer,
	 *      java.lang.Object)
	 */
	@Override
	protected void setupViewer(final GraphViewer viewer, Cell initialSelection) {
		// content and label provider
		viewer.setContentProvider(new ReverseCellGraphContentProvider());
		viewer.setLabelProvider(new GraphLabelProvider(viewer, HaleUI.getServiceProvider()));

		// layout
		final TreeLayoutAlgorithm layout = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.RIGHT_LEFT);
		viewer.getControl().addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				int width = viewer.getControl().getSize().x;
				layout.setNodeSpace(new Dimension((width - 10) / 3, 30));
			}
		});
		viewer.setLayoutAlgorithm(layout, true);
		viewer.applyLayout();

		// input and selection
		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		viewer.setInput(as.getAlignment().getTypeCells());
		if (initialSelection != null)
			viewer.setSelection(new StructuredSelection(initialSelection));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog#getObjectFromSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	protected Cell getObjectFromSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			if (sel.getFirstElement() instanceof Cell)
				return (Cell) sel.getFirstElement();
		}
		return null;
	}

}
