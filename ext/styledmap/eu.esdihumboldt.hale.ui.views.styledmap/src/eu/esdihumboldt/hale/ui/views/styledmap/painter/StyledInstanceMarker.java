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

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.ui.PlatformUI;
import org.geotools.geometry.jts.GeomCollectionIterator;
import org.geotools.geometry.jts.LiteShape2;
import org.geotools.renderer.lite.StyledShapePainter;
import org.geotools.renderer.style.GraphicStyle2D;
import org.geotools.renderer.style.MarkStyle2D;
import org.geotools.renderer.style.SLDStyleFactory;
import org.geotools.renderer.style.Style2D;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;
import org.geotools.util.Range;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

import de.fhg.igd.mapviewer.geom.Point3D;
import de.fhg.igd.mapviewer.marker.BoundingBoxMarker;
import de.fhg.igd.mapviewer.marker.area.Area;
import de.fhg.igd.mapviewer.marker.area.BoxArea;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.ui.common.service.style.StyleService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.style.StyleHelper;
import eu.esdihumboldt.hale.ui.style.service.internal.StylePreferences;
import eu.esdihumboldt.hale.ui.views.styledmap.util.CRSConverter;

/**
 * Instance marker support styles provided through the {@link StyleService}.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class StyledInstanceMarker extends InstanceMarker {

	private final AtomicBoolean styleInitialized = new AtomicBoolean(false);
	private volatile Color styleFillColor;
	private volatile Color styleStrokeColor;
	private volatile java.awt.Stroke styleStroke;
	private boolean hasFill = true;
	private PointSymbolizer pointSymbolizer;
	private static final StyleBuilder styleBuilder = new StyleBuilder();

	/**
	 * The way-point the marker is associated with
	 */
	private final InstanceWaypoint wp;

	/**
	 * Create a instance marker supporting styles.
	 * 
	 * @param wp the way-point the marker is associated with
	 */
	public StyledInstanceMarker(InstanceWaypoint wp) {
		this.wp = wp;
	}

	/**
	 * Initialize the style information.
	 * 
	 * @param context the context
	 */
	private synchronized void initStyle(InstanceWaypoint context) {
		if (!styleInitialized.compareAndSet(false, true)) {
			// already initialized
			return;
		}

		// check if there is a Rule from the Rulestyle-Page and apply to the
		// instancemarker on the map
		// performs a special task if the found symbolizer is a point symbolizer
		Rule honoredRule = honorRules(context);
		pointSymbolizer = null;
		for (Symbolizer sym : honoredRule.symbolizers()) {
			if (sym instanceof PointSymbolizer) {
				pointSymbolizer = (PointSymbolizer) sym;
				break;
			}
		}

		fillStyle(honoredRule, context);
		strokeStyle(honoredRule, context);
	}

	/**
	 * Checks if there is a rule for the certain Instance
	 * 
	 * @param context the context
	 * @return a certain style rule for the instance, else-rule if nothing found
	 *         or null if there is no else-rule
	 */
	private Rule honorRules(InstanceWaypoint context) {
		Style style = getStyle(context);
		Rule[] rules = SLD.rules(style);

		// do rules exist?

		if (rules == null || rules.length == 0) {
			return null;
		}

		// sort the elserules at the end
		if (rules.length > 1) {
			rules = sortRules(rules);
		}

		// if rule exists
		InstanceReference ir = context.getValue();
		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		boolean instanceInitialized = false;
		Instance inst = null; // instance variable - only initialize if needed

		for (int i = 0; i < rules.length; i++) {

			if (rules[i].getFilter() != null) {
				if (!instanceInitialized) {
					// initialize instance (as it is needed for the filter)
					inst = is.getInstance(ir);
					instanceInitialized = true;
				}
				if (rules[i].getFilter().evaluate(inst)) {
					return rules[i];
				}
			}

			// if a rule exist without a filter and without being an
			// else-filter,
			// the found rule applies to all types
			else {
				if (!rules[i].isElseFilter()) {
					return rules[i];
				}
			}
		}

		// if there is no appropriate rule, check if there is an else-rule
		for (int i = 0; i < rules.length; i++) {
			if (rules[i].isElseFilter()) {
				return rules[i];
			}
		}

		// return null if no rule was found
		return null;
	}

	/**
	 * Sorts an array of rules, so the else-filter-rules are at the end
	 * 
	 * @param rules an array of Rules
	 * @return a new array of Rules with sorted elements
	 */
	private Rule[] sortRules(Rule[] rules) {

		ArrayList<Rule> temp = new ArrayList<Rule>();

		for (int i = 0; i < rules.length; i++) {

			if (!rules[i].isElseFilter()) {
				temp.add(rules[i]);
			}
		}

		for (int i = 0; i < rules.length; i++) {

			if (rules[i].isElseFilter()) {
				temp.add(rules[i]);
			}

		}

		Rule[] newRules = new Rule[temp.size()];
		return temp.toArray(newRules);
	}

	/**
	 * Retrieves the fill for the map marker.
	 * 
	 * @param rule a certain rule to apply, may be <code>null</code>
	 * @param context the InstanceWayPoint, which gets marked
	 */
	private synchronized void fillStyle(Rule rule, InstanceWaypoint context) {
		// retrieve fill
		Fill fill = null;

		// try the Symbolizers from the Rule
		for (int i = 0; rule != null && fill == null && i < rule.getSymbolizers().length; i++) {
			if (rule.getSymbolizers()[i] instanceof PolygonSymbolizer) {
				fill = SLD.fill((PolygonSymbolizer) rule.getSymbolizers()[i]);
			}
			else if (rule.getSymbolizers()[i] instanceof PointSymbolizer) {
				fill = SLD.fill((PointSymbolizer) rule.getSymbolizers()[i]);
			}
		}

		// if we have a fill now
		if (fill != null) {
			Color sldColor = SLD.color(fill);
			double opacity = SLD.opacity(fill);
			if (sldColor != null) {
				styleFillColor = new Color(sldColor.getRed(), sldColor.getGreen(),
						sldColor.getBlue(), (int) (opacity * 255));
			}
			else {
				styleFillColor = super.getPaintColor(context);
			}
			hasFill = true;
		}
		// if we still don't have a fill
		else {
			styleFillColor = null;
			hasFill = false;
		}
	}

	/**
	 * retrieves the stroke for the map marker
	 * 
	 * @param rule a certain rule to apply, maybe null
	 * @param context the InstanceWayPoint, wich gets marked
	 */
	private synchronized void strokeStyle(Rule rule, InstanceWaypoint context) {

		// retrieve stroke
		Stroke stroke = null;

		// try the Symbolizers from the Rule

		for (int i = 0; rule != null && stroke == null && i < rule.getSymbolizers().length; i++) {
			if (rule.getSymbolizers()[i] instanceof LineSymbolizer) {
				stroke = SLD.stroke((LineSymbolizer) rule.getSymbolizers()[i]);
			}
			else if (rule.getSymbolizers()[i] instanceof PolygonSymbolizer) {
				stroke = SLD.stroke((PolygonSymbolizer) rule.getSymbolizers()[i]);
			}
			else if (rule.getSymbolizers()[i] instanceof PointSymbolizer) {
				stroke = SLD.stroke((PointSymbolizer) rule.getSymbolizers()[i]);
			}
		}

		// if we have a stroke now
		if (stroke != null) {
			// XXX is there any Geotools stroke to AWT stroke lib/code
			// somewhere?!
			// XXX have a look at the renderer code (StreamingRenderer)

			// stroke color
			Color sldColor = SLD.color(stroke);
			double opacity = SLD.opacity(stroke);
			if (Double.isNaN(opacity)) {
				// fall back to default opacity
				opacity = StyleHelper.DEFAULT_FILL_OPACITY;
			}
			if (sldColor != null) {
				styleStrokeColor = new Color(sldColor.getRed(), sldColor.getGreen(),
						sldColor.getBlue(), (int) (opacity * 255));
			}
			else {
				styleStrokeColor = super.getBorderColor(context);
			}

			// stroke width
			int strokeWidth = SLD.width(stroke);
			if (strokeWidth == SLD.NOTFOUND) {
				// fall back to default width
				strokeWidth = StylePreferences.getDefaultWidth();
			}
			styleStroke = new BasicStroke(strokeWidth);
		}
		else {
			styleStroke = null;
			styleStrokeColor = null;
		}
	}

	/**
	 * Reset the marker style
	 */
	public void resetStyle() {
		styleInitialized.set(false);
		areaReset();
	}

	/**
	 * @see InstanceMarker#getPaintColor(InstanceWaypoint)
	 */
	@Override
	protected Color getPaintColor(InstanceWaypoint context) {
		initStyle(context);

		if (styleFillColor == null || context.isSelected()) {
			// for selection don't use style
			return super.getPaintColor(context);
		}

		return styleFillColor;
	}

	/**
	 * @see InstanceMarker#getBorderColor(InstanceWaypoint)
	 */
	@Override
	protected Color getBorderColor(InstanceWaypoint context) {
		initStyle(context);

		if (styleStrokeColor == null || context.isSelected()) {
			return super.getBorderColor(context);
		}

		return styleStrokeColor;
	}

	/**
	 * Get the stroke for drawing lines.
	 * 
	 * @param context the context
	 * @return the stroke
	 */
	@Override
	protected java.awt.Stroke getLineStroke(InstanceWaypoint context) {
		initStyle(context);

		if (styleStroke != null && !context.isSelected()) {
			return styleStroke;
		}
		else {
			return super.getLineStroke(context);
		}
	}

	/**
	 * @see BoundingBoxMarker#applyFill(Graphics2D, SelectableWaypoint)
	 */
	@Override
	protected boolean applyFill(Graphics2D g, InstanceWaypoint context) {
		initStyle(context);

		if (hasFill) {
			g.setPaint(getPaintColor(context));
			return true;
		}

		return false;
	}

	/**
	 * @see BoundingBoxMarker#applyStroke(Graphics2D, SelectableWaypoint)
	 */
	@Override
	protected boolean applyStroke(Graphics2D g, InstanceWaypoint context) {
		initStyle(context);

		return super.applyStroke(g, context);
	}

	/**
	 * Get the style for a given way-point.
	 * 
	 * @param context the way-point
	 * @return the style
	 */
	private Style getStyle(InstanceWaypoint context) {
		StyleService ss = PlatformUI.getWorkbench().getService(StyleService.class);
		InstanceReference ref = context.getValue();
		return ss.getStyle(context.getInstanceType(), ref.getDataSet());
	}

	/**
	 * @see BoundingBoxMarker#isToSmall(int, int, int)
	 */
	@Override
	protected boolean isToSmall(int width, int height, int zoom) {
		if (representsSinglePoint()) {
			// disable fallback marker, as paintPoint would never be called
			return false;
		}
		return super.isToSmall(width, height, zoom);
	}

	/**
	 * Determines if the associated way-point represents a single point.
	 * 
	 * @return if the way-point represents a single point
	 */
	private boolean representsSinglePoint() {
		boolean pointFound = false;
		for (GeometryProperty<?> geom : wp.getGeometries()) {
			if (geom.getGeometry() != null && geom.getCRSDefinition() != null) {
				// valid geometry
				if (pointFound) {
					// a point was already found before
					return false;
				}

				if (geom.getGeometry() instanceof Point || (geom.getGeometry() instanceof MultiPoint
						&& geom.getGeometry().getNumGeometries() == 1)) {
					// a single point found
					pointFound = true;
				}
				else {
					// another geometry found
					return false;
				}
			}
		}
		return pointFound;
	}

	/**
	 * @see InstanceMarker#paintPoint(Point, Graphics2D, CRSDefinition,
	 *      InstanceWaypoint, PixelConverter, int, CoordinateReferenceSystem,
	 *      boolean)
	 */
	@Override
	protected Area paintPoint(Point geometry, Graphics2D g, CRSDefinition crsDefinition,
			InstanceWaypoint context, PixelConverter converter, int zoom,
			CoordinateReferenceSystem mapCRS, boolean calculateArea) {
		initStyle(context);
		Area area = null;
		try {
			if (pointSymbolizer == null || (SLD.mark(pointSymbolizer) == null
					&& pointSymbolizer.getGraphic().graphicalSymbols().isEmpty())) {
				// only marks supported for now
				// if there is no specialized PointSymbolizer, fall back to a
				// generic
				return super.paintFallback(g, context, converter, zoom, null, calculateArea);
			}

			// get CRS converter
			CRSConverter conv = CRSConverter.getConverter(crsDefinition.getCRS(), mapCRS);

			// manually convert to map CRS
			Point3D mapPoint = conv.convert(geometry.getX(), geometry.getY(), 0);

			GeoPosition pos = new GeoPosition(mapPoint.getX(), mapPoint.getY(),
					converter.getMapEpsg());

			// determine pixel coordinates
			Point2D point = converter.geoToPixel(pos, zoom);
			Coordinate coordinate = new Coordinate(point.getX(), point.getY());
			Point newPoint = geometry.getFactory().createPoint(coordinate);

			// create a LiteShape and instantiate the Painter and the
			// StyleFactory
			LiteShape2 lites = new LiteShape2(newPoint, null, null, false);
			StyledShapePainter ssp = new StyledShapePainter();
			SLDStyleFactory styleFactory = new SLDStyleFactory();

			Range<Double> range = new Range<Double>(Double.class, 0.5, 1.5);

			PointSymbolizer pointS;
			// is the Waypoint selected?
			if (context.isSelected()) {
				// switch to the SelectionSymbolizer
				pointS = getSelectionSymbolizer(pointSymbolizer);
			}
			// use the specific PointSymbolizer
			else
				pointS = pointSymbolizer;

			// Create the Style2D object for painting with the use of a
			// DummyFeature wich extends SimpleFeatures
			// because Geotools can only work with that
			DummyFeature dummy = new DummyFeature();
			Style2D style2d = styleFactory.createStyle(dummy, pointS, range);
			// create the area object of the painted image for further use
			area = getArea(point, style2d, lites);
			// actually paint
			ssp.paint(g, lites, style2d, 1);

			// used to draw selection if a graphic style is used (external
			// graphic)
			if (context.isSelected() && style2d instanceof GraphicStyle2D) {
				GraphicStyle2D gs2d = (GraphicStyle2D) style2d;

				// get minX and minY for the drawn rectangle arround the image
				int minX = (int) point.getX() - gs2d.getImage().getWidth() / 2;
				int minY = (int) point.getY() - gs2d.getImage().getHeight() / 2;

				// apply the specification of the selection rectangle
				applyFill(g, context);
				applyStroke(g, context);
				// draw the selection rectangle
				g.drawRect(minX - 1, minY - 1, gs2d.getImage().getWidth() + 1,
						gs2d.getImage().getHeight() + 1);

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return area;

	}

	/**
	 * Creates a certain Point Symbolizer if the waypoint is selected
	 * 
	 * @param symbolizer a symbolizer which is used to create the selection
	 *            symbolizer
	 * @return returns the selection symbolizer
	 */
	private PointSymbolizer getSelectionSymbolizer(PointSymbolizer symbolizer) {
		// XXX only works with marks and external graphics right now
		Mark mark = SLD.mark(symbolizer);
		if (mark != null) {
			Mark mutiMark = styleBuilder.createMark(mark.getWellKnownName(),
					styleBuilder.createFill(StylePreferences.getSelectionColor(),
							StyleHelper.DEFAULT_FILL_OPACITY),
					styleBuilder.createStroke(StylePreferences.getSelectionColor(),
							StylePreferences.getSelectionWidth()));

			// create new symbolizer
			return styleBuilder
					.createPointSymbolizer(styleBuilder.createGraphic(null, mutiMark, null));
		}

		else {
			return symbolizer;

		}
	}

	/**
	 * Returns the area of the drawn point.
	 * 
	 * @param point the point
	 * @param style the Style2D object
	 * @param shape the Light Shape
	 * @return the area, which should equal the space of the drawn object
	 */
	private Area getArea(Point2D point, Style2D style, LiteShape2 shape) {
		// if it is a mark style
		if (style instanceof MarkStyle2D) {
			PathIterator citer = getPathIterator(shape);

			float[] coords = new float[2];
			MarkStyle2D ms2d = (MarkStyle2D) style;

			Shape transformedShape;
			while (!(citer.isDone())) {
				citer.currentSegment(coords);
				transformedShape = ms2d.getTransformedShape(coords[0], coords[1]);
				if (transformedShape != null) {
					java.awt.geom.Area areatemp = new java.awt.geom.Area(transformedShape);
					Rectangle rec = areatemp.getBounds();
					AdvancedBoxArea area = new AdvancedBoxArea(areatemp, rec.x, rec.y,
							rec.x + rec.width, rec.y + rec.height);

					return area;
				}
			}
		}
		// if it is an external graphic style
		else if (style instanceof GraphicStyle2D) {
			GraphicStyle2D gs2d = (GraphicStyle2D) style;

			int minX = (int) point.getX() - gs2d.getImage().getWidth() / 2;
			int minY = (int) point.getY() - gs2d.getImage().getHeight() / 2;
			int maxX = (int) point.getX() + gs2d.getImage().getWidth() / 2;
			int maxY = (int) point.getX() + gs2d.getImage().getHeight() / 2;

			BoxArea area = new BoxArea(minX, minY, maxX, maxY);
			return area;
		}

		return null;
	}

	/**
	 * Returns a path iterator.
	 * 
	 * @param shape a shape to determine the iterator
	 * @return the path iterator
	 */
	private PathIterator getPathIterator(final LiteShape2 shape) {
		// DJB: changed this to handle multi* geometries and line and
		// polygon geometries better
		GeometryCollection gc;
		if (shape.getGeometry() instanceof GeometryCollection)
			gc = (GeometryCollection) shape.getGeometry();
		else {
			Geometry[] gs = new Geometry[1];
			gs[0] = shape.getGeometry();
			// make a Point,Line, or Poly into a GC
			gc = shape.getGeometry().getFactory().createGeometryCollection(gs);
		}
		AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
		GeomCollectionIterator citer = new GeomCollectionIterator(gc, IDENTITY_TRANSFORM, false,
				1.0);
		return citer;
	}

}
