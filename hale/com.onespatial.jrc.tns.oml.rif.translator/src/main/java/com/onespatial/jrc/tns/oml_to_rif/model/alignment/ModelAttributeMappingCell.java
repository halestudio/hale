/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttributePath;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;

/**
 * An interim model of a {@link Cell} within an {@link Alignment} that defines a
 * mapping between source and target classes. Used as a stage in translation to
 * RIF-PRD.
 * 
 * @author simonp
 */
public class ModelAttributeMappingCell
{
    private GmlAttributePath sourceAttribute;
    private GmlAttributePath targetAttribute;

    /**
     * @param sourceAttribute
     *            {@link GmlAttributePath}
     * @param targetAttribute
     *            {@link GmlAttributePath}
     */
    public ModelAttributeMappingCell(GmlAttributePath sourceAttribute,
            GmlAttributePath targetAttribute)
    {
        super();
        this.sourceAttribute = sourceAttribute;
        this.targetAttribute = targetAttribute;
    }

    /**
     * @return {@link GmlAttributePath}
     */
    public GmlAttributePath getSourceAttribute()
    {
        return sourceAttribute;
    }

    /**
     * @return {@link GmlAttributePath}
     */
    public GmlAttributePath getTargetAttribute()
    {
        return targetAttribute;
    }
}
