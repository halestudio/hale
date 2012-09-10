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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.impl;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.LayoutAugmentation;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;

/**
 * Layout that allows all painters to be fully painted.
 * 
 * @author Simon Templer
 */
public class NoLayout implements PainterLayout {

	/**
	 * @see PainterLayout#createClips(int)
	 */
	@Override
	public List<Clip> createClips(int count) {
		List<Clip> clips = new ArrayList<Clip>(count);
		for (int i = 0; i < count; i++) {
			clips.add(null); // null is cleaner than NoClip
		}
		return clips;
	}

	/**
	 * @see PainterLayout#getAugmentation(int)
	 */
	@Override
	public LayoutAugmentation getAugmentation(int count) {
		return null;
	}

}
