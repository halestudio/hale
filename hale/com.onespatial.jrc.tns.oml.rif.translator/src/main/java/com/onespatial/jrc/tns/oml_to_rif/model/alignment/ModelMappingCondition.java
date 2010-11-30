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
