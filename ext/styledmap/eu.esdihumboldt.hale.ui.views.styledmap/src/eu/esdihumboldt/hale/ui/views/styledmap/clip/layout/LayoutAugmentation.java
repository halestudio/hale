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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout;

import java.awt.Graphics2D;
import java.util.List;

import org.jdesktop.swingx.mapviewer.JXMapViewer;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterProxy;

/**
 * Paints an augmentation over a map layouted with the corresponding
 * {@link PainterLayout}.
 * 
 * @author Simon Templer
 */
public interface LayoutAugmentation {

	/**
	 * Paint the layout augmentation.
	 * 
	 * @param g the graphics to paint on
	 * @param map the corresponding map viewer
	 * @param painters the list of layouted painters
	 * @param width the width of the paint area
	 * @param height the height of the paint area
	 */
	public void paint(Graphics2D g, JXMapViewer map, List<PainterProxy> painters, int width,
			int height);

}
