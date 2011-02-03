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

package eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.writers;

import java.util.List;

import org.geotools.feature.NameImpl;
import org.junit.Test;

import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.PathElement;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.CustomDefaultAttribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Tests for {@link Pattern}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PatternTest {
	
	private static final String GML_NS = "http://www.opengis.net/gml";
	
	/**
	 * Test a direct match
	 */
	@Test
	public void testDirect() {
		Pattern pattern = Pattern.parse("Curve");
		TypeDefinition start = createCurveType();
		DefinitionPath path = pattern.match(start, new DefinitionPath(start), GML_NS);
		assertNotNull("A match should have been found", path);
		assertTrue("Path should be empty", path.isEmpty());
		assertEquals(start, path.getLastType());
	}
	
	/**
	 * Test a direct match that should fails
	 */
	@Test
	public void testDirectFail() {
		Pattern pattern = Pattern.parse("CurveType");
		TypeDefinition start = createCurveType();
		DefinitionPath path = pattern.match(start, new DefinitionPath(start), GML_NS);
		assertNull("A match should not have been found", path);
	}
	
	/**
	 * Test a descending match
	 */
	@Test
	public void testDescent() {
		Pattern pattern = Pattern.parse("**/LineStringSegment");
		TypeDefinition start = createCurveType();
		DefinitionPath path = pattern.match(start, new DefinitionPath(start), GML_NS);
		assertNotNull("A match should have been found", path);
		assertFalse("Path should not be empty", path.isEmpty());
		List<PathElement> steps = path.getSteps();
		assertEquals(2, steps.size());
		String[] names = new String[]{"segments", "LineStringSegment"};
		// check path elements
		for (int i = 0; i < steps.size(); i++) {
			assertEquals(names[i], steps.get(i).getName().getLocalPart());
		}
	}
	
	private TypeDefinition createCurveType() {
		// create the curve type
		TypeDefinition curve = new TypeDefinition(new NameImpl(GML_NS, "CurveType"), null, null);
		curve.addDeclaringElement(new SchemaElement(new NameImpl(GML_NS, "Curve"), curve.getName(), curve));
		
		// create the segments property for curve
		TypeDefinition segArray = new TypeDefinition(new NameImpl(GML_NS, "CurveSegmentArrayPropertyType"), null, null);
		curve.addDeclaredAttribute(new CustomDefaultAttribute("segments", segArray.getName(), segArray, GML_NS));
		
		// create the AbstractCurveSegement property for segArray
		TypeDefinition absSeg = new TypeDefinition(new NameImpl(GML_NS, "AbstractCurveSegementType"), null, null);
		absSeg.setAbstract(true);
		segArray.addDeclaredAttribute(new CustomDefaultAttribute("AbstractCurveSegment", absSeg.getName(), absSeg, GML_NS));
		
		// add dummy sub-type
		new TypeDefinition(new NameImpl("somespace", "SomeSegmentType"), null, absSeg);
		
		// create the LineStringSegmentType sub-type
		TypeDefinition lineSeg = new TypeDefinition(new NameImpl(GML_NS, "LineStringSegmentType"), null, absSeg);
		lineSeg.addDeclaringElement(new SchemaElement(new NameImpl(GML_NS, "LineStringSegment"), lineSeg.getName(), lineSeg));
		
		return curve;
	}

}
