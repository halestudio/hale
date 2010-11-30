/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.comparison;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.AbstractFilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal.LeafNode;

/**
 * A node that holds things shared by all comparisons.
 * 
 * @author simonp
 */
public abstract class AbstractComparisonNode extends AbstractFilterNode
{
    private LeafNode left;
    private LeafNode right;

    /**
     * Default constructor.
     */
    public AbstractComparisonNode()
    {
        left = new LeafNode();
        right = new LeafNode();
    }

    /**
     * @return {@link LeafNode}
     */
    public LeafNode getLeft()
    {
        return left;
    }

    /**
     * @param left
     *            {@link LeafNode}
     */
    public void setLeft(LeafNode left)
    {
        this.left = left;
    }

    /**
     * @return {@link LeafNode}
     */
    public LeafNode getRight()
    {
        return right;
    }

    /**
     * @param right
     *            {@link LeafNode}
     */
    public void setRight(LeafNode right)
    {
        this.right = right;
    }
}
