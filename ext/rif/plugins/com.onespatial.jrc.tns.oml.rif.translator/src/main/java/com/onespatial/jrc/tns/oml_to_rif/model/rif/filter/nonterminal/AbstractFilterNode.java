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
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
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
