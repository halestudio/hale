/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.goml.oml.io;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.PropertyComposition;
import eu.esdihumboldt.goml.omwg.PropertyOperator;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;
import eu.esdihumboldt.mediator.TransformationQueueManager.ProcessStatus;

/**
 * A Test for the deserialization of the PropertyComposition.
 *
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class OmlRdfReaderPropertyCompositionTest {

	/** location of the schema1 */
	private final static String SCHEMA_1_LOCATION = "source.xsd";
	/** name of the formalism1 */
	private final static String FORMALISM_1_NAME = "XSD";
	/** location of the schema1 */
	private final static String SCHEMA_2_LOCATION = "HydroPhysicalWaters.xsd";
	/** name of the formalism1 */
	private final static String FORMALISM_2_NAME = "XSD";
	
	/** RDF About for the schema1 */
	public final static String RDF_ABOUT_SCHEMA_1 ="targetNamespace-of-source";
	
	/** RDF About for the schema2 */
	public final static String RDF_ABOUT_SCHEMA_2 ="urn:x-inspire:specification:gmlas:HydroPhysicalWaters:3.0";
	
	
	private Alignment alignment;
	
	/**
	 * Creates the expected OMLObject containing PropertyComposition
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.alignment = new Alignment();
		this.alignment.setAbout(new About(UUID.randomUUID()));
		this.alignment.setLevel("PropertyCompositionTest");
		this.alignment.setLevel("2OMWG");
		//creates formalism
		Formalism formalism1 = new Formalism(FORMALISM_1_NAME, null);
		
		//creates schema1
		Schema schema1 = new Schema(SCHEMA_1_LOCATION, formalism1);
		//sets rdf:about for the schema1
		schema1.setAbout(new About(RDF_ABOUT_SCHEMA_1));
		this.alignment.setSchema1(schema1);
		//creates formalism2
		Formalism formalism2 = new Formalism(FORMALISM_2_NAME, null);
		
		//creates schema2
		Schema schema2 = new Schema(SCHEMA_2_LOCATION, formalism2);
		//sets rdf:about for the schema2
		schema2.setAbout(new About(RDF_ABOUT_SCHEMA_2));
	    this.alignment.setSchema2(schema2);
		//creates map
	    List<ICell> map = new ArrayList<ICell>();
		// set up cell to use for testing
		Cell cell = new Cell();
		//create entity1
		Entity entity1 = new Property(new About(""));
		
		//create Transformation
		Transformation transformation1 = new Transformation(new Resource("eu.esdihumboldt.cst.transformer.impl.GeographicalNameFunction"));
		entity1.setTransformation(transformation1);
		//create PropertyComposition
		//1. create list of the properties
		
		List<Property> properties = new ArrayList<Property>();
		//1.0 create first item  and put it to the properties list
		Property item1 = new Property(new About(""));
		//1.0.1 create Transformation for the item1
		Transformation item1Transformation = new Transformation(new Resource("eu.esdihumboldt.cst.transformer.impl.GeographicalNameFunction"));
		List<IParameter> item1Params = new ArrayList<IParameter>();
		item1Params.add(new Parameter("language", "ger"));
		item1Params.add(new Parameter("nativeness","endonym"));
		item1Params.add(new Parameter("nameStatus","official") );
		item1Params.add(new Parameter("sourceOfName","sourceOfName0"));
		item1Params.add(new Parameter("pronunciationIPA",""));
		item1Params.add(new Parameter("pronunciationSoundLink",""));
		item1Params.add(new Parameter("grammaticalGender",""));
		item1Params.add(new Parameter("grammaticalNumber",""));
		item1Transformation.setParameters(item1Params);
		item1.setTransformation(item1Transformation);
		
		//1.0.2 create PropertyComposition for the item1
		//create a properties list
		List<Property> subCollection = new ArrayList<Property>();
		Property prop1 = new Property(new About("GermanName_LatnScript"));
		Transformation tranProp1 = new Transformation(new Resource("eu.esdihumboldt.cst.transformer.impl.SpellingFunction"));
		List<IParameter> tranProp1Params = new ArrayList<IParameter>();
		tranProp1Params.add(new Parameter("text","GermanName"));
		tranProp1Params.add(new Parameter("script", "Latn"));
		tranProp1Params.add(new Parameter("transliterationScheme", ""));
		tranProp1.setParameters(tranProp1Params);
		prop1.setTransformation(tranProp1);
		subCollection.add(prop1);
		Property prop2 = new Property(new About("GermanName_GreekScript"));
		Transformation tranProp2 = new Transformation(new Resource("eu.esdihumboldt.cst.transformer.impl.SpellingFunction"));
		List<IParameter> tranProp2Params = new ArrayList<IParameter>();
		tranProp2Params.add(new Parameter("text","GermanNameGreekScript"));
		tranProp2Params.add(new Parameter("script", "Grek"));
		tranProp2Params.add(new Parameter("transliterationScheme", ""));
		tranProp2.setParameters(tranProp1Params);
		prop2.setTransformation(tranProp1);
		subCollection.add(prop2);
		PropertyComposition subComposition = new PropertyComposition(PropertyOperator.UNION, subCollection);
		item1.setPropertyComposition(subComposition);
		//1.0.3 put item1 to the list
		properties.add(item1);
	
		//1.1 create second item and put it to the properties list
		Property item2 = new Property(new About(""));
		Transformation item2Transformation = new Transformation(new Resource("eu.esdihumboldt.cst.transformer.impl.GeographicalNameFunction"));
		List<IParameter> item2Params = new ArrayList<IParameter>();
		item2Params.add(new Parameter("language", "eng"));
		item2Params.add(new Parameter("nativeness","exonym"));
		item2Params.add(new Parameter("nameStatus","official") );
		item2Params.add(new Parameter("sourceOfName","sourceOfName1"));
		item2Params.add(new Parameter("pronunciationIPA",""));
		item2Params.add(new Parameter("pronunciationSoundLink",""));
		item2Params.add(new Parameter("grammaticalGender",""));
		item2Params.add(new Parameter("grammaticalNumber",""));
		item2Transformation.setParameters(item1Params);
		item2.setTransformation(item2Transformation);
		//1.1.2 create PropertyComposition for the item2
		//TODO add implementation
		Property subProperty = new Property(new About("EnglishName_LatnScript"));
		Transformation subTrans = new Transformation (new Resource("eu.esdihumboldt.cst.transformer.impl.SpellingFunction"));
		List<IParameter> subTransParams = new ArrayList<IParameter>();
		subTransParams.add(new Parameter("text", "EnglishName"));
		subTransParams.add(new Parameter("script", "Latn"));
		subTransParams.add(new Parameter("transliterationScheme",""));
		subTrans.setParameters(subTransParams);
		subProperty.setTransformation(subTrans);
		PropertyComposition subPropComposition = new PropertyComposition(PropertyOperator.UNION, subProperty);
		item2.setPropertyComposition(subPropComposition);
		//1.1.3 put item2 to the list
		properties.add(item2);
		//2.0  create PropertyComposition for this properties-list
		PropertyComposition  propComp = new PropertyComposition(PropertyOperator.UNION,properties);
		//3.0  add PropertyComposition to the Entity1
		((Property)entity1).setPropertyComposition(propComp);
		cell.setEntity1(entity1);
		
		//create entity2
		Entity entity2 = new Property(new About("hy-p:geographicalName"));  
		//create Domain restriction for the Entity2
		List<FeatureClass> domRestrictions = new ArrayList<FeatureClass>();
        FeatureClass domRestr = new FeatureClass(new About("hy-p:Watercourse"));
        domRestrictions.add(domRestr);
        ((Property)entity2).setDomainRestriction(domRestrictions);
        cell.setEntity2(entity2);
        map.add(cell);
        alignment.setMap(map);
		
		
		
	}
	
	@Test
	 public void testPropertyComposition(){
		//TODO add implementation
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
