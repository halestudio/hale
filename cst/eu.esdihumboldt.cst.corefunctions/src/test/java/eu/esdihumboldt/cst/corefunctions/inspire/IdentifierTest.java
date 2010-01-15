package eu.esdihumboldt.cst.corefunctions.inspire;

import junit.framework.TestCase;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;


import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

public class IdentifierTest extends TestCase {
	private final String sourceLocalname = "FT1";
	private final String sourceLocalnamePropertyAID = "PropertyAID";
	private final String sourceNamespace = "http://esdi-humboldt.eu";
	
	private final String targetLocalname = "FT2";
	private final String targetLocalnamePropertyBID = "PropertyBID";
	private final String targetNamespace = "http://xsdi.org";
	
	
	

	@Test
	public void testTransformFeatureFeature() {

		// set up cell to use for testing
		Cell cell = new Cell();
		ComposedProperty cp = new ComposedProperty(
				new About(this.sourceNamespace, this.sourceLocalname));
		cp.getCollection().add(new Property(
				new About(this.sourceNamespace, this.sourceLocalname, 
						this.sourceLocalnamePropertyAID)));
		
		Transformation t = new Transformation();
		t.setService(new Resource(IdentifierFunction.class.toString()));
		t.getParameters().add(new Parameter("countryName", "de"));
		t.getParameters().add(new Parameter("providerName", "TUM"));
		t.getParameters().add(new Parameter("productName", "PRName"));
		cp.setTransformation(t);
		cell.setEntity1(cp);
//		cell.setEntity1(new Property(new About(this.sourceNamespace, this.sourceLocalname, this.sourceLocalnamePropertyAID)));
		cell.setEntity2(new Property(new About(this.targetNamespace, this.targetLocalname, this.targetLocalnamePropertyBID)));

		// build source and target Features
		SimpleFeatureType sourcetype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				new String[]{this.sourceLocalnamePropertyAID});
		SimpleFeatureType targettype = this.getFeatureType(
				this.targetNamespace, 
				this.targetLocalname, 
				new String[]{this.targetLocalnamePropertyBID});
		Feature source = SimpleFeatureBuilder.build(
				sourcetype, new Object[]{"ID1"}, "1");
		Feature target = SimpleFeatureBuilder.build(
				targettype, new Object[]{}, "2");
		// perform actual test
		
		System.out.println(source.getProperty(this.sourceLocalnamePropertyAID).getValue().toString());
		System.out.println("about " +cell.getEntity1().getAbout().getAbout());
		IdentifierFunction idf = new IdentifierFunction();
		idf.configure(cell);

		Feature neu = idf.transform(source, target);
		assertTrue(neu.getProperty(
				this.targetLocalnamePropertyBID).getValue().toString().equals("urn:de:TUM:PRName:http://esdi-humboldt.eu/FT1:ID1"));
		

		
	}
	
	private SimpleFeatureType getFeatureType(String featureTypeNamespace, 
			String featureTypeName, String[] propertyNames) {
	
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			for (String s : propertyNames) {
				ftbuilder.add(s, String.class);
			}
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}
}
