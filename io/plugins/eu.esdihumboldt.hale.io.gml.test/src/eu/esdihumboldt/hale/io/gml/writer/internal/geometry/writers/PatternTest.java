/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.PathElement;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Tests for {@link Pattern}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@SuppressWarnings("restriction")
public class PatternTest {

	private static final String GML_NS = "http://www.opengis.net/gml"; //$NON-NLS-1$

	/**
	 * The curve element name
	 */
	private static final QName CURVE_ELEMENT = new QName(GML_NS, "Curve"); //$NON-NLS-1$

	/**
	 * Test a direct match
	 */
	@Ignore
	@Test
	public void testDirect() {
		Pattern pattern = Pattern.parse("Curve"); //$NON-NLS-1$
		TypeDefinition start = createCurveType();
		DefinitionPath path = pattern.match(start, new DefinitionPath(start, CURVE_ELEMENT, false),
				GML_NS);
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
		DefinitionPath path = pattern.match(start, new DefinitionPath(start, CURVE_ELEMENT, false),
				GML_NS);
		assertNull("A match should not have been found", path); //$NON-NLS-1$
	}

	/**
	 * Test a descending match
	 */
	@Ignore
	@Test
	public void testDescent() {
		Pattern pattern = Pattern.parse("**/LineStringSegment"); //$NON-NLS-1$
		TypeDefinition start = createCurveType();
		DefinitionPath path = pattern.match(start, new DefinitionPath(start, CURVE_ELEMENT, false),
				GML_NS);
		assertNotNull("A match should have been found", path); //$NON-NLS-1$
		assertFalse("Path should not be empty", path.isEmpty()); //$NON-NLS-1$
		List<PathElement> steps = path.getSteps();
		assertEquals(2, steps.size());
		String[] names = new String[] { "segments", "LineStringSegment" }; //$NON-NLS-1$ //$NON-NLS-2$
		// check path elements
		for (int i = 0; i < steps.size(); i++) {
			assertEquals(names[i], steps.get(i).getName().getLocalPart());
		}
	}

	private TypeDefinition createCurveType() {
		// create the curve type
		DefaultTypeDefinition curve = new DefaultTypeDefinition(new QName(GML_NS, "CurveType"));
		XmlElement curveElement = new XmlElement(CURVE_ELEMENT, curve, null);
		curve.getConstraint(XmlElements.class).addElement(curveElement);

		// create the segments property for curve
		TypeDefinition segArray = new DefaultTypeDefinition(new QName(GML_NS,
				"CurveSegmentArrayPropertyType")); //$NON-NLS-1$
		new DefaultPropertyDefinition(new QName("segments"), curve, segArray);

		// create the AbstractCurveSegement property for segArray
		DefaultTypeDefinition absSeg = new DefaultTypeDefinition(new QName(GML_NS,
				"AbstractCurveSegementType")); //$NON-NLS-1$
		absSeg.setConstraint(AbstractFlag.ENABLED);
		new DefaultPropertyDefinition(new QName("AbstractCurveSegment"), segArray, absSeg);

		// add dummy sub-type
		DefaultTypeDefinition subtype = new DefaultTypeDefinition(new QName(
				"somespace", "SomeSegmentType")); //$NON-NLS-1$ //$NON-NLS-2$
		subtype.setSuperType(absSeg);

		// create the LineStringSegmentType sub-type
		DefaultTypeDefinition lineSeg = new DefaultTypeDefinition(new QName(GML_NS,
				"LineStringSegmentType")); //$NON-NLS-1$
		lineSeg.setSuperType(absSeg);
		XmlElement lineSegElement = new XmlElement(new QName(GML_NS, "LineStringSegment"), lineSeg,
				new QName(GML_NS, "AbstractCurveSegment"));
		lineSeg.getConstraint(XmlElements.class).addElement(lineSegElement);

		return curve;
	}

}
