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
package eu.esdihumboldt.hale.ui.util.graph.shapes;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Stretched hexagon example shape.
 * 
 * @author Simon Templer
 */
public class StretchedHexagon extends AbstractPolygonPainter {

	private final int inset;

	private final Insets insets;

	/**
	 * Creates a stretched hexagon shape.
	 * 
	 * @param inset the horizontal inset in pixels
	 */
	public StretchedHexagon(int inset) {
		super();
		this.inset = inset;

		insets = new Insets(0, inset, 0, inset);
	}

	/**
	 * Get the figure's outline points
	 * 
	 * @return the figure's outline
	 */
	@Override
	protected int[] getPoints(Rectangle bounds, int lineWidth) {
		int[] points = new int[12];

		points[0] = bounds.x;
		points[1] = bounds.y + bounds.height / 2;

		points[2] = bounds.x + inset;
		points[3] = bounds.y + lineWidth - 1;

		points[4] = bounds.right() - inset - 1;
		points[5] = bounds.y + lineWidth - 1;

		points[6] = bounds.right() - 1;
		points[7] = bounds.y + bounds.height / 2;

		points[8] = bounds.right() - inset - 1;
		points[9] = bounds.bottom() - 1;

		points[10] = bounds.x + inset;
		points[11] = bounds.bottom() - 1;

		return points;
	}

	@Override
	public Insets getInsets() {
		return insets;
	}

}
