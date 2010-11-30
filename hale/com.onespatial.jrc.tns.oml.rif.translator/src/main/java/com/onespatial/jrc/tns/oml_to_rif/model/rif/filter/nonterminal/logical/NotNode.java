/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical;

import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.NOT_NODE;

import org.opengis.filter.Filter;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * Represents the component of a {@link Filter} that expresses the negation of a
 * single child argument.
 * 
 * @author simonp
 */
public class NotNode extends AbstractLogicalNode
{
    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = NOT_NODE;

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }
}
