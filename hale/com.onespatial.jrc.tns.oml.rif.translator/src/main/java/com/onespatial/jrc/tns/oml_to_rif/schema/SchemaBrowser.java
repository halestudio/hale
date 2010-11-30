/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import java.util.List;

import org.xml.sax.SAXException;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.sun.xml.xsom.XSElementDecl;

/**
 * Schema Browser. Allows GML Application schema to be browsed in a
 * version-independent manner.
 * 
 * @author richards
 */
public interface SchemaBrowser
{

    /**
     * Retrieve list of Elements that represent features in the application
     * schema.
     * 
     * @return list of features sorted by their local name.
     * @throws SAXException
     *             if failed to process application schema.
     */
    List<XSElementDecl> getFeatureClassNames() throws SAXException;

    /**
     * Decompose a slash separated string list of attributes.
     * 
     * @param attributePath
     *            the path to decompose.
     * @return an object representing the decomposition.
     * @throws SAXException
     *             if failed to process application schema.
     * @throws TranslationException
     *             if any exceptions are thrown during translation
     */
    GmlAttributePath decomposeHaleAttributePath(String attributePath) throws SAXException,
            TranslationException;

    /**
     * Find an attribute find for each of the attributes of a feature class.
     * 
     * @param featureClass
     *            the feature class to analyise.
     * @return list of attributes.
     */
    List<GmlAttribute> findAttributes(XSElementDecl featureClass);

}
