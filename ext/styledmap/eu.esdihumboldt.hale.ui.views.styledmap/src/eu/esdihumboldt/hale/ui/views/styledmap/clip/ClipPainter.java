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

package eu.esdihumboldt.hale.ui.views.styledmap.clip;

/**
 * Enhancement interface for painters that support a clipping region.
 * 
 * @author Simon Templer
 */
public interface ClipPainter {

	/**
	 * Set the clip algorithm.
	 * 
	 * @param clip the clip algorithm
	 */
	public void setClip(Clip clip);

}
