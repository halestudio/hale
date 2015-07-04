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
