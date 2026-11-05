/*
 * Copyright (c) 2026 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.util.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IFigureProvider;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;

/**
 * Graph viewer that creates {@link CustomFigureGraphNode}s for the figures of
 * an {@link IFigureProvider}, so that the figures get the text and style of
 * their node. Use it instead of {@link GraphViewer} if the label provider
 * provides figures.
 */
public class CustomFigureGraphViewer extends GraphViewer {

	/**
	 * Create a graph viewer with a new graph.
	 * 
	 * @param composite the parent composite
	 * @param style the style of the graph
	 */
	public CustomFigureGraphViewer(Composite composite, int style) {
		super(composite, style);
	}

	@Override
	protected GraphNode createNodeObject(Graph graphModel, IFigure figure) {
		return new CustomFigureGraphNode(graphModel, SWT.NONE, figure);
	}

}
