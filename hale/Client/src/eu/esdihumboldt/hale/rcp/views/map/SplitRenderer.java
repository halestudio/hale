/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.projection.ProjectionException;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.opengis.feature.Feature;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.StyleService;

/**
 * This renderer renders the instances provided by the {@link InstanceService}
 * according to the defined styles. It supports several splitting and overlay 
 * styles to make it possible to visually compare data before and after 
 * transformation.
 * 
 * For efficient updating during the transformation definition, the renderer 
 * actually maintains a subrenderer per FeatureType.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SplitRenderer {
	
	private static Logger _log = Logger.getLogger(SplitRenderer.class);
	
	private final String epsg31251wkt = "PROJCS[\"MGI(Ferro)/AustriaGKWestZone\",GEOGCS[\"MGI(Ferro)\",DATUM[\"Militar_Geographische_Institut_Ferro\",SPHEROID[\"Bessel1841\",6377397.155,299.1528128,AUTHORITY[\"EPSG\",\"7004\"]],AUTHORITY[\"EPSG\",\"6805\"]],PRIMEM[\"Ferro\",-17.66666666666667,AUTHORITY[\"EPSG\",\"8909\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4805\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",28],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",-5000000],AUTHORITY[\"EPSG\",\"31251\"],AXIS[\"Y\",EAST],AXIS[\"X\",NORTH]]";
	
	private SplitStyle splitStyle = SplitStyle.VERTICAL;
	
	private StyleService styleService = null;
	
	private StreamingRenderer renderer;
	
	private ReferencedEnvelope mapArea;
	
	private Rectangle paintArea;
	
	private MathTransform math;
	
	/**
	 * Default minimum constructor. References to {@link StyleService} and
	 * {@link InstanceService} are required since the renderer needs to update
	 * on changes.
	 * @param is
	 * @param ss
	 */
	public SplitRenderer(StyleService ss){
		super();
		this.styleService = ss;
		this.paintArea = new Rectangle();
		this.configureRenderer();
	}
	
	/**
	 * Renders the given Set of {@link Feature} objects. If there are Features
	 * already managed by the renderer that have the same {@link FeatureType},
	 * as well as the same ID, they will be removed.
	 * 
	 * @param ft
	 * @param features
	 */
	public BufferedImage renderFeatures(FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		if (fc == null) {
			_log.warn("No FeatureCollection object was provided to the " +
					"renderer.");
			return null;
		}
		// prepare the passed Feature objects for rendering
		Feature f = fc.features().next();
		CoordinateReferenceSystem crs = 
			f.getDefaultGeometryProperty().getDescriptor().getCoordinateReferenceSystem();
		
		if (crs == null) {
			crs = fc.getSchema().getCoordinateReferenceSystem();
		}
		
		if (crs == null) {
			try {
				_log.warn("Retrieving the CRS from the schema and the "
						+ "instance data failed; defaulting to EPSG:31251."); 
				crs = CRS.parseWKT(epsg31251wkt);
//				crs = CRS.decode("EPSG:31254");
				_log.debug("1st Axis Alias: " + crs.getCoordinateSystem().getAxis(0).getName().getCode());
				_log.debug("1st Axis Direction name: " + crs.getCoordinateSystem().getAxis(0).getDirection().name());
				_log.debug("2nd Axis Alias: " + crs.getCoordinateSystem().getAxis(1).getName().getCode());
				_log.debug("2nd Axis Direction name: " + crs.getCoordinateSystem().getAxis(1).getDirection().name());
			} catch (Exception e) {
				_log.error("Decoding the default CRS failed, no accurate " +
						"projection will be shown", e);
			}
		}
		
		// transform geometry if necessary.
		_log.debug("Bounds before Tx: " + fc.getBounds().toString());
		if (crs != null) {
			_log.info("Transforming FeatureCollection to WGS84 for rendering");
			try {
				CoordinateReferenceSystem crsTarget = CRS.decode("EPSG:4326");
				this.math = CRS.findMathTransform(crs, crsTarget);
				_log.debug("Received MathTransform.");
				fc = this.transformSRS((FeatureCollection<?, SimpleFeature>) fc);
				_log.debug("Performed transformation of " + fc.size() + " features.");
//				crs = crsTarget;
			} catch (Exception e) {
				_log.error("Could not perform required CRS transformation", e);
			}
		}
		_log.debug("Bounds after Tx: " + fc.getBounds().toString());
		
		// set up MapContext.
		MapContext mapContext = new DefaultMapContext(crs);
		Style style = this.styleService.getStyle(fc.getSchema());
        mapContext.addLayer(
        		(FeatureCollection<SimpleFeatureType, SimpleFeature>) fc, style);
        this.renderer.setContext(mapContext);

        // perform actual rendering.
        BufferedImage image = new BufferedImage(paintArea.width, paintArea.height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(126, 166, 210));
        graphics.fillRect(0, 0, paintArea.width, paintArea.height);
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawRect(0, 0, paintArea.width - 1, paintArea.height - 1);
        this.renderer.paint((Graphics2D) graphics, paintArea, mapArea);
       
        return image;
	}
	
	private void configureRenderer() {
		this.renderer = new StreamingRenderer();
		Map hints = new HashMap();
        if (renderer instanceof StreamingRenderer) {
            hints = renderer.getRendererHints();
            if (hints == null) {
                hints = new HashMap();
            }
            /*if (hints.containsKey(StreamingRenderer.LABEL_CACHE_KEY)) {
                labelCache = (LabelCache) hints
                        .get(StreamingRenderer.LABEL_CACHE_KEY);
            } else {
                hints.put(StreamingRenderer.LABEL_CACHE_KEY, labelCache);
            }*/
            renderer.setRendererHints(hints);
        }
        hints.put("memoryPreloadingEnabled", Boolean.FALSE);
        this.renderer.setRendererHints(hints);
    }
	
	/**
	 * Remove all Features of the given {@link FeatureType} from the renderer.
	 * @param ft
	 */
	public void clearFeatures(FeatureType ft) {
		
	}
	
    public ReferencedEnvelope getMapArea() {
        return this.mapArea;
    }

    public void setMapArea(ReferencedEnvelope mapArea) {
        this.mapArea = mapArea;
    }
	
	public void setSplitStyle(SplitStyle spst) {
		this.splitStyle = spst;
	}
	
	public SplitStyle getSplitStyle() {
		return this.splitStyle;
	}
	
	/**
	 * @return the paintArea
	 */
	public Rectangle getPaintArea() {
		return paintArea;
	}

	/**
	 * @param paintArea the paintArea to set
	 */
	public void setPaintArea(Rectangle paintArea) {
		this.paintArea = paintArea;
	}
	
	/**
	 * @param fc the {@link FeatureCollection} whose Feature's coordinates are 
	 * to be transformed.
	 * @return a new {@link FeatureCollection} with modified coordinates.
	 */
	private FeatureCollection<?, ?> transformSRS(FeatureCollection<?, SimpleFeature> fc) {
		int accumulated_length = 0;
		_log.debug("Starting transformation of FeatureCollection with " 
				+ fc.size() + " Features.");
		FeatureCollection<?, SimpleFeature> result = FeatureCollections.newCollection();
		FeatureIterator<SimpleFeature> fi = fc.features();
		GeometryFactory gf = new GeometryFactory();
		while (fi.hasNext()) {
			SimpleFeature f = fi.next();
			Coordinate[] coordinates = ((Geometry)f.getDefaultGeometry()).getCoordinates();
			accumulated_length += coordinates.length;
			Geometry new_geometry = null;
			if (f.getDefaultGeometry() instanceof Point) {
				new_geometry = gf.createPoint(this.convert(coordinates[0]));
			}
			else if (f.getDefaultGeometry() instanceof MultiPoint) {
				new_geometry = gf.createMultiPoint(this.convert(coordinates));
			}
			else if (f.getDefaultGeometry() instanceof LineString) {
				new_geometry = gf.createLineString(this.convert(coordinates));
			}
			else if (f.getDefaultGeometry() instanceof Polygon) {
				new_geometry = gf.createPolygon(
						gf.createLinearRing(this.convert(coordinates)), null);
			} 
			else if (f.getDefaultGeometry() instanceof MultiPolygon) {
				MultiPolygon mp = (MultiPolygon) f.getDefaultGeometry();
				Polygon[] new_polys = new Polygon[mp.getNumGeometries()];
				for (int n = 0; n < mp.getNumGeometries(); n++) {
					Geometry g = mp.getGeometryN(n);
					new_polys[n] = gf.createPolygon(
							gf.createLinearRing(this.convert(g.getCoordinates())), null);
				}
				new_geometry = gf.createMultiPolygon(new_polys);
			}
			else if (f.getDefaultGeometry() instanceof MultiLineString) {
				MultiLineString mp = (MultiLineString) f.getDefaultGeometry();
				LineString[] new_polys = new LineString[mp.getNumGeometries()];
				for (int n = 0; n < mp.getNumGeometries(); n++) {
					Geometry g = mp.getGeometryN(n);
					new_polys[n] = gf.createLineString(this.convert(g.getCoordinates()));
				}
				new_geometry = gf.createMultiLineString(new_polys);
			}
			else {
				_log.warn("Encountered an unsupported Geometry Type (" 
						+ f.getDefaultGeometryProperty().getClass() 
						+ "), discarding Geometry.");
			}
			try {
				f.setDefaultGeometry(new_geometry);
			} catch (IllegalAttributeException e) {
				throw new RuntimeException("An exception occured trying to " +
						"set the transformed geometry for a feature.", e);
			}
			result.add(f);
		}
		
		_log.debug("Completed Transformation of FeatureCollection with " 
				+ accumulated_length + " coordinates.");
		return result;
	}
	
	/**
	 * Wrapper method to work on an Array or {@link Coordinate}s.
	 * @param coordinates the {@link Coordinate}s array to transform.
	 * @return the transformed {@link Coordinate}s as an array.
	 */
	private Coordinate[] convert(Coordinate[] coordinates) {
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = this.convert(coordinates[i]);
		}
		return coordinates;
	}
	
	/**
	 * @see de.fhg.igd.CityServer3D.converterLayer.srs.common.SRSConverter#convert(double, double, double)
	 * This method converts the given coordinates and returns them as a Point3D.
	 * @return The converted coordinates as a Point3D.
	 */
	private Coordinate convert(Coordinate c) {
		DirectPosition convertedPosition = null;
		try {
			convertedPosition = this.math.transform(new DirectPosition2D(c.x, c.y), null);
		} catch (ProjectionException pex) {
			_log.warn(pex);
			convertedPosition = new GeneralDirectPosition(0d, 0d);
		} catch (TransformException e) {
			throw new RuntimeException(e);
		}
		return new Coordinate(convertedPosition.getOrdinate(0), convertedPosition.getOrdinate(1), c.z);
	}

	public enum SplitStyle {
		NONE,
		HORIZONTAL,
		VERTICAL,
		DIAGONAL_UP,
		DIAGONAL_DOWN,
		OVERLAY
	}

}
