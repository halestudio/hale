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

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.JXMapViewer;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.impl.BottomRightClip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.impl.TopLeftClip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.LayoutAugmentation;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterProxy;

/**
 * Diagonal layout that splits the view-port in top-left and bottom-right.
 * Supports only two painters, for additional painters no clip is provided.
 * 
 * @author Simon Templer
 */
public class DiagonalUpLayout implements PainterLayout {

	/**
	 * @see PainterLayout#createClips(int)
	 */
	@Override
	public List<Clip> createClips(int count) {
		List<Clip> result = new ArrayList<Clip>();
		result.add(new TopLeftClip());
		result.add(new BottomRightClip());
		return result;
	}

	/**
	 * @see PainterLayout#getAugmentation(int)
	 */
	@Override
	public LayoutAugmentation getAugmentation(int count) {
		return new AbstractDefaultAugmentation() {

			@Override
			protected void doPaint(Graphics2D g, JXMapViewer map, List<PainterProxy> painters,
					int width, int height) {
				drawSplitLine(g, 0, height, width, 0);
			}
		};
	}

}
