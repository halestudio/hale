package eu.esdihumboldt.gmlhandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.deegree.feature.FeatureCollection;
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
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.hale.gmlparser.HaleGMLParser;

public class DgToGtConvertorTest {

	private static org.deegree.feature.FeatureCollection DeegreeFC;
	private static org.geotools.feature.FeatureCollection GeoToolsFC;
	private static org.geotools.feature.FeatureCollection GeoToolsGMLFC;

	@BeforeClass
	public static void loadGeotoolsData() {		
		GeoToolsFC = FeatureCollections
		.newCollection();
		try {
			SimpleFeatureType TYPE = DataUtilities.createType("Location",
					"location:Point,name:String"); // see createFeatureType();

			
			GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);

			Point point = factory.createPoint(new Coordinate(15, 50));
			SimpleFeature feature = SimpleFeatureBuilder.build(TYPE, new Object[] {
					point, "name" }, null);
			
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
			URL url = new URL("file://" + (new DgToGtConvertorTest()).getClass().getResource("./inputdata/Watercourses_BY.gml").getFile());			
			HaleGMLParser parser = new HaleGMLParser(new GMLConfiguration());
			GeoToolsGMLFC =  (org.geotools.feature.FeatureCollection<FeatureType, Feature>) parser.parse(url.openStream());
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
	/*@BeforeClass
	public static void loadDeegreeData() {
		String SCHEMA_URL = "http://svn.esdi-humboldt.eu/repo/humboldt2/trunk/cst/eu.esdihumboldt.cst.corefunctions/src/test/resource/eu/esdihumboldt/cst/corefunctions/inspire/inspire_v3.0_xsd/"
				+ "HydroPhysicalWaters.xsd";
		String GML32_INSTANCE_LOCATION = "http://svn.esdi-humboldt.eu/repo/humboldt2/branches/humboldt-deegree3/resource/sourceData/va_target_v3.gml";
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

		try {
			GmlHandler gmlHandler = new GmlHandler(GMLVersions.gml3_2_1,
					SCHEMA_URL, namespaces);
			gmlHandler.setGmlUrl(GML32_INSTANCE_LOCATION);

			URL url = new URL(GML32_INSTANCE_LOCATION);
			// read FeatureCollection

			DeegreeFC = gmlHandler.readFC();

			// check feature collection size
			assertEquals(998, DeegreeFC.size());

			// validate a single feature with id=Watercourses=VA.942

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}*/

	@Test
	public void testConversiontoGT() {
		//DgToGtConvertor.covertDgtoGt(DeegreeFC);
		DgToGtConvertor.covertGttoDg(GeoToolsFC);


	}
}
