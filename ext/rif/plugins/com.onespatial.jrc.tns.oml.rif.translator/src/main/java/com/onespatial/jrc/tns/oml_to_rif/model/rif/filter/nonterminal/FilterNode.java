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

import java.util.List;

/**
 * Any type of node that can occur within a filter that is stored as a predicate
 * tree.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
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
