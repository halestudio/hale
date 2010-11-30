/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Schema Browser for GML 3.2.1.
 * 
 * @author richards
 */
public class Gml321SchemaBrowser extends AbstractGmlSchemaBrowser
{

    /**
     * Name of the Abstract Feature Element.
     */
    private static final String GML_ABSTRACT_FEATURE_ELEMENT_NAME = "AbstractFeature";

    /**
     * Name of the GML Namespace.
     */
    private static final String GML_NAMESPACE = "http://www.opengis.net/gml/3.2";

    /**
     * @param applicationSchemaUrl
     *            {@link String}
     * @param httpProxyHost
     *            {@link String}
     * @param httpProxyPort
     *            int
     * @throws MalformedURLException
     *             if unable to create a {@link URL} from the
     *             applicationSchemaUrl parameter value
     */
    public Gml321SchemaBrowser(String applicationSchemaUrl, String httpProxyHost, int httpProxyPort)
            throws MalformedURLException
    {
        super(applicationSchemaUrl, httpProxyHost, httpProxyPort);
    }

    /**
     * @param applicationSchemaUrl
     *            {@link String}
     * @throws MalformedURLException
     *             if unable to create a {@link URL} from the
     *             applicationSchemaUrl parameter value
     */
    public Gml321SchemaBrowser(String applicationSchemaUrl) throws MalformedURLException
    {
        super(applicationSchemaUrl);
    }

    @Override
    protected String getAbstractFeatureElementName()
    {
        return GML_ABSTRACT_FEATURE_ELEMENT_NAME;
    }

    @Override
    protected String getGmlNamespace()
    {

        return GML_NAMESPACE;
    }
}
