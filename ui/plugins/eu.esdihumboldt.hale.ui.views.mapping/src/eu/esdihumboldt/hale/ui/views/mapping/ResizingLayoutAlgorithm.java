/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.ui.views.mapping;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

/**
 * Layout algorithm that supports horizontal resizing of its nodes according to
 * a specified {@link ResizingStrategy}.
 * 
 * This algorithm assumes that the tree has a 3-column layout and will not work
 * well for arbitrary trees. All layouting except the width calculation is
 * delegated to {@link TreeLayoutAlgorithm}.
 * 
 * @author Florian Esser
 */
public class ResizingLayoutAlgorithm implements LayoutAlgorithm {

	private final ResizingStrategy resizingStrategy;
	private final TreeLayoutAlgorithm layoutDelegate;
	private LayoutContext context;

	/**
	 * Create a Tree Layout for the Alignment view with a specified direction.
	 * 
	 * @param direction The direction, one of
	 *            {@link TreeLayoutAlgorithm#BOTTOM_UP},
	 *            {@link TreeLayoutAlgorithm#LEFT_RIGHT},
	 *            {@link TreeLayoutAlgorithm#RIGHT_LEFT},
	 *            {@link TreeLayoutAlgorithm#TOP_DOWN}
	 * @param resizingStrategy resizing strategy to use
	 */
	public ResizingLayoutAlgorithm(int direction, ResizingStrategy resizingStrategy) {
		this.layoutDelegate = new TreeLayoutAlgorithm(direction);
		this.resizingStrategy = resizingStrategy;
	}

	/**
	 * @see org.eclipse.zest.layouts.LayoutAlgorithm#setLayoutContext(org.eclipse.zest.layouts.interfaces.LayoutContext)
	 */
	@Override
	public void setLayoutContext(LayoutContext context) {
		this.context = context;
		layoutDelegate.setLayoutContext(context);
	}

	/**
	 * @see org.eclipse.zest.layouts.LayoutAlgorithm#applyLayout(boolean)
	 */
	@Override
	public void applyLayout(boolean clean) {
		resizingStrategy.resizeEntities(context);
		layoutDelegate.applyLayout(clean);
	}

	/**
	 * @see org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm#setNodeSpace(Dimension)
	 */
	@SuppressWarnings("javadoc")
	public void setNodeSpace(Dimension nodeSpace) {
		this.layoutDelegate.setNodeSpace(nodeSpace);
	}
}
