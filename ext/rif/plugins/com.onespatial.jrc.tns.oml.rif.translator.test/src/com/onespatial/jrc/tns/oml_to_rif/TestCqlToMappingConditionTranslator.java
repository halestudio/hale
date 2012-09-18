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
 *     1Spatial PLC <http://www.1spatial.com>
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package com.onespatial.jrc.tns.oml_to_rif;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.filter.Filter;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.api.Translator;
import com.onespatial.jrc.tns.oml_to_rif.digest.CqlToMappingConditionTranslator;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelMappingCondition;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.FilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.AbstractComparisonNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.EqualToNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.GreaterThanNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.LessThanNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric.UnitOfMeasureType;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric.WithinNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical.AndNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical.NotNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical.OrNode;

/**
 * Tests for CQL to model RIF translator.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class TestCqlToMappingConditionTranslator
{
    private static Translator<Filter, ModelMappingCondition> digester;

    /**
     * Does class-wide setup (once-only).
     */
    @BeforeClass
    public static void setUpClass()
    {
        digester = new CqlToMappingConditionTranslator();
    }

    /**
     * Test that generates model RIF from a simple CQL query comprising an
     * equality test against a named property.
     * 
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateSimpleEquality() throws CQLException, TranslationException
    {
        String cqlPredicate = "ATTR1='17'"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());
        assertThat(result.getRoot(), is(instanceOf(EqualToNode.class)));
        EqualToNode node = (EqualToNode) result.getRoot();
        assertNotNull(node.getLeft());
        assertNotNull(node.getRight());
        assertThat(node.getLeft().getPropertyName(), is(equalTo("ATTR1"))); //$NON-NLS-1$
        assertThat(node.getRight().getLiteralValue().toString(), is(equalTo("17"))); //$NON-NLS-1$
    }

    /**
     * Test that takes a simple logical expression comprising a parent logical
     * filter and for each child filter, an equality test.
     * 
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateSimpleLogical() throws CQLException, TranslationException
    {
        String cqlPredicate = "ATTR1 < 10 AND ATTR2 < 2"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());
        assertThat(result.getRoot(), is(instanceOf(AndNode.class)));
        assertNotNull(result.getRoot().getChildren());
        assertThat(result.getRoot().getChildren().size(), is(equalTo(2)));

        FilterNode firstNode = result.getRoot().getChildren().get(0);
        assertThat(firstNode, is(instanceOf(LessThanNode.class)));
        LessThanNode less1 = (LessThanNode) firstNode;
        assertNotNull(less1.getLeft());
        assertNotNull(less1.getLeft().getPropertyName());
        assertThat(less1.getLeft().getPropertyName(), is(equalTo("ATTR1"))); //$NON-NLS-1$
        assertNotNull(less1.getRight());
        assertNotNull(less1.getRight().getLiteralValue());
        assertThat(less1.getRight().getLiteralValue().toString(), is(equalTo("10"))); //$NON-NLS-1$

        FilterNode secondNode = result.getRoot().getChildren().get(1);
        assertThat(secondNode, is(instanceOf(LessThanNode.class)));
        LessThanNode less2 = (LessThanNode) secondNode;
        assertNotNull(less2.getLeft());
        assertNotNull(less2.getLeft().getPropertyName());
        assertThat(less2.getLeft().getPropertyName(), is(equalTo("ATTR2"))); //$NON-NLS-1$
        assertNotNull(less2.getRight());
        assertNotNull(less2.getRight().getLiteralValue());
        assertThat(less2.getRight().getLiteralValue().toString(), is(equalTo("2"))); //$NON-NLS-1$
    }

    /**
     * Test that takes a slightly more complex logical expression comprising a
     * nested tree of predicates each comprising a comparison operation. Also it
     * tests the greater than and equals operations, and contains a mix of
     * string, floating-point and integer literals.
     * 
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateMoreComplexLogical() throws CQLException, TranslationException
    {
        String cqlPredicate = "ATTR1 < 10.5 AND ATTR2 = 'chocolate' OR ATTR3 > 10"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());
        assertThat(result.getRoot(), is(instanceOf(OrNode.class)));
        assertNotNull(result.getRoot().getChildren());
        assertThat(result.getRoot().getChildren().size(), is(equalTo(2)));
        assertThat(result.getRoot().getChild(0), is(instanceOf(AndNode.class)));
        AndNode andNode = (AndNode) result.getRoot().getChild(0);
        assertNotNull(andNode.getChildren());
        assertThat(andNode.getChildren().size(), is(equalTo(2)));

        assertThat(andNode.getChild(0), is(instanceOf(LessThanNode.class)));
        LessThanNode lessNode = (LessThanNode) andNode.getChild(0);
        checkComparisonRightLiteral(lessNode, "ATTR1", "10.5"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(andNode.getChild(1), is(instanceOf(EqualToNode.class)));
        EqualToNode equalNode = (EqualToNode) andNode.getChild(1);
        checkComparisonRightLiteral(equalNode, "ATTR2", "chocolate"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(result.getRoot().getChild(1), is(instanceOf(GreaterThanNode.class)));
        GreaterThanNode greaterNode = (GreaterThanNode) result.getRoot().getChild(1);
        checkComparisonRightLiteral(greaterNode, "ATTR3", "10"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Same as testTranslateSlightlyMoreComplexLogical but brackets around the
     * 'OR' express overrides the natural precedence of the 'AND' filter.
     * 
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateLogicalPrecedenceOverridden() throws CQLException,
            TranslationException
    {
        String cqlPredicate = "ATTR1 < 10.5 AND (ATTR2 = 'chocolate' OR ATTR3 > 10)"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());
        assertThat(result.getRoot(), is(instanceOf(AndNode.class)));
        AndNode andNode = (AndNode) result.getRoot();
        assertNotNull(andNode.getChildren());
        assertThat(andNode.getChildren().size(), is(equalTo(2)));

        assertThat(andNode.getChild(0), is(instanceOf(LessThanNode.class)));
        LessThanNode lessNode = (LessThanNode) andNode.getChild(0);
        checkComparisonRightLiteral(lessNode, "ATTR1", "10.5"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(andNode.getChild(1), is(instanceOf(OrNode.class)));
        OrNode orNode = (OrNode) andNode.getChild(1);

        assertThat(orNode.getChildren().size(), is(equalTo(2)));

        assertThat(orNode.getChild(0), is(instanceOf(EqualToNode.class)));
        EqualToNode equalNode = (EqualToNode) orNode.getChild(0);
        checkComparisonRightLiteral(equalNode, "ATTR2", "chocolate"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(orNode.getChild(1), is(instanceOf(GreaterThanNode.class)));
        GreaterThanNode greaterNode = (GreaterThanNode) orNode.getChild(1);
        checkComparisonRightLiteral(greaterNode, "ATTR3", "10"); //$NON-NLS-1$ //$NON-NLS-2$

    }

    /**
     * Tests translating a CQL expression that negates the result of a simple
     * equality filter.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     */
    @Test
    public void testTranslateNegatedEquality() throws CQLException, TranslationException
    {
        String cqlPredicate = "ATTR1 <> '17'"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());
        assertThat(result.getRoot(), is(instanceOf(NotNode.class)));
        NotNode notNode = (NotNode) result.getRoot();
        assertThat(notNode.getChildren().size(), is(equalTo(1)));
        assertThat(notNode.getChild(0), is(instanceOf(EqualToNode.class)));
        EqualToNode equalNode = (EqualToNode) notNode.getChild(0);
        assertThat(equalNode.getChildren().size(), is(equalTo(0)));
        checkComparisonRightLiteral(equalNode, "ATTR1", "17"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Tests translating a CQL expression where an 'AND' filter has three child
     * comparison operators.
     * 
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateAndWithThreeChildren() throws CQLException, TranslationException
    {
        String cqlPredicate = "ATTR1 < 10.5 AND ATTR2 = 'chocolate' AND ATTR3 > 10"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());

        assertThat(result.getRoot(), instanceOf(AndNode.class));
        AndNode andNode = (AndNode) result.getRoot();
        assertThat(andNode.getChildren().size(), is(equalTo(2)));

        assertThat(andNode.getChild(0), is(instanceOf(AndNode.class)));
        AndNode nestedAndNode = (AndNode) andNode.getChild(0);
        assertThat(nestedAndNode.getChild(0), is(instanceOf(LessThanNode.class)));
        LessThanNode lessNode = (LessThanNode) nestedAndNode.getChild(0);
        checkComparisonRightLiteral(lessNode, "ATTR1", "10.5"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(nestedAndNode.getChild(1), is(instanceOf(EqualToNode.class)));
        EqualToNode equalNode = (EqualToNode) nestedAndNode.getChild(1);
        checkComparisonRightLiteral(equalNode, "ATTR2", "chocolate"); //$NON-NLS-1$ //$NON-NLS-2$

        assertThat(andNode.getChild(1), is(instanceOf(GreaterThanNode.class)));
        GreaterThanNode greaterNode = (GreaterThanNode) andNode.getChild(1);
        checkComparisonRightLiteral(greaterNode, "ATTR3", "10"); //$NON-NLS-1$ //$NON-NLS-2$

    }

    /**
     * Tests translating a CQL expression that creates a simple equality filter
     * where both sides of the expression are property names.
     * 
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateEqualityTwoProperties() throws CQLException, TranslationException
    {
        String cqlPredicate = "ATTR1 < ATTR2"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());

        assertThat(result.getRoot(), is(instanceOf(LessThanNode.class)));
        LessThanNode lessNode = (LessThanNode) result.getRoot();
        checkComparisonRightProperty(lessNode, "ATTR1", "ATTR2"); //$NON-NLS-1$ //$NON-NLS-2$

    }

    /**
     * Tests translating a CQL expression which includes a geometric 'within'
     * predicate.
     * 
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    // @Test
    public void testTranslateGeometricWithin() throws CQLException, TranslationException
    {
        String cqlPredicate = "DWITHIN(ATTR1, POINT(1 2), 10, kilometers)"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());
        assertThat(result.getRoot(), is(instanceOf(WithinNode.class)));

        WithinNode withinNode = (WithinNode) result.getRoot();
        assertThat(withinNode.getLeaves().size(), is(equalTo(2)));

        assertNotNull(withinNode.getLeft());
        assertThat(withinNode.getLeft().getPropertyName(), is(equalTo("ATTR1"))); //$NON-NLS-1$

        assertNotNull(withinNode.getRight());

        // CHECKSTYLE:OFF
        assertThat(withinNode.getDistance(), is(equalTo(10.0)));
        // CHECKSTYLE:ON
        assertThat(withinNode.getDistanceUnits(), is(equalTo(UnitOfMeasureType.kilometers)));
    }

    /**
     * Tests translating a CQL expression which includes a geometric 'contains'
     * predicate.
     * 
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    // @Test
    public void testTranslateGeometricContains() throws CQLException, TranslationException
    {
        String cqlPredicate = "CONTAINS(ATTR1, ATTR2)"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());

        // TODO finish test assertions
    }

    /**
     * Tests the negation filter.
     * 
     * @throws CQLException
     *             if any errors occurred parsing the CQL string
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateNegation() throws CQLException, TranslationException
    {
        String cqlPredicate = "TEMA<>'RL_TUNNEL' OR TEMA<>'CL_RAIL' OR TEMA<>'UNSHOWN_RL'"; //$NON-NLS-1$
        ModelMappingCondition result = digester.translate(CQL.toFilter(cqlPredicate));
        assertNotNull(result);
        assertNotNull(result.getRoot());

        // TODO finish test assertions
    }

    // ===========================================
    // TEST FIXTURE METHODS

    /**
     * Used for checking comparisons where left is a property name and right is
     * a literal.
     */
    private void checkComparisonRightLiteral(AbstractComparisonNode node, String leftValue,
            String rightValue)
    {
        assertNotNull(node.getLeft());
        assertThat(node.getChildren().size(), is(equalTo(0)));
        assertThat(node.getLeft().getPropertyName(), is(equalTo(leftValue)));
        assertNull(node.getLeft().getLiteralValue());
        assertNotNull(node.getRight());
        assertThat(node.getRight().getLiteralValue().toString(), is(equalTo(rightValue)));
        assertNull(node.getRight().getPropertyName());
    }

    /**
     * Used for checking comparisons where left is a property name and right is
     * also a property name.
     */
    private void checkComparisonRightProperty(AbstractComparisonNode node, String leftValue,
            String rightValue)
    {
        assertNotNull(node.getLeft());
        assertThat(node.getChildren().size(), is(equalTo(0)));
        assertThat(node.getLeft().getPropertyName(), is(equalTo(leftValue)));
        assertNull(node.getLeft().getLiteralValue());
        assertNotNull(node.getRight());
        assertNull(node.getRight().getLiteralValue());
        assertThat(node.getRight().getPropertyName().toString(), is(equalTo(rightValue)));
    }
}
