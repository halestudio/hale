/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.mapping;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.hale.ui.common.graph.content.CellGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.util.graph.ExportGraphAction;
import eu.esdihumboldt.hale.ui.util.graph.LayoutAction;
import eu.esdihumboldt.hale.ui.util.viewer.PostSelectionSupport;
import eu.esdihumboldt.hale.ui.util.viewer.ViewerMenu;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;

/**
 * Abstract mapping graph view. Subclasses are responsible for setting the
 * viewer input.
 * @author Simon Templer
 */
public abstract class AbstractMappingView extends PropertiesViewPart implements IZoomableWorkbenchPart {
	
	private GraphViewer viewer;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		viewer = new GraphViewer(parent, SWT.BORDER);
		viewer.setContentProvider(createContentProvider());
//		viewer.setContentProvider(new CellRelationshipContentProvider());
//		viewer.setContentProvider(new NestedCellRelationshipContentProvider());
		viewer.setLabelProvider(createLabelProvider());
		viewer.setInput(null);
		LayoutAlgorithm layout = createLayout();
		viewer.setLayoutAlgorithm(layout, true);
		viewer.applyLayout();
		fillToolBar();
		
		// set selection provider
		getSite().setSelectionProvider(new PostSelectionSupport(getViewer()));
		
		// create context menu
		new ViewerMenu(getSite(), getViewer());
	}
	
	/**
	 * Create the label provider to be used for the graph
	 * @return the label provider
	 */
	protected IBaseLabelProvider createLabelProvider() {
		return new GraphLabelProvider();
	}

	/**
	 * Create the content provider to be used for the graph
	 * @return the content provider
	 */
	protected IContentProvider createContentProvider() {
		return new CellGraphContentProvider();
	}

	/**
	 * Create the initial layout to use
	 * @return the layout
	 */
	protected LayoutAlgorithm createLayout() {
		LayoutAlgorithm layout;
		layout = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.LEFT_RIGHT);
		return layout;
	}
	
	private void fillToolBar() {
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
