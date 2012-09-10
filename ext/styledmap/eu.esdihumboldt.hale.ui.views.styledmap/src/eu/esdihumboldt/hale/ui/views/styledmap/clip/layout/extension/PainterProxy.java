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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.ClipPainter;

/**
 * Proxy for a painter that is to be layouted.
 * 
 * @author Simon Templer
 */
public interface PainterProxy extends ClipPainter {

	/**
	 * Enable the painter.
	 */
	public void enable();

	/**
	 * Disable the painter.
	 */
	public void disable();

	/**
	 * Get the painter name.
	 * 
	 * @return the painter name, <code>null</code> if not known
	 */
	public String getName();

}
