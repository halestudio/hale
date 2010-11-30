/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.FilterNode;

/**
 * An interim model of a mapping condition that is to be translated into a
 * filter predicate in RIF-PRD.
 * 
 * @author simonp
 */
public class ModelMappingCondition
{
    private FilterNode root;

    /**
     * @return {@link FilterNode}
     */
    public FilterNode getRoot()
    {
        return root;
    }

    /**
     * @param root
     *            {@link FilterNode}
     */
    public void setRoot(FilterNode root)
    {
        this.root = root;
    }
}
