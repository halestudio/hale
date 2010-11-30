/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * A node within a predicate tree that expresses a logical filter whereby both
 * sides of the expression need not be true, but at least one must be true, in
 * order for the filter to return true for any tested fact.
 * 
 * @author simonp
 */
public class OrNode extends AbstractLogicalNode
{
    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = NodeType.OR_NODE;

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }
}
