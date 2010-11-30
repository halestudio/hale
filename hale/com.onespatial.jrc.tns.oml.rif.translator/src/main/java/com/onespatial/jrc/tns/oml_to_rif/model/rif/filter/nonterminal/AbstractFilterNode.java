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
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal;

import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.AND_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.CONTAINS_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.EQUAL_TO_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.GREATER_THAN_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.INTERSECTS_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.LESS_THAN_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.LIKE_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.NOT_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.OR_NODE;
import static com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.NodeType.WITHIN_NODE;

import java.util.ArrayList;
import java.util.List;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal.LeafNode;

/**
 * Common place for functionality generic to filter nodes. Supports navigation
 * of the filter's predicate tree.
 * 
 * @author simonp
 */
public abstract class AbstractFilterNode implements FilterNode
{

    /**
     * Set as protected because this value is set by extending classes.
     */
    protected NodeType nodeType;

    private List<FilterNode> children;

    private List<LeafNode> leaves;

    /**
     * Default constructor.
     */
    public AbstractFilterNode()
    {
        children = new ArrayList<FilterNode>();
        leaves = new ArrayList<LeafNode>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilterNode getChild(int num)
    {
        return children.get(num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FilterNode> getChildren()
    {
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilterNode getFirstChild()
    {
        return children.get(0);
    }

    /**
     * @return List&lt;{@link FilterNode}&gt; returns leaf nodes
     */
    public List<LeafNode> getLeaves()
    {
        return leaves;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(FilterNode child)
    {
        children.add(child);
    }

    /**
     * @return {@link NodeType}
     */
    public abstract NodeType getNodeType();

    /**
     * @param otherType
     *            {@link NodeType}
     * @return boolean
     */
    public boolean isSameNodeTypeAs(NodeType otherType)
    {
        return otherType.equals(nodeType);
    }

    /**
     * @return boolean
     */
    public boolean isLogical()
    {
        return getNodeType().equals(AND_NODE) || getNodeType().equals(OR_NODE)
                || getNodeType().equals(NOT_NODE);
    }

    /**
     * @return boolean
     */
    public boolean isComparison()
    {
        return getNodeType().equals(EQUAL_TO_NODE) || getNodeType().equals(GREATER_THAN_NODE)
                || getNodeType().equals(LESS_THAN_NODE) || getNodeType().equals(LIKE_NODE);
    }

    /**
     * @return boolean
     */
    public boolean isGeometric()
    {
        return getNodeType().equals(CONTAINS_NODE) || getNodeType().equals(INTERSECTS_NODE)
                || getNodeType().equals(WITHIN_NODE);
    }

}
