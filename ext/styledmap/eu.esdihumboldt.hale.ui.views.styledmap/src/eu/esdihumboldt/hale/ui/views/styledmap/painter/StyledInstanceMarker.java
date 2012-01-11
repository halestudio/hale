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

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.eclipse.ui.PlatformUI;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;

import de.fhg.igd.mapviewer.marker.BoundingBoxMarker;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.ui.service.instance.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.style.StyleHelper;
import eu.esdihumboldt.hale.ui.style.service.StyleService;
import eu.esdihumboldt.hale.ui.style.service.internal.StylePreferences;

/**
 * Instance marker support styles provided through the {@link StyleService}.
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class StyledInstanceMarker extends InstanceMarker {

	/**
	 * @see BoundingBoxMarker#applyFill(Graphics2D, SelectableWaypoint)
	 */
	@Override
	protected boolean applyFill(Graphics2D g, InstanceWaypoint context) {
		if (context.isSelected()) {
			// for selection use default
			return super.applyFill(g, context);
		}
		
		Style style = getStyle(context);
		
		//TODO honor rules!
		//TODO do all this only once (reset information on style change)
		
		// retrieve fill
		Fill fill = null;
		// try polygon
		PolygonSymbolizer polygonSymbolizer = SLD.polySymbolizer(style);
		if (polygonSymbolizer != null) {
			fill = SLD.fill(polygonSymbolizer);
		}
		// try point
		if (fill == null) {
			PointSymbolizer pointSymbolizer = SLD.pointSymbolizer(style);
			if (pointSymbolizer != null) {
				fill = SLD.fill(pointSymbolizer);
			}
		}
		
		if (fill != null) {
			Color sldColor = SLD.color(fill);
			double opacity = SLD.opacity(fill);
			Color fillColor;
			if (sldColor != null) {
				fillColor = new Color(sldColor.getRed(), sldColor.getGreen(), 
						sldColor.getBlue(), (int) (opacity * 255));
			}
			else {
				fillColor = super.getPaintColor(context);
			}
			g.setPaint(fillColor);
			return true;
		}
		
		// no fill specified
		return false;
	}

	/**
	 * @see BoundingBoxMarker#applyStroke(Graphics2D, SelectableWaypoint)
	 */
	@Override
	protected boolean applyStroke(Graphics2D g, InstanceWaypoint context) {
		if (context.isSelected()) {
			// for selection use default
			return super.applyFill(g, context);
		}
		
		Style style = getStyle(context);
		
		//TODO honor rules!
		//TODO do all this only once (reset information on style change)
		
		// retrieve stroke
		Stroke stroke = null;
		// try line
		LineSymbolizer lineSymbolizer = SLD.lineSymbolizer(style);
		if (lineSymbolizer != null) {
			stroke = SLD.stroke(lineSymbolizer);
		}
		// try polygon
		if (stroke != null) {
			PolygonSymbolizer polygonSymbolizer = SLD.polySymbolizer(style);
			if (polygonSymbolizer != null) {
				stroke = SLD.stroke(polygonSymbolizer);
			}
		}
		// try point
		if (stroke == null) {
			PointSymbolizer pointSymbolizer = SLD.pointSymbolizer(style);
			if (pointSymbolizer != null) {
				stroke = SLD.stroke(pointSymbolizer);
			}
		}
		
		if (stroke != null) {
			//XXX is there any Geotools stroke to AWT stroke lib/code somewhere?!
			
			// stroke color
			Color sldColor = SLD.color(stroke);
			double opacity = SLD.opacity(stroke);
			if (Double.isNaN(opacity)) {
				// fall back to default opacity
				opacity = StyleHelper.DEFAULT_FILL_OPACITY;
			}
			Color strokeColor;
			if (sldColor != null) {
				strokeColor = new Color(sldColor.getRed(), sldColor.getGreen(), 
						sldColor.getBlue(), (int) (opacity * 255));
			}
			else {
				strokeColor = super.getBorderColor(context);
			}
			g.setColor(strokeColor);
			
			// stroke width
			int strokeWidth = SLD.width(stroke);
			if (strokeWidth == SLD.NOTFOUND) {
				// fall back to default width
				strokeWidth = StylePreferences.getDefaultWidth(); 
			}
			g.setStroke(new BasicStroke(strokeWidth));
			
			return true;
		}
		
		// fall-back to default
		return super.applyStroke(g, context);
	}
	
	/**
	 * Get the style for a given way-point.
	 * @param context the way-point
	 * @return the style
	 */
	private Style getStyle(InstanceWaypoint context) {
		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		
		InstanceReference ref = context.getValue();
		Instance instance = is.getInstance(ref);
		
		return ss.getStyle(instance.getDefinition());
	}

}
