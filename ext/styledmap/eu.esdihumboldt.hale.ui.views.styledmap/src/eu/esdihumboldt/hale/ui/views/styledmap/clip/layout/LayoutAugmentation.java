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
