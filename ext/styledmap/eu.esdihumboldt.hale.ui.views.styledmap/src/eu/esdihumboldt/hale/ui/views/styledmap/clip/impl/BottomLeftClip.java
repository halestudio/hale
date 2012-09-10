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
