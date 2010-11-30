/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * A node within a predicate tree that expresses the geometric "within"
 * predicate.
 * 
 * @author simonp
 */
public class WithinNode extends AbstractGeometricNode
{
    /**
     * IRI for the predicate to be deployed in the RIF output.
     */
    public static final String WITHIN_PREDICATE_IRI = "http://"
            + "www.opengeospatial.org/standards/sfa/Geometry#within";

    private double distance;
    private UnitOfMeasureType distanceUnits;

    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = NodeType.WITHIN_NODE;

    /**
     * @return double
     */
    public double getDistance()
    {
        return distance;
    }

    /**
     * @param dist
     *            double
     */
    public void setDistance(double dist)
    {
        distance = dist;

    }

    /**
     * @return {@link UnitOfMeasureType}
     */
    public UnitOfMeasureType getDistanceUnits()
    {
        return distanceUnits;
    }

    /**
     * @param units
     *            String
     */
    public void setDistanceUnits(UnitOfMeasureType units)
    {
        distanceUnits = units;
    }

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }

}
