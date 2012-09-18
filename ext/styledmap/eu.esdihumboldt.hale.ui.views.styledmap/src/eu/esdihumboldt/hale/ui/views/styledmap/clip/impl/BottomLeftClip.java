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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.impl;

import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Diagonal clip for painting the bottom left area of the view-port.
 * 
 * @author Simon Templer
 */
public class BottomLeftClip extends AbstractPolygonClip {

	/**
	 * @see AbstractPolygonClip#getVisiblePolygon(Rectangle)
	 */
	@Override
	protected Polygon getVisiblePolygon(Rectangle viewportBounds) {
		return new Polygon(new int[] { viewportBounds.x, viewportBounds.x + viewportBounds.width,
				viewportBounds.x },
				new int[] { viewportBounds.y, viewportBounds.y + viewportBounds.height,
						viewportBounds.y + viewportBounds.height }, 3);
	}

}
