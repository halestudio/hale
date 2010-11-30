/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttributePath;

/**
 * An interim model of a static assignment of a value to a target attribute.
 * 
 * @author simonp
 */
public class ModelStaticAssignmentCell
{
    private final String content;
    private final GmlAttributePath target;

    /**
     * @param target
     *            {@link GmlAttributePath}
     * @param content
     *            {@link String}
     */
    public ModelStaticAssignmentCell(GmlAttributePath target, String content)
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
     * @return {@link GmlAttributePath}
     */
    public GmlAttributePath getTarget()
    {
        return target;
    }
}
