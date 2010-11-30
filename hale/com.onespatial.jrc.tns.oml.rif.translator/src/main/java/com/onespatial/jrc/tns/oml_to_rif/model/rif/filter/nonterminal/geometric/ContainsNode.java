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
