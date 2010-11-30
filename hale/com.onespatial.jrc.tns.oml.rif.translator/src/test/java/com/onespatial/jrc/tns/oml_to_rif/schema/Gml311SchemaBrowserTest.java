/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test unit for {@link Gml311SchemaBrowser}.
 * 
 * @author richards
 */
public class Gml311SchemaBrowserTest extends AbstractSchemaBrowserTest
{

    /**
     * Tests reading the list of feature classes from a given Schema URL.
     * 
     * @throws MalformedURLException
     *             if the URL is invalid
     * @throws SAXException
     *             if any errors occurred reading the list of feature classes
     *             from the resource
     */
    @Test
    public final void testGettingFeatureClasses() throws MalformedURLException, SAXException
    {

        String schemaUrl = "http://geoserver:8080/tnstg-geoserver/wfs?service=WFS"
                + "&version=1.1.0&request=DescribeFeatureType&typeName=nitn%3ATRANSPORT_point_50k,"
                + "nitn%3ATRANSPORT_line_50k&outputFormat=text/xml;%20subtype=gml/3.1.1";

        List<String> expectedNames = Arrays.asList("TRANSPORT_line_50k", "TRANSPORT_point_50k");

        testFeatureClassNames(schemaUrl, expectedNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SchemaBrowser getBrowser(String schemaUrl) throws MalformedURLException
    {
        return new Gml311SchemaBrowser(schemaUrl);
    }
}
