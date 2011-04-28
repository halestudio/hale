/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.writer.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureImpl;
import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.identity.Identifier;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.transformer.service.CstServiceFactory;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.gmlparser.CstFeatureCollection;
import eu.esdihumboldt.hale.gmlparser.GmlHelper;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryConverterRegistry;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryConverterRegistry.ConversionLadder;
import eu.esdihumboldt.hale.io.xml.validator.Report;
import eu.esdihumboldt.hale.io.xml.validator.ValidatorFactory;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;
import eu.esdihumboldt.tools.FeatureInspector;

/**
 * Tests for {@link StreamGmlWriter}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@SuppressWarnings("restriction")
public class StreamGmlWriterTest {
	
	/**
	 * If temporary files shall be deleted
	 */
	private static final boolean DEL_TEMP_FILES = true;

	private static final String DEF_SRS_NAME = "EPSG:31467"; //$NON-NLS-1$
	
	/**
	 * The geometry factory
	 */
	private final GeometryFactory geomFactory = new GeometryFactory();

	/**
	 * Test writing a simple feature from a simple schema (Watercourses VA)
	 * 
	 * @throws Exception if any error occurs 
	 */
	@Test
	public void testFillWrite_WatercourseVA() throws Exception {
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		
		// create the geometry
//		MultiLineString mls = geomFactory.createMultiLineString(
//				new LineString[]{createLineString(0.0), createLineString(1.0),
//						createLineString(2.0)});
		//XXX for some reason the MultiLineString is converted to a LineString when set a value -> so we are using a LineString instead to allow value comparison
		LineString mls = createLineString(0.0);
		
		values.put(Arrays.asList("LENGTH"), Double.valueOf(10.2)); //$NON-NLS-1$
		values.put(Arrays.asList("NAME"), "Test"); //$NON-NLS-1$ //$NON-NLS-2$
		values.put(Arrays.asList("the_geom"), mls); //$NON-NLS-1$
		
		Report report = fillFeatureTest("Watercourses_VA", //$NON-NLS-1$
				getClass().getResource("/data/sample_wva/wfs_va.xsd").toURI(),  //$NON-NLS-1$
				values, "fillWrite_WVA", "EPSG:31251"); //$NON-NLS-1$ //$NON-NLS-2$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link Point} to a GML 2 geometry type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometry_2_Point() throws Exception {
		// create the geometry
		Point point = createPoint(10.0);
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), point); //$NON-NLS-1$
		
		Report report = fillFeatureTest("Test", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml2.xsd").toURI(),  //$NON-NLS-1$
				values, "geometry_2_Point", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link Point} to a GML 3.2 geometry primitive type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryPrimitive_32_Point() throws Exception {
		// create the geometry
		Point point = createPoint(10.0);
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), point); //$NON-NLS-1$
		
		Report report = fillFeatureTest("PrimitiveTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryPrimitive_32_Point", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link Point} to a GML 3.2 geometry aggregate type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryAggregate_32_Point() throws Exception {
		// create the geometry
		Point point = createPoint(10.0);
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), point); //$NON-NLS-1$
		
		Report report = fillFeatureTest("AggregateTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryAggregate_32_Point", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link MultiPoint} to a GML 2 geometry type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometry_2_MultiPoint() throws Exception {
		// create the geometry
		MultiPoint mp = geomFactory.createMultiPoint(new Point[]{
				createPoint(0.0), createPoint(1.0), createPoint(2.0) 
		});
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), mp); //$NON-NLS-1$
		
		Report report = fillFeatureTest("Test", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml2.xsd").toURI(),  //$NON-NLS-1$
				values, "geometry_2_MultiPoint", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link MultiPoint} to a GML 3.2 geometry aggregate type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryAggregate_32_MultiPoint() throws Exception {
		// create the geometry
		MultiPoint mp = geomFactory.createMultiPoint(new Point[]{
				createPoint(0.0), createPoint(1.0), createPoint(2.0) 
		});
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), mp); //$NON-NLS-1$
		
		Report report = fillFeatureTest("AggregateTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryAggregate_32_MultiPoint", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Create a point
	 * 
	 * @param x the x ordinate
	 * 
	 * @return a point
	 */
	private Point createPoint(double x) {
		return geomFactory.createPoint(new Coordinate(x, x + 1));
	}
	
	/**
	 * Test writing a {@link Polygon} to a GML 2 geometry type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometry_2_Polygon() throws Exception {
		// create the geometry
		Polygon polygon = createPolygon(0.0);
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), polygon); //$NON-NLS-1$
		
		Report report = fillFeatureTest("Test", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml2.xsd").toURI(),  //$NON-NLS-1$
				values, "geometry_2_Polygon", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	// deactivated test because schema is invalid (according to xerces)
//	/**
//	 * Test writing a {@link Polygon} to a GML 3.0 geometry primitive type
//	 * 
//	 * @throws Exception if an error occurs
//	 */
//	@Test
//	public void testGeometryPrimitive_3_Polygon() throws Exception {
//		// create the geometry
//		Polygon polygon = createPolygon(0.0);
//		
//		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
//		values.put(Arrays.asList("geometry"), polygon);
//		
//		Report report = fillFeatureTest("PrimitiveTest",
//				getClass().getResource("/data/geom_schema/geom-gml3.xsd").toURI(), 
//				values, "geometryPrimitive_3_Polygon", DEF_SRS_NAME);
//		
//		assertTrue("Expected GML output to be valid", report.isValid());
//	}

	/**
	 * Test writing a {@link Polygon} to a GML 3.1 geometry primitive type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryPrimitive_31_Polygon() throws Exception {
		// create the geometry
		Polygon polygon = createPolygon(0.0);
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), polygon); //$NON-NLS-1$
		
		Report report = fillFeatureTest("PrimitiveTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml31.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryPrimitive_31_Polygon", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link Polygon} to a GML 3.2 geometry primitive type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryPrimitive_32_Polygon() throws Exception {
		// create the geometry
		Polygon polygon = createPolygon(0.0);
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), polygon); //$NON-NLS-1$
		
		Report report = fillFeatureTest("PrimitiveTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryPrimitive_32_Polygon", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Create a polygon
	 * 
	 * @param offset the offset along both axes
	 * 
	 * @return the created polygon
	 */
	private Polygon createPolygon(double offset) {
		LinearRing shell = geomFactory.createLinearRing(new Coordinate[]{
				new Coordinate(-1.0 + offset, offset), new Coordinate(offset, 1.0 + offset),
				new Coordinate(1.0 + offset, offset), new Coordinate(offset, -1.0 + offset),
				new Coordinate(-1.0 + offset, offset)});
		return geomFactory.createPolygon(shell, null);
	}
	
	/**
	 * Test writing a {@link LineString} to a GML 2 geometry type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometry_2_LineString() throws Exception {
		// create the geometry
		LineString lineString = createLineString(0.0);
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), lineString); //$NON-NLS-1$
		
		Report report = fillFeatureTest("Test", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml2.xsd").toURI(),  //$NON-NLS-1$
				values, "geometry_2_LineString", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}

	/**
	 * Test writing a {@link LineString} to a GML 3.2 geometry primitive type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryPrimitive_32_LineString() throws Exception {
		// create the geometry
		LineString lineString = createLineString(0.0);
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), lineString); //$NON-NLS-1$
		
		Report report = fillFeatureTest("PrimitiveTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryPrimitive_32_LineString", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link LineString} to a GML 3.2 geometry aggregate type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryAggregate_32_LineString() throws Exception {
		// create the geometry
		LineString lineString = createLineString(0.0);
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), lineString); //$NON-NLS-1$
		
		Report report = fillFeatureTest("AggregateTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryAggregate_32_LineString", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Create a line string geometry
	 * 
	 * @param offset the line y-offset
	 * 
	 * @return the line string
	 */
	private LineString createLineString(double offset) {
		return geomFactory.createLineString(new Coordinate[]{
				new Coordinate(0.0, offset), new Coordinate(1.0, 1.0 + offset),
				new Coordinate(2.0, 2.0 + offset), new Coordinate(3.0, 1.0 + offset),
				new Coordinate(4.0, offset)});
	}
	
	/**
	 * Test writing a {@link MultiLineString} to a GML 2 geometry type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometry_2_MultiLineString() throws Exception {
		// create the geometry
		MultiLineString mls = geomFactory.createMultiLineString(
				new LineString[]{createLineString(0.0), createLineString(1.0),
						createLineString(2.0)});
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), mls); //$NON-NLS-1$
		
		Report report = fillFeatureTest("Test", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml2.xsd").toURI(),  //$NON-NLS-1$
				values, "geometry_2_MultiLineString", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link MultiLineString} to a GML 3.2 geometry primitive type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryPrimitive_32_MultiLineString() throws Exception {
		// create the geometry
		MultiLineString mls = geomFactory.createMultiLineString(
				new LineString[]{createLineString(0.0), createLineString(1.0),
						createLineString(2.0)});
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), mls); //$NON-NLS-1$
		
		Report report = fillFeatureTest("PrimitiveTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryPrimitive_32_MultiLineString", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link MultiLineString} to a GML 3.2 geometry aggregate type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryAggregate_32_MultiLineString() throws Exception {
		// create the geometry
		MultiLineString mls = geomFactory.createMultiLineString(
				new LineString[]{createLineString(0.0), createLineString(1.0),
						createLineString(2.0)});
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), mls); //$NON-NLS-1$
		
		Report report = fillFeatureTest("AggregateTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryAggregate_32_MultiLineString", DEF_SRS_NAME, //$NON-NLS-1$
				true); //XXX in a MultiCurve Geotools only creates a LineString for each curve
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link MultiPolygon} to a GML 2 geometry type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometry_2_MultiPolygon() throws Exception {
		// create the geometry
		MultiPolygon mp = geomFactory.createMultiPolygon(new Polygon[]{
				createPolygon(0.0), createPolygon(1.0), createPolygon(-1.0) 
		});
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), mp); //$NON-NLS-1$
		
		Report report = fillFeatureTest("Test", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml2.xsd").toURI(),  //$NON-NLS-1$
				values, "geometry_2_MultiPolygon", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link MultiPolygon} to a GML 3.2 geometry primitive type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryPrimitive_32_MultiPolygon() throws Exception {
		// create the geometry
		MultiPolygon mp = geomFactory.createMultiPolygon(new Polygon[]{
				createPolygon(0.0), createPolygon(1.0), createPolygon(-1.0) 
		});
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), mp); //$NON-NLS-1$
		
		Report report = fillFeatureTest("PrimitiveTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryPrimitive_32_MultiPolygon", DEF_SRS_NAME, //$NON-NLS-1$
				true); //XXX no value equality check because Geotools parser doesn't seem to support CompositeSurface and only creates a Polygon instead of a MultiPolygon
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing a {@link MultiPolygon} to a GML 3.2 geometry aggregate type
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryAggregate_32_MultiPolygon() throws Exception {
		// create the geometry
		MultiPolygon mp = geomFactory.createMultiPolygon(new Polygon[]{
				createPolygon(0.0), createPolygon(1.0), createPolygon(-1.0) 
		});
		
		Map<List<String>, Object> values = new HashMap<List<String>, Object>();
		values.put(Arrays.asList("geometry"), mp); //$NON-NLS-1$
		
		Report report = fillFeatureTest("AggregateTest", //$NON-NLS-1$
				getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI(),  //$NON-NLS-1$
				values, "geometryAggregate_32_MultiPolygon", DEF_SRS_NAME); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing the result from a CST transformation
	 * 
	 * @throws Exception if any error occurs
	 */
	@Test
	public void testTransformWrite_WVA() throws Exception {
		Report report = transformTest(
				getClass().getResource("/data/sample_wva/wfs_va_sample.gml").toURI(), //$NON-NLS-1$
				getClass().getResource("/data/sample_wva/wfs_va.xsd").toURI(), //$NON-NLS-1$
				getClass().getResource("/data/sample_wva/watercourse_va.xml.goml").toURI(), //$NON-NLS-1$
				getClass().getResource("/data/sample_wva/inspire3/HydroPhysicalWaters.xsd").toURI(), //$NON-NLS-1$
				"transformWrite_WVA", //$NON-NLS-1$
				true,
				"EPSG:31251"); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing the result from a CST transformation
	 * 
	 * @throws Exception if any error occurs
	 */
	@Test
	public void testTransformWrite_DKM() throws Exception {
		Report report = transformTest(
				getClass().getResource("/data/dkm_austria/KA_14168_EPSG25833.gml").toURI(), //$NON-NLS-1$
				getClass().getResource("/data/dkm_austria/KA_14168_EPSG25833.xsd").toURI(), //$NON-NLS-1$
				getClass().getResource("/data/dkm_austria/mapping_dkm_inspire.xml.goml").toURI(), //$NON-NLS-1$
				getClass().getResource("/data/dkm_austria/inspire3/CadastralParcels.xsd").toURI(), //$NON-NLS-1$
				"transformWrite_DKM", //$NON-NLS-1$
				true,
				"EPSG:25833"); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	/**
	 * Test writing the result from a CST transformation
	 * 
	 * @throws Exception if any error occurs
	 */
	@Test
	public void testTransformWrite_ERM() throws Exception {
		Report report = transformTest(
				getClass().getResource("/data/erm/ERM_Watercourse_FME.gml").toURI(), //$NON-NLS-1$
				getClass().getResource("/data/erm/ERM_Watercourse_FME.xsd").toURI(), //$NON-NLS-1$
				getClass().getResource("/data/erm/_watrcrsl_inspire.xml.goml").toURI(), //$NON-NLS-1$
				getClass().getResource("/data/erm/inspire3/HydroPhysicalWaters.xsd").toURI(), //$NON-NLS-1$
				"transformWrite_ERM", //$NON-NLS-1$
				true,
				"EPSG:4326"); //$NON-NLS-1$
		
		assertTrue("Expected GML output to be valid", report.isValid()); //$NON-NLS-1$
	}
	
	private Report transformTest(URI sourceData, URI sourceSchemaLocation,
			URI mappingLocation, URI targetSchemaLocation, String testName,
			boolean onlyOne, String srsName)
			throws Exception {
		// load both schemas
		SchemaProvider sp = new ApacheSchemaProvider();
		Schema sourceSchema = sp.loadSchema(sourceSchemaLocation, null);
		Schema targetSchema = sp.loadSchema(targetSchemaLocation, null);
		
		// load source data
		FeatureCollection<FeatureType, Feature> fc = loadGML(sourceData, sourceSchema);
		
		// load alignment
		OmlRdfReader reader = new OmlRdfReader();
		Alignment alignment = reader.read(mappingLocation.toURL());
		
		// transform
		FeatureCollection<FeatureType, Feature> result = transform(fc, 
				alignment, targetSchema);
		
		if (onlyOne && result.size() > 1) {
			// only retain one feature of each type
			Set<FeatureType> types = new HashSet<FeatureType>();
			List<Feature> features = new ArrayList<Feature>();
			
			Iterator<Feature> it = result.iterator();
			while (it.hasNext()) {
				Feature feature = it.next();
				
				if (!types.contains(feature.getType())) {
					features.add(feature);
					types.add(feature.getType());
				}
			}
			
			result.clear();
			result.addAll(features);
		}
		
		// write
		// write to file
		DefaultGmlWriter writer = new DefaultGmlWriter();
		File outFile = File.createTempFile(testName, ".gml");  //$NON-NLS-1$
		OutputStream out = new FileOutputStream(outFile);
		List<Schema> addSchemas;
		try {
			addSchemas = writer.writeFeatures(result, targetSchema, out, srsName);
		} finally {
			out.flush();
			out.close();
		}
		
		System.out.println(outFile.getAbsolutePath());
		System.out.println(targetSchema.getLocation().toString());
		
		try {
			return validate(targetSchema, outFile.toURI(), addSchemas);
		}
		finally {
			if (DEL_TEMP_FILES) {
				outFile.deleteOnExit();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private FeatureCollection<FeatureType, Feature> transform(
			FeatureCollection<FeatureType, Feature> fc, IAlignment alignment, Schema targetSchema) {
		Set<FeatureType> types = new HashSet<FeatureType>(targetSchema.getTypes().values());
		return (FeatureCollection<FeatureType, Feature>) CstServiceFactory.getInstance().transform(fc, alignment, types);
	}
	
	/**
	 * Create a feature, fill it with values, write it as GML, validate the GML
	 * and load the GML file again to compare the loaded values with the ones
	 * that were written
	 * 
	 * @param elementName the element name of the feature type to use, if
	 *   <code>null</code> a random element will be used
	 * @param targetSchema the schema to use, the first element will be used 
	 *   for the type of the feature
	 * @param values the values to set on the feature
	 * @param testName the name of the test
	 * @param srsName the SRS name
	 * @return the validation report
	 * @throws Exception if any error occurs
	 */
	private Report fillFeatureTest(String elementName, URI targetSchema, Map<List<String>, 
			Object> values, String testName, String srsName) throws Exception {
		return fillFeatureTest(elementName, targetSchema, values, testName, srsName, false);
	}

	/**
	 * Create a feature, fill it with values, write it as GML, validate the GML
	 * and load the GML file again to compare the loaded values with the ones
	 * that were written
	 * 
	 * @param elementName the element name of the feature type to use, if
	 *   <code>null</code> a random element will be used
	 * @param targetSchema the schema to use, the first element will be used 
	 *   for the type of the feature
	 * @param values the values to set on the feature
	 * @param testName the name of the test
	 * @param srsName the SRS name
	 * @param skipValueTest if the check for equality shall be skipped
	 * @return the validation report
	 * @throws Exception if any error occurs
	 */
	private Report fillFeatureTest(String elementName, URI targetSchema, Map<List<String>, 
			Object> values, String testName, String srsName, boolean skipValueTest) throws Exception {
		SchemaProvider sp = new ApacheSchemaProvider();
		
		// load the sample schema
		Schema schema = sp.loadSchema(targetSchema, null);
		
		FeatureCollection<FeatureType, Feature> fc = new CstFeatureCollection();
		
		SchemaElement element = null;
		if (elementName == null) {
			element = schema.getElements().values().iterator().next();
			if (element == null) {
				fail("No element found in the schema"); //$NON-NLS-1$
			}
		}
		else {
			for (SchemaElement candidate : schema.getElements().values()) {
				if (candidate.getElementName().getLocalPart().equals(elementName)) {
					element = candidate;
					break;
				}
			}
			if (element == null) {
				fail("Element " + elementName + " not found in the schema"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		if (element == null) {
			throw new IllegalStateException();
		}
		
		// create feature
		Feature feature = createFeature(element.getType());
		
		// set some values
		for (Entry<List<String>, Object> entry : values.entrySet()) {
			FeatureInspector.setPropertyValue(feature, entry.getKey(), entry.getValue());
		}
		
		fc.add(feature );
		
		// write to file
		DefaultGmlWriter writer = new DefaultGmlWriter();
		File outFile = File.createTempFile(testName, ".gml");  //$NON-NLS-1$
		OutputStream out = new FileOutputStream(outFile);
		List<Schema> addSchema;
		try {
			addSchema = writer.writeFeatures(fc, schema, out, srsName);
		} finally {
			out.flush();
			out.close();
		}
		
		if (!DEL_TEMP_FILES && Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(outFile);
		}
		
		Report report = validate(schema, outFile.toURI(), addSchema);
		
		// load file
		FeatureCollection<FeatureType, Feature> loaded = loadGML(
				outFile.toURI(), schema);
		
		assertEquals(1, loaded.size());
		
		if (!skipValueTest) {
			Feature l = loaded.iterator().next();
			// test values
			for (Entry<List<String>, Object> entry : values.entrySet()) {
				//XXX conversion?
				
				Object expected = entry.getValue();
				Object value = FeatureInspector.getPropertyValue(l, entry.getKey(), null);
				
				if (expected instanceof Geometry && value instanceof Geometry) {
					matchGeometries((Geometry) expected, (Geometry) value);
				}
				else {
					assertEquals(
							expected.toString(), 
							value.toString());
				}
			}
		}
		
		System.out.println(outFile.getAbsolutePath());
		System.out.println(targetSchema.toString());
		
		if (DEL_TEMP_FILES) {
			outFile.deleteOnExit();
		}
		
		return report;
	}

	/**
	 * Let the test fail if the given geometries don't match
	 * 
	 * @param expected the expected geometry
	 * @param value the geometry value
	 */
	private void matchGeometries(Geometry expected, Geometry value) {
		if (expected.toString().equals(value.toString())) {
			// direct match
			return;
		}
		
		// check match for all no-loss conversions on value
		ConversionLadder ladder = GeometryConverterRegistry.getInstance().createNoLossLadder(value);
		while (ladder.hasNext()) {
			Geometry converted = ladder.next();
			if (expected.toString().equals(converted.toString())) {
				// match
				return;
			}
		}
		
		assertEquals("Geometry not compatible to expected geometry", expected.toString(), value.toString()); //$NON-NLS-1$
	}

	/**
	 * Load GML from a file
	 * 
	 * @param sourceData the GML file 
	 * @param schema the schema location
	 * @return the features
	 * @throws IOException if loading the file fails
	 */
	private FeatureCollection<FeatureType, Feature> loadGML(URI sourceData, Schema schema) throws IOException {
		InputStream in = sourceData.toURL().openStream();
		ConfigurationType type;
		try {
			type = GmlHelper.determineVersion(in, ConfigurationType.GML3);
		} finally {
			in.close();
		}
		
		in = sourceData.toURL().openStream();
		try {
			return GmlHelper.loadGml(in, type, schema);
		} finally {
			in.close();
		}
	}

	/**
	 * Validate an XML file against a schema
	 * 
	 * @param schema the schema
	 * @param xmlLocation the location of the xml file
	 * @param additionalSchemas additional schemas needed for validation
	 * @return the validation report
	 * @throws IOException if I/O operations fail
	 * @throws MalformedURLException if a wrong URI is given 
	 */
	private Report validate(Schema schema, URI xmlLocation, List<Schema> additionalSchemas) throws MalformedURLException, IOException {
		// validate using the XML api
		if (additionalSchemas == null || additionalSchemas.isEmpty()) {
			return ValidatorFactory.getInstance().createValidator(schema).validate(xmlLocation.toURL().openStream());
		}
		else {
			Schema[] schemas = new Schema[additionalSchemas.size() + 1];
			schemas[0] = schema;
			for (int i = 1; i <= additionalSchemas.size(); i++) {
				schemas[i] = additionalSchemas.get(i - 1);
			}
			return ValidatorFactory.getInstance().createValidator(schemas).validate(xmlLocation.toURL().openStream());
		}
	}

	private Feature createFeature(TypeDefinition type) {
		Collection<Property> properties = new HashSet<Property>();
		SimpleFeatureType targetType = ((SimpleFeatureType) type.getFeatureType());
		for (AttributeDescriptor ad : targetType.getAttributeDescriptors()) {
			Identifier id = new FeatureIdImpl(ad.getLocalName());
			// create normal AttributeImpls
			if (ad instanceof GeometryDescriptorImpl) {
				properties.add(new GeometryAttributeImpl(
						null, (GeometryDescriptor)ad, id));
			}
			else if (ad instanceof AttributeDescriptorImpl) {
				properties.add(new AttributeImpl(null, ad, id));
			}
		}
		return new FeatureImpl(properties, targetType, 
					new FeatureIdImpl("_" + UUID.randomUUID().toString())); // ID must start with _ or letter (not with digit) //$NON-NLS-1$
	}
	
}
