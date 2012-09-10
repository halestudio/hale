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
 * Style service listener adapter
 * 
 * @author Simon Templer
 */
public class StyleServiceAdapter implements StyleServiceListener {

	/**
	 * @see StyleServiceListener#stylesAdded(StyleService)
	 */
	@Override
	public void stylesAdded(StyleService styleService) {
		// please override me
	}

	/**
	 * @see StyleServiceListener#stylesRemoved(StyleService)
	 */
	@Override
	public void stylesRemoved(StyleService styleService) {
		// please override me
	}

	/**
	 * @see StyleServiceListener#styleSettingsChanged(StyleService)
	 */
	@Override
	public void styleSettingsChanged(StyleService styleService) {
		// please override me
	}

	/**
	 * @see StyleServiceListener#backgroundChanged(StyleService, RGB)
	 */
	@Override
	public void backgroundChanged(StyleService styleService, RGB background) {
		// please override me
	}

}
