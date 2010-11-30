/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.geometric;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.AbstractFilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal.LeafNode;

/**
 * A node within a predicate tree that represents a geometric operation.
 * 
 * @author simonp
 */
public abstract class AbstractGeometricNode extends AbstractFilterNode
{

    private LeafNode left;
    private LeafNode right;

    /**
     * @return {@link LeafNode}
     */
    public LeafNode getLeft()
    {
        return left;
    }

    /**
     * @param node
     *            {@link LeafNode}
     */
    public void setLeft(LeafNode node)
    {
        left = node;
        super.getLeaves().add(node);
    }

    /**
     * @return {@link LeafNode}
     */
    public LeafNode getRight()
    {
        return right;
    }

    /**
     * @param node
     *            {@link LeafNode}
     */
    public void setRight(LeafNode node)
    {
        right = node;
        super.getLeaves().add(node);
    }

}
