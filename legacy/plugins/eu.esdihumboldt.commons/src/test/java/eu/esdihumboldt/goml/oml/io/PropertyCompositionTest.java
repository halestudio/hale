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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.align.Formalism;
import eu.esdihumboldt.commons.goml.align.Schema;
import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.commons.goml.omwg.ComposedProperty;
import eu.esdihumboldt.commons.goml.omwg.ComposedProperty.PropertyOperatorType;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.commons.goml.rdf.Resource;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;
import eu.esdihumboldt.specification.cst.align.ext.ITransformation;

/**
 * A Test for the deserialization of the PropertyComposition.
 * 
 * 
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public class PropertyCompositionTest {

	/** location of the schema1 */
	private final static String SCHEMA_1_LOCATION = "source.xsd";
	/** name of the formalism1 */
	private final static String FORMALISM_1_NAME = "XSD";
	/** location of the schema1 */
	private final static String SCHEMA_2_LOCATION = "HydroPhysicalWaters.xsd";
	/** name of the formalism1 */
	private final static String FORMALISM_2_NAME = "XSD";

	/** RDF About for the schema1 */
	public final static String RDF_ABOUT_SCHEMA_1 = "targetNamespace-of-source";

	/** RDF About for the schema2 */
	public final static String RDF_ABOUT_SCHEMA_2 = "urn:x-inspire:specification:gmlas:HydroPhysicalWaters:3.0";

	private Alignment alignment;

	/**
	 * Creates the expected OMLObject containing PropertyComposition
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		/*
		 * creates an OML structure according to the jaxb-based mapping:
		 * 
		 * ComposedProperty entity1|-->ComposedProperty item1|-->Property prop1
		 * | |-->Property prop2 |-->ComposedProperty item2 --> Property
		 * subProperty
		 */

		this.alignment = new Alignment();
		this.alignment.setAbout(new About(UUID.randomUUID()));
		this.alignment.setLevel("PropertyCompositionTest");
		this.alignment.setLevel("2OMWG");
		// creates formalism
		Formalism formalism1 = new Formalism(FORMALISM_1_NAME, null);

		// creates schema1
		Schema schema1 = new Schema(SCHEMA_1_LOCATION, formalism1);
		// sets rdf:about for the schema1
		schema1.setAbout(new About(RDF_ABOUT_SCHEMA_1));
		this.alignment.setSchema1(schema1);
		// creates formalism2
		Formalism formalism2 = new Formalism(FORMALISM_2_NAME, null);

		// creates schema2
		Schema schema2 = new Schema(SCHEMA_2_LOCATION, formalism2);
		// sets rdf:about for the schema2
		schema2.setAbout(new About(RDF_ABOUT_SCHEMA_2));
		this.alignment.setSchema2(schema2);
		// creates map
		List<ICell> map = new ArrayList<ICell>();
		// set up cell to use for testing
		Cell cell = new Cell();
		// create entity1 as Composed Property
		Entity entity1 = new ComposedProperty(PropertyOperatorType.OR,
				new About(""));

		// create Transformation
		Transformation transformation1 = new Transformation(
				new Resource(
						"eu.esdihumboldt.cst.transformer.impl.GeographicalNameFunction"));
		entity1.setTransformation(transformation1);

		// 1. create list of the properties

		List<Property> properties = new ArrayList<Property>();
		// 1.0 create first item as ComposedProperty and put it to the
		// properties list
		ComposedProperty item1 = new ComposedProperty(PropertyOperatorType.OR,
				new About(""));
		// 1.0.1 create Transformation for the item1
		Transformation item1Transformation = new Transformation(
				new Resource(
						"eu.esdihumboldt.cst.transformer.impl.GeographicalNameFunction"));
		List<IParameter> item1Params = new ArrayList<IParameter>();
		item1Params.add(new Parameter("language", "ger"));
		item1Params.add(new Parameter("nativeness", "endonym"));
		item1Params.add(new Parameter("nameStatus", "official"));
		item1Params.add(new Parameter("sourceOfName", "sourceOfName0"));
		item1Params.add(new Parameter("pronunciationIPA", ""));
		item1Params.add(new Parameter("pronunciationSoundLink", ""));
		item1Params.add(new Parameter("grammaticalGender", ""));
		item1Params.add(new Parameter("grammaticalNumber", ""));
		item1Transformation.setParameters(item1Params);
		item1.setTransformation(item1Transformation);

		// create a properties list for the ComposedProperty item1
		List<Property> subCollection = new ArrayList<Property>();
		Property prop1 = new Property(new About("GermanName_LatnScript"));
		Transformation tranProp1 = new Transformation(new Resource(
				"eu.esdihumboldt.cst.transformer.impl.SpellingFunction"));
		List<IParameter> tranProp1Params = new ArrayList<IParameter>();
		tranProp1Params.add(new Parameter("text", "GermanName"));
		tranProp1Params.add(new Parameter("script", "Latn"));
		tranProp1Params.add(new Parameter("transliterationScheme", ""));
		tranProp1.setParameters(tranProp1Params);
		prop1.setTransformation(tranProp1);
		subCollection.add(prop1);
		Property prop2 = new Property(new About("GermanName_GreekScript"));
		Transformation tranProp2 = new Transformation(new Resource(
				"eu.esdihumboldt.cst.transformer.impl.SpellingFunction"));
		List<IParameter> tranProp2Params = new ArrayList<IParameter>();
		tranProp2Params.add(new Parameter("text", "GermanNameGreekScript"));
		tranProp2Params.add(new Parameter("script", "Grek"));
		tranProp2Params.add(new Parameter("transliterationScheme", ""));
		tranProp2.setParameters(tranProp1Params);
		prop2.setTransformation(tranProp1);
		subCollection.add(prop2);
		item1.setCollection(subCollection);

		// 1.0.3 put item1 to the list
		properties.add(item1);

		// 1.1 create second item as ComposedProperty and put it to the
		// properties list
		ComposedProperty item2 = new ComposedProperty(PropertyOperatorType.OR,
				new About(""));
		Transformation item2Transformation = new Transformation(
				new Resource(
						"eu.esdihumboldt.cst.transformer.impl.GeographicalNameFunction"));
		List<IParameter> item2Params = new ArrayList<IParameter>();
		item2Params.add(new Parameter("language", "eng"));
		item2Params.add(new Parameter("nativeness", "exonym"));
		item2Params.add(new Parameter("nameStatus", "official"));
		item2Params.add(new Parameter("sourceOfName", "sourceOfName1"));
		item2Params.add(new Parameter("pronunciationIPA", ""));
		item2Params.add(new Parameter("pronunciationSoundLink", ""));
		item2Params.add(new Parameter("grammaticalGender", ""));
		item2Params.add(new Parameter("grammaticalNumber", ""));
		item2Transformation.setParameters(item1Params);
		item2.setTransformation(item2Transformation);
		// add single property to the item2 property collection
		Property subProperty = new Property(new About("EnglishName_LatnScript"));
		Transformation subTrans = new Transformation(new Resource(
				"eu.esdihumboldt.cst.transformer.impl.SpellingFunction"));
		List<IParameter> subTransParams = new ArrayList<IParameter>();
		subTransParams.add(new Parameter("text", "EnglishName"));
		subTransParams.add(new Parameter("script", "Latn"));
		subTransParams.add(new Parameter("transliterationScheme", ""));
		subTrans.setParameters(subTransParams);
		subProperty.setTransformation(subTrans);
		item2.getCollection().add(subProperty);
		// 1.1.3 put item2 to the list
		properties.add(item2);
		((ComposedProperty) entity1).setCollection(properties);
		cell.setEntity1(entity1);
		// create entity2
		Entity entity2 = new Property(new About("hy-p:geographicalName"));
		// create Domain restriction for the Entity2
		List<FeatureClass> domRestrictions = new ArrayList<FeatureClass>();
		FeatureClass domRestr = new FeatureClass(new About("hy-p:Watercourse"));
		domRestrictions.add(domRestr);
		((Property) entity2).setDomainRestriction(domRestrictions);
		cell.setEntity2(entity2);
		map.add(cell);
		alignment.setMap(map);
	}

	@Test
	public void testOmlRdfRead() throws MalformedURLException {
		URI uri = null;
		/* try { */
		// uri = new
		// URI(PropertyCompositionTest.class.getResource("PropertyCompositionTest.xml").);
		String url = PropertyCompositionTest.class.getResource(
				"PropertyCompositionTest.xml").toExternalForm();
		/* } catch (URISyntaxException e) { */
		// TODO Auto-generated catch block
		/*
		 * e.printStackTrace(); }
		 */

		Alignment alignment = new OmlRdfReader().read(new URL(url));

		// test for ComposedProperty
		ComposedProperty entity1 = ((ComposedProperty) alignment.getMap()
				.get(0).getEntity1());
		// test operator
		assertEquals(PropertyOperatorType.OR.name(), entity1
				.getPropertyOperatorType().name());
		// test Property is not included in the propComposition
		assertFalse(entity1.getCollection().size() == 1);
		// test Realtion is not incluede in the propComposition
		assertNull(entity1.getRelation());
		// test Collection<Property> is not null
		List<Property> deducedProperties = entity1.getCollection();
		assertTrue(deducedProperties.size() > 1);
		// test size of the Collection<Property>
		assertEquals(2, deducedProperties.size());
		// test the deserialization for the each collection item
		ComposedProperty item1 = (ComposedProperty) deducedProperties.get(0);
		// test about for item1
		assertEquals("", item1.getAbout().getAbout());
		// test transformation for item1
		ITransformation transf1 = item1.getTransformation();
		// test rdfResource
		assertEquals(
				"eu.esdihumboldt.cst.transformer.impl.GeographicalNameFunction",
				transf1.getService().getLocation());
		// test parameters count
		assertEquals(8, transf1.getParameters().size());
		// test value for parameter 7
		assertEquals("grammaticalNumber", transf1.getParameters().get(7)
				.getName());
		assertEquals("", transf1.getParameters().get(7).getValue());

		// test operator for the ComposedProperty item1
		assertEquals(PropertyOperatorType.OR.name(), item1
				.getPropertyOperatorType().name());
		// test property is null
		assertFalse(item1.getCollection().size() == 0);
		// test relation is null
		assertNull(item1.getRelation());
		// test property collection is not null and has size 2
		List<Property> subColl1 = item1.getCollection();
		assertNotNull(subColl1);
		assertEquals(2, subColl1.size());
		// test property composition item 0
		Property subProperty1 = subColl1.get(0);
		// test about
		assertEquals("GermanName_LatnScript", subProperty1.getAbout()
				.getAbout());

		// test transformation
		ITransformation subTransf1 = subProperty1.getTransformation();
		assertEquals("eu.esdihumboldt.cst.transformer.impl.SpellingFunction",
				subTransf1.getService().getLocation());
		List<IParameter> subParams1 = subTransf1.getParameters();
		assertEquals(3, subParams1.size());
		assertEquals("transliterationScheme", subParams1.get(2).getName());
		assertEquals("", subParams1.get(2).getValue());

		ComposedProperty entity2 = (ComposedProperty) deducedProperties.get(1);
		// test about for entity2
		assertEquals("", entity2.getAbout().getAbout());
		// test transformation for entity2
		ITransformation transf2 = entity2.getTransformation();
		// test rdfResource
		assertEquals(
				"eu.esdihumboldt.cst.transformer.impl.GeographicalNameFunction",
				transf2.getService().getLocation());
		// test parameters count
		assertEquals(8, transf2.getParameters().size());
		// test value for parameter 3
		assertEquals("sourceOfName", transf2.getParameters().get(3).getName());
		assertEquals("sourceOfName1", transf2.getParameters().get(3).getValue());
		// test operator for ComposedProperty entity2
		assertEquals(PropertyOperatorType.OR.name(), entity2
				.getPropertyOperatorType().name());
		// test property
		Property subProp2 = entity2.getCollection().get(0);
		assertNotNull(subProp2);
		// test about
		assertEquals("EnglishName_LatnScript", subProp2.getAbout().getAbout());
		// test transformation
		ITransformation subTransf2 = subProp2.getTransformation();
		// test transformation resource
		assertEquals("eu.esdihumboldt.cst.transformer.impl.SpellingFunction",
				subTransf2.getService().getLocation());
		// test transforamtion parameters
		List<IParameter> subParams2 = subTransf2.getParameters();
		assertEquals(3, subParams2.size());
		assertEquals("text", subParams2.get(0).getName());
		assertEquals("EnglishName", subParams2.get(0).getValue());
		// test relation is null
		assertNull(entity2.getRelation());
		// test propertyCollection is null
		assertTrue(entity2.getCollection().size() == 1);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
