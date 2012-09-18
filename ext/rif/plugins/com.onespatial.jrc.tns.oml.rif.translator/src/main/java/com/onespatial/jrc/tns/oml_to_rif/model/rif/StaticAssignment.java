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

import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;

/**
 * A class that represents the assignment of a static value to a target
 * attribute.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class StaticAssignment
{
    private String content;
    private RifVariable target;

    /**
     * @param target
     *            {@link RifVariable}
     * @param content
     *            {@link String}
     */
    public StaticAssignment(RifVariable target, String content)
    {
        super();
        this.target = target;
        this.content = content;
    }

    /**
     * @return {@link String}
     */
    public String getContent()
    {
        return content;
    }

    /**
     * @param content
     *            {@link String}
     */
    public void setContent(String content)
    {
        this.content = content;
    }

    /**
     * @return {@link RifVariable}
     */
    public RifVariable getTarget()
    {
        return target;
    }

    /**
     * @param target
     *            {@link RifVariable}
     */
    public void setTarget(RifVariable target)
    {
        this.target = target;
    }
}
