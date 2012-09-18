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
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal;

/**
 * Class that represents a leaf node in a predicate tree.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class LeafNode
{

    private String propertyName;
    private LiteralValue literalValue;

    /**
     * @return String
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * @param propertyName
     *            String
     */
    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;

    }

    /**
     * @return String
     */
    public LiteralValue getLiteralValue()
    {
        return literalValue;
    }

    /**
     * @param value
     *            String
     */
    public void setLiteralValue(LiteralValue value)
    {
        literalValue = value;
    }

    /**
     * @return boolean
     */
    public boolean isNumeric()
    {
        return literalValue.getValueClass() == Long.class
                || literalValue.getValueClass() == Double.class;
    }

}
