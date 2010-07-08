package eu.esdihumboldt.gmlhandler.gt2deegree;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.deegree.feature.FeatureCollection;
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
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.gmlhandler.gt2deegree.GtToDgConvertor;
import eu.esdihumboldt.hale.gmlparser.HaleGMLParser;

public class GtToDgConvertorTest {

	private static org.deegree.feature.FeatureCollection DeegreeFC;
	private static org.geotools.feature.FeatureCollection GeoToolsFC;
	private static org.geotools.feature.FeatureCollection GeoToolsGMLFC;

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

	@Test
	public void testComplexAttributeConversion() {
		// TODO provide implementation
	}
}
