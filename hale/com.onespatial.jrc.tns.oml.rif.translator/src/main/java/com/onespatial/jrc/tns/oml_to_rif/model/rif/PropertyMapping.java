/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif;

import org.w3._2007.rif.Sentence;

import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;

/**
 * Holds the data required to construct a property-to-property mapping within a
 * RIF {@link Sentence}.
 * 
 * @author simonp
 */
public class PropertyMapping
{
    private RifVariable source;
    private RifVariable target;

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getSource()
    {
        return source;
    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getTarget()
    {
        return target;
    }

    /**
     * @param s
     *            {@link RifVariable}
     * @param t
     *            {@link RifVariable}
     */
    public PropertyMapping(RifVariable s, RifVariable t)
    {
        super();
        source = s;
        target = t;
    }

    /**
     * @param att
     *            {@link RifVariable}
     */
    public void setTarget(RifVariable att)
    {
        target = att;
    }

}
