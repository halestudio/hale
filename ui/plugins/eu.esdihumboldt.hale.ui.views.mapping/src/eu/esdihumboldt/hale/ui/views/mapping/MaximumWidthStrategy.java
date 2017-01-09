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

import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

/**
 * {@link ResizingStrategy} for a maximum node width.
 * 
 * @author Florian Esser
 */
public class MaximumWidthStrategy implements ResizingStrategy {

	private final double maximumWidth;

	/**
	 * Creates a {@link ResizingStrategy} that returns the given constant as
	 * maximum node width.
	 * 
	 * @param constant constant maximum width
	 */
	public MaximumWidthStrategy(double constant) {
		this.maximumWidth = constant;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.ResizingStrategy#getMaximumNodeWidth(LayoutContext)
	 */
	@Override
	public void resizeEntities(LayoutContext context) {
		EntityLayout[] entities = context.getEntities();

		for (EntityLayout entity : entities) {
			// Shrink entities that are too wide
			if (entity.getSize().width > maximumWidth) {
				entity.setSize(maximumWidth, entity.getSize().height);
			}
		}
	}

}
