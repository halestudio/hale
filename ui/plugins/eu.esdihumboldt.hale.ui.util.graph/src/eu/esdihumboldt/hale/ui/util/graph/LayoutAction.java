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
