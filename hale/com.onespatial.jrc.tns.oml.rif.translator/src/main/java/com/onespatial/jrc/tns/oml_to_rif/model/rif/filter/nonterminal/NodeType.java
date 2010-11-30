/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal;

/**
 * An enumeration of the different kinds of predicate tree nodes used in
 * filters.
 * 
 * @author simonp
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
