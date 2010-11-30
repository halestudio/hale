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

import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.NOT_NODE;

import org.opengis.filter.Filter;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * Represents the component of a {@link Filter} that expresses the negation of a
 * single child argument.
 * 
 * @author simonp
 */
public class NotNode extends AbstractLogicalNode
{
    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = NOT_NODE;

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }
}
