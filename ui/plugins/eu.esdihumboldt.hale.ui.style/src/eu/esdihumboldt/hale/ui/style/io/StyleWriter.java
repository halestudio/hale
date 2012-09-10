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

package eu.esdihumboldt.hale.ui.style.io;

import org.geotools.styling.Style;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;

/**
 * Provides support for writing styles.
 * 
 * @author Simon Templer
 */
public interface StyleWriter extends ExportProvider {

	/**
	 * Set the style to write.
	 * 
	 * @param style the style to write
	 */
	public void setStyle(Style style);

}
