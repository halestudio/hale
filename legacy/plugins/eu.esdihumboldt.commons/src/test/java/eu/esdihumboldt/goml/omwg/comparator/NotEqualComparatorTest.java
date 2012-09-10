/**
 * 
 */
package eu.esdihumboldt.goml.omwg.comparator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.commons.goml.omwg.comparator.IComparator;
import eu.esdihumboldt.commons.goml.omwg.comparator.NotEqualComparator;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * @author Mark Doyle
 * @partner Logica
 * 
 */
public class NotEqualComparatorTest {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger
			.getLogger(NotEqualComparatorTest.class);

	/**
	 * Test source name for the source Feature generated for the tests
	 */
	private final static String SOURCE_LOCAL_NAME = "OneOf comparator test feature";

	/**
	 * Test source property name for the source Feature generated for the tests
	 */
	private final static String SOURCE_LOCAL_NAME_PROPERTY_A = "PropertyA";

	/**
	 * Test source namespace for the source Feature generated for the tests
	 */
	private final static String SOURCE_NAMESPACE = "http://esdi-humboldt.eu";

	/**
	 * Source restriction read from the Test oml file for all tests.
	 */
	private static Restriction SOURCE_RESTRICTION = null;

	/**
	 * A value for the source Feature that will evaluate as a success based upon
	 * the EqualComparatorTest.oml file.
	 */
	private final static String SUCCESSFUL_EVALUATION_VALUE = "65";

	/**
	 * A value for the source Feature that will evaluate as a failure based upon
	 * the EqualComparatorTest.oml file.
	 */
	private final static String UNSUCCESSFUL_EVALUATION_VALUE = "1000";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		URI uri = null;
		try {
			uri = new URI(NotEqualComparatorTest.class.getResource(
					"./EqualComparatorTest.oml").getFile());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Alignment alignment = new OmlRdfReader().read(new URL("file", null, uri
				.getPath()));

		ICell cell = alignment.getMap().get(0);
		Property propA = (Property) cell.getEntity1();

		List<Restriction> sourceRestrictions = propA.getValueCondition();
		SOURCE_RESTRICTION = sourceRestrictions.get(0);
	}

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.commons.goml.omwg.comparator.EqualComparator#evaluate(eu.esdihumboldt.commons.goml.omwg.Restriction, org.opengis.feature.Property)}
	 * .
	 */
	@Test
	public void testEvaluateTrue() {
		LOG.info("Testing true evaluation");
		org.opengis.feature.Property sourceProperty = createSource(true)
				.getProperty(SOURCE_LOCAL_NAME_PROPERTY_A);

		IComparator notEqualComp = new NotEqualComparator();
		boolean result = notEqualComp.evaluate(SOURCE_RESTRICTION,
				sourceProperty);

		assertTrue("Evaluation should return true", result);
	}

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.commons.goml.omwg.comparator.EqualComparator#evaluate(eu.esdihumboldt.commons.goml.omwg.Restriction, org.opengis.feature.Property)}
	 * .
	 */
	@Test
	public void testEvaluateFalse() {
		LOG.info("Testing false evaluation");
		org.opengis.feature.Property sourceProperty = createSource(false)
				.getProperty(SOURCE_LOCAL_NAME_PROPERTY_A);

		IComparator notEqualComp = new NotEqualComparator();
		boolean result = notEqualComp.evaluate(SOURCE_RESTRICTION,
				sourceProperty);

		assertFalse("Evaluation should return false", result);
	}

	/**
	 * @return
	 * 
	 */
	private SimpleFeature createSource(boolean withSuccessfulValue) {
		LOG.debug("Creating SimpleFeature source for "
				+ this.getClass().getName() + " test");
		SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
		ftbuilder.setName(SOURCE_LOCAL_NAME);
		ftbuilder.setNamespaceURI(SOURCE_NAMESPACE);
		ftbuilder.add(SOURCE_LOCAL_NAME_PROPERTY_A, String.class);
		SimpleFeatureType sourceFeatureType = ftbuilder.buildFeatureType();

		SimpleFeature sourceFeature = null;
		if (withSuccessfulValue) {
			LOG.debug("Creating a source feature which should map to Fluss, Bach in the target");
			sourceFeature = SimpleFeatureBuilder.build(sourceFeatureType,
					new Object[] { SUCCESSFUL_EVALUATION_VALUE }, "1");
		} else {
			LOG.debug("Creating a source feature which should not map to Fluss, Bach in the target");
			sourceFeature = SimpleFeatureBuilder.build(sourceFeatureType,
					new Object[] { UNSUCCESSFUL_EVALUATION_VALUE }, "1");
		}

		assertNotNull(sourceFeature);

		return sourceFeature;
	}

}
