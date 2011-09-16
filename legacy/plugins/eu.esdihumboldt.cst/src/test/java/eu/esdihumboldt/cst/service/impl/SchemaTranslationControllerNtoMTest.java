/**
 * 
 */
package eu.esdihumboldt.cst.service.impl;


import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.esdihumboldt.cst.transformer.service.impl.SchemaTranslationController;
import eu.esdihumboldt.specification.cst.align.IAlignment;

/**
 * This is a more complex test for the {@link SchemaTranslationController}
 * which also takes into account .
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaTranslationControllerNtoMTest {

	private static SchemaTranslationController stc = null;
	private static SimpleFeatureType sourceType = null;
	private static SimpleFeatureType targetType = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// set up log4j logger manually if necessary
//		if (!Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
//			Appender appender = new ConsoleAppender(
//					new PatternLayout("%d{ISO8601} %5p %C{1}:%L %m%n"), 
//					ConsoleAppender.SYSTEM_OUT );
//			appender.setName("A1");
//			Logger.getRootLogger().addAppender(appender);
//		}
		
		/*stc = new SchemaTranslationController(getTestAlignment());
		sourceType = SchemaTranslationController1to1Test.getFeatureType(
				GenericMathFunctionTest.sourceNamespace, 
				GenericMathFunctionTest.sourceLocalname, 
				new String[]{GenericMathFunctionTest.sourceLocalnamePropertyA, 
						GenericMathFunctionTest.sourceLocalnamePropertyB, 
						GenericMathFunctionTest.sourceLocalnamePropertyC});
		targetType = SchemaTranslationController1to1Test.getFeatureType(
				GenericMathFunctionTest.targetNamespace, 
				GenericMathFunctionTest.targetLocalname, 
				new String[]{GenericMathFunctionTest.targetLocalnamePropertyD});
		
		// also, we'll have to provide the target types to the TargetSchemaProvider
		Collection<FeatureType> types = new ArrayList<FeatureType>();
		types.add(targetType);
		TargetSchemaProvider.getInstance().addTypes(types);*/
	}
	
	@Test
	public void testTranslate() throws URISyntaxException {
		assertTrue(true);
	}

	private static IAlignment getTestAlignment() {
		// TODO Auto-generated method stub
		return null;
	}

}
