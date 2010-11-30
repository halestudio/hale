/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

import java.util.List;

import com.onespatial.jrc.tns.oml_to_rif.schema.SchemaBrowser;

import eu.esdihumboldt.goml.align.Alignment;

/**
 * A model of a HALE {@link Alignment}, used as an interim stage in translation
 * to RIF-PRD.
 * 
 * @author simonp
 */
public class ModelAlignment
{
    private List<ModelClassMappingCell> classMappings;
    private List<ModelAttributeMappingCell> attributeMappings;
    private List<ModelStaticAssignmentCell> staticAssignments;

    private SchemaBrowser sourceSchemaBrowser;
    private SchemaBrowser targetSchemaBrowser;

    /**
     * @param sourceSchemaBrowser
     *            {@link SchemaBrowser}
     * @param targetSchemaBrowser
     *            {@link SchemaBrowser}
     * @param classMappings
     *            List&lt;{@link ModelClassMappingCell}&gt;
     * @param attributeMappings
     *            List&lt;{@link ModelAttributeMappingCell}&gt;
     * @param staticAssignments
     *            List&lt;{@link ModelStaticAssignmentCell}&gt;
     */
    public ModelAlignment(SchemaBrowser sourceSchemaBrowser, SchemaBrowser targetSchemaBrowser,
            List<ModelClassMappingCell> classMappings,
            List<ModelAttributeMappingCell> attributeMappings,
            List<ModelStaticAssignmentCell> staticAssignments)
    {
        super();
        this.sourceSchemaBrowser = sourceSchemaBrowser;
        this.targetSchemaBrowser = targetSchemaBrowser;
        this.classMappings = classMappings;
        this.attributeMappings = attributeMappings;
        this.staticAssignments = staticAssignments;
    }

    /**
     * @return List&lt;{@link ModelStaticAssignmentCell}&gt;
     */
    public List<ModelStaticAssignmentCell> getStaticAssignments()
    {
        return staticAssignments;
    }

    /**
     * @return List&lt;{@link ModelClassMappingCell}&gt;
     */
    public List<ModelClassMappingCell> getClassMappings()
    {
        return classMappings;
    }

    /**
     * @return List&lt;{@link ModelAttributeMappingCell}&gt;
     */
    public List<ModelAttributeMappingCell> getAttributeMappings()
    {
        return attributeMappings;
    }

    /**
     * @return {@link SchemaBrowser}
     */
    public SchemaBrowser getSourceSchemaBrowser()
    {
        return sourceSchemaBrowser;
    }

    /**
     * @return {@link SchemaBrowser}
     */
    public SchemaBrowser getTargetSchemaBrowser()
    {
        return targetSchemaBrowser;
    }
}
