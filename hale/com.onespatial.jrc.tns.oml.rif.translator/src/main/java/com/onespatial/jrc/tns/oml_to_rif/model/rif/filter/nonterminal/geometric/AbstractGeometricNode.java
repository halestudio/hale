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
