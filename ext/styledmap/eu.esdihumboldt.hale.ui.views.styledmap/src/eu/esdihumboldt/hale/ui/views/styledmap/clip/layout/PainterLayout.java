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
