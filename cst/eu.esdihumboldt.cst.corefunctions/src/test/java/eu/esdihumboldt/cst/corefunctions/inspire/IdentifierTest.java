package eu.esdihumboldt.cst.corefunctions.inspire;



import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.corefunctions.util.TypeLoader;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.inspire.data.InspireIdentifier;


public class IdentifierTest {
	private final String sourceLocalname = Messages.getString("IdentifierTest.0"); //$NON-NLS-1$
	private final String sourceLocalnamePropertyAID = Messages.getString("IdentifierTest.1"); //$NON-NLS-1$
	private final String sourceNamespace = Messages.getString("IdentifierTest.2"); //$NON-NLS-1$
	
	private final String targetLocalname = Messages.getString("IdentifierTest.3"); //$NON-NLS-1$
	private final String targetLocalnamePropertyBID = Messages.getString("IdentifierTest.4"); //$NON-NLS-1$
	private final String targetNamespace = Messages.getString("IdentifierTest.5"); //$NON-NLS-1$
	
	private String localID = Messages.getString("IdentifierTest.6"); //$NON-NLS-1$
	private String countryname = Messages.getString("IdentifierTest.7"); //$NON-NLS-1$
	private String provName = Messages.getString("IdentifierTest.8"); //$NON-NLS-1$
	private String prodName = Messages.getString("IdentifierTest.9"); //$NON-NLS-1$
	private String version = Messages.getString("IdentifierTest.10"); //$NON-NLS-1$
	
	@Test
	public void testTransformTypeBinding() {
		Cell cell = this.getCell();
		
		// build source and target Features
		SimpleFeatureType sourcetype = this.getFeatureType(
				this.sourceNamespace, 
				this.sourceLocalname, 
				new String[]{this.sourceLocalnamePropertyAID});
		
		String url = getClass().getResource(
				Messages.getString("IdentifierTest.11")).toString(); //$NON-NLS-1$
			FeatureType targettype = TypeLoader.getType(Messages.getString("IdentifierTest.12"), url); //$NON-NLS-1$
		Feature source = SimpleFeatureBuilder.build(
				sourcetype, new Object[]{Messages.getString("IdentifierTest.13")}, localID); //$NON-NLS-1$
		Feature target = SimpleFeatureBuilder.build(
				(SimpleFeatureType) targettype, new Object[]{}, Messages.getString("IdentifierTest.14")); //$NON-NLS-1$
		
		// perform actual test
		IdentifierFunction idf = new IdentifierFunction();
		idf.configure(cell);

		Feature transformed = idf.transform(source, target);
		Object o = transformed.getProperty(this.targetLocalnamePropertyBID).getValue();
		assertNotNull(o);
		Object identifier = ((Collection)o).iterator().next();
		assertNotNull(identifier);
		
	}
	

	@Test
	public void testTransformFeatureFeatureStringBinding() {
		// set up cell to use for testing
		Cell cell = this.getCell();

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
				sourcetype, new Object[]{Messages.getString("IdentifierTest.15")}, localID); //$NON-NLS-1$
		Feature target = SimpleFeatureBuilder.build(
				targettype, new Object[]{}, Messages.getString("IdentifierTest.16")); //$NON-NLS-1$

		// perform actual test
		IdentifierFunction idf = new IdentifierFunction();
		idf.configure(cell);

		Feature neu = idf.transform(source, target);
		System.out.println(Messages.getString("IdentifierTest.17")+neu.getProperty( //$NON-NLS-1$
				this.targetLocalnamePropertyBID).getValue().toString());
		assertTrue(neu.getProperty(this.targetLocalnamePropertyBID).getValue()
				.toString().equals(
						IdentifierFunction.INSPIRE_IDENTIFIER_PREFIX + Messages.getString("IdentifierTest.18") //$NON-NLS-1$
								+ this.countryname + Messages.getString("IdentifierTest.19") + this.provName + Messages.getString("IdentifierTest.20") //$NON-NLS-1$ //$NON-NLS-2$
								+ this.prodName + Messages.getString("IdentifierTest.21") + this.sourceLocalname //$NON-NLS-1$
								+ Messages.getString("IdentifierTest.22") + this.localID + Messages.getString("IdentifierTest.23") + this.version)); //$NON-NLS-1$ //$NON-NLS-2$

	}
	
	@Test
	public void testTransformFeatureFeatureInspireIdentifierBinding() {

		Cell cell = this.getCell();

		// build source and target Features
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(this.sourceLocalname);
		ftbuilder.setNamespaceURI(this.sourceNamespace);
		ftbuilder.add(this.sourceLocalnamePropertyAID, String.class);
		SimpleFeatureType sourcetype = ftbuilder.buildFeatureType();
		
		SimpleFeatureTypeBuilder ftbuilder2 = new SimpleFeatureTypeBuilder();
		ftbuilder2.setName(this.targetLocalname);
		ftbuilder2.setNamespaceURI(this.targetNamespace);
		ftbuilder2.add(this.targetLocalnamePropertyBID, InspireIdentifier.class);
		SimpleFeatureType targettype = ftbuilder2.buildFeatureType();
		

		Feature source = SimpleFeatureBuilder.build(
				sourcetype, new Object[]{Messages.getString("IdentifierTest.24")}, Messages.getString("IdentifierTest.25")); //$NON-NLS-1$ //$NON-NLS-2$
		Feature target = SimpleFeatureBuilder.build(
				targettype, new Object[]{}, Messages.getString("IdentifierTest.26")); //$NON-NLS-1$

		// perform actual test
		IdentifierFunction idf = new IdentifierFunction();
		idf.configure(cell);

		Feature neu = idf.transform(source, target);
		
		
		InspireIdentifier itemp = (InspireIdentifier)neu.getProperty(
				this.targetLocalnamePropertyBID).getValue();
		System.out.println(Messages.getString("IdentifierTest.27") + IdentifierFunction.INSPIRE_IDENTIFIER_PREFIX+Messages.getString("IdentifierTest.28")+itemp.getNameSpace()+Messages.getString("IdentifierTest.29")+itemp.getLocalID()+Messages.getString("IdentifierTest.30")+itemp.getVersionID()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		
		InspireIdentifier expectedII = this.getInspireIDToCheck();
		// this is the geographical name result
		InspireIdentifier receivedII = (InspireIdentifier)target.getProperty(
				this.targetLocalnamePropertyBID).getValue();
		//check value
		assertTrue(receivedII.equals(expectedII));

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
	
	
	private InspireIdentifier getInspireIDToCheck()
	{
		// build the expected inspireIdentifier as result
		InspireIdentifier ii = new InspireIdentifier();
		ii.setLocalID(localID);
		ii.setNameSpace(this.countryname+Messages.getString("IdentifierTest.31")+this.provName+Messages.getString("IdentifierTest.32")+this.prodName+Messages.getString("IdentifierTest.33")+this.sourceLocalname); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ii.setVersionID(this.version);
		return ii;
	}
	
	private Cell getCell() {
		Cell cell = new Cell();
		Transformation t = new Transformation();
		t.setService(new Resource(IdentifierFunction.class.toString()));
		t.getParameters().add(new Parameter(Messages.getString("IdentifierTest.34"), this.countryname)); //$NON-NLS-1$
		t.getParameters().add(new Parameter(Messages.getString("IdentifierTest.35"), this.provName)); //$NON-NLS-1$
		t.getParameters().add(new Parameter(Messages.getString("IdentifierTest.36"), this.prodName)); //$NON-NLS-1$
		t.getParameters().add(new Parameter(Messages.getString("IdentifierTest.37"), this.version)); //$NON-NLS-1$
		Property p1 = new Property(new About(this.sourceNamespace, this.sourceLocalname, this.sourceLocalnamePropertyAID));
		p1.setTransformation(t);
		cell.setEntity1(p1);
		cell.setEntity2(new Property(new About(this.targetNamespace, this.targetLocalname, this.targetLocalnamePropertyBID)));
		return cell;
	}
}
