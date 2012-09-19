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
