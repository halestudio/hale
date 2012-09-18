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

}
