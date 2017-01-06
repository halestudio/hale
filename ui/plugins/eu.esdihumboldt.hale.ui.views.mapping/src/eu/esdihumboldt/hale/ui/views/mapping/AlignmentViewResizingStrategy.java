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

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Item;
import org.eclipse.zest.core.widgets.custom.CGraphNode;
import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

/**
 * {@link ResizingStrategy} for HALE's Alignment view
 * 
 * @author Florian Esser
 */
public class AlignmentViewResizingStrategy implements ResizingStrategy {

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.ResizingStrategy#resizeEntities(org.eclipse.zest.layouts.interfaces.LayoutContext)
	 */
	@Override
	public void resizeEntities(LayoutContext context) {
		EntityLayout[] entities = context.getEntities();

		// XXX This simple strategy assumes that there is always a 3-column
		// layout and won't work well for arbitrary trees
		final double maxNodeWidth = (context.getBounds().width - 10) / 3;

		for (EntityLayout entity : entities) {
			// Shrink entities that are too wide
			if (entity.getSize().width > maxNodeWidth) {
				entity.setSize(maxNodeWidth, entity.getSize().height);
			}

			// Re-enlarge entities that have been previously shrunk if there is
			// more space available now
			// XXX Will work only for Entities with exactly one Item
			if (entity.getItems() != null && entity.getItems().length == 1) {
				Item item = entity.getItems()[0];
				if (item instanceof CGraphNode) {
					IFigure figure = ((CGraphNode) item).getFigure();
					int preferredWidth = figure.getPreferredSize().width;
					if (preferredWidth > entity.getSize().width && preferredWidth <= maxNodeWidth) {
						entity.setSize(preferredWidth, entity.getSize().height);
					}
				}
			}
		}
	}

}
