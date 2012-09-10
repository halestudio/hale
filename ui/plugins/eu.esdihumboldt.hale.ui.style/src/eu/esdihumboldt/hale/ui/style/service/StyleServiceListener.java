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

package eu.esdihumboldt.hale.ui.style.service;

import org.eclipse.swt.graphics.RGB;

/**
 * Style service listener.
 * 
 * @author Simon Templer
 */
public interface StyleServiceListener {

	/**
	 * Called when styles have been added to the service
	 * 
	 * @param styleService the style service instance
	 */
	public void stylesAdded(StyleService styleService);

	/**
	 * Called when styles have been removed from the service
	 * 
	 * @param styleService the style service instance
	 */
	public void stylesRemoved(StyleService styleService);

	/**
	 * Called when the settings have been changed (e.g. the default background
	 * and the default styles)
	 * 
	 * @param styleService the style service instance
	 */
	public void styleSettingsChanged(StyleService styleService);

	/**
	 * Called when the background has changed
	 * 
	 * @param styleService the style service instance
	 * @param background the new background
	 */
	public void backgroundChanged(StyleService styleService, RGB background);

}
