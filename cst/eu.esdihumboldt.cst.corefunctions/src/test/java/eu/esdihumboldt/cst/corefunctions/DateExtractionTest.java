package eu.esdihumboldt.cst.corefunctions;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;
import junit.framework.TestCase;

public class DateExtractionTest extends TestCase {
	
	
	private final String sourceLocalname = "FT1";
	private final String sourceLocalnamePropertyADate = "PropertyADate";
	private final String sourceNamespace = "http://esdi-humboldt.eu";
	
	private final String targetLocalname = "FT2";
	private final String targetLocalnamePropertyBDate = "PropertyBdate";
	private final String targetNamespace = "http://xsdi.org";
	
	
	

	@Test
	public void testTransformFeatureFeature() {

		// set up cell to use for testing
		Cell cell = new Cell();
		Transformation t = new Transformation();
		t.setService(new Resource(DateExtractionFunction.class.toString()));
		t.getParameters().add(new Parameter("dateFormatSource", "dd.MM.yyyy HH:mm"));
		t.getParameters().add(new Parameter("dateFormatTarget", "MM-dd-yy h:mm a"));
		Property p1 = new Property(new About(this.sourceNamespace,
				this.sourceLocalname, this.sourceLocalnamePropertyADate));
		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(this.targetNamespace,
				this.targetLocalname, this.targetLocalnamePropertyBDate)));

		// build source and target Features
		SimpleFeatureType sourcetype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				new String[]{this.sourceLocalnamePropertyADate});
		SimpleFeatureType targettype = this.getFeatureType(
				this.targetNamespace, 
				this.targetLocalname, 
				new String[]{this.targetLocalnamePropertyBDate});
		Feature source = SimpleFeatureBuilder.build(
				sourcetype, new Object[]{"15.07.1982 16:30"}, "1");
		Feature target = SimpleFeatureBuilder.build(
				targettype, new Object[]{"DateHere"}, "2");
	
		
		// perform actual test

		DateExtractionFunction def = new DateExtractionFunction();
		def.configure(cell);
		Feature neu = def.transform(source, target);
		System.out.println(neu.getProperty(
				this.targetLocalnamePropertyBDate).getValue().toString());
		assertTrue(neu.getProperty(
				this.targetLocalnamePropertyBDate).getValue().toString().equals("07-15-82 4:30 PM"));
		

		
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
