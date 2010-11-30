/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif.filter.terminal;

/**
 * Class that represents a leaf node in a predicate tree.
 * 
 * @author simonp
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
