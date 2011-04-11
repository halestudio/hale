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
package com.onespatial.jrc.tns.oml_to_rif.model.rif;

import org.w3._2007.rif.Sentence;

import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;

/**
 * Holds the data required to construct a property-to-property mapping within a
 * RIF {@link Sentence}.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class PropertyMapping
{
    private RifVariable source;
    private RifVariable target;

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getSource()
    {
        return source;
    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getTarget()
    {
        return target;
    }

    /**
     * @param s
     *            {@link RifVariable}
     * @param t
     *            {@link RifVariable}
     */
    public PropertyMapping(RifVariable s, RifVariable t)
    {
        super();
        source = s;
        target = t;
    }

    /**
     * @param att
     *            {@link RifVariable}
     */
    public void setTarget(RifVariable att)
    {
        target = att;
    }
    
    /**
     * @param s
     * 			{@link RifVariable}
     */
    public void setSource(RifVariable s)
    {
    	source = s;
    }

}
