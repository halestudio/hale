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

import java.awt.Rectangle;
import java.awt.Shape;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;

/**
 * Clip algorithm that forbids drawing.
 * 
 * @author Simon Templer
 */
public class Hide implements Clip {

	private static Hide instance;

	/**
	 * Get the {@link Hide} instance.
	 * 
	 * @return the instance
	 */
	public static Hide getInstance() {
		if (instance == null) {
			instance = new Hide();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private Hide() {
		super();
	}

	/**
	 * @see Clip#getClip(Rectangle, int, int, int, int)
	 */
	@Override
	public Shape getClip(Rectangle viewportBounds, int originX, int originY, int width, int height) {
		// nothing should be painted
		return null;
	}

}
