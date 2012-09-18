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
