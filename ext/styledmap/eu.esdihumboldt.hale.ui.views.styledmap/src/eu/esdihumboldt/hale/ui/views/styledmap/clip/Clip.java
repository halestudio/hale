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

package eu.esdihumboldt.hale.ui.views.styledmap.clip;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Interface for classes defining an algorithm to compute a clipping region.
 * 
 * @author Simon Templer
 */
public interface Clip {

	/**
	 * Determine the clip region for painting.
	 * 
	 * @param viewportBounds the view-port bounds (world pixel coordinates)
	 * @param originX the x position of the origin of the graphics to clip
	 *            (world pixel coordinates)
	 * @param originY the y position of the origin of the graphics to clip
	 *            (world pixel coordinates)
	 * @param width the graphics width
	 * @param height the graphics height
	 * @return the clip shape, or <code>null</code> if nothing should be painted
	 */
	public Shape getClip(Rectangle viewportBounds, int originX, int originY, int width, int height);

}
