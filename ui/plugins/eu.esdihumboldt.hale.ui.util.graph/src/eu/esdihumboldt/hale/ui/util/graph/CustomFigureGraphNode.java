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
import org.eclipse.zest.core.widgets.CGraphNode;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;
import org.eclipse.zest.core.widgets.ILabeledFigure;
import org.eclipse.zest.core.widgets.IStyleableFigure;

/**
 * Graph node with a custom figure that, unlike {@link CGraphNode}, is updated
 * from the node: figures implementing {@link ILabeledFigure} get the node's
 * text and image, figures implementing {@link IStyleableFigure} its colors and
 * border.
 * 
 * {@link CGraphNode} of GEF Classic does not update its figure, so the text and
 * the border of hale's figures (see {@link CustomShapeFigure}) were never set
 * and the nodes were invisible. The custom node of Zest 2 updated them.
 */
public class CustomFigureGraphNode extends GraphNode {

	/**
	 * Create a graph node with a custom figure.
	 * 
	 * @param graphModel the graph or container the node is added to
	 * @param style the node style
	 * @param figure the node figure
	 */
	public CustomFigureGraphNode(IContainer graphModel, int style, IFigure figure) {
		super(graphModel, style, figure);
	}

	/**
	 * @return the custom figure of the node
	 */
	public IFigure getCustomFigure() {
		return getModelFigure();
	}

	@Override
	protected IFigure createFigureForModel() {
		// called from the constructor, the figure is the node data at that
		// time; it is updated when the viewer sets text and style of the node
		return (IFigure) getData();
	}

}
