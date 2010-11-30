/*
 * Copyright (c) 1Spatial Group Ltd.
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
