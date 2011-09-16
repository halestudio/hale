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
package eu.esdihumboldt.cst.service.rename;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.cst.NameHelper;
import eu.esdihumboldt.cst.transformer.service.rename.FeatureSplitter;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;

/**
 * A Test for the {@link FeatureSplitter} used in the {@link RenameFeatureFunction}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureSplitterTest {
	
	List<Feature> testFeaturesPoints = new ArrayList<Feature>();
	List<Feature> testFeaturesLineStrings = new ArrayList<Feature>();
	List<Feature> testFeaturesPolygons = new ArrayList<Feature>();
	
	@Before
	public void init() {
		// populate test features List
		// multipoint features
		
		for (int i = 0; i < 10; i++) {
			this.testFeaturesPoints.add(this.getFeature(
					NameHelper.sourceNamespace, 
					NameHelper.sourceLocalname + "_MPoint",  //$NON-NLS-1$
					NameHelper.sourceLocalnamePropertyA, 
					MultiPoint.class));
		}
		
		// multilinestring features
		for (int i = 0; i < 10; i++) {
			this.testFeaturesLineStrings.add(this.getFeature(
					NameHelper.sourceNamespace, 
					NameHelper.sourceLocalname + "_MLineString",  //$NON-NLS-1$
					NameHelper.sourceLocalnamePropertyA, 
					MultiLineString.class));
		}
		
		// multipolygon features
		for (int i = 0; i < 10; i++) {
			this.testFeaturesPolygons.add(this.getFeature(
					NameHelper.sourceNamespace, 
					NameHelper.sourceLocalname + "_MPolygon",  //$NON-NLS-1$
					NameHelper.sourceLocalnamePropertyA, 
					MultiPolygon.class));
		}
	}
	
	/**
	 * Test method for {@link eu.esdihumboldt.cst.transformer.service.rename.FeatureSplitter#FeatureSplitter(String, String)}.
	 */
	@Test
	public void testFeatureSplitter() {
		new FeatureSplitter("test", "split:extractSubgeometry(Point)", "default_geometry"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		new FeatureSplitter("test", "split:extractSubgeometry(LineString)", "default_geometry"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		new FeatureSplitter("test", "split:extractSubgeometry(Polygon)", "default_geometry"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		try {
			new FeatureSplitter("test", "split:extractSubgeometry(Blablah)", "default_geometry"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch (Exception ex) {
			assertEquals(ex.getMessage(), "You can only extract Points, " + //$NON-NLS-1$
					"Polygons and LineStrings."); //$NON-NLS-1$
		}
	}

	/**
	 * Test method for {@link eu.esdihumboldt.cst.transformer.service.rename.FeatureSplitter#split(org.opengis.feature.Feature, org.opengis.feature.type.FeatureType)}.
	 */
	@Test
	public void testSplit() {
		List<Feature> features = null;
		FeatureSplitter fs = new FeatureSplitter(NameHelper.sourceLocalnamePropertyA, "split:extractSubgeometry(Polygon)", "default_geometry"); //$NON-NLS-1$ //$NON-NLS-2$
		for (Feature f : this.testFeaturesPolygons) {
			features = fs.split(f, this.getTargetFT(Polygon.class));
		}
		assertTrue(features.size() == 10);
		
		fs = new FeatureSplitter(NameHelper.sourceLocalnamePropertyA, "split:extractSubgeometry(LineString)", "default_geometry"); //$NON-NLS-1$ //$NON-NLS-2$
		for (Feature f : this.testFeaturesLineStrings) {
			features = fs.split(f, this.getTargetFT(LineString.class));
		}
		assertTrue(features.size() == 10);
		
		fs = new FeatureSplitter(NameHelper.sourceLocalnamePropertyA, "split:extractSubgeometry(Point)", "default_geometry"); //$NON-NLS-1$ //$NON-NLS-2$
		for (Feature f : this.testFeaturesPoints) {
			features = fs.split(f, this.getTargetFT(Point.class));
		}
		assertTrue(features.size() == 10);
		for (Feature f : this.testFeaturesLineStrings) {
			features = fs.split(f, this.getTargetFT(Point.class));
		}
		assertTrue(features.size() == 100);
		for (Feature f : this.testFeaturesPolygons) {
			features = fs.split(f, this.getTargetFT(Point.class));
		}
		assertTrue(features.size() == 90);
		
	}
	
	private FeatureType getTargetFT(Class<?> geometryClass) {
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(NameHelper.targetLocalname);
			ftbuilder.setNamespaceURI(NameHelper.targetNamespace);
			ftbuilder.add("default_geometry", geometryClass); //$NON-NLS-1$
			ftbuilder.setDefaultGeometry("default_geometry"); //$NON-NLS-1$
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}

	/**
	 * Create a Feature on the basis of a namespace, a a typename, and a set 
	 * of property names and types.
	 * @param featureTypeNamespace
	 * @param featureTypeName
	 * @param propertyNames
	 * @return
	 */
	private Feature getFeature(String featureTypeNamespace, 
			String featureTypeName, String geometryName, Class<?> geometryClass) {
	
		Feature f = null;
		try {
			SimpleFeatureType ft = null;
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			ftbuilder.add(geometryName, geometryClass);
			ftbuilder.setDefaultGeometry(geometryName);
			ft = ftbuilder.buildFeatureType();
			SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ft);
			f = builder.buildFeature(UUID.randomUUID().toString(), new Object[]{});
			
			Geometry geometry = null;
			GeometryFactory geomFactory = new GeometryFactory();
			if (geometryClass.equals(MultiPoint.class)) {
				Point[] points = new Point[10];
				for (int n = 0; n < points.length; n++) {
					points[n] = geomFactory.createPoint(
							new Coordinate(Math.random(), Math.random()));
				}
				geometry = geomFactory.createMultiPoint(points);
			}
			else if (geometryClass.equals(MultiLineString.class)) {
				LineString[] lineStrings = new LineString[10];
				for (int i = 0; i < lineStrings.length; i++) {
					Coordinate[] coordinates = new Coordinate[10];
					for (int n = 0; n < lineStrings.length; n++) {
						coordinates[n] = new Coordinate(
								Math.random(), Math.random());
					}
					lineStrings[i] = geomFactory.createLineString(coordinates);
				}
				geometry = geomFactory.createMultiLineString(lineStrings);
			}
			else if (geometryClass.equals(MultiPolygon.class)) {
				Polygon[] polygons = new Polygon[10];
				for (int i = 0; i < polygons.length; i++) {
					Coordinate[] coordinates = new Coordinate[10];
					for (int n = 0; n < polygons.length -1; n++) {
						coordinates[n] = new Coordinate(
								Math.random(), Math.random());
					}
					coordinates[coordinates.length -1] = coordinates[0];
					polygons[i] = geomFactory.createPolygon(
							geomFactory.createLinearRing(coordinates), null);
				}
				geometry = geomFactory.createMultiPolygon(polygons);
			}
			
			((SimpleFeature)f).setDefaultGeometry(geometry);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return f;
	}

}
