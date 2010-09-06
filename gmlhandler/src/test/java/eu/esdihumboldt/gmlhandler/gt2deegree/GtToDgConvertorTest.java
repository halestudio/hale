package eu.esdihumboldt.gmlhandler.gt2deegree;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.deegree.commons.tom.primitive.PrimitiveType;
import org.deegree.commons.tom.primitive.PrimitiveValue;
import org.deegree.cs.CRS;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.feature.FeatureCollection;
import org.deegree.filter.FilterEvaluationException;
import org.deegree.filter.IdFilter;
import org.deegree.geometry.standard.multi.DefaultMultiLineString;
import org.deegree.geometry.standard.primitive.DefaultPoint;
import org.geotools.data.DataUtilities;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.gml3.GMLConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.gml2.GMLHandler;

import eu.esdihumboldt.gmlhandler.GMLVersions;
import eu.esdihumboldt.gmlhandler.GmlHandler;
import eu.esdihumboldt.gmlhandler.GmlHandlerTest;
import eu.esdihumboldt.gmlhandler.gt2deegree.GtToDgConvertor;
import eu.esdihumboldt.hale.gmlparser.HaleGMLParser;

public class GtToDgConvertorTest {

	/** URL of XSD for tests **/
	private static final String xsdUrl = "file://" + (new GtToDgConvertorTest()).getClass()
										  .getResource("./inputdata/Watercourses_BY.xml").getFile();
	private static final String xsdUrlDemo = "file://" + (new GtToDgConvertorTest()).getClass()
	  .getResource("./inputdata/Sample_GN_HU_GeoServer.xsd").getFile();
	/** generated instance location */
	private static final String GML32_GENERATED_LOCATION = "src/test/resources/generated_3_2_1.gml";
	private static final String DEMO_GML32_GENERATED_LOCATION = "src/test/resources/demo_generated_3_2_1.gml";
	private static org.deegree.feature.FeatureCollection DeegreeFC;
	private static org.geotools.feature.FeatureCollection GeoToolsFC;
	private static org.geotools.feature.FeatureCollection GeoToolsGMLFC;
	private static org.geotools.feature.FeatureCollection GeoToolsDemoFC;

	@BeforeClass
	public static void loadGeotoolsData() {
		GeoToolsFC = FeatureCollections.newCollection();
		try {
			SimpleFeatureType TYPE = DataUtilities.createType("Location",
					"location:Point,name:String"); // see createFeatureType();

			GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);

			Point point = factory.createPoint(new Coordinate(15, 50));
			SimpleFeature feature = SimpleFeatureBuilder.build(TYPE,
					new Object[] { point, "name" }, null);

			GeoToolsFC.add(feature);
		} catch (FactoryRegistryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SchemaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void loadGeotoolsGMLData() {
		try {
			GeoToolsGMLFC = FeatureCollections.newCollection();
			URL url = new URL("file://"
					+ (new GtToDgConvertorTest()).getClass()
							.getResource("./inputdata/Watercourses_BY.gml")
							.getFile());
			HaleGMLParser parser = new HaleGMLParser(new GMLConfiguration());
			GeoToolsGMLFC = (org.geotools.feature.FeatureCollection<FeatureType, Feature>) parser
					.parse(url.openStream());
			System.out.println(GeoToolsGMLFC.getSchema().getName());

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@BeforeClass
	public static void loadGeotoolDemoData() {
		try {
			System.out.println("Reading source data to geotools model...");
			GeoToolsDemoFC = FeatureCollections.newCollection();
			URL url = new URL("file://"
					+ (new GtToDgConvertorTest()).getClass()
							.getResource("./inputdata/Sample_GN_HU_GeoServer.gml")
							.getFile());
			HaleGMLParser parser = new HaleGMLParser(new GMLConfiguration());
			GeoToolsDemoFC = (org.geotools.feature.FeatureCollection<FeatureType, Feature>) parser
					.parse(url.openStream());
			System.out.println(GeoToolsDemoFC.getSchema().getName());

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Testcase for convertion of the geotools SimpleAttributes created using geotools FeatureBuilder (manuelly).
	 */
	@Test
	public void testSimpleFeatureConversion() {
		FeatureCollection dgFC = GtToDgConvertor.convertGtToDg(GeoToolsFC);
		assertEquals(1, dgFC.size());
		org.deegree.feature.Feature dgFeature = dgFC.iterator().next();
		// check feature name
		assertEquals("Location", dgFeature.getName().getLocalPart());
		// check properties count
		assertEquals(2, dgFeature.getProperties().length);
		// check first property instance
		org.deegree.feature.property.Property locationProp = dgFeature
				.getProperties()[0];
		assertEquals("location", locationProp.getName().getLocalPart());
		// check property type
		DefaultPoint point = (DefaultPoint) locationProp.getValue();
		assertEquals(Point.class, point.getJTSGeometry().getClass());
		Point jtsPoint = (Point) point.getJTSGeometry();
		// check property name
		Coordinate[] coordinates = jtsPoint.getCoordinates();
		assertEquals(1, coordinates.length);
		assertEquals(15, coordinates[0].x, 0.0);
		assertEquals(50, coordinates[0].y, 0.0);
		// check second property
		// check second property
		org.deegree.feature.property.Property nameProp = dgFeature
				.getProperties()[1];
		assertEquals("name", nameProp.getName().getLocalPart());

	}
	
	/**
	 * Testcase for convertion of the geotools SimpleAttributes created using geotools FeatureBuilder (manuelly).
	 * @throws TransformationException 
	 * @throws UnknownCRSException 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testDemoDataConversion() throws FileNotFoundException, XMLStreamException, UnknownCRSException, TransformationException {
		System.out.println("Conversion to deegree starts..");
		
		//1. Create dgFC
		FeatureCollection dgFC = GtToDgConvertor.convertGtToDg(GeoToolsDemoFC);
		//2. Create GML Handler
		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("wfs", "http://www.opengis.net/wfs");
		namespaces.put("HUMBOLDT","http://www.esdi-humboldt.eu");
		namespaces.put("xsi","http://www.w3.org/2001/XMLSchema-instance");

		// set up GMLHandler with the test configuration
		GmlHandler gmlHandler = new GmlHandler(GMLVersions.gml3_2_1, xsdUrl,
				namespaces);
		// set target gml destination
		gmlHandler.setTargetGmlUrl(DEMO_GML32_GENERATED_LOCATION);
		gmlHandler.writeFC(dgFC);
		
	}

	
	/**
	 * Testcase for convertion of the geotools SimpleAttributes created using HaleParser.
	 */
	@Test
	public void testSimpleAttributeConversion() {
		FeatureCollection dgFC = GtToDgConvertor.convertGtToDg(GeoToolsGMLFC);
		//TODO check imported schemas 
		//1. check schema location
		//2. check namespaces
		//dgFC.getType().getSchema().getXSModel().getNamespaces();
		//3. check other xml attributes like number of features and time stamp
		//check fc  size
		//assertEquals(4, dgFC.size());
		//check feature with feature id = gml:id="Watercourses_BY.3
		
		org.deegree.feature.Feature dgFeature;
		try {
			 //check we have only one feature instance with id = "Watercourses_BY.3"
			 assertEquals(1, dgFC.getMembers(new IdFilter("Watercourses_BY.3")).size());
			 dgFeature = (org.deegree.feature.Feature)dgFC.getMembers(new IdFilter("Watercourses_BY.3")).iterator().next();
			 assertEquals(18, dgFeature.getProperties().length);
			 //check geometry property
			 org.deegree.feature.property.Property [] geomProperty = dgFeature.getGeometryProperties();
			 assertEquals(1,geomProperty.length);
			 //check geometry property type
			 DefaultMultiLineString multiLineString  = (DefaultMultiLineString)geomProperty[0].getValue();
			 CRS deegreeCRS = multiLineString.getCoordinateSystem();
			 //TODO provide fix for the CRS
			 //assertEquals("urn:x-ogc:def:crs:EPSG:31468", deegreeCRS.getName());
			 assertEquals(MultiLineString.class, multiLineString.getJTSGeometry().getClass());
			 MultiLineString jtsMultiLineString = (MultiLineString)multiLineString.getJTSGeometry();
			 assertEquals(1, jtsMultiLineString.getNumGeometries());
			 LineString jtsLineString = (LineString)jtsMultiLineString.getGeometryN(0);
			 assertEquals(12, jtsLineString.getCoordinateSequence().size());
			 Coordinate coordinate = jtsLineString.getCoordinateN(0);
			 assertEquals(5276443.08, coordinate.x, 0.0);
			 assertEquals(4322361.16, coordinate.y, 0.0);
			 //check property <topp:GN>Nonnenbach</topp:GN>
			 org.deegree.feature.property.Property gnProperty = dgFeature.getProperties()[9];
			 //test property name including the namespace
			 //assertEquals("topp", gnProperty.getName().getPrefix());
			 assertEquals("GN", gnProperty.getName().getLocalPart());
			 assertEquals("Nonnenbach",((PrimitiveValue)gnProperty.getValue()).getAsText());
			//2. Create GML Handler
			 System.out.println("Conversion to deegree starts..");
				HashMap<String, String> namespaces = new HashMap<String, String>();
				namespaces.put("gco", "http://www.isotc211.org/2005/gco");
				namespaces.put("gmd", "http://www.isotc211.org/2005/gmd");
				namespaces.put("gn",
						"urn:x-inspire:specification:gmlas:GeographicalNames:3.0");
				namespaces.put("hy-p",
						"urn:x-inspire:specification:gmlas:HydroPhysicalWaters:3.0");
				namespaces.put("hy", "urn:x-inspire:specification:gmlas:HydroBase:3.0");
				namespaces.put("base",
						"urn:x-inspire:specification:gmlas:BaseTypes:3.2");
				namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
				namespaces.put("topp", "http://www.openplans.org/topp"); 

				// set up GMLHandler with the test configuration
				GmlHandler gmlHandler = new GmlHandler(GMLVersions.gml3_2_1, xsdUrl,
						namespaces);
				// set target gml destination
				gmlHandler.setTargetGmlUrl(GML32_GENERATED_LOCATION);
				try {
					gmlHandler.writeFC(dgFC);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownCRSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			 
			 
		} catch (FilterEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Testcase for convertion of the geotools ComplexAttributes created using HaleParser.
	 */
	//@Test
	public void testComplexAttributeConversion(){
		//TODO provide implementation
		//1. Read a FeatureCollection having ComplexAttributes from the gml-file to the geotools object
		//2. Convert geotools object to deegree FeatureCollection using GtToDgConvertor
		//3. Implement assertions
	}
}
