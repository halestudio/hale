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

package eu.esdihumboldt.cst.transformer.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IValueExpression;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.goml.oml.ext.ValueExpression;
import eu.esdihumboldt.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.goml.omwg.ComparatorType;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ClassificationMappingFunctionTest {
	
	//TODO setup loggers?
//	private final Logger LOGGER = Logger.getLogger(ClassificationMappingFunctionTest.class);

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	ClassificationMappingFunction cmf = new ClassificationMappingFunction();
	OmlRdfReader reader = null;
	OmlRdfGenerator org = null;
	File testFile = null;

	private final static String SOURCE_LOCAL_NAME = "FT1";
	private final static String SOURCE_LOCAL_NAME_PROPERTY_A = "PropertyA";
	private final static String SOURCE_NAMESPACE = "http://esdi-humboldt.eu";

	private final static String TARGET_LOCAL_NAME = "FT2";
	private final static String TARGET_LOCAL_NAME_PROPERTY_B = "PropertyB";
	private final static String TARGET_NAMESPACE = "http://xsdi.org";

	@Before
	public void setUp() {
		System.out.println("Setting up for test...");
		reader = new OmlRdfReader();
		org = new OmlRdfGenerator();
		
		URL oml= getClass().getResource("ClassificationMappingFunctionTest.oml");
		testFile = new File(oml.getFile());
		assertNotNull(testFile);
		assertTrue("Testing if the test file exists.", testFile.exists());
		assertTrue("Testing if we can read the test file.", testFile.canRead());
	}



	/**
	 * Test method for
	 * {@link eu.esdihumboldt.cst.transformer.impl.ClassificationMappingFunction#configure(eu.esdihumboldt.cst.align.ICell)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConfigureICell() throws Exception {
		Alignment a = createTestAlignment();
		
		
		File testFile = tempFolder.newFile("test.oml");
		org.write(a, testFile.getAbsolutePath());

		ICell cell = a.getMap().get(0);
		assertTrue("The ClassificationMappingFunction's configure method did not return true", this.cmf.configure(cell));
		
		// FIXME Only tests if the configure method returns true.  We need to do some checking against a correct oml file.  Use the ClassificationMappingFunctionTest.oml in resources
	}



	/**
	 * Creates an alignment object.  The alignment matches the test oml file used by this test (ClassificationMappingFunctionTest.oml)
	 * This file is located in the test resources package.  
	 * @return an {@link Alignment} object
	 * @throws URISyntaxException
	 */
	private Alignment createTestAlignment() throws URISyntaxException {
		Alignment a = new Alignment();
		a.setAbout(new About("lala"));
		a.setSchema1(new Schema(SOURCE_NAMESPACE, new Formalism("GML", new URI("http://schemas.opengis.org/gml"))));
		a.setSchema2(new Schema(TARGET_NAMESPACE, new Formalism("GML", new URI("http://schemas.opengis.org/gml"))));

		Cell cell = new Cell();

		BigInteger sequenceId = new BigInteger("1");

		Property entity1 = new Property(new About(SOURCE_NAMESPACE, SOURCE_LOCAL_NAME, SOURCE_LOCAL_NAME_PROPERTY_A));

		List<IValueExpression> valueExpressions = new ArrayList<IValueExpression>();
		valueExpressions.add(new ValueExpression("2"));
		valueExpressions.add(new ValueExpression("3"));
		valueExpressions.add(new ValueExpression("4"));
		valueExpressions.add(new ValueExpression("70"));
		valueExpressions.add(new ValueExpression("71"));
		valueExpressions.add(new ValueExpression("72"));
		valueExpressions.add(new ValueExpression("73"));
		valueExpressions.add(new ValueExpression("74"));
		Restriction r = new Restriction(valueExpressions);
		r.setSeq(sequenceId);
		r.setComparator(ComparatorType.ONE_OF);

		List<Restriction> valueConditions = new ArrayList<Restriction>();
		valueConditions.add(r);
		entity1.setValueCondition(valueConditions);

		Property entity2 = new Property(new About(TARGET_NAMESPACE, TARGET_LOCAL_NAME, TARGET_LOCAL_NAME_PROPERTY_B));

		List<IValueExpression> valueExpressions2 = new ArrayList<IValueExpression>();
		valueExpressions2.add(new ValueExpression("Fluss, Bach"));
		Restriction r2 = new Restriction(valueExpressions2);
		r2.setSeq(sequenceId); // key link to other Seq Identifier
		r2.setComparator(ComparatorType.ONE_OF);

		List<Restriction> valueConditions2 = new ArrayList<Restriction>();
		valueConditions2.add(r2);
		entity2.setValueCondition(valueConditions2);

		cell.setEntity1(entity1);
		cell.setEntity2(entity2);

		a.getMap().add(cell);
		return a;
	}

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.cst.transformer.impl.ClassificationMappingFunction#transform(org.geotools.feature.FeatureCollection)}
	 * .
	 */
//	@Test
//	public void testTransformFeatureCollection() {
//		fail("Not yet implemented");
//	}

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.cst.transformer.impl.ClassificationMappingFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)}
	 * .
	 * @throws URISyntaxException 
	 */
	@Test
	public void testTransformFeatureFeatureOneOf() throws URISyntaxException {
		// NOTE The reader fails to populate the alignment object properly
//		System.out.println("Reading in test rdf alignment file");
//		Alignment alignment = reader.read(this.testFile.getAbsolutePath());
		
		Alignment alignment = createTestAlignment();
		List<ICell> map = alignment.getMap();

		ClassificationMappingFunction cmf = new ClassificationMappingFunction();
		// TODO Only works with the provided test file.  I guess this is fine though since it's a test.
		cmf.configure(map.get(0));
		
		
		System.out.println("Creating SimpleFeatures for testing");
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(SOURCE_LOCAL_NAME);
		ftbuilder.setNamespaceURI(SOURCE_NAMESPACE);
		ftbuilder.add(SOURCE_LOCAL_NAME_PROPERTY_A, String.class);
		SimpleFeatureType sourceFeatureType = ftbuilder.buildFeatureType();
		System.out.println("Creating a source feature which should map to Fluss, Bach in the target");
		SimpleFeature sourceFeature = SimpleFeatureBuilder.build(sourceFeatureType, new Object[]{"70"}, "1");
		assertNotNull(sourceFeature);
		dumpFeatureInfo(sourceFeature);
		
		ftbuilder.setName(TARGET_LOCAL_NAME);
		ftbuilder.setNamespaceURI(TARGET_NAMESPACE);
		ftbuilder.add(TARGET_LOCAL_NAME_PROPERTY_B, String.class);
		SimpleFeatureType targetFeatureType = ftbuilder.buildFeatureType();
		System.out.println("Creating an empty target which should be populated with Fluss, Bach after mapping");
		SimpleFeature targetFeature = SimpleFeatureBuilder.build(targetFeatureType, new Object[]{}, "2");
		assertNotNull(targetFeature);
		dumpFeatureInfo(targetFeature);
		
		cmf.transform(sourceFeature, targetFeature);
		
		Object resultValue = targetFeature.getAttribute(TARGET_LOCAL_NAME_PROPERTY_B);
		System.out.println(resultValue);
		
		// FIXME Hardcoded assert value (expected parameter)!
		assertEquals("The target Feature Property " + TARGET_LOCAL_NAME_PROPERTY_B + " should have been mapped to Fluss, Bach", "Fluss, Bach", resultValue);
	}
	

	public void testTransformFeatureFeatureOtherwise() throws URISyntaxException {
		// Create a test alignment
		// Should use the reader so we can add oml files as test resources.  This would prevent the build up of many specific alignment generation methods
	}
	
	private void dumpFeatureInfo(Feature feature) {
		System.out.println("Feature type name = " + feature.getType().getName());
	}
}




