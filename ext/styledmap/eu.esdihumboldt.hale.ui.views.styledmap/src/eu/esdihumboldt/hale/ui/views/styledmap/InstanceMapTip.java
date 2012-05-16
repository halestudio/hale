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

package eu.esdihumboldt.hale.ui.views.styledmap;

import java.awt.Point;
import java.util.List;

import org.jdesktop.swingx.mapviewer.PixelConverter;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.tip.HoverMapTip;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.AbstractInstancePainter;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.InstanceWaypoint;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.SourceInstancePainter;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.TransformedInstancePainter;

/**
 * Map tooltip for instances. Based on {@link AbstractInstancePainter}s.
 * @author Simon Templer
 */
public class InstanceMapTip extends HoverMapTip {

	private final BasicMapKit mapKit;

	/**
	 * Create instance tool tips.
	 * @param mapKit the map kit
	 */
	public InstanceMapTip(BasicMapKit mapKit) {
		this.mapKit = mapKit;
	}

	/**
	 * @see de.fhg.igd.mapviewer.tip.HoverMapTip#getTipText(int, int, org.jdesktop.swingx.mapviewer.PixelConverter, int)
	 */
	@Override
	protected String getTipText(int x, int y, PixelConverter converter, int zoom) {
		Point point = new Point(x, y);
		
		String sourceName = null;
		List<SourceInstancePainter> sourcePainters = mapKit.getTilePainters(SourceInstancePainter.class);
		if (!sourcePainters.isEmpty()) {
			InstanceWaypoint wp = sourcePainters.get(0).findWaypoint(point);
			if (wp != null) {
				sourceName = wp.getName();
			}
		}
		
		String transformedName = null;
		List<TransformedInstancePainter> transformedPainters = mapKit.getTilePainters(TransformedInstancePainter.class);
		if (!transformedPainters.isEmpty()) {
			InstanceWaypoint wp = transformedPainters.get(0).findWaypoint(point);
			if (wp != null) {
				transformedName = wp.getName();
			}
		}
		
		if (sourceName != null && sourceName.equals(transformedName)) {
			// if both are equal, return one name only
			return sourceName;
		}
		
		if (sourceName != null) {
			sourceName += " (source)";
		}
		if (transformedName != null) {
			transformedName += " (transformed)";
		}
		
		if (sourceName != null && transformedName != null) {
			return sourceName + "; " + transformedName;
		}
		else if (sourceName != null) {
			return sourceName;
		}
		else if (transformedName != null) {
			return transformedName;
		}
		
		return null;
	}

}
