/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif;

import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;

/**
 * A class that represents the assignment of a static value to a target
 * attribute.
 * 
 * @author simonp
 */
public class StaticAssignment
{
    private String content;
    private RifVariable target;

    /**
     * @param target
     *            {@link RifVariable}
     * @param content
     *            {@link String}
     */
    public StaticAssignment(RifVariable target, String content)
    {
        super();
        this.target = target;
        this.content = content;
    }

    /**
     * @return {@link String}
     */
    public String getContent()
    {
        return content;
    }

    /**
     * @param content
     *            {@link String}
     */
    public void setContent(String content)
    {
        this.content = content;
    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getTarget()
    {
        return target;
    }

    /**
     * @param target
     *            {@link RifVariable}
     */
    public void setTarget(RifVariable target)
    {
        this.target = target;
    }
}
