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
