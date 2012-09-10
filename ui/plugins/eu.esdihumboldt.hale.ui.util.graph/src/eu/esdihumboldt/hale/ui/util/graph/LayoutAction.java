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

package eu.esdihumboldt.hale.ui.util.graph;

import org.eclipse.jface.action.Action;
import org.eclipse.zest.core.viewers.GraphViewer;

import eu.esdihumboldt.hale.ui.util.graph.internal.GraphUIUtilBundle;

/**
 * Action that triggers relayouting a {@link GraphViewer}
 * 
 * @author Simon Templer
 */
public class LayoutAction extends Action {

	private final GraphViewer viewer;

	/**
	 * Constructor
	 * 
	 * @param viewer the graph viewer to layout
	 */
	public LayoutAction(GraphViewer viewer) {
		super();

		this.viewer = viewer;

		setText("Apply layout");
		setToolTipText("Layout the graph");
		setImageDescriptor(GraphUIUtilBundle.getImageDescriptor("icons/layout.gif"));
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		viewer.applyLayout();
	}

}
