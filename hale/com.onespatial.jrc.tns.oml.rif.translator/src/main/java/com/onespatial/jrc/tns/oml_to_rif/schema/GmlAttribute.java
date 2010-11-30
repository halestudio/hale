/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import com.sun.xml.xsom.XSElementDecl;

/**
 * A class that represents an XML attribute within a GML document.
 * 
 * @author simonp
 */
public class GmlAttribute implements Comparable<GmlAttribute>
{

    private final XSElementDecl objectElement;
    private final XSElementDecl attributeElement;
    private final int minOccurs;
    private final int maxOccurs;

    /**
     * @param objectElement
     *            {@link XSElementDecl}
     * @param attributeElement
     *            {@link XSElementDecl}
     * @param minOccurs
     *            int the min occurs value
     * @param maxOccurs
     *            int the max occurs value
     */
    public GmlAttribute(XSElementDecl objectElement, XSElementDecl attributeElement, int minOccurs,
            int maxOccurs)
    {
        super();
        this.objectElement = objectElement;
        this.attributeElement = attributeElement;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;
    }

    /**
     * @return {@link XSElementDecl} the parent object in the schema
     */
    public XSElementDecl getObjectElement()
    {
        return objectElement;
    }

    /**
     * @return {@link XSElementDecl} the attribute within the schema
     */
    public XSElementDecl getAttributeElement()
    {
        return attributeElement;
    }

    /**
     * @return int the min occurs value
     */
    public int getMinOccurs()
    {
        return minOccurs;
    }

    /**
     * @return int the max occurs value
     */
    public int getMaxOccurs()
    {
        return maxOccurs;
    }

    /**
     * @see Comparable#compareTo(Object) which this overrides.
     * @param other
     *            {@link GmlAttribute} the one to compare with
     * @return int zero for equality, positive or negative to indicate
     *         difference
     */
    @Override
    public int compareTo(GmlAttribute other)
    {
        return attributeElement.getName().compareTo(other.attributeElement.getName());
    }
}
