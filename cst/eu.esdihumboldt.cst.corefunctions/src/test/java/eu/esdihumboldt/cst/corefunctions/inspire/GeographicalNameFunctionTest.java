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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureImpl;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.corefunctions.util.TypeLoader;
import eu.esdihumboldt.cst.transformer.service.rename.FeatureBuilder;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.inspire.data.GrammaticalGenderValue;
import eu.esdihumboldt.inspire.data.GrammaticalNumberValue;
import eu.esdihumboldt.inspire.data.NameStatusValue;
import eu.esdihumboldt.inspire.data.NativenessValue;



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
	public static String targetLocalNameProperty = "name";
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
			
		// ************* BUIL SOURCE AND TARGET FEATURES ****************
	   SimpleFeatureType sourceType = this.getFeatureType(
				GeographicalNameFunctionTest.sourceNamespace,
			    GeographicalNameFunctionTest.sourceLocalName,
			    String[].class);

		String url = getClass().getResource(
		"inspire_v3.0_xsd/CadastralParcels.xsd").toString();
		FeatureType targetType = TypeLoader.getType("CadastralZoning", url);
		
		Feature source = FeatureBuilder.buildFeature(sourceType, null,false);
		source.getProperty(sourceLocalnameProperty).setValue(name_value);
		source.getProperty(sourceLocalnameProperty2).setValue(nom_value);
		source.getProperty(sourceLocalnameProperty3).setValue(nombre_value);
		
		Feature target = FeatureBuilder.buildFeature(targetType,null,false);	
		
		// ************* PERFORM ACTUAL TEST ****************
		GeographicalNameFunction gnf = new GeographicalNameFunction();
		OmlRdfReader reader = new OmlRdfReader();
		String alignmentUrl = GeographicalNameFunctionTest.class.getResource("PropertyCompositionTest.xml").toExternalForm();
		Alignment al = null;
		try {
			al = reader.read(new URL(alignmentUrl));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gnf.configure(al.getMap().get(0));
		//gnf.configure(GeographicalNameFunctionTest.getTestCell());
	    Feature result = gnf.transform(source, target);
				
		// ************* BUILD THE EXPECTED FEATURE ****************
		Feature expectedGN = setGeographicalNameResult(targetType);
		
		// ************* CHECK EQUALITY OF EXPECTED AND RECEIVED FEATURES ****************
		AttributeImpl resultgn=(AttributeImpl)result.getProperty("name");
		
		
		AttributeImpl expectedGNgn = (AttributeImpl)expectedGN.getProperty("name");
		
		assertEquals(resultgn,expectedGNgn);
		//assertTrue(1==1);
		//assertTrue(expectedGN.equals(result.equals(expectedGN)));
	}
	
	/**
	 * Function getFeatureType
	 * @param featureTypeNamespace
	 * @param featureTypeName
	 * @param name
	 * @return FeatureType
	 */
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
	
	/**
	 * Function getTestCell
	 * @return An ICell containing data for configure GeographicNameFunction
	 */
	private static ICell getTestCell(){
		// ************* SET UP CELL TO USE FOR TESTING ****************

		Cell testcell = new Cell();
		
		ComposedProperty cp = new ComposedProperty( 
				new About(sourceNamespace, sourceLocalName));
		
		ComposedProperty cpsp1 = new ComposedProperty(new About(sourceNamespace, sourceLocalName));
		ComposedProperty cpsp2 = new ComposedProperty(new About(sourceNamespace, sourceLocalName));
		
		Property p1 = new Property(new About(sourceNamespace, sourceLocalName,sourceLocalnameProperty));
		Property p2 = new Property(new About(sourceNamespace, sourceLocalName,sourceLocalnameProperty2));
		Property p3 = new Property(new About(sourceNamespace, sourceLocalName,sourceLocalnameProperty3));
		
		// ************* SET TRANSFORMATION OF EACH MAPPING ****************
		Transformation transform1 = new Transformation();
		transform1.setService(new Resource(GeographicalNameFunction.class.getName()));
		
		transform1.getParameters().add( 
				new Parameter ("language", language.toString()));
		transform1.getParameters().add( 
				new Parameter ("nameStatus", name_status.toString()));
		transform1.getParameters().add( 
				new Parameter ("nativeness", nativeness.toString()));
		transform1.getParameters().add( 
				new Parameter ("grammaticalGender", grammatical_gender.toString()));
		transform1.getParameters().add( 
				new Parameter ("grammaticalNumber", grammatical_number.toString()));
		cpsp1.setTransformation(transform1);
		
		Transformation transform2 = new Transformation();
		transform2.setService(new Resource(GeographicalNameFunction.class.getName()));
		
		transform2.getParameters().add( 
				new Parameter ("language", language2.toString()));
		transform2.getParameters().add( 
				new Parameter ("nameStatus", name_status.toString()));
		transform2.getParameters().add( 
				new Parameter ("nativeness", nativeness.toString()));
		transform2.getParameters().add( 
				new Parameter ("grammaticalGender", grammatical_gender.toString()));
		transform2.getParameters().add( 
				new Parameter ("grammaticalNumber", grammatical_number.toString()));
		cpsp2.setTransformation(transform2);
		
		// ************* SET TRANSFORMATION OF EACH PROPERTY ****************
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
		
		// ************* SET MAPPING COMPOSED PROPERTIES WITH PROPERTIES ****************
		cpsp1.getCollection().add(p1);
		cpsp1.getCollection().add(p3);
		cpsp2.getCollection().add(p2);
		
		// ************* SET MAIN COMPOSED PROPERTY WITH MAPPING ONES ****************
		cp.getCollection().add(cpsp1);
		cp.getCollection().add(cpsp2);
		
		// ************* SET ENTITIES ****************
		testcell.setEntity1(cp);
		testcell.setEntity2(new Property ( 
				new About (targetNamespace, targetLocalName, 
						targetLocalNameProperty)));
		return testcell;
	}
	
	/**
	 * 
	 * @param targettype: Type required to build feature in a correct way
	 * @return a test feature to compare with resulting one of transform function
	 */
	private Feature setGeographicalNameResult(FeatureType targettype)
	{
		// ************* CREATION OF THE TARGET ****************
		Feature target = FeatureBuilder.buildFeature(targettype,null,false);
		
		// ************* OBTAINING BASIC TYPES OF ATTRIBUTES ****************
		PropertyType pt = target.getProperty(
				targetLocalNameProperty).getType();
		SimpleFeatureType geoNameType = (SimpleFeatureType)
						((SimpleFeatureType) pt).getDescriptor("GeographicalName").getType();
		SimpleFeatureType spellingofnamepropertytype = (SimpleFeatureType) 
						geoNameType.getDescriptor("spelling").getType();
		SimpleFeatureType spellingofnametype = (SimpleFeatureType) 
						(spellingofnamepropertytype.getDescriptor("SpellingOfName")).getType();
		SimpleFeatureType pronunciationofnametype = (SimpleFeatureType) ((SimpleFeatureType) 
						(geoNameType).getDescriptor("pronunciation").getType()).getDescriptor("PronunciationOfName").getType();

		
		// ************* CREATION OF THE COLLECTION OF GEOGRAPHICALNAMES ****************
		Collection<FeatureImpl> geographicalnames=new HashSet<FeatureImpl>();
		
		// ************* CREATION OF THE COLLECTIONS OF SPELLINGS ****************
		Collection<FeatureImpl> colecc=new HashSet<FeatureImpl>();
		Collection<FeatureImpl> colecc2=new HashSet<FeatureImpl>();
		
		// ************* SET UP THREE SPELLINGS ****************
		FeatureImpl spellingofname1 = (FeatureImpl)FeatureBuilder.buildFeature(spellingofnametype, null, false);
		spellingofname1.getProperty("script").setValue("script1");
		spellingofname1.getProperty("text").setValue(name_value);
		FeatureImpl spellingofnameproperty1 = (FeatureImpl)FeatureBuilder.buildFeature(spellingofnamepropertytype, null, false);
		spellingofnameproperty1.getProperty("SpellingOfName").setValue(Collections.singleton(spellingofname1));
		
		FeatureImpl spellingofname2 = (FeatureImpl) FeatureBuilder.buildFeature(spellingofnametype, null, false);
		spellingofname2.getProperty("script").setValue("script2");
		spellingofname2.getProperty("text").setValue(nom_value);
		FeatureImpl spellingofnameproperty2 = (FeatureImpl)FeatureBuilder.buildFeature(spellingofnamepropertytype,null, false);
		spellingofnameproperty2.getProperty("SpellingOfName").setValue(Collections.singleton(spellingofname2));
		
		FeatureImpl spellingofname3 = (FeatureImpl) FeatureBuilder.buildFeature(spellingofnametype,null, false);
		spellingofname3.getProperty("script").setValue("script3");
		spellingofname3.getProperty("text").setValue(nombre_value);
		FeatureImpl spellingofnameproperty3 = (FeatureImpl)FeatureBuilder.buildFeature(spellingofnamepropertytype, null, false);
		spellingofnameproperty3.getProperty("SpellingOfName").setValue(Collections.singleton(spellingofname3));
		
		// ************* JOIN TWO SPELLINGS IN SAME GEOGRAPHICALNAME  ****************
		colecc.add(spellingofnameproperty1);
		colecc.add(spellingofnameproperty3);
		
		// ************* THE OTHER SPELLING WILL BE IN ANOTHER GEOGRAPHICALNAME  ****************
		colecc2.add(spellingofnameproperty2);
		
		// ************* CREATION OF TWO PRONUNCIATIONS (VOID)  ****************
		FeatureImpl pronunciation1 = (FeatureImpl) FeatureBuilder.buildFeature(pronunciationofnametype, null, false);
		pronunciation1.getProperty("pronunciationIPA").setValue(null);
		pronunciation1.getProperty("pronunciationSoundLink").setValue(null);
		
		
		FeatureImpl pronunciation2 = (FeatureImpl) FeatureBuilder.buildFeature(pronunciationofnametype, null, false);
		pronunciation2.getProperty("pronunciationIPA").setValue(null);
		pronunciation2.getProperty("pronunciationSoundLink").setValue(null);

		// ************* SET UP AND FILL TWO GEOGRAPHICALNAMES ****************
		FeatureImpl geographicalname1 = (FeatureImpl) FeatureBuilder.buildFeature((FeatureType)geoNameType, null, false);
		FeatureImpl geographicalname2 = (FeatureImpl) FeatureBuilder.buildFeature((FeatureType)geoNameType, null, false);
		
		geographicalname1.getProperty("spelling").setValue(colecc);
		geographicalname1.getProperty("language").setValue(language.toString());
		geographicalname1.getProperty("sourceOfName").setValue("");
		geographicalname1.getProperty("nativeness").setValue(Collections.singleton(nativeness.toString()));
		geographicalname1.getProperty("nameStatus").setValue(Collections.singleton(name_status.toString()));
		geographicalname1.getProperty("grammaticalGender").setValue(Collections.singleton(grammatical_gender.toString()));
		geographicalname1.getProperty("grammaticalNumber").setValue(Collections.singleton(grammatical_number.toString()));
		geographicalname1.getProperty("pronunciation").setValue(Collections.singleton(pronunciation1));
		
		geographicalname2.getProperty("spelling").setValue(colecc2);
		geographicalname2.getProperty("language").setValue(language2.toString());
		geographicalname2.getProperty("sourceOfName").setValue("");
		geographicalname2.getProperty("nativeness").setValue(Collections.singleton(nativeness.toString()));
		geographicalname2.getProperty("nameStatus").setValue(Collections.singleton(name_status.toString()));
		geographicalname2.getProperty("grammaticalGender").setValue(Collections.singleton(grammatical_gender.toString()));
		geographicalname2.getProperty("grammaticalNumber").setValue(Collections.singleton(grammatical_number.toString()));
		geographicalname2.getProperty("pronunciation").setValue(Collections.singleton(pronunciation2));
		
		
		geographicalnames.add(geographicalname1);
		geographicalnames.add(geographicalname2);

		// ************* SET UP FINAL FEATURE  ****************
		//((SimpleFeature)target).setAttribute(targetLocalNameProperty, geographicalnames);
		target.getProperty(targetLocalNameProperty).setValue(geographicalnames);
		/*
		// ************* CREATION OF THE TARGET ****************
		SimpleFeature target = SimpleFeatureBuilder.build((SimpleFeatureType)targettype, new Object[]{}, "2");
		
		// ************* OBTAINING BASIC TYPES OF ATTRIBUTES ****************
		PropertyType pt = target.getProperty(
				targetLocalNameProperty).getType();
		SimpleFeatureType geoNameType = (SimpleFeatureType)
						((SimpleFeatureType) pt).getDescriptor("GeographicalName").getType();
		SimpleFeatureType spellingofnamepropertytype = (SimpleFeatureType) 
						geoNameType.getDescriptor("spelling").getType();
		SimpleFeatureType spellingofnametype = (SimpleFeatureType) 
						(spellingofnamepropertytype.getDescriptor("SpellingOfName")).getType();
		SimpleFeatureType pronunciationofnametype = (SimpleFeatureType) ((SimpleFeatureType) 
						(geoNameType).getDescriptor("pronunciation").getType()).getDescriptor("PronunciationOfName").getType();

		
		// ************* CREATION OF THE COLLECTION OF GEOGRAPHICALNAMES ****************
		Collection<SimpleFeatureImpl> geographicalnames=new HashSet<SimpleFeatureImpl>();
		
		// ************* CREATION OF THE COLLECTIONS OF SPELLINGS ****************
		Collection<SimpleFeatureImpl> colecc=new HashSet<SimpleFeatureImpl>();
		Collection<SimpleFeatureImpl> colecc2=new HashSet<SimpleFeatureImpl>();
		
		// ************* SET UP THREE SPELLINGS ****************
		SimpleFeatureImpl spellingofname1 = (SimpleFeatureImpl) SimpleFeatureBuilder.build(spellingofnametype, new Object[]{},"SpellingOfName");
		spellingofname1.setAttribute("script", "script1");
		spellingofname1.setAttribute("text", name_value);
		SimpleFeatureImpl spellingofnameproperty1 = (SimpleFeatureImpl)SimpleFeatureBuilder.build(spellingofnamepropertytype, new Object[]{},"SpellingOfNameProperty");
		spellingofnameproperty1.setAttribute("SpellingOfName", Collections.singleton(spellingofname1));
		
		SimpleFeatureImpl spellingofname2 = (SimpleFeatureImpl) SimpleFeatureBuilder.build(spellingofnametype, new Object[]{},"SpellingOfName");
		spellingofname2.setAttribute("script", "script2");
		spellingofname2.setAttribute("text", nom_value);
		SimpleFeatureImpl spellingofnameproperty2 = (SimpleFeatureImpl)SimpleFeatureBuilder.build(spellingofnamepropertytype, new Object[]{},"SpellingOfNameProperty");
		spellingofnameproperty2.setAttribute("SpellingOfName", Collections.singleton(spellingofname2));
		
		SimpleFeatureImpl spellingofname3 = (SimpleFeatureImpl) SimpleFeatureBuilder.build(spellingofnametype, new Object[]{},"SpellingOfName");
		spellingofname3.setAttribute("script", "script3");
		spellingofname3.setAttribute("text", nombre_value);
		SimpleFeatureImpl spellingofnameproperty3 = (SimpleFeatureImpl)SimpleFeatureBuilder.build(spellingofnamepropertytype, new Object[]{},"SpellingOfNameProperty");
		spellingofnameproperty3.setAttribute("SpellingOfName", Collections.singleton(spellingofname3));
		
		// ************* JOIN TWO SPELLINGS IN SAME GEOGRAPHICALNAME  ****************
		colecc.add(spellingofnameproperty1);
		colecc.add(spellingofnameproperty3);
		
		// ************* THE OTHER SPELLING WILL BE IN ANOTHER GEOGRAPHICALNAME  ****************
		colecc2.add(spellingofnameproperty2);
		
		// ************* CREATION OF TWO PRONUNCIATIONS (VOID)  ****************
		SimpleFeatureImpl pronunciation1 = (SimpleFeatureImpl) SimpleFeatureBuilder.build(pronunciationofnametype, new Object[]{},"PronunctiationOfName");
		pronunciation1.setAttribute("pronunciationIPA",null);
		pronunciation1.setAttribute("pronunciationSoundLink",null);
		
		
		SimpleFeatureImpl pronunciation2 = (SimpleFeatureImpl) SimpleFeatureBuilder.build(pronunciationofnametype, new Object[]{},"PronunctiationOfName");
		pronunciation2.setAttribute("pronunciationIPA",null);
		pronunciation2.setAttribute("pronunciationSoundLink",null);

		// ************* SET UP AND FILL TWO GEOGRAPHICALNAMES ****************
		SimpleFeatureImpl geographicalname1 = (SimpleFeatureImpl) SimpleFeatureBuilder.build((SimpleFeatureType)geoNameType, new Object[]{},"GeographicalName");
		SimpleFeatureImpl geographicalname2 = (SimpleFeatureImpl) SimpleFeatureBuilder.build((SimpleFeatureType)geoNameType, new Object[]{},"GeographicalName");
		
		geographicalname1.setAttribute("spelling",colecc);
		geographicalname1.setAttribute("language",language.toString());
		geographicalname1.setAttribute("nativeness",Collections.singleton(nativeness.toString()));
		geographicalname1.setAttribute("nameStatus",Collections.singleton(name_status.toString()));
		geographicalname1.setAttribute("grammaticalGender",Collections.singleton(grammatical_gender.toString()));
		geographicalname1.setAttribute("grammaticalNumber",Collections.singleton(grammatical_number.toString()));
		geographicalname1.setAttribute("pronunciation",Collections.singleton(pronunciation1));
		geographicalname1.setAttribute("sourceOfName",null);
		
		geographicalname2.setAttribute("spelling",colecc2);
		geographicalname2.setAttribute("language",language2.toString());
		geographicalname2.setAttribute("nativeness",Collections.singleton(nativeness.toString()));
		geographicalname2.setAttribute("nameStatus",Collections.singleton(name_status.toString()));
		geographicalname2.setAttribute("grammaticalGender",Collections.singleton(grammatical_gender.toString()));
		geographicalname2.setAttribute("grammaticalNumber",Collections.singleton(grammatical_number.toString()));
		geographicalname2.setAttribute("pronunciation",Collections.singleton(pronunciation2));
		geographicalname2.setAttribute("sourceOfName",null);
		
		geographicalnames.add(geographicalname1);
		geographicalnames.add(geographicalname2);

		// ************* SET UP FINAL FEATURE  ****************
		((SimpleFeature)target).setAttribute(targetLocalNameProperty, geographicalnames);*/
		return target;
		
	}
}
