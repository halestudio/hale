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
package com.onespatial.jrc.tns.oml_to_rif.digest;

import java.util.List;

import org.geotools.filter.AttributeExpressionImpl;
import org.geotools.filter.CompareFilterImpl;
import org.geotools.filter.GeometryFilterImpl;
import org.geotools.filter.IsNullImpl;
import org.geotools.filter.LikeFilterImpl;
import org.geotools.filter.LiteralExpressionImpl;
import org.geotools.filter.LogicFilterImpl;
import org.geotools.filter.NotImpl;
import org.geotools.filter.spatial.ContainsImpl;
import org.geotools.filter.spatial.IntersectsImpl;
import org.geotools.filter.spatial.WithinImpl;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

import com.onespatial.jrc.tns.oml_to_rif.api.AbstractFollowableTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelMappingCondition;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.FilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.AbstractComparisonNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric.AbstractGeometricNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal.LeafNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal.LiteralValue;

/**
 * CQL {@link Filter} to model RIF translator.
 * <p>
 * CQL = OGC Common Query Language, see <a
 * href="http://docs.codehaus.org/display/GEOTDOC/14+CQL">here</a> for more
 * information.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Susanne Reinwarth / TU Dresden
 */
public class CqlToMappingConditionTranslator extends
        AbstractFollowableTranslator<Filter, ModelMappingCondition>
{

    private FilterNodeFactory factory;

    /**
     * Default constructor.
     */
    public CqlToMappingConditionTranslator()
    {
        factory = new FilterNodeFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelMappingCondition translate(Filter source) throws TranslationException
    {
        ModelMappingCondition filter = new ModelMappingCondition();
        FilterNode root = null;
        if (isLogical(source))
        {
            LogicFilterImpl logicFilter = (LogicFilterImpl) source;
            //filter IS NOT NULL can not be treated as a logical one
            if (logicFilter instanceof NotImpl && logicFilter.getChildren().get(0) instanceof IsNullImpl)
            {
            	CompareFilterImpl compare = (CompareFilterImpl) logicFilter.getChildren().get(0);
            	AbstractComparisonNode cn = factory.createComparisonNode(compare, true);
            	cn.setLeft(getLeftContents((AttributeExpressionImpl) compare.getExpression1()));
            	root = cn;
            }
            else
            {
            	root = factory.createLogicNode(logicFilter);
            	List<?> children = logicFilter.getChildren();
            	for (Object child : children)
            	{
            		createNode(root, (Filter) child);
            	}
            }
        }
        // if it's a comparison filter we assume we are at the start of the tree
        else if (isComparison(source))
        {
            CompareFilterImpl compare = (CompareFilterImpl) source;
            AbstractComparisonNode cn = factory.createComparisonNode(compare, false);
            cn.setLeft(getLeftContents((AttributeExpressionImpl) compare.getExpression1()));
            if (!(source instanceof IsNullImpl))
            {
            	cn.setRight(getRightContents(compare.getExpression2()));
            }
            root = cn;
        }
        // likewise if it's a geometric filter
        else if (isGeometric(source))
        {
            GeometryFilterImpl geometric = (GeometryFilterImpl) source;
            AbstractGeometricNode gn = factory.createGeometricNode(geometric);
            gn.setLeft(getLeftContents((AttributeExpressionImpl) geometric.getExpression1()));
            gn.setRight(getRightContents(geometric.getExpression2()));
            setGeometricOperationSpecifics(gn, geometric);
            root = gn;
        }
        filter.setRoot(root);
        return filter;
    }

    private void setGeometricOperationSpecifics(AbstractGeometricNode node,
            GeometryFilterImpl geometric)
    {
        if (geometric instanceof WithinImpl)
        {
            // ok and nothing needed here

            // NB below code was for use of DWITHIN which is not
            // the correct CQL predicate to use, WITHIN is the right one

            // DWithinImpl within = (DWithinImpl) geometric;
            // assert node instanceof WithinNode;
            // WithinNode withinNode = (WithinNode) node;
            // withinNode.setDistance(within.getDistance());
            // withinNode.setDistanceUnits(getDistanceUnits(within.
            // getDistanceUnits()));
        }
        else if (geometric instanceof ContainsImpl)
        {
            // ok and nothing required here
        }
        else if (geometric instanceof IntersectsImpl)
        {
            // ok and nothing required here
        }
        else
        {
            throw new UnsupportedOperationException("Filter operation is not supported: "
                    + node.getClass().getCanonicalName());
        }
    }

    private LeafNode getLeftContents(AttributeExpressionImpl expression)
    {
        LeafNode node = new LeafNode();
        node.setPropertyName(expression.getPropertyName());
        return node;
    }

    private LeafNode getRightContents(Expression expression)
    {
        LeafNode node = new LeafNode();
        if (LiteralExpressionImpl.class.isAssignableFrom(expression.getClass()))
        {
            LiteralExpressionImpl literal = (LiteralExpressionImpl) expression;
            if (literal.getValue() instanceof com.vividsolutions.jts.geom.Geometry)
            {
                throw new IllegalArgumentException("Geometric literals are "
                        + "not supported! Found "
                        + literal.getValue().getClass().getCanonicalName());
            }
            node.setLiteralValue(LiteralValue.getNew(literal.getValue()));
        }
        else if (AttributeExpressionImpl.class.isAssignableFrom(expression.getClass()))
        {
            AttributeExpressionImpl attribute = (AttributeExpressionImpl) expression;
            node.setPropertyName(attribute.getPropertyName());
        }
        else
        {
            throw new IllegalArgumentException("Unsupported expression type: "
                    + expression.getClass().getCanonicalName());
        }
        return node;
    }

    private FilterNode createNode(FilterNode parent, Filter child)
    {
        FilterNode childNode = null;
        if (isLogical(child))
        {
            LogicFilterImpl logicFilter = (LogicFilterImpl) child;
            childNode = factory.createLogicNode(logicFilter);
            List<?> children = logicFilter.getChildren();
            for (Object c : children)
            {
                createNode(childNode, (Filter) c);
            }
        }
        else if (isComparison(child))
        {
            AbstractComparisonNode comparisonNode = null;
            if (LikeFilterImpl.class.isAssignableFrom(child.getClass()))
            {
                LikeFilterImpl like = (LikeFilterImpl) child;
                comparisonNode = factory.createComparisonNode(like, false);
                comparisonNode.setLeft(getLeftContents((AttributeExpressionImpl) like
                        .getExpression()));
                LeafNode node = new LeafNode();
                node.setLiteralValue(LiteralValue.getNew(like.getLiteral()));
                comparisonNode.setRight(node);
            }
            else
            {
                CompareFilterImpl compare = (CompareFilterImpl) child;
                comparisonNode = factory.createComparisonNode(compare, false);
                comparisonNode.setLeft(getLeftContents((AttributeExpressionImpl) compare
                        .getExpression1()));
                if (!(compare instanceof IsNullImpl))
                {
                	comparisonNode.setRight(getRightContents(compare.getExpression2()));
                }
            }
            childNode = comparisonNode;
        }
        else if (isGeometric(child))
        {
            childNode = factory.createGeometricNode((GeometryFilterImpl) child);
        }
        if (parent != null)
        {
            parent.addChild(childNode);
        }
        return childNode;
    }

    private boolean isGeometric(Filter source)
    {
        return source instanceof GeometryFilterImpl;
    }

    private boolean isComparison(Filter source)
    {
        return source instanceof CompareFilterImpl || source instanceof LikeFilterImpl;
    }

    private boolean isLogical(Filter source)
    {
        return source instanceof LogicFilterImpl;
    }

}
