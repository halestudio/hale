package eu.esdihumboldt.cst.corefunctions;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Locale;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

public class DateExtractionTest {
	
	
	private final String sourceLocalname = "FT1";
	private final String sourceLocalnamePropertyADate = "PropertyADate";
	private final String sourceNamespace = "http://esdi-humboldt.eu";
	
	private final String targetLocalname = "FT2";
	private final String targetLocalnamePropertyBDate = "PropertyBdate";
	private final String targetNamespace = "http://xsdi.org";

	
	@Before
	public void init() throws Exception{
		Locale.setDefault(Locale.ENGLISH); 		
	}
	@Test
	public void testTransformFeatureFeature() throws Exception{

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
		
		assertEquals(neu.getProperty(
				this.targetLocalnamePropertyBDate).getValue().toString(),"07-15-82 4:30 PM");

	}
	
	
	@Test
	public void testTransformFeatureFeatureDateBinding() throws Exception{

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
		
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(this.targetLocalname);
		ftbuilder.setNamespaceURI(this.targetNamespace);
		ftbuilder.add(this.targetLocalnamePropertyBDate, Timestamp.class);
		SimpleFeatureType targettype = ftbuilder.buildFeatureType();
		
		Feature source = SimpleFeatureBuilder.build(
				sourcetype, new Object[]{"15.07.1982 16:30"}, "1");
		Feature target = SimpleFeatureBuilder.build(
				targettype, new Object[]{new Timestamp(0)}, "2");
	
		System.out.println("Before: " +target.getProperty(
				this.targetLocalnamePropertyBDate).getValue().toString());
		
		// perform actual test

		DateExtractionFunction def = new DateExtractionFunction();
		def.configure(cell);
		Feature neu = def.transform(source, target);
	
		assertEquals(neu.getProperty(
				this.targetLocalnamePropertyBDate).getValue().toString(),"1982-07-15 16:30:00.0");

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
