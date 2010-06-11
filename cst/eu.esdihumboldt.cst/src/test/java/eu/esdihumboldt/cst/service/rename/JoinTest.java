package eu.esdihumboldt.cst.service.rename;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.store.ContentFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.cst.NameHelper;
import eu.esdihumboldt.cst.transformer.service.rename.FeatureAggregator;
import eu.esdihumboldt.cst.transformer.service.rename.FeatureSpatialJoiner;

public class JoinTest {
	
	
	
	@Test
	public void testSpatialJoin(){
	
		GeometryFactory geomFactory = new GeometryFactory();
//		SimpleFeatureType ft = null;
//		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
//		ftbuilder.setName(NameHelper.sourceLocalname);
//		ftbuilder.setNamespaceURI(NameHelper.sourceNamespace);
//		ftbuilder.add("SomePoint", Point.class);
//		ftbuilder.add("SomeValues", Integer.class);
//		ft = ftbuilder.buildFeatureType();
//		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ft);
//		Feature source1 = SimpleFeatureBuilder.build(
//				ft, new Object[]{geomFactory.createPoint(
//						new Coordinate(1.0, 1.0)), new Integer (2)}, UUID.randomUUID().toString());
//		Feature source2 = SimpleFeatureBuilder.build(
//				ft, new Object[]{geomFactory.createPoint(
//						new Coordinate(1.0,1.0)), new Integer (3)}, UUID.randomUUID().toString());
//		
		
		SimpleFeatureType ft = null;
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName("FirstType");
		ftbuilder.setNamespaceURI("http://first.de");
		ftbuilder.add("1_Polygon", Polygon.class);
		ftbuilder.add("SomeValues", Integer.class);
		ft = ftbuilder.buildFeatureType();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ft);
		Feature sourceFt1_1 = builder.build(
				ft, new Object[]{geomFactory.createPolygon(
						geomFactory.createLinearRing(new Coordinate[] {new Coordinate (0.0, 0,0), new Coordinate(2.0, 0.0),new Coordinate(0.0, 2.0), new Coordinate(2.0, 2.0), new Coordinate(0.0, 0.0)}), null), new Integer (2)}, UUID.randomUUID().toString());
						
		
		SimpleFeatureType ft2 = null;
		SimpleFeatureTypeBuilder ftbuilder2 = new SimpleFeatureTypeBuilder();
		ftbuilder2.setName("SecondType");
		ftbuilder2.setNamespaceURI("http://second.de");
		ftbuilder2.add("SomePolygon", Polygon.class);
		ftbuilder2.add("SomeValues", Integer.class);
		ft2 = ftbuilder2.buildFeatureType();
		SimpleFeatureBuilder builder2 = new SimpleFeatureBuilder(ft2);
		Feature sourceFt2_1 = builder2.build(
				ft, new Object[]{geomFactory.createPolygon(
						geomFactory.createLinearRing(new Coordinate[] {new Coordinate (0.0, 0,0), new Coordinate(1.0, 0.0), new Coordinate(1.0, 1.0), new Coordinate(0.0, 1.0), new Coordinate(0.0, 0.0)}), null), new Integer (2)}, UUID.randomUUID().toString());

				
						

		
		
		Collection<Feature> features1 = new ArrayList<Feature>();
		features1.add(sourceFt1_1);

		Collection<Feature> features2 = new ArrayList<Feature>();
		features2.add(sourceFt2_1);
		
		List<Collection<Feature>> all = new ArrayList<Collection<Feature>>();
		all.add(features1);
		all.add(features2);

		FeatureSpatialJoiner fj = new FeatureSpatialJoiner("SomeValues",null, true, "join:intersects");
		List<Feature>results = fj.join(all, ft2);
		System.out.println("TESTSIZE " +results.size());

		for (Feature f : results){
			System.out.println("TEST");
			System.out.println(f.getType().getDescriptors());
		}

//		assertTrue(results.get(0).getProperty("SomePoint").getType().getBinding().equals(MultiPoint.class));
	}

}
