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
package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

import com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.nonterminal.FilterNode;

/**
 * An interim model of a mapping condition that is to be translated into a
 * filter predicate in RIF-PRD.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
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
