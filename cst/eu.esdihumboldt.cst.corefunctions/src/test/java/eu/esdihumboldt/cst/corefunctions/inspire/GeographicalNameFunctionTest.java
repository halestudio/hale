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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.util.TypeLoader;
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
	public static String sourceLocalnameProperty2 = "NOM";
	public static String sourceLocalnameProperty3 = "NOMBRE";
	public static String sourceNamespace = "http://www.esdi-humboldt.eu";
	
	public static String targetLocalName = "FT2";
	public static String targetLocalNameProperty = "Collection";
	public static String targetNamespace = "urn:x-inspire:specification:gmlas-v31:Hydrography:2.0";
	
	public static String name_value = "Danube";
	public static String nom_value = "Donau";
	public static String nombre_value="Danubio";
	public static Locale language = Locale.ENGLISH;
	public static Locale language2 = Locale.GERMAN;
	public static NameStatusValue name_status = NameStatusValue.official;
	public static NativenessValue nativeness = NativenessValue.endonym;
	public static GrammaticalGenderValue grammatical_gender = GrammaticalGenderValue.neuter;
	public static GrammaticalNumberValue grammatical_number = GrammaticalNumberValue.singular;
	
	@Test
	public void testTransformFeatureFeature() {
		String url = getClass().getResource("/inspire_v3.0_xsd/GeographicalNames.xsd").toString();
		FeatureType geoNameType = TypeLoader.getType("GeographicalName", url);
		//		"file:///d:/hale-workspace/eu.esdihumboldt.cst.corefunctions/" +
		//		"src/test/resource/inspire_v3.0_xsd/GeographicalNames.xsd");		
		
		//build source and target Features
		SimpleFeatureType sourceType = this.getFeatureType(
				GeographicalNameFunctionTest.sourceNamespace,
			    GeographicalNameFunctionTest.sourceLocalName,
			    String[].class);
			    //String[].getClass(){GeographicalNameFunctionTest3.name_value,GeographicalNameFunctionTest3.nom_value});
		SimpleFeatureType targetType = this.getFeatureType(
				GeographicalNameFunctionTest.targetNamespace,
			    GeographicalNameFunctionTest.targetLocalName, 
			    Collection.class);
		
		Feature source = SimpleFeatureBuilder.build(
				sourceType, new Object[]{name_value,nom_value,nombre_value}, "1");
		Feature target = SimpleFeatureBuilder.build(
				targetType, new Object[]{}, "2");
		
		//perform actual test
		GeographicalNameFunction gnf = new GeographicalNameFunction();
		gnf.configure(GeographicalNameFunctionTest.getTestCell());
		Feature result = gnf.transform(source, target);

		PropertyDescriptor pd =  result.getProperty(
				GeographicalNameFunctionTest.targetLocalNameProperty).getDescriptor();
		//check binding
		assertTrue(pd.getType().getBinding().getName().equals(Collection.class.getName()));
		
		// build the geographical name expected 
		Collection<GeographicalName> expectedGN = setGeographicalNameResult();
		// this is the geographical name result
		/*Collection<GeographicalName> col = (Collection<GeographicalName>)target.getProperty(
				GeographicalNameFunctionTest3.targetLocalNameProperty).getValue();
		
		for (Iterator<GeographicalName> coliter = col.iterator();coliter.hasNext();)
		{
			GeographicalName gn = coliter.next();
		}*/
		Collection<AttributeImpl> receivedGN = (Collection<AttributeImpl>)target.getProperty(
				GeographicalNameFunctionTest.targetLocalNameProperty).getValue();
		//check value
		assertTrue(receivedGN.size()==expectedGN.size());
		for (Iterator<AttributeImpl> reciter=receivedGN.iterator();reciter.hasNext();)
		{
			AttributeImpl atim = reciter.next();
			GeographicalName gnrec = (GeographicalName)atim.getValue();
			boolean isinside = false;
			for (Iterator<GeographicalName> expiter=expectedGN.iterator();expiter.hasNext();)
			{
				GeographicalName gnexp = expiter.next();
				if (gnrec.equals(gnexp))
				{
					isinside=true;
					break;
				}
			}
			assertTrue(isinside);
		}	
	}
	
	private SimpleFeatureType getFeatureType(String featureTypeNamespace, String featureTypeName, Class <? extends Object> name) {
		
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			if (name.getName().equals(String.class.getName()))
				ftbuilder.add(sourceLocalnameProperty, name);
			if (name.getName().equals(String[].class.getName()))
			{
				ftbuilder.add(sourceLocalnameProperty,String.class);
				ftbuilder.add(sourceLocalnameProperty2,String.class);
				ftbuilder.add(sourceLocalnameProperty3,String.class);
			}
			if (name.getName().equals(Collection.class.getName()))
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
		
		ComposedProperty cpsp1 = new ComposedProperty(new About(sourceNamespace, sourceLocalName));
		ComposedProperty cpsp2 = new ComposedProperty(new About(sourceNamespace, sourceLocalName));
		
		Property p1 = new Property(new About(sourceNamespace, sourceLocalName,sourceLocalnameProperty));
		Property p2 = new Property(new About(sourceNamespace, sourceLocalName,sourceLocalnameProperty2));
		Property p3 = new Property(new About(sourceNamespace, sourceLocalName,sourceLocalnameProperty3));
		
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
		cpsp1.setTransformation(t);
		
		Transformation t2 = new Transformation();
		t.setService(new Resource(GeographicalNameFunction.class.getName()));
		
		t2.getParameters().add( 
				new Parameter ("language", language2.toString()));
		t2.getParameters().add( 
				new Parameter ("nameStatus", name_status.toString()));
		t2.getParameters().add( 
				new Parameter ("nativeness", nativeness.toString()));
		t2.getParameters().add( 
				new Parameter ("grammaticalGender", grammatical_gender.toString()));
		t2.getParameters().add( 
				new Parameter ("grammaticalNumber", grammatical_number.toString()));
		p2.setTransformation(t);
		
		Transformation tsp1 = new Transformation();
		tsp1.setService(new Resource(GeographicalNameFunction.class.getName()));
		tsp1.getParameters().add(new Parameter ("script", "script1"));
		
		Transformation tsp2 = new Transformation();
		tsp2.setService(new Resource(GeographicalNameFunction.class.getName()));
		tsp2.getParameters().add(new Parameter ("script", "script2"));
		
		Transformation tsp3 = new Transformation();
		tsp3.setService(new Resource(GeographicalNameFunction.class.getName()));
		tsp3.getParameters().add(new Parameter ("script", "script3"));
		
		p1.setTransformation(tsp1);
		p2.setTransformation(tsp2);
		p3.setTransformation(tsp3);
		cpsp2.setTransformation(t2);
		
		cpsp1.getCollection().add(p1);
		cpsp1.getCollection().add(p3);
		cpsp2.getCollection().add(p2);
		cp.getCollection().add(cpsp1);
		cp.getCollection().add(cpsp2);
		
		//set entities
		cell.setEntity1(cp);
		cell.setEntity2(new Property ( 
				new About (targetNamespace, targetLocalName, 
						targetLocalNameProperty)));
		return cell;
	}
	
	private Collection<GeographicalName> setGeographicalNameResult()
	{
		// build the expected geographicalname as result
		Collection<GeographicalName> colgn=new HashSet<GeographicalName>();
		
		GeographicalName gn1 = new GeographicalName();
		GeographicalName gn2 = new GeographicalName();
		
		SpellingOfName sn1 = new SpellingOfName();
		SpellingOfName sn2 = new SpellingOfName();
		SpellingOfName sn3 = new SpellingOfName();
		sn1.setText(name_value);
		sn2.setText(nom_value);
		sn3.setText(nombre_value);
		sn1.setScript("script1");
		sn2.setScript("script2");
		sn3.setScript("script3");
		
		PronunciationOfName pn = new PronunciationOfName();
		
		gn1.addSpelling(sn1);
		gn1.addSpelling(sn3);
		gn1.setPronunciation(pn);
		gn1.setLanguage(language.toString());
		gn1.setNameStatus(name_status);
		gn1.setNativeness(nativeness);
		gn1.setGrammaticalGender(grammatical_gender);
		gn1.setGrammaticalNumber(grammatical_number);
		
		gn2.addSpelling(sn2);
		gn2.setPronunciation(pn);
		gn2.setLanguage(language2.toString());
		gn2.setNameStatus(name_status);
		gn2.setNativeness(nativeness);
		gn2.setGrammaticalGender(grammatical_gender);
		gn2.setGrammaticalNumber(grammatical_number);
		
		colgn.add(gn1);
		colgn.add(gn2);
		
		return colgn;
	}
}
