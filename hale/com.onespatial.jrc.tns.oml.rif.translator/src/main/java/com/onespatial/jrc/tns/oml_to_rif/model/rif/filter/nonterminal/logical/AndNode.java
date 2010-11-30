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
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.logical;

import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.AND_NODE;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * A node within a predicate tree that expresses a logical filter whereby both
 * sides of the expression must be true in order for the filter to return true
 * for any tested fact.
 * 
 * @author simonp
 */
public class AndNode extends AbstractLogicalNode
{

    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = AND_NODE;

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }

}
