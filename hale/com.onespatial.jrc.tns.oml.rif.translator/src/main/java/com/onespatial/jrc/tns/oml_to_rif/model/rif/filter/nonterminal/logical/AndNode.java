/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical;

import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.AND_NODE;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * A node within a predicate tree that expresses a logical filter whereby both
 * sides of the expression must be true in order for the filter to return true
 * for any tested fact.
 * 
 * @author simonp
 */
public class AndNode extends AbstractLogicalNode
{

    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = AND_NODE;

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }

}
