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
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType;

/**
 * Node for the 'equals' comparison.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
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
        public static final String NUMERIC_EQUALS_PREDICATE_IRI = "http://" //$NON-NLS-1$
                + "www.w3.org/2007/rif-builtin-predicate#numeric-equal"; //$NON-NLS-1$

    }

    class StringEqualToNode extends EqualToNode
    {

        /**
         * RIF-DTB supports String equality tests as a function that compares
         * two parameters, which is made available here to support building up
         * the RIF output.
         */
        public static final String STRING_COMPARE_FUNCTION_IRI = "http://" //$NON-NLS-1$
                + "www.w3.org/2007/rif-builtin-function#compare"; //$NON-NLS-1$

    }

    @Override
    public NodeType getNodeType()
    {
        return nodeType;
    }

}
