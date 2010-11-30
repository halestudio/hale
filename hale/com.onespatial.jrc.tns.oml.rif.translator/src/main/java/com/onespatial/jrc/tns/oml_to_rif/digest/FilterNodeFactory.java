/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.digest;

import org.geotools.filter.AbstractFilter;
import org.geotools.filter.AndImpl;
import org.geotools.filter.CompareFilterImpl;
import org.geotools.filter.GeometryFilterImpl;
import org.geotools.filter.IsEqualsToImpl;
import org.geotools.filter.IsGreaterThanImpl;
import org.geotools.filter.IsLessThenImpl;
import org.geotools.filter.LikeFilterImpl;
import org.geotools.filter.LogicFilterImpl;
import org.geotools.filter.NotImpl;
import org.geotools.filter.OrImpl;
import org.geotools.filter.spatial.ContainsImpl;
import org.geotools.filter.spatial.IntersectsImpl;
import org.geotools.filter.spatial.WithinImpl;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.FilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.AbstractComparisonNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.EqualToNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.GreaterThanNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.LessThanNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison.LikeNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric.AbstractGeometricNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric.ContainsNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric.IntersectsNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric.WithinNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical.AndNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical.NotNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical.OrNode;

/**
 * A factory to create filters nodes as required.
 * 
 * @author simonp
 */
public class FilterNodeFactory
{

    /**
     * @param source
     *            {@link CompareFilterImpl}
     * @return {@link AbstractComparisonNode}
     */
    public AbstractComparisonNode createComparisonNode(AbstractFilter source)
    {
        if (source instanceof IsEqualsToImpl)
        {
            return createIsEqualToNode();
        }
        else if (source instanceof IsGreaterThanImpl)
        {
            return createIsGreaterThanNode();
        }
        else if (source instanceof IsLessThenImpl)
        {
            return createIsLessThanNode();
        }
        else if (source instanceof LikeFilterImpl)
        {
            return createLikeNode();
        }
        throw new UnsupportedOperationException("Filter is not supported: "
                + source.getClass().getCanonicalName());
    }

    /**
     * @param logicFilter
     *            {@link LogicFilterImpl}
     * @return {@link FilterNode}
     */
    public FilterNode createLogicNode(LogicFilterImpl logicFilter)
    {
        if (logicFilter instanceof AndImpl)
        {
            return createAndNode();
        }
        else if (logicFilter instanceof OrImpl)
        {
            return createOrNode((OrImpl) logicFilter);
        }
        else if (logicFilter instanceof NotImpl)
        {
            return createNotNode((NotImpl) logicFilter);
        }
        throw new UnsupportedOperationException("Unrecognised type "
                + logicFilter.getClass().getCanonicalName());
    }

    /**
     * @param geometricFilter
     *            {@link GeometryFilterImpl}
     * @return {@link FilterNode}
     */
    public AbstractGeometricNode createGeometricNode(GeometryFilterImpl geometricFilter)
    {
        if (geometricFilter instanceof ContainsImpl)
        {
            return createContainsNode();
        }
        else if (geometricFilter instanceof WithinImpl)
        {
            return createWithinNode();
        }
        else if (geometricFilter instanceof IntersectsImpl)
        {
            return createIntersectsNode();
        }
        throw new UnsupportedOperationException("Unsupported filter type: "
                + geometricFilter.getClass().getCanonicalName());
    }

    AndNode createAndNode()
    {
        return new AndNode();
    }

    OrNode createOrNode(OrImpl filter)
    {
        return new OrNode();
    }

    NotNode createNotNode(NotImpl filter)
    {
        return new NotNode();
    }

    EqualToNode createIsEqualToNode()
    {
        EqualToNode node = new EqualToNode();
        return node;
    }

    GreaterThanNode createIsGreaterThanNode()
    {
        return new GreaterThanNode();
    }

    LessThanNode createIsLessThanNode()
    {
        return new LessThanNode();
    }

    ContainsNode createContainsNode()
    {
        return new ContainsNode();
    }

    WithinNode createWithinNode()
    {
        return new WithinNode();
    }

    AbstractGeometricNode createIntersectsNode()
    {
        return new IntersectsNode();
    }

    LikeNode createLikeNode()
    {
        return new LikeNode();
    }
}
