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
 * Clip algorithm that performs no clipping.
 * 
 * @author Simon Templer
 */
public class NoClip implements Clip {

	private static NoClip instance;

	/**
	 * Get the {@link NoClip} instance.
	 * 
	 * @return the instance
	 */
	public static NoClip getInstance() {
		if (instance == null) {
			instance = new NoClip();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private NoClip() {
		super();
	}

	/**
	 * @see Clip#getClip(Rectangle, int, int, int, int)
	 */
	@Override
	public Shape getClip(Rectangle viewportBounds, int originX, int originY, int width, int height) {
		// no clipping, return the whole area
		return new Rectangle(width, height);
	}

}
