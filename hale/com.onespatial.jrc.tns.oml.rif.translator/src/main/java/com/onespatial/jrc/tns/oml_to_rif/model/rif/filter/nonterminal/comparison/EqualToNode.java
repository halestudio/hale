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
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * Node for the 'equals' comparison.
 * 
 * @author simonp
 */
public class EqualToNode extends AbstractComparisonNode
{
    /**
     * Set as protected in case this class is extended.
     */
    protected NodeType nodeType = NodeType.EQUAL_TO_NODE;

    class NumericEqualToNode extends EqualToNode
    {

        /**
         * RIF-DTB support numeric equality tests as a predicate, which is made
         * available here to support building up the RIF output.
         */
        public static final String NUMERIC_EQUALS_PREDICATE_IRI = "http://"
                + "www.w3.org/2007/rif-builtin-predicate#numeric-equal";

    }

    class StringEqualToNode extends EqualToNode
    {

        /**
         * RIF-DTB supports String equality tests as a function that compares
         * two parameters, which is made available here to support building up
         * the RIF output.
         */
        public static final String STRING_COMPARE_FUNCTION_IRI = "http://"
                + "www.w3.org/2007/rif-builtin-function#compare";

    }

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }

}
