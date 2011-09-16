/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

import java.util.List;

import eu.esdihumboldt.commons.goml.align.Alignment;

/**
 * A model of a HALE {@link Alignment}, used as an interim stage in translation
 * to RIF-PRD.
 *
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class ModelAlignment
{
    private List<ModelClassMappingCell> classMappings;
    private List<ModelAttributeMappingCell> attributeMappings;
    private List<ModelStaticAssignmentCell> staticAssignments;

    /**
     * @param classMappings
     *            List&lt;{@link ModelClassMappingCell}&gt;
     * @param attributeMappings
     *            List&lt;{@link ModelAttributeMappingCell}&gt;
     * @param staticAssignments
     *            List&lt;{@link ModelStaticAssignmentCell}&gt;
     */
    public ModelAlignment(
            List<ModelClassMappingCell> classMappings,
            List<ModelAttributeMappingCell> attributeMappings,
            List<ModelStaticAssignmentCell> staticAssignments)
    {
        super();
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

}
