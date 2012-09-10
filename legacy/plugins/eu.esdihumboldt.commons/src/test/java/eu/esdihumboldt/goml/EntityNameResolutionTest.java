package eu.esdihumboldt.goml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.rdf.About;

public class EntityNameResolutionTest {

	@Test
	public final void testEntityName() {
		String testFTname = "TestFTName";
		String testNamespace = "http://www.xsdi.org/test";
		String testPropertyname = "TestPropertyName";
		Entity e = new FeatureClass(new About(testNamespace, testFTname));

		assertEquals(testFTname, e.getLocalname());
		assertEquals(testNamespace, e.getNamespace());

		e = new Property(new About(testNamespace, testFTname, testPropertyname));

		assertEquals(testFTname, ((Property) e).getFeatureClassName());
		assertEquals(testPropertyname, e.getLocalname());
		assertEquals(testNamespace, e.getNamespace());

	}

}
