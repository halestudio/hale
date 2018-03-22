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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.graph.content.ReverseCellGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.util.graph.ExportGraphAction;
import eu.esdihumboldt.hale.ui.util.graph.LayoutAction;
import eu.esdihumboldt.hale.ui.util.selection.SelectionProviderFacade;
import eu.esdihumboldt.hale.ui.util.viewer.PostSelectionSupport;
import eu.esdihumboldt.hale.ui.util.viewer.ViewerMenu;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;

/**
 * Abstract mapping graph view. Subclasses are responsible for setting the
 * viewer input.
 * 
 * @author Simon Templer
 */
public abstract class AbstractMappingView extends PropertiesViewPart
		implements IZoomableWorkbenchPart {

	private GraphViewer viewer;

	private SelectionProviderFacade selectionFacade;

	@Override
	public void createViewControl(Composite parent) {
		viewer = new GraphViewer(parent, SWT.BORDER);
		viewer.setContentProvider(createContentProvider());
//		viewer.setContentProvider(new CellRelationshipContentProvider());
//		viewer.setContentProvider(new NestedCellRelationshipContentProvider());
		viewer.setLabelProvider(createLabelProvider(viewer));
		viewer.setInput(null);
		LayoutAlgorithm layout = createLayout();
		viewer.setLayoutAlgorithm(layout, true);
		viewer.applyLayout();
		fillToolBar();

		// set selection provider
		selectionFacade = new SelectionProviderFacade();
		selectionFacade.setSelectionProvider(getViewer());
		getSite().setSelectionProvider(new PostSelectionSupport(selectionFacade));

		viewer.getControl().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 'a' && (e.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
					// XXX even though getSelection returns the current state, a
					// selection update is not triggered by the control
					// -> force selection change event after Ctrl+A
					ISelection sel = viewer.getSelection();
					selectionFacade.setSelection(sel);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// nothing to do
			}
		});

		// create context menu
		new ViewerMenu(getSite(), getViewer()) {

			/**
			 * @see eu.esdihumboldt.hale.ui.util.ViewContextMenu#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
			 */
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				super.menuAboutToShow(manager);
				AbstractMappingView.this.menuAboutToShow(manager);
			}
		};
	}

	/**
	 * Override to change menu contents.
	 * 
	 * @param manager the menu manager
	 * @see IMenuListener#menuAboutToShow(IMenuManager)
	 */
	protected void menuAboutToShow(IMenuManager manager) {
		// By default do nothing.
	}

	/**
	 * Create the label provider to be used for the graph
	 * 
	 * @param viewer the graph viewer the label provider will be associated with
	 * 
	 * @return the label provider
	 */
	protected IBaseLabelProvider createLabelProvider(GraphViewer viewer) {
		return new GraphLabelProvider(viewer, HaleUI.getServiceProvider());
	}

	/**
	 * Create the content provider to be used for the graph
	 * 
	 * @return the content provider
	 */
	protected IContentProvider createContentProvider() {
		return new ReverseCellGraphContentProvider();
	}

	/**
	 * Create the initial layout to use
	 * 
	 * @return the layout
	 */
	protected LayoutAlgorithm createLayout() {
		LayoutAlgorithm layout;
		layout = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.RIGHT_LEFT);
		return layout;
	}

	/**
	 * Fill the view toolbar.
	 */
	protected void fillToolBar() {
		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(
				this);
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(toolbarZoomContributionViewItem);
		bars.getToolBarManager().add(new LayoutAction(viewer));

		bars.getToolBarManager().add(new ExportGraphAction(viewer));
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * @return the viewer
	 */
	protected GraphViewer getViewer() {
		return viewer;
	}

	/**
	 * @see IZoomableWorkbenchPart#getZoomableViewer()
	 */
	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return viewer;
	}

}
