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
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal;

/**
 * An enumeration of the different kinds of predicate tree nodes used in
 * filters.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public enum NodeType
{
    /**
     * Node with an AND in it.
     */
    AND_NODE,
    /**
     * Node with an OR in it.
     */
    OR_NODE,
    /**
     * Node with a NOT in it.
     */
    NOT_NODE,
    /**
     * Node with an EQUAL_TO expression in it.
     */
    EQUAL_TO_NODE,
    /**
     * Node with a GREATER_THAN expression in it.
     */
    GREATER_THAN_NODE,
    /**
     * Node with a LESS_THAN expression in it.
     */
    LESS_THAN_NODE,
    /**
     * Node with a CONTAINS in it.
     */
    CONTAINS_NODE,
    /**
     * Node with an INTERSECTS in it.
     */
    INTERSECTS_NODE,
    /**
     * Node with a WITHIN in it.
     */
    WITHIN_NODE,
    /**
     * Node with a LIKE in it.
     */
    LIKE_NODE;
}
