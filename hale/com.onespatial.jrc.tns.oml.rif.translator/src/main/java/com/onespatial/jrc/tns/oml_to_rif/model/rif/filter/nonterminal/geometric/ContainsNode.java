/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * A node within a predicate tree that expresses the geometric "contains"
 * predicate.
 * 
 * @author simonp
 */
public class ContainsNode extends AbstractGeometricNode
{
    /**
     * IRI for the predicate to be deployed in the RIF output.
     */
    public static final String CONTAINS_PREDICATE_IRI = "http://"
            + "www.opengeospatial.org/standards/sfa/Geometry#contains";

    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = NodeType.CONTAINS_NODE;

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }
}
