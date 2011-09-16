package eu.esdihumboldt.cst.corefunctions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.commons.goml.rdf.Resource;

public class DateExtractionTest {
	
	
	private final String sourceLocalname = "FT1"; //$NON-NLS-1$
	private final String sourceLocalnamePropertyADate = "PropertyADate"; //$NON-NLS-1$
	private final String sourceNamespace = "http://esdi-humboldt.eu"; //$NON-NLS-1$
	
	private final String targetLocalname = "FT2"; //$NON-NLS-1$
	private final String targetLocalnamePropertyBDate = "PropertyBdate"; //$NON-NLS-1$
	private final String targetNamespace = "http://xsdi.org"; //$NON-NLS-1$

	
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
		t.getParameters().add(new Parameter("dateFormatSource", "dd.MM.yyyy HH:mm")); //$NON-NLS-1$ //$NON-NLS-2$
		t.getParameters().add(new Parameter("dateFormatTarget", "MM-dd-yy h:mm a")); //$NON-NLS-1$ //$NON-NLS-2$
		Property p1 = new Property(new About(this.sourceNamespace,
				this.sourceLocalname, this.sourceLocalnamePropertyADate));
		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(this.targetNamespace,
				this.targetLocalname, this.targetLocalnamePropertyBDate)));

		// build source and target Features
		Map<String, Class> propsSource = new HashMap<String, Class>();
		propsSource.put(this.sourceLocalnamePropertyADate, String.class);
		SimpleFeatureType sourcetype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				propsSource);
		Map<String, Class> propsTarget = new HashMap<String, Class>();
		propsTarget.put(this.targetLocalnamePropertyBDate, String.class);
		SimpleFeatureType targettype = this.getFeatureType(
				this.targetNamespace, 
				this.targetLocalname, 
				propsTarget);
		Feature source = SimpleFeatureBuilder.build(
				sourcetype, new Object[]{"15.07.1982 16:30"}, "1"); //$NON-NLS-1$ //$NON-NLS-2$
		Feature target = SimpleFeatureBuilder.build(
				targettype, new Object[]{"DateHere"}, "2"); //$NON-NLS-1$ //$NON-NLS-2$
	
		
		// perform actual test

		DateExtractionFunction def = new DateExtractionFunction();
		def.configure(cell);
		Feature neu = def.transform(source, target);
		
		assertTrue(neu.getProperty(
				this.targetLocalnamePropertyBDate).getValue().toString().equals("07-15-82 4:30 PM")); //$NON-NLS-1$

	}
	
	
	@Test
	public void testTransformFeatureFeatureDateBinding() throws Exception{

		// set up cell to use for testing
		Cell cell = new Cell();
		Transformation t = new Transformation();
		t.setService(new Resource(DateExtractionFunction.class.toString()));
		t.getParameters().add(new Parameter("dateFormatSource", "dd.MM.yyyy HH:mm")); //$NON-NLS-1$ //$NON-NLS-2$
		t.getParameters().add(new Parameter("dateFormatTarget", "MM-dd-yy h:mm a")); //$NON-NLS-1$ //$NON-NLS-2$
		Property p1 = new Property(new About(this.sourceNamespace,
				this.sourceLocalname, this.sourceLocalnamePropertyADate));
		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(this.targetNamespace,
				this.targetLocalname, this.targetLocalnamePropertyBDate)));

		// build source and target Features
		Map<String, Class> propsSource = new HashMap<String, Class>();
		propsSource.put(this.sourceLocalnamePropertyADate, String.class);
		SimpleFeatureType sourcetype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				propsSource);
		
		Map<String, Class> propsTarget = new HashMap<String, Class>();
		propsTarget.put(this.targetLocalnamePropertyBDate, Timestamp.class);
		SimpleFeatureType targettype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				propsTarget);
		
		
//		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
//		ftbuilder.setName(this.targetLocalname);
//		ftbuilder.setNamespaceURI(this.targetNamespace);
//		ftbuilder.add(this.targetLocalnamePropertyBDate, Timestamp.class);
//		SimpleFeatureType targettype = ftbuilder.buildFeatureType();
		
		Feature source = SimpleFeatureBuilder.build(
				sourcetype, new Object[]{"15.07.1982 16:30"}, "1"); //$NON-NLS-1$ //$NON-NLS-2$
		Feature target = SimpleFeatureBuilder.build(
				targettype, new Object[]{new Timestamp(0)}, "2"); //$NON-NLS-1$
		
		// perform actual test

		DateExtractionFunction def = new DateExtractionFunction();
		def.configure(cell);
		Feature neu = def.transform(source, target);
	
		assertTrue(neu.getProperty(
				this.targetLocalnamePropertyBDate).getValue().toString().equals("07-15-82 4:30 PM")); //$NON-NLS-1$

	}
	
	
	
	
	private SimpleFeatureType getFeatureType(String featureTypeNamespace, 
			String featureTypeName, Map<String, Class> properties) {
	
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			for (String s : properties.keySet()) {
				ftbuilder.add(s, properties.get(s));
			}
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}

}
