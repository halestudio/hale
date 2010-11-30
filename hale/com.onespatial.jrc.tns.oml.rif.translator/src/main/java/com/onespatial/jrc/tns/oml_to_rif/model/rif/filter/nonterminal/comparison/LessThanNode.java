/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * A node within a predicate tree that expresses a "less-than" comparison.
 * 
 * @author simonp
 */
public class LessThanNode extends AbstractComparisonNode
{
    /**
     * IRI for the predicate to be deployed in the RIF output.
     */
    public static final String RIF_PREDICATE_IRI = "http://"
            + "www.w3.org/2007/rif-builtin-predicate#numeric-less-than";

    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = NodeType.LESS_THAN_NODE;

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }
}
