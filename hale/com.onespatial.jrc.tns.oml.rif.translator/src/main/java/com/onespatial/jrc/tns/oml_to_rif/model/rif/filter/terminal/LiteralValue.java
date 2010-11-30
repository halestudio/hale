/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal;

/**
 * A class that stores a value that is in one of a variety of datatypes (it can
 * be either {@link String}, {@link Double} or {@link Long}.
 * 
 * @author simonp
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
