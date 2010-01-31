/**
 * 
 */
package eu.esdihumboldt.goml.omwg.comparator;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * @author Mark Doyle
 * @partner Logica
 *
 */
public class EqualComparatorTest {
	
	/**
	 * Source restriction read from the Test oml file for all tests.
	 */
	private static Restriction SOURCE_RESTRICTION = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		URL url = OneOfComparatorTest.class.getResource("./EqualComparatorTest.oml");
		Alignment alignment = new OmlRdfReader().read(url.getFile());
		ICell cell = alignment.getMap().get(0);
		Property propA = (Property)cell.getEntity1();
		
		List<Restriction> sourceRestrictions = propA.getValueCondition();
		SOURCE_RESTRICTION  = sourceRestrictions.get(0);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Test method for {@link eu.esdihumboldt.goml.omwg.comparator.EqualComparator#evaluate(eu.esdihumboldt.goml.omwg.Restriction, org.opengis.feature.Property)}.
	 */
	@Test
	public void testEvaluate() {
		fail("Not yet implemented");
	}

}
