package eu.esdihumboldt.cst.service.rename;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.cst.NameHelper;
import eu.esdihumboldt.cst.transformer.service.rename.FeatureAggregator2;


public class FeatureAggregatorTest {
	
	

	
	@Test
	public void testAggregateSum(){
		SimpleFeatureType ft = null;
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(NameHelper.sourceLocalname);
		ftbuilder.setNamespaceURI(NameHelper.sourceNamespace);
		ftbuilder.add("SomeAttr", Integer.class);
		ft = ftbuilder.buildFeatureType();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ft);
		Feature source1 = SimpleFeatureBuilder.build(
				ft, new Object[]{new Integer(2)}, UUID.randomUUID().toString());
		Feature source2 = SimpleFeatureBuilder.build(
				ft, new Object[]{new Integer(2)}, UUID.randomUUID().toString());
		List<Feature> features = new ArrayList<Feature>();
		features.add(source1);
		features.add(source2);

		FeatureAggregator2 fa = new FeatureAggregator2("SomeAttr", "aggregate:Collection_Sum");
		List<Feature>results = fa.aggregate(features, ft);
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getProperty("SomeAttr").getValue().toString().equals("4"));
	}
	
	
	
	
	@Test
	public void testAggregateMultiPoint(){
		GeometryFactory geomFactory = new GeometryFactory();
		SimpleFeatureType ft = null;
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(NameHelper.sourceLocalname);
		ftbuilder.setNamespaceURI(NameHelper.sourceNamespace);
		ftbuilder.add("SomePoint", Point.class);
		ft = ftbuilder.buildFeatureType();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ft);
		Feature source1 = SimpleFeatureBuilder.build(
				ft, new Object[]{geomFactory.createPoint(
						new Coordinate(Math.random(), Math.random()))}, UUID.randomUUID().toString());
		Feature source2 = SimpleFeatureBuilder.build(
				ft, new Object[]{geomFactory.createPoint(
						new Coordinate(Math.random(), Math.random()))}, UUID.randomUUID().toString());
		List<Feature> features = new ArrayList<Feature>();
		features.add(source1);
		features.add(source2);

		FeatureAggregator2 fa = new FeatureAggregator2("SomePoint", "aggregate:Multi");
		List<Feature>results = fa.aggregate(features, this.getTargetFT(MultiPoint.class, "SomePoint"));
//		System.out.println(results.size());
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getProperty("SomePoint").getType().getBinding().equals(MultiPoint.class));
	}
	
	
	@Test
	public void testAggregateMultiLineString(){
		GeometryFactory geomFactory = new GeometryFactory();
		SimpleFeatureType ft = null;
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(NameHelper.sourceLocalname);
		ftbuilder.setNamespaceURI(NameHelper.sourceNamespace);
		ftbuilder.add("SomeLine", LineString.class);
		ft = ftbuilder.buildFeatureType();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(ft);
		Feature source1 = SimpleFeatureBuilder.build(
				ft, new Object[]{geomFactory.createLineString(new Coordinate[] {new Coordinate(0,2), new Coordinate (2,0), new Coordinate (8,6)})}, UUID.randomUUID().toString());
		Feature source2 = SimpleFeatureBuilder.build(
				ft, new Object[]{geomFactory.createLineString(new Coordinate[] {new Coordinate(2,2), new Coordinate (3,3), new Coordinate (4,4)})}, UUID.randomUUID().toString());
		List<Feature> features = new ArrayList<Feature>();
		features.add(source1);
		features.add(source2);

		FeatureAggregator2 fa = new FeatureAggregator2("SomeLine", "aggregate:Multi");
		List<Feature>results = fa.aggregate(features, this.getTargetFT(MultiLineString.class, "SomeLine"));
//		System.out.println(results.size());
		assertTrue(results.size() == 1);
		assertTrue(results.get(0).getProperty("SomeLine").getType().getBinding().equals(MultiLineString.class));
	}
	
	
	
	
	private FeatureType getTargetFT(Class<?> geometryClass, String attributeName) {
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(NameHelper.targetLocalname);
			ftbuilder.setNamespaceURI(NameHelper.targetNamespace);
			ftbuilder.add(attributeName, geometryClass);
			ftbuilder.setDefaultGeometry(attributeName);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}

}
