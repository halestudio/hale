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
