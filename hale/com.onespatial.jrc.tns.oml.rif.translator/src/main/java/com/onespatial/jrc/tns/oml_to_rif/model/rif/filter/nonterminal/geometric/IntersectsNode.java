/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * A node within a predicate tree that expresses the geometric "intersects"
 * predicate.
 * 
 * @author simonp
 */
public class IntersectsNode extends AbstractGeometricNode
{
    /**
     * IRI for the predicate to be deployed in the RIF output.
     */
    public static final String INTERSECTS_PREDICATE_IRI = "http://"
            + "www.opengeospatial.org/standards/sfa/Geometry#intersects";

    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = NodeType.INTERSECTS_NODE;

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }
}
