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

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.JXMapViewer;

import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.impl.BottomLeftClip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.impl.TopRightClip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.LayoutAugmentation;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension.PainterProxy;

/**
 * Diagonal layout that splits the view-port in bottom-left and top-right.
 * Supports only two painters, for additional painters no clip is provided.
 * 
 * @author Simon Templer
 */
public class DiagonalDownLayout implements PainterLayout {

	/**
	 * @see PainterLayout#createClips(int)
	 */
	@Override
	public List<Clip> createClips(int count) {
		List<Clip> result = new ArrayList<Clip>();
		result.add(new BottomLeftClip());
		result.add(new TopRightClip());
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
				drawSplitLine(g, 0, 0, width, height);
			}
		};
	}

}
