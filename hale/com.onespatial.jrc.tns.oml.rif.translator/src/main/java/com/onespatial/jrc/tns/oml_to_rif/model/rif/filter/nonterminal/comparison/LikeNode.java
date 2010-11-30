/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * A node within a predicate tree that expresses a "like" comparison between
 * strings, NB this allows CQL wild-cards to be used (%).
 * 
 * @author simonp
 */
public class LikeNode extends AbstractComparisonNode
{
    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = NodeType.LIKE_NODE;

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }

}
