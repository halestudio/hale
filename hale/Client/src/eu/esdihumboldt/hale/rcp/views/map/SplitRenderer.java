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
import java.util.HashMap;
import java.util.Map;

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
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;

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
	
	private final String epsg31251wkt = 
		"PROJCS[\"MGI (Ferro)/AustriaGKWestZone\"," +
			"GEOGCS[\"MGI (Ferro)\"," +
				"DATUM[\"Militar-Geographische Institut (Ferro)\"," +
					"SPHEROID[\"Bessel 1841\",6377397.155,299.1528128," +
					"AUTHORITY[\"EPSG\",\"7004\"]],AUTHORITY[\"EPSG\",\"6805\"]]," +
				"PRIMEM[\"Ferro\",-17.666666666666668,AUTHORITY[\"EPSG\",\"8909\"]]," +
				"UNIT[\"degree\",0.017453292519943295]," +
				"AXIS[\"Geodetic latitude\",NORTH]," +
				"AXIS[\"Geodetic longitude\",EAST]," +
				"AUTHORITY[\"EPSG\",\"4805\"]]," +
			"PROJECTION[\"Transverse Mercator\"]," +
		"PARAMETER[\"central_meridian\",28.0]," +
		"PARAMETER[\"latitude_of_origin\",0.0]," +
		"PARAMETER[\"scale_factor\",1.0]," +
		"PARAMETER[\"false_easting\",0.0]," +
		"PARAMETER[\"false_northing\",-5000000.0]," +
		"UNIT[\"m\",1.0]," +
		"AXIS[\"Y\",EAST]," +
		"AXIS[\"X\",NORTH]," +
		"AUTHORITY[\"EPSG\",\"31251\"]]";
	
	private SplitStyle splitStyle = SplitStyle.VERTICAL;
	
	private StyleService styleService = null;
	
	private InstanceService instanceService = null;
	
	private StreamingRenderer renderer;
	
	private ReferencedEnvelope mapArea;
	
	private Rectangle paintArea;
	
	/**
	 * Default minimum constructor. References to {@link StyleService} and
	 * {@link InstanceService} are required since the renderer needs to update
	 * on changes.
	 * @param is the {@link InstanceService} from which to retrieve Features.
	 * @param ss the {@link StyleService} from which to retrieve Styles.
	 */
	public SplitRenderer(StyleService ss, InstanceService is){
		super();
		if (ss == null || is == null) {
			throw new NullPointerException("You have to provide valid " +
					"references to an InstanceService and a StyleService.");
		}
		this.styleService = ss;
		this.instanceService = is;
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
	public BufferedImage renderFeatures() {

		// prepare the passed Feature objects for rendering
		CoordinateReferenceSystem crs = this.determineCRS(
				this.instanceService.getFeatures(DatasetType.reference));
		this.mapArea = new ReferencedEnvelope(this.mapArea, crs);
		
		// set up MapContext.
		MapContext sourceMapContext = this.buildMapContext(crs, DatasetType.reference);
		MapContext targetMapContext = this.buildMapContext(crs, DatasetType.transformed);

        // perform actual rendering.
        BufferedImage image = new BufferedImage(paintArea.width, paintArea.height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(126, 166, 210));
        graphics.fillRect(0, 0, paintArea.width, paintArea.height);
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawRect(0, 0, paintArea.width - 1, paintArea.height - 1);
        if (this.splitStyle.equals(SplitStyle.VERTICAL)) {
        	this.renderVerticalSplit(graphics, sourceMapContext, targetMapContext);
        }
        else {
            this.renderer.setContext(sourceMapContext);
        	this.renderer.paint((Graphics2D) graphics, paintArea, mapArea);
        }

        return image;
	}
	
	/**
	 * @param crs the {@link CoordinateReferenceSystem} to use.
	 * @param type the {@link DatasetType} to render.
	 * @return a {@link MapContext} with the given CRS and the 
	 * {@link FeatureCollection} identified by the given {@link DatasetType}.
	 */
	private MapContext buildMapContext(CoordinateReferenceSystem crs, DatasetType type) {
		MapContext mc = new DefaultMapContext(crs); 
		FeatureCollection<?, ?> fc = this.instanceService.getFeatures(type);
		if (fc != null) {
			_log.info("features size: " + fc.size());
			_log.info("features bounds: " + fc.getBounds());
			Style style = this.styleService.getStyle(fc.getSchema());
			mc.addLayer(
	        		(FeatureCollection<SimpleFeatureType, SimpleFeature>) fc, style);
		}
		return mc;
	}
	
	/**
	 * This method executes the rendering of reference and transformed data in 
	 * two panels, one taking the left half of the map view and the other 
	 * taking the right half.
	 * 
	 * @param graphics the {@link Graphics2D} to draw the map on.
	 * @param sourceMapContext the reference data
	 * @param targetMapContext the transformed data
	 */
	private void renderVerticalSplit(Graphics2D graphics, 
			MapContext sourceMapContext, MapContext targetMapContext) {
		if (targetMapContext.getLayerCount() > 0) {
			this.mapArea = targetMapContext.getLayer(0).getBounds();
		}
		
		Rectangle leftHalfArea = new Rectangle();
    	leftHalfArea.x = paintArea.x;
    	leftHalfArea.y = paintArea.y;
    	leftHalfArea.height = paintArea.height;
    	leftHalfArea.width = paintArea.width / 2;
    	Graphics2D graphics_left = (Graphics2D) graphics.create(
    			0, 0, leftHalfArea.width, leftHalfArea.height);
    	
    	Rectangle rightHalfArea = new Rectangle();
    	rightHalfArea.x = paintArea.x + paintArea.width / 2;
    	rightHalfArea.y = paintArea.y;
    	rightHalfArea.height = paintArea.height;
    	rightHalfArea.width = paintArea.width / 2;
    	Graphics2D graphics_right = (Graphics2D) graphics.create(
    			rightHalfArea.x, rightHalfArea.y, rightHalfArea.width, rightHalfArea.height);
    	
    	ReferencedEnvelope leftEnvelope = new ReferencedEnvelope(
    			mapArea.getMinX(), 
    			mapArea.getMinX() + mapArea.getWidth() / 2, 
    			mapArea.getMinY(), 
    			mapArea.getMaxY(), 
    			mapArea.getCoordinateReferenceSystem());
    	
    	ReferencedEnvelope rightEnvelope = new ReferencedEnvelope(
    			mapArea.getMinX() + mapArea.getWidth() / 2, 
    			mapArea.getMaxX(), 
    			mapArea.getMinY(), 
    			mapArea.getMaxY(), 
    			mapArea.getCoordinateReferenceSystem());
    	
    	this.configureRenderer();
        this.renderer.setContext(sourceMapContext);
    	this.renderer.paint(graphics_left, leftHalfArea, leftEnvelope);
    	
    	this.configureRenderer();
        this.renderer.setContext(targetMapContext);
    	this.renderer.paint(graphics_right, leftHalfArea, rightEnvelope);
    	
    	graphics.drawLine(paintArea.width / 2, 0, paintArea.width / 2, paintArea.height);
	}
	
	private CoordinateReferenceSystem determineCRS(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
		// try the instance data first.
		Feature f = fc.features().next();
		CoordinateReferenceSystem crs = 
			f.getDefaultGeometryProperty().getDescriptor().getCoordinateReferenceSystem();
		
		// then check the schema.
		if (crs == null) {
			crs = fc.getSchema().getCoordinateReferenceSystem();
		}
		
		// if none is available, use a default.
		if (crs == null) {
			try {
				_log.warn("Retrieving the CRS from the schema and the "
						+ "instance data failed; defaulting to EPSG:31251."); 
				crs = CRS.parseWKT(epsg31251wkt);
			} catch (Exception e) {
				_log.error("Decoding the default CRS failed, no accurate " +
						"projection will be shown", e);
			}
		}
		return crs;
	}
	

	@SuppressWarnings("unchecked")
	private void configureRenderer() {
		this.renderer = new StreamingRenderer();
		Map hints = new HashMap();
        if (renderer instanceof StreamingRenderer) {
            hints = renderer.getRendererHints();
            if (hints == null) {
                hints = new HashMap();
            }
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
	
	// getters / setters .......................................................
	
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
	

	public enum SplitStyle {
		NONE,
		HORIZONTAL,
		VERTICAL,
		DIAGONAL_UP,
		DIAGONAL_DOWN,
		OVERLAY
	}
	
	protected class CRSTransformer {
		
		private MathTransform math;
		
		public FeatureCollection<?, ?> transformToWGS84(
				CoordinateReferenceSystem crs,
				FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
			if (crs != null) {
				_log.debug("Bounds before Tx: " + fc.getBounds().toString());
				_log.debug("Transforming FeatureCollection to WGS84 for rendering");
				try {
					CoordinateReferenceSystem crsTarget = CRS.decode("EPSG:4326");
					this.math = CRS.findMathTransform(crs, crsTarget, true);
					_log.debug("Received MathTransform.");
					fc = this.transformSRS((FeatureCollection<?, SimpleFeature>) fc);
					_log.debug("Performed transformation of " + fc.size()
							+ " features.");
					crs = crsTarget;
				} catch (Exception e) {
					_log.error("Could not perform required CRS transformation",
							e);
				}
				_log.debug("Bounds after Tx: " + fc.getBounds().toString());
			}
			return fc;
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
	}

}
