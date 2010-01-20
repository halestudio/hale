/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.corefunctions.inspire;

import static org.junit.Assert.*;

import java.util.Locale;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.inspire.data.GeographicalName;
import eu.esdihumboldt.inspire.data.GrammaticalGenderValue;
import eu.esdihumboldt.inspire.data.GrammaticalNumberValue;
import eu.esdihumboldt.inspire.data.NameStatusValue;
import eu.esdihumboldt.inspire.data.NativenessValue;
import eu.esdihumboldt.inspire.data.PronunciationOfName;
import eu.esdihumboldt.inspire.data.SpellingOfName;



/**
 * This is the test for the GeographicalNameFunction 
 *  @see eu.esdihumboldt.cst.corefunctions.inspire.GeographicalNameFunction (eu.esdihumboldt.cst.corefunctions.inspire)
 * 
 * @author Ana Belen Anton 
 * @partner 02 / ETRA Research and Development
 * @version $Id$ 
 */

public class GeographicalNameFunctionTest {

	public static String sourceLocalName = "FT1";
	public static String sourceLocalnameProperty = "NAME";
	public static String sourceNamespace = "http://www.esdi-humboldt.eu";
	
	public static String targetLocalName = "FT2";
	public static String targetLocalNameProperty = "geographicalName";
	public static String targetNamespace = "urn:x-inspire:specification:gmlas-v31:Hydrography:2.0";
	
	public static String name_value = "Danube";
	public static Locale language = Locale.ENGLISH;
	public static NameStatusValue name_status = NameStatusValue.official;
	public static NativenessValue nativeness = NativenessValue.endonym;
	public static GrammaticalGenderValue grammatical_gender = GrammaticalGenderValue.neuter;
	public static GrammaticalNumberValue grammatical_number = GrammaticalNumberValue.singular;
	
	@Test
	public void testTransformFeatureFeature() {
		
		//build source and target Features
		SimpleFeatureType sourceType = this.getFeatureType(
				GeographicalNameFunctionTest.sourceNamespace,
			    GeographicalNameFunctionTest.sourceLocalName, 
			    String.class);
		SimpleFeatureType targetType = this.getFeatureType(
				GeographicalNameFunctionTest.targetNamespace,
			    GeographicalNameFunctionTest.targetLocalName, 
			    GeographicalName.class);
		
		Feature source = SimpleFeatureBuilder.build(
				sourceType, new Object[]{name_value}, "1");
		Feature target = SimpleFeatureBuilder.build(
				targetType, new Object[]{}, "2");
		
		//perform actual test
		GeographicalNameFunction gnf = new GeographicalNameFunction();
		gnf.configure(GeographicalNameFunctionTest.getTestCell());
		Feature result = gnf.transform(source, target);
		
		PropertyDescriptor pd =  result.getProperty(
				GeographicalNameFunctionTest.targetLocalNameProperty).getDescriptor();
		//check binding
		assertTrue(pd.getType().getBinding().getName().equals(GeographicalName.class.getName()));
		
		// build the geographical name expected 
		GeographicalName expectedGN = setGeographicalNameResult();
		// this is the geographical name result
		GeographicalName receivedGN = (GeographicalName)target.getProperty(
				GeographicalNameFunctionTest.targetLocalNameProperty).getValue();
		//check value
		assertTrue(receivedGN.equals(expectedGN));
	
	}
	
	private SimpleFeatureType getFeatureType(String featureTypeNamespace, String featureTypeName, Class <? extends Object> name) {
		
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			if (name.getName().equals(String.class.getName()))
				ftbuilder.add(sourceLocalnameProperty, name);
			if (name.getName().equals(GeographicalName.class.getName()))
				ftbuilder.add(targetLocalNameProperty, name);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}
	
	private static ICell getTestCell(){
		
		//set up cell to use for testing
		Cell cell = new Cell();
		
		ComposedProperty cp = new ComposedProperty( 
				new About(sourceNamespace, sourceLocalName));
		cp.getCollection().add(new Property(
				new About(sourceNamespace, sourceLocalName, 
						sourceLocalnameProperty)));

		//set transformation and parameters 
		Transformation t = new Transformation();
		t.setService(new Resource(GeographicalNameFunction.class.getName()));
		
		t.getParameters().add( 
				new Parameter ("language", language.toString()));
		t.getParameters().add( 
				new Parameter ("nameStatus", name_status.toString()));
		t.getParameters().add( 
				new Parameter ("nativeness", nativeness.toString()));
		t.getParameters().add( 
				new Parameter ("grammaticalGender", grammatical_gender.toString()));
		t.getParameters().add( 
				new Parameter ("grammaticalNumber", grammatical_number.toString()));
		cp.setTransformation(t);
		
		//set entities
		cell.setEntity1(cp);
		cell.setEntity2(new Property ( 
				new About (targetNamespace, targetLocalName, 
						targetLocalNameProperty)));
		return cell;
	}
	
	private GeographicalName setGeographicalNameResult()
	{
		// build the expected geographicalname as result
		GeographicalName gn = new GeographicalName();
		
		SpellingOfName sn = new SpellingOfName();
		sn.setText(name_value);
		
		PronunciationOfName pn = new PronunciationOfName();
		
		gn.setSpelling(sn);
		gn.setPronunciation(pn);
		gn.setLanguage(language.toString());
		gn.setNameStatus(name_status);
		gn.setNativeness(nativeness);
		gn.setGrammaticalGender(grammatical_gender);
		gn.setGrammaticalNumber(grammatical_number);
		
		return gn;
	}
}
