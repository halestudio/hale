/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.AbstractFilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * A class that contains elements that are common to all logical nodes.
 * 
 * @author simonp
 */
public class AbstractLogicalNode extends AbstractFilterNode
{
    /**
     * Prevents this class being instantiated directly.
     */
    protected AbstractLogicalNode()
    {
        // does nothing
    }

    @Override
    public NodeType getNodeType()
    {
        return null;
    }
}
