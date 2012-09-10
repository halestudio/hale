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
