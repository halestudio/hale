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
 * A class that stores a value that is in one of a variety of datatypes (it can
 * be either {@link String}, {@link Double} or {@link Long}.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public final class LiteralValue
{
    private Long valueLong;
    private String valueString;
    private Double valueDouble;

    /**
     * @param value
     *            String
     * @return LiteralValue
     */
    public static LiteralValue getNew(Object value)
    {
        if (String.class.isAssignableFrom(value.getClass()))
        {
            return new LiteralValue((String) value);
        }
        else if (Long.class.isAssignableFrom(value.getClass()))
        {
            return new LiteralValue((Long) value);
        }
        else if (Double.class.isAssignableFrom(value.getClass()))
        {
            return new LiteralValue((Double) value);
        }
        return null;
    }

    /**
     * @param value
     *            {@link String}
     */
    private LiteralValue(String value)
    {
        valueString = value;
    }

    /**
     * @param value
     *            {@link Long}
     */
    private LiteralValue(Long value)
    {
        valueLong = value;
    }

    /**
     * @param value
     *            {@link Double}
     */
    private LiteralValue(Double value)
    {
        valueDouble = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if (valueString == null)
        {
            if (valueLong == null)
            {
                return valueDouble.toString();
            }
            return valueLong.toString();
        }
        return valueString;
    }

    /**
     * @return boolean
     */
    public Class<?> getValueClass()
    {
        if (valueLong != null)
        {
            return Long.class;
        }
        if (valueDouble != null)
        {
            return Double.class;
        }
        return String.class;
    }
}
