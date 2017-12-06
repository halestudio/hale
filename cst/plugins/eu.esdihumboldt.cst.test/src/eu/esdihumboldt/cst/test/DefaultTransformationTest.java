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

package eu.esdihumboldt.cst.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Tests for the CST's alignment processor implementation
 * 
 * @author Simon Templer
 */
public abstract class DefaultTransformationTest extends AbstractTransformationTest {

	/**
	 * Test based on a very simple mapping with a retype and renames.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleRename() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.SIMPLE_RENAME));
	}

	/**
	 * Test based on a very simple mapping where on the target side there is a
	 * simple type property with attributes.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleAttribute() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.SIMPLE_ATTRIBUTE));
	}

	/**
	 * Test based on a very simple mapping with a retype, renames and an
	 * assignment.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleAssign() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.SIMPLE_ASSIGN));
	}

	/**
	 * Test based on a simple mapping with a retype and renames, where high
	 * cardinalities are allowed.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCardinalityRename() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CARD_RENAME));
	}

	/**
	 * Test based on a simple mapping with a retype, rename and assign,
	 * duplicated targets should also get the assigned values.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testDupeAssign() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.DUPE_ASSIGN));
	}

	/**
	 * Test based on a join and some renames.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertyJoin() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.PROPERTY_JOIN));
	}

	/**
	 * Test with a join that links two types w/ multiple conditions.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testJoinMultiCond_1() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.JOIN_MULTI_COND_1));
	}

	/**
	 * Test based on a retype and a formatted string with several inputs where
	 * one input exists several times, whereas the others only exist once. So
	 * those should be used all the times.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleMerge() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.SIMPLE_MERGE));
	}

	/**
	 * Test based on a retype and a formatted string with several inputs where
	 * each input exists several times, so they should be combined accordingly.
	 * 
	 * If some inputs exist more often than others there is no way to decide
	 * which of the others to use, so none should be used, so formatted string
	 * will not produce a value.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCardinalityMerge_1() throws Exception {
		testTransform(
				TransformationExamples.getExample(TransformationExamples.CARDINALITY_MERGE_1));
	}

	/**
	 * Test based on a retype and a formatted string with several inputs where
	 * each input exists several times, so they should be combined accordingly.
	 * 
	 * If some inputs exist more often than others there is no way to decide
	 * which of the others to use, so none should be used, so formatted string
	 * will not produce a value.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCardinalityMerge_2() throws Exception {
		testTransform(
				TransformationExamples.getExample(TransformationExamples.CARDINALITY_MERGE_2));
	}

	/**
	 * Test where multiple properties from a simple source type are mapped to a
	 * complex property structure including a choice in the target type.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testChoice() throws Exception {
		fail("What should happen?");
		// TODO what is expected here? That any of the two choice properties is
		// used?
		// Currently expected result contains both choice properties, which is
		// the result of the transform, too.
//		test("/testdata/choice/t1.xsd", "/testdata/choice/t2.xsd",
//				"/testdata/choice/t1t2.halex.alignment.xml", "/testdata/choice/instance1.xml",
//				"/testdata/choice/instance2.xml");
	}

	/**
	 * Test where a type with complex properties is mapped to itself, switching
	 * certain attributes.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleComplex() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.SIMPLE_COMPLEX));
	}

	/**
	 * Test where elements with a high cardinality are mapped to an element
	 * which may only occur once within an element that allows a high
	 * cardinality. The elements should be grouped together to fill the target
	 * element.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCardinalityMove() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CARDINALITY_MOVE));
	}

	/**
	 * Simple structural rename transformation test.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testStructuralRename1() throws Exception {
		testTransform(
				TransformationExamples.getExample(TransformationExamples.STRUCTURAL_RENAME_1));
	}

	/**
	 * Structural rename with cyclic references transformation test.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testStructuralRename2() throws Exception {
		testTransform(
				TransformationExamples.getExample(TransformationExamples.STRUCTURAL_RENAME_2));
	}

	/**
	 * Structural rename with choices transformation test.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testStructuralRename3() throws Exception {
		testTransform(
				TransformationExamples.getExample(TransformationExamples.STRUCTURAL_RENAME_3));
	}

	/**
	 * Simple structural retype transformation test.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testStructuralRetype1() throws Exception {
		testTransform(
				TransformationExamples.getExample(TransformationExamples.STRUCTURAL_RETYPE_1));
	}

	/**
	 * A simple Math Expression transformation test.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testMathExpression() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.MATH_EXPRESSION));
	}

	/**
	 * A Regex String Analysis transformation test.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testRegexAnalysis() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.REGEX_ANALYSIS));
	}

	/**
	 * A transformation test to consider function priority.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPriority() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.PRIORITY));
	}

	/**
	 * A transformation test to where null values occur as result.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testNullValue() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.NULLVALUE));
	}

	/**
	 * Test for the generateduid. Since the uid is always different, just test
	 * for them being unique.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testGenerateUID() throws Exception {
		TransformationExample example = TransformationExamples
				.getExample(TransformationExamples.GENERATEUID);
		List<Instance> transformedData = transformData(example);
		TreeSet<String> uniqueIdSet = new TreeSet<String>();
		for (Instance instance : transformedData) {
			Iterable<QName> propertyNames = instance.getPropertyNames();
			for (QName propertyName : propertyNames) {
				Object[] property = instance.getProperty(propertyName);
				for (Object object : property) {
					boolean added = uniqueIdSet.add(object.toString());
					if (!added) {
						assertTrue(
								"Found duplicated id when should be unique: " + object.toString(),
								added);
					}
				}
			}
		}
	}

	/**
	 * Test for the classification.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testClassification1() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CLASSIFICATION_1));
	}

	/**
	 * Test for the classification with non matching values and use source.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testClassification2() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CLASSIFICATION_2));
	}

	/**
	 * Test for the classification with non matching values and use a fixed
	 * value.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testClassification3() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CLASSIFICATION_3));
	}

	/**
	 * Test with simple property conditions.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertyCondition1() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.PROPCONDITION1));
	}

	/**
	 * Test with simple property conditions.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertyCondition2() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.PROPCONDITION2));
	}

	/**
	 * Test with simple type filter.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testTypeFilter() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.TYPEFILTER));
	}

	/**
	 * Test with simple property filters.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertyFilter() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.PROPERTYFILTER));
	}

	/**
	 * Test where properties from a simple source type are mapped to to complex
	 * properties in that target type, with some of the needed information being
	 * given only implicit through the corresponding source property.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testImplicitAssign() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.IMPASSIGN));
	}

	/**
	 * Test for the formatstring function
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testFormatstring() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.FORMATSTRING));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_MULTI_1}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMMulti1() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_MULTI_1));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_MULTI_2}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMMulti2() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_MULTI_2));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_MULTI_3}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMMulti3() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_MULTI_3));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_MULTI_4}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMMulti4() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_MULTI_4));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_NESTED_1}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMNested1() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_NESTED_1));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_UNION_1}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMUnion1() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_UNION_1));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_UNION_2}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMUnion2() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_UNION_2));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_UNION_3}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMUnion3() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_UNION_3));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_UNION_4}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMUnion4() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_UNION_4));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_UNION_5}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCMUnion5() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_UNION_5));
	}

	/**
	 * Transformation test for the context matching example
	 * {@link TransformationExamples#CM_UNION_6}.
	 * 
	 * @throws Exception if an error occurs executing the test
	 */
	@Ignore
	@Test
	public void testCMUnion6() throws Exception {
		testTransform(TransformationExamples.getExample(TransformationExamples.CM_UNION_6));
	}

}
