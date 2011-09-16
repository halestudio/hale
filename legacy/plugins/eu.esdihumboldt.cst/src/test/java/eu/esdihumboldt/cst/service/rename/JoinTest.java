package eu.esdihumboldt.cst.service.rename;

import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.cst.transformer.service.rename.FeatureSpatialJoiner;

public class JoinTest {
	private static Logger _log = Logger.getLogger(JoinTest.class);
	
	@BeforeClass
	public static void testSchemaTranslationController() throws URISyntaxException {
		// set up log4j logger manually if necessary
//		if (!Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
//			Appender appender = new ConsoleAppender(
//					new PatternLayout("%d{ISO8601} %5p %C{1}:%L %m%n"), 
//					 ConsoleAppender.SYSTEM_OUT );
//			appender.setName("A1");
//			Logger.getRootLogger().addAppender(appender);
//		}
	}
	
	@Test
	public void testAplhaJoin(){
		GeometryFactory geomFactory = new GeometryFactory();
		SimpleFeatureType ft = null;
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName("FirstType"); //$NON-NLS-1$
		ftbuilder.setNamespaceURI("http://first.de"); //$NON-NLS-1$
		ftbuilder.add("1_P", Point.class); //$NON-NLS-1$
		ftbuilder.add("SomeValues0", Integer.class); //$NON-NLS-1$
		ft = ftbuilder.buildFeatureType();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ft);
		Feature sourceFt1_1 = builder.build(
				ft, new Object[]{geomFactory.createPoint(
						new Coordinate(20.0,20.0)), new Integer (2)}, "first"); //$NON-NLS-1$
		Feature sourceFt1_2 = builder.build(
				ft, new Object[]{geomFactory.createPoint(
						new Coordinate(100.0,200.0)), new Integer (3)}, "second"); //$NON-NLS-1$

		SimpleFeatureType ft2 = null;
		SimpleFeatureTypeBuilder ftbuilder2 = new SimpleFeatureTypeBuilder();
		ftbuilder2.setName("SecondType"); //$NON-NLS-1$
		ftbuilder2.setNamespaceURI("http://second.de"); //$NON-NLS-1$
		ftbuilder2.add("SomeP", Point.class); //$NON-NLS-1$
		ftbuilder2.add("SomeValues1", Integer.class); //$NON-NLS-1$
		ft2 = ftbuilder2.buildFeatureType();
		SimpleFeatureBuilder builder2 = new SimpleFeatureBuilder(ft2);
		Feature sourceFt2_1 = builder2.build(
				ft2, new Object[]{geomFactory.createPoint(
						new Coordinate(1.0,2.0)),new Integer (2)}, UUID.randomUUID().toString());
		Feature sourceFt2_2 = builder2.build(
				ft2, new Object[]{geomFactory.createPoint(
						new Coordinate(1.0,2.0)), new Integer (10)}, UUID.randomUUID().toString());
		Feature sourceFt2_3 = builder2.build(
				ft2, new Object[]{geomFactory.createPoint(
						new Coordinate(6.0,6.0)),new Integer (2)}, UUID.randomUUID().toString());

		Collection<Feature> features1 = new ArrayList<Feature>();
		features1.add(sourceFt1_1);
		features1.add(sourceFt1_2);

		Collection<Feature> features2 = new ArrayList<Feature>();
		features2.add(sourceFt2_1);
		features2.add(sourceFt2_2);
		features2.add(sourceFt2_3);
		
		List<Collection<Feature>> all = new ArrayList<Collection<Feature>>();
		all.add(features1);
		all.add(features2);
	

		FeatureSpatialJoiner fj = new FeatureSpatialJoiner("SomeValues0","SomeValues1", false, "join"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		List<Feature>results = fj.join(all);
		_log.info("JOINSIZE " +results.size()); //$NON-NLS-1$

		assertTrue(results.size()==2);
		SimpleFeatureType joinedFt = null;
		SimpleFeatureTypeBuilder ftbuilder3 = new SimpleFeatureTypeBuilder();
		ftbuilder3.setName("Joined_" + "FirstType"+ "_"+"SecondType"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ftbuilder3.setNamespaceURI("http://first.de"); //$NON-NLS-1$
		ftbuilder3.add("1_P", Point.class); //$NON-NLS-1$
		ftbuilder3.add("SomeValues0", Integer.class); //$NON-NLS-1$
		ftbuilder3.add("SomeP", Point.class); //$NON-NLS-1$
		ftbuilder3.add("SomeValues1", Integer.class); //$NON-NLS-1$
		joinedFt = ftbuilder3.buildFeatureType();
		for (Feature f : results){
			_log.info("RESULT__________________________________"); //$NON-NLS-1$
			_log.info("RESULT " + f); //$NON-NLS-1$
			assertTrue(f.getType().equals(joinedFt));
		}

	}
	

	
	@Test
	public void testSpatialJoin(){
		GeometryFactory geomFactory = new GeometryFactory();
		SimpleFeatureType ft = null;
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName("FirstType"); //$NON-NLS-1$
		ftbuilder.setNamespaceURI("http://first.de"); //$NON-NLS-1$
		ftbuilder.add("1_P", Polygon.class); //$NON-NLS-1$
		ftbuilder.add("SomeValues0", Integer.class); //$NON-NLS-1$
		ft = ftbuilder.buildFeatureType();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ft);
		Feature sourceFt1_1 = builder.build(
				ft, new Object[]{geomFactory.createPolygon(
						geomFactory.createLinearRing(new Coordinate[] {new Coordinate (0.0, 0.0), new Coordinate(1.0, 0.0),new Coordinate(0.0, 1.0), new Coordinate(1.0, 1.0), new Coordinate(0.0, 0.0)}), null), new Integer (2)}, "first"); //$NON-NLS-1$
		Feature sourceFt1_2 = builder.build(
				ft, new Object[]{geomFactory.createPolygon(
						geomFactory.createLinearRing(new Coordinate[] {new Coordinate (100.0, 100.0), new Coordinate(200.0, 100.0),new Coordinate(100.0, 200.0), new Coordinate(200.0, 200.0), new Coordinate(100.0, 100.0)}), null), new Integer (3)}, "second"); //$NON-NLS-1$

		SimpleFeatureType ft2 = null;
		SimpleFeatureTypeBuilder ftbuilder2 = new SimpleFeatureTypeBuilder();
		ftbuilder2.setName("SecondType"); //$NON-NLS-1$
		ftbuilder2.setNamespaceURI("http://second.de"); //$NON-NLS-1$
		ftbuilder2.add("SomeP", Polygon.class); //$NON-NLS-1$
		ftbuilder2.add("SomeValues1", Integer.class); //$NON-NLS-1$
		ft2 = ftbuilder2.buildFeatureType();
		SimpleFeatureBuilder builder2 = new SimpleFeatureBuilder(ft2);
		Feature sourceFt2_1 = builder2.build(
				ft2, new Object[]{geomFactory.createPolygon(
						geomFactory.createLinearRing(new Coordinate[] {new Coordinate (0.0, 0.0), new Coordinate(2.0, 0.0),new Coordinate(0.0, 2.0), new Coordinate(2.0, 2.0), new Coordinate(0.0, 0.0)}), null),new Integer (2)}, UUID.randomUUID().toString());
		Feature sourceFt2_2 = builder2.build(
				ft2, new Object[]{geomFactory.createPolygon(
						geomFactory.createLinearRing(new Coordinate[] {new Coordinate (0.0, 0.0), new Coordinate(2.0, 0.0),new Coordinate(0.0, 2.0), new Coordinate(2.0, 2.0), new Coordinate(0.0, 0.0)}), null), new Integer (10)}, UUID.randomUUID().toString());
		Feature sourceFt2_3 = builder2.build(
				ft2, new Object[]{geomFactory.createPolygon(
						geomFactory.createLinearRing(new Coordinate[] {new Coordinate (300.0, 300.0), new Coordinate(400.0, 400.0),new Coordinate(400.0, 402.0), new Coordinate(402.0, 402.0), new Coordinate(300.0, 300.0)}), null),new Integer (2)}, UUID.randomUUID().toString());

		Collection<Feature> features1 = new ArrayList<Feature>();
		features1.add(sourceFt1_1);
		features1.add(sourceFt1_2);

		Collection<Feature> features2 = new ArrayList<Feature>();
		features2.add(sourceFt2_1);
		features2.add(sourceFt2_2);
		features2.add(sourceFt2_3);
		
		List<Collection<Feature>> all = new ArrayList<Collection<Feature>>();
		all.add(features1);
		all.add(features2);
	

		FeatureSpatialJoiner fj = new FeatureSpatialJoiner("1_P","SomeP", true, "join:intersects"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		List<Feature>results = fj.join(all);
		_log.info("JoinSIZE " +results.size()); //$NON-NLS-1$

		assertTrue(results.size()==2);
		SimpleFeatureType joinedFt = null;
		SimpleFeatureTypeBuilder ftbuilder3 = new SimpleFeatureTypeBuilder();
		ftbuilder3.setName("Joined_" + "FirstType"+ "_"+"SecondType"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ftbuilder3.setNamespaceURI("http://first.de"); //$NON-NLS-1$
		ftbuilder3.add("1_P", Polygon.class); //$NON-NLS-1$
		ftbuilder3.add("SomeValues0", Integer.class); //$NON-NLS-1$
		ftbuilder3.add("SomeP", Polygon.class); //$NON-NLS-1$
		ftbuilder3.add("SomeValues1", Integer.class); //$NON-NLS-1$
		joinedFt = ftbuilder3.buildFeatureType();
		for (Feature f : results){
			_log.info("RESULT__________________________________"); //$NON-NLS-1$
			_log.info("RESULT " + f); //$NON-NLS-1$
			assertTrue(f.getType().equals(joinedFt));
		}

	}

}
