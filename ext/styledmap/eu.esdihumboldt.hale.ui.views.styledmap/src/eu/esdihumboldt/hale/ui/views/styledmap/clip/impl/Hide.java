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
