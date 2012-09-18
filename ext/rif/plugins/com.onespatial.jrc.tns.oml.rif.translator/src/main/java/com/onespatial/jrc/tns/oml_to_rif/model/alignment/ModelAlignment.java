/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     1Spatial PLC <http://www.1spatial.com>
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
