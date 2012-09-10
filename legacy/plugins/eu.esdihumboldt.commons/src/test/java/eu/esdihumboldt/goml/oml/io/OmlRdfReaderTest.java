/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2008 to 2010.
 */

package eu.esdihumboldt.goml.oml.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.commons.goml.omwg.ComparatorType;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ICell.RelationType;

/**
 * @author Anna Pitaev, Logica
 * 
 */
public class OmlRdfReaderTest {

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader#read(java.lang.String)}
	 * .
	 * 
	 * @throws MalformedURLException
	 */
	@Test
	public final void testRead() {
		URI uri = null;
		try {
			uri = new URI(OmlRdfReaderTest.class.getResource(
					"./WatercoursesBY2Inspire.xml").getFile());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Alignment alignment;
		try {
			alignment = new OmlRdfReader().read(new URL("file", null, uri
					.getPath()));

			// test alignment basic elements
			assertEquals("Watercourses_BY.xsd", alignment.getSchema1()
					.getLocation());
			assertEquals("Hydrography.xsd", alignment.getSchema2()
					.getLocation());
			assertEquals("2OMWG", alignment.getLevel());
			assertEquals(40, alignment.getMap().size());
			// test some alignment maps.
			// TODO in all mappings test add test for the domain restriction
			// element
			// 1.test the mapping for the attribute renaming mapping3
			ICell renaming = alignment.getMap().get(3);
			// assertEquals(1.0,renaming.getMeasure());
			assertEquals(RelationType.Equivalence, renaming.getRelation());
			// check that entity1 is not empty
			assertNotNull(renaming.getEntity1());
			// tests about element for the both entities
			Property prop1 = (Property) renaming.getEntity1();
			Property prop2 = (Property) renaming.getEntity2();
			assertEquals("watercoursesBY:GN",
					((About) prop1.getAbout()).getAbout());
			assertEquals(
					"inspireHY:geographicalName/inspireHY:spelling/inspireHY:text",
					((About) prop2.getAbout()).getAbout());

			// 2. test for the mapping for the augmentation
			ICell augmentation = alignment.getMap().get(2);
			// assertEquals(1.0,augmentation.getMeasure());
			assertEquals(RelationType.Extra, augmentation.getRelation());
			// check that entity1 is empty

			// tests about element for the both entities
			prop1 = (Property) augmentation.getEntity1();
			prop2 = (Property) augmentation.getEntity2();
			assertEquals("", ((About) prop1.getAbout()).getAbout());
			assertEquals("inspireHY:endLifespanVersion",
					((About) prop2.getAbout()).getAbout());
			// check value condition for the entity2
			assertEquals(1, prop2.getValueCondition().size());
			Restriction restriction = prop2.getValueCondition().get(0);
			assertEquals(ComparatorType.EQUAL, restriction.getComparator());
			assertEquals("unpopulated", restriction.getValue().get(0)
					.getLiteral());
			// check DomainValueCondition
			assertEquals(1, prop2.getDomainRestriction().size());
			assertEquals("inspireHY:Watercourse", ((About) prop2
					.getDomainRestriction().get(0).getAbout()).getAbout());

			// 3. test the mapping for filter
			Cell filter = (Cell) alignment.getMap().get(0);
			// read operation name
			assertEquals("filter", filter.getLabel().get(0));
			// assertEquals(1.0,filter.getMeasure());
			assertEquals(RelationType.Equivalence, filter.getRelation());
			// check entity1 properties
			FeatureClass fc1 = (FeatureClass) filter.getEntity1();
			assertEquals(1, fc1.getAttributeValueCondition().size());
			assertEquals(null, fc1.getAttributeValueCondition().get(0)
					.getComparator());
			assertEquals("watercoursesBY:OBJART=5101", fc1
					.getAttributeValueCondition().get(0).getCqlStr());
			// check entity2 properties
			FeatureClass fc2 = (FeatureClass) filter.getEntity2();
			assertEquals("inspireHY:Watercourse",
					((About) fc2.getAbout()).getAbout());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
