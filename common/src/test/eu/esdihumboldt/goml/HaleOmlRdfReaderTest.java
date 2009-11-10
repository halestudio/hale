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


package test.eu.esdihumboldt.goml;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;



import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.goml.omwg.ComparatorType;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;

/**
 * @author Anna Pitaev, Logica
 *
 */
public class HaleOmlRdfReaderTest {

	/**
	 * Test method for {@link eu.esdihumboldt.goml.oml.io.OmlRdfReader#read(java.lang.String)}.
	 */
	@Test
	public final void testRead() {
		Alignment aligment = new OmlRdfReader().read("res/schema/testproject.xml");
		//test alignment basic elements
		assertEquals("http://www.opengis.net/gml", aligment.getSchema1().getLocation());
		assertEquals("urn:x-inspire:specification:gmlas-v31:Network:3.1", aligment.getSchema2().getLocation());
		assertEquals("",aligment.getLevel());
		assertEquals(2,aligment.getMap().size());
		
		//test the cell defining renaming
		//1.test the mapping for the attribute renaming mapping3
		ICell renaming = aligment.getMap().get(0);
		//test entity1 contexts
		//check that entity1 is not empty
		assertNotNull(renaming.getEntity1());
		FeatureClass fc1 = (FeatureClass) renaming.getEntity1();
		assertNotNull(fc1);
		//test fc1 labels
		List<String> labels = fc1.getLabel();
		assertEquals(2, labels.size());
		assertEquals("http://www.esdi-humboldt.org/waterVA", labels.get(0));
		assertEquals("Watercourses_VA", labels.get(1));
			
		//test fc1 transformer
		assertNotNull(fc1.getTransformation());
		assertEquals("eu.esdihumboldt.cst.transformer.impl.RenameFeatureTransformer", fc1.getTransformation().getAbout().getAbout());
		//test entity2 contexts
		//test measure
		assertEquals(0.0, renaming.getMeasure(),0);
		//test relation is null
		assertNull(renaming.getRelation());
		
	
		//test the cell defining networkexpansion
		
		
		/*
		//test some alignment maps.
		//TODO in all mappings test add test for the domain restriction element
		//1.test the mapping for the attribute renaming mapping3
		ICell renaming = aligment.getMap().get(3);
		//assertEquals(1.0,renaming.getMeasure());
		assertEquals(RelationType.Equivalence, renaming.getRelation());
		//check that entity1 is not empty
		assertNotNull(renaming.getEntity1());
		//tests about element for the both entities
		Property prop1 = (Property)renaming.getEntity1();
		Property prop2 = (Property)renaming.getEntity2();
		assertEquals("watercoursesBY:GN",((About)prop1.getAbout()).getAbout());
		assertEquals("inspireHY:geographicalName/inspireHY:spelling/inspireHY:text",((About)prop2.getAbout()).getAbout());
		
		//2. test for the mapping for the augmentation
		ICell augmentation = aligment.getMap().get(2);
		//assertEquals(1.0,augmentation.getMeasure());
		assertEquals(RelationType.Extra, augmentation.getRelation());
		//check that entity1 is empty
		
		//tests about element for the both entities
		 prop1 = (Property)augmentation.getEntity1();
		 prop2 = (Property)augmentation.getEntity2();
		assertEquals("",((About)prop1.getAbout()).getAbout());
		assertEquals("inspireHY:endLifespanVersion",((About)prop2.getAbout()).getAbout());
		//check value condition for the entity2
		assertEquals(1, prop2.getValueCondition().size());
		Restriction restriction = prop2.getValueCondition().get(0);
		assertEquals(ComparatorType.EQUAL, restriction.getComparator());
		assertEquals("unpopulated", restriction.getValue().get(0).getLiteral());
		//check DomainValueCondition
		assertEquals(1,prop2.getDomainRestriction().size());
		assertEquals("inspireHY:Watercourse", ((About) prop2.getDomainRestriction().get(0).getAbout()).getAbout());
		
		//3. test the mapping for filter
		Cell filter =(Cell)aligment.getMap().get(0);
		//read operation name
		assertEquals("filter",filter.getLabel().get(0));
		//assertEquals(1.0,filter.getMeasure());
		assertEquals(RelationType.Equivalence, filter.getRelation());
		//check entity1 properties
		FeatureClass fc1 = (FeatureClass)filter.getEntity1();
		assertEquals(1,fc1.getAttributeValueCondition().size());
		assertEquals(null,fc1.getAttributeValueCondition().get(0).getComparator());
		assertEquals("watercoursesBY:OBJART=5101", fc1.getAttributeValueCondition().get(0).getCqlStr());
		//check entity2 properties
		FeatureClass fc2 = (FeatureClass)filter.getEntity2();
		assertEquals("inspireHY:Watercourse", ((About)fc2.getAbout()).getAbout());
		*/
	}
 
}
