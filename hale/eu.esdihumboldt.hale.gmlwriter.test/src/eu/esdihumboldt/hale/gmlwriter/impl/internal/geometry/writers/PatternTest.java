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
	
	private static final String GML_NS = "http://www.opengis.net/gml"; //$NON-NLS-1$
	
	/**
	 * The curve element name
	 */
	private static final NameImpl CURVE_ELEMENT = new NameImpl(GML_NS, "Curve"); //$NON-NLS-1$
	
	/**
	 * Test a direct match
	 */
	@Test
	public void testDirect() {
		Pattern pattern = Pattern.parse("Curve"); //$NON-NLS-1$
		TypeDefinition start = createCurveType();
		DefinitionPath path = pattern.match(start, new DefinitionPath(start, CURVE_ELEMENT), GML_NS);
		assertNotNull("A match should have been found", path); //$NON-NLS-1$
		assertTrue("Path should be empty", path.isEmpty()); //$NON-NLS-1$
		assertEquals(start, path.getLastType());
	}
	
	/**
	 * Test a direct match that should fails
	 */
	@Test
	public void testDirectFail() {
		Pattern pattern = Pattern.parse("CurveType"); //$NON-NLS-1$
		TypeDefinition start = createCurveType();
		DefinitionPath path = pattern.match(start, new DefinitionPath(start, CURVE_ELEMENT), GML_NS);
		assertNull("A match should not have been found", path); //$NON-NLS-1$
	}
	
	/**
	 * Test a descending match
	 */
	@Test
	public void testDescent() {
		Pattern pattern = Pattern.parse("**/LineStringSegment"); //$NON-NLS-1$
		TypeDefinition start = createCurveType();
		DefinitionPath path = pattern.match(start, new DefinitionPath(start, CURVE_ELEMENT), GML_NS);
		assertNotNull("A match should have been found", path); //$NON-NLS-1$
		assertFalse("Path should not be empty", path.isEmpty()); //$NON-NLS-1$
		List<PathElement> steps = path.getSteps();
		assertEquals(2, steps.size());
		String[] names = new String[]{"segments", "LineStringSegment"}; //$NON-NLS-1$ //$NON-NLS-2$
		// check path elements
		for (int i = 0; i < steps.size(); i++) {
			assertEquals(names[i], steps.get(i).getName().getLocalPart());
		}
	}
	
	private TypeDefinition createCurveType() {
		// create the curve type
		TypeDefinition curve = new TypeDefinition(new NameImpl(GML_NS, "CurveType"), null, null); //$NON-NLS-1$
		curve.addDeclaringElement(new SchemaElement(CURVE_ELEMENT, curve.getName(), curve, null));
		
		// create the segments property for curve
		TypeDefinition segArray = new TypeDefinition(new NameImpl(GML_NS, "CurveSegmentArrayPropertyType"), null, null); //$NON-NLS-1$
		curve.addDeclaredAttribute(new CustomDefaultAttribute("segments", segArray.getName(), segArray, GML_NS, null)); //$NON-NLS-1$
		
		// create the AbstractCurveSegement property for segArray
		TypeDefinition absSeg = new TypeDefinition(new NameImpl(GML_NS, "AbstractCurveSegementType"), null, null); //$NON-NLS-1$
		absSeg.setAbstract(true);
		segArray.addDeclaredAttribute(new CustomDefaultAttribute("AbstractCurveSegment", absSeg.getName(), absSeg, GML_NS, null)); //$NON-NLS-1$
		
		// add dummy sub-type
		new TypeDefinition(new NameImpl("somespace", "SomeSegmentType"), null, absSeg); //$NON-NLS-1$ //$NON-NLS-2$
		
		// create the LineStringSegmentType sub-type
		TypeDefinition lineSeg = new TypeDefinition(new NameImpl(GML_NS, "LineStringSegmentType"), null, absSeg); //$NON-NLS-1$
		lineSeg.addDeclaringElement(new SchemaElement(new NameImpl(GML_NS, "LineStringSegment"),  //$NON-NLS-1$
				lineSeg.getName(), lineSeg, new NameImpl(GML_NS, "AbstractCurveSegment"))); //$NON-NLS-1$
		
		return curve;
	}

}
