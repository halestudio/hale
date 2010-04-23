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
import eu.esdihumboldt.cst.NameHelper;
import eu.esdihumboldt.cst.transformer.service.rename.FeatureAggregator2;


public class FeatureAggregatorTest {
	
	

	
	@Test
	public void testAggregate(){
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

}
