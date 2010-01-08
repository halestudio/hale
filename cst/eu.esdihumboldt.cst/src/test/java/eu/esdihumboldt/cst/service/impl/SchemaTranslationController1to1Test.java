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

package eu.esdihumboldt.cst.service.impl;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.transformer.impl.GenericMathFunctionTest;
import eu.esdihumboldt.cst.transformer.impl.RenameFeatureFunction;
import eu.esdihumboldt.cst.transformer.service.impl.SchemaTranslationController;
import eu.esdihumboldt.cst.transformer.service.impl.TargetSchemaProvider;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

/**
 * This is a simple example test for the {@link SchemaTranslationController}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaTranslationController1to1Test {
	
	private static SchemaTranslationController stc = null;
	private static SimpleFeatureType sourceType = null;
	private static SimpleFeatureType targetType = null;

	/**
	 * Set up method using {@link eu.esdihumboldt.cst.transformer.service.impl.SchemaTranslationController#SchemaTranslationController(eu.esdihumboldt.cst.align.IAlignment)}.
	 * @throws URISyntaxException 
	 */
	@BeforeClass
	public static void testSchemaTranslationController() throws URISyntaxException {
		// set up log4j logger manually if necessary
		if (!Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
			Appender appender = new ConsoleAppender(
					new PatternLayout("%d{ISO8601} %5p %C{1}:%L %m%n"), 
					ConsoleAppender.SYSTEM_OUT );
			appender.setName("A1");
			Logger.getRootLogger().addAppender(appender);
		}
		
		stc = new SchemaTranslationController(getTestAlignment());
		sourceType = getFeatureType(
				GenericMathFunctionTest.sourceNamespace, 
				GenericMathFunctionTest.sourceLocalname, 
				new String[]{GenericMathFunctionTest.sourceLocalnamePropertyA, 
						GenericMathFunctionTest.sourceLocalnamePropertyB, 
						GenericMathFunctionTest.sourceLocalnamePropertyC});
		targetType = getFeatureType(
				GenericMathFunctionTest.targetNamespace, 
				GenericMathFunctionTest.targetLocalname, 
				new String[]{GenericMathFunctionTest.targetLocalnamePropertyD});
		
		// also, we'll have to provide the target types to the TargetSchemaProvider
		Collection<FeatureType> types = new ArrayList<FeatureType>();
		types.add(targetType);
		TargetSchemaProvider.getInstance().addTypes(types);
	}

	/**
	 * This is a more complex test that tests the actual translation and makes
	 * assertions afterwards.
	 * 
	 * Test method for {@link eu.esdihumboldt.cst.transformer.service.impl.SchemaTranslationController#translate(eu.esdihumboldt.cst.align.IAlignment, org.geotools.feature.FeatureCollection)}.
	 * @throws URISyntaxException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTranslate() throws URISyntaxException {
		
		// set up a FeatureCollection to use for transformation.
		FeatureCollection features = FeatureCollections.newCollection();
		
		// build source Feature(s)
		Feature source = SimpleFeatureBuilder.build(
				sourceType, new Object[]{"4.5", "2", "1"}, "1");
		features.add(source);
		
		// now it's time to actually execute the translation
		FeatureCollection result = stc.translate(features);
		
		// and finally, we have to assert that what we got back is what we expected.
		assertTrue(result != null);
		assertTrue(result.size() == 1);
		assertTrue(result.getSchema().getName().getLocalPart().equals(
				GenericMathFunctionTest.targetLocalname));
		SimpleFeature target = (SimpleFeature) result.features().next();
		Property prop = target.getProperty(
				GenericMathFunctionTest.targetLocalnamePropertyD);
		assertTrue(prop.getValue().toString().equals("5.0"));
	}
	
	/**
	 * Similar test to testTranslate(), but uses multiple features instead of 
	 * one.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTranslateManyFeatures() {
		
		// set up a FeatureCollection to use for transformation.
		FeatureCollection features = FeatureCollections.newCollection();
		
		// build source Feature(s)
		for (int i = 0; i < 1000; i++) {
			Feature source = SimpleFeatureBuilder.build(
					sourceType, new Object[]{"" + i, "" + i * 2, "" + i / 2.0}, "" + i);
			features.add(source);
		}
		
		// now it's time to actually execute the translation
		FeatureCollection result = stc.translate(features);
		
		// and finally, we have to assert that what we got back is what we expected.
		assertTrue(result != null);
		assertTrue(result.size() == 1000);
		assertTrue(result.getSchema().getName().getLocalPart().equals(
				GenericMathFunctionTest.targetLocalname));
	}
	
	/**
	 * Similar test to testTranslateManyFeatures(), but also defines a filter,
	 * so that less objects should be returned.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTranslateManyFeaturesWithFilter() {
		
		IAlignment a = getTestAlignment();
		for (ICell cell : a.getMap()) {
			if (RelationType.Equivalence.equals(cell.getRelation())) {
				Restriction r = new Restriction(null);
				r.setCqlStr("PropertyA < 500");
				List<Restriction> attributeValueConditions = new ArrayList<Restriction>();
				attributeValueConditions.add(r);
				((FeatureClass) cell.getEntity1()).setAttributeValueCondition(attributeValueConditions );
			}
		}
		stc = new SchemaTranslationController(a);
		
		// set up a FeatureCollection to use for transformation.
		FeatureCollection features = FeatureCollections.newCollection();
		
		// build source Feature(s)
		for (int i = 0; i < 1000; i++) {
			Feature source = SimpleFeatureBuilder.build(
					sourceType, new Object[]{i * 1.0, i * 2.0, i / 2.0}, "" + i);
			features.add(source);
		}
		
		// now it's time to actually execute the translation
		FeatureCollection result = stc.translate(features);
		
		// and finally, we have to assert that what we got back is what we expected.
		assertTrue(result != null);
		System.out.println(result.size());
		assertTrue(result.size() == 500);
		assertTrue(result.getSchema().getName().getLocalPart().equals(
				GenericMathFunctionTest.targetLocalname));
	}
	
	/**
	 * @return the {@link Alignment} to use as input for this test.
	 */
	private static IAlignment getTestAlignment(){
		// first, use an alignment created by a different test as a basis.
		IAlignment a = GenericMathFunctionTest.getTestAlignment();
		
		// second, add a FeatureType Rename operation.
		Cell cell = new Cell();
		cell.setEntity1(
				new FeatureClass(new About(
						GenericMathFunctionTest.sourceNamespace, 
						GenericMathFunctionTest.sourceLocalname)));
		cell.setEntity2(new FeatureClass(new About(
				GenericMathFunctionTest.targetNamespace, 
				GenericMathFunctionTest.targetLocalname)));
		cell.setRelation(RelationType.Equivalence);
		
		Transformation t = new Transformation();
		t.setLabel(RenameFeatureFunction.class.getName());
		t.setService(new Resource(RenameFeatureFunction.class.getName()));
		((FeatureClass)cell.getEntity1()).setTransformation(t);
		a.getMap().add(cell);
		
		return a;
	}
	
	/**
	 * Create a FeatureType in the basis of a namespace, a a typename, and a set 
	 * of property names.
	 * @param featureTypeNamespace
	 * @param featureTypeName
	 * @param propertyNames
	 * @return
	 */
	public static SimpleFeatureType getFeatureType(String featureTypeNamespace, 
			String featureTypeName, String[] propertyNames) {
	
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			for (String s : propertyNames) {
				ftbuilder.add(s, Double.class);
			}
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}

}
