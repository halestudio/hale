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

package eu.esdihumboldt.hale.ui.common.service.style;

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
