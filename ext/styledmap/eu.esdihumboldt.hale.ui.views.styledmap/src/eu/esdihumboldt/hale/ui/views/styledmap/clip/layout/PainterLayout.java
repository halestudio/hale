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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout;

import java.util.List;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;

/**
 * Painter layouts organize multiple painters on a map by assigning them
 * clipping regions.
 * 
 * @author Simon Templer
 */
public interface PainterLayout {

	/**
	 * Create clip algorithms for a given number of painters.
	 * 
	 * @param count the number of painters to layout
	 * @return a clip for each painter to layout, it is also possible for an
	 *         element to be <code>null</code>, which means no clipping should
	 *         be applied to the corresponding painter. If the size of the list
	 *         is smaller than count, the remaining painters should be disabled
	 */
	public List<Clip> createClips(int count);

	/**
	 * Get the layout augmentation painter for a given number of painters.
	 * 
	 * @param count the number of painters to layout
	 * @return the augmentation painter or <code>null</code> if there is none
	 *         available
	 */
	public LayoutAugmentation getAugmentation(int count);

}
