/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal;

import java.util.List;

/**
 * Any type of node that can occur within a filter that is stored as a predicate
 * tree.
 * 
 * @author simonp
 */
public interface FilterNode
{

    /**
     * @return List&lt;{@link FilterNode}&gt;
     */
    public List<FilterNode> getChildren();

    /**
     * @return {@link FilterNode}
     */
    public FilterNode getFirstChild();

    /**
     * @param num
     *            int the index of the child to return
     * @return {@link FilterNode}
     */
    public FilterNode getChild(int num);

    /**
     * Add a new member to the current node's collection of children.
     * 
     * @param child
     *            {@link FilterNode}
     */
    public void addChild(FilterNode child);

}
