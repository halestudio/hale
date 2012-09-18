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

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.AbstractFilterNode;
import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal.LeafNode;

/**
 * A node that holds things shared by all comparisons.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
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
