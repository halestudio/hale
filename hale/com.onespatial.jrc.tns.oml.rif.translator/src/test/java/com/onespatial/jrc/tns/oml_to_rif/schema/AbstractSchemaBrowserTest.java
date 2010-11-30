/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSElementDecl;

/**
 * Test unit for {@link Gml311SchemaBrowser}.
 * 
 * @author richards
 */
public abstract class AbstractSchemaBrowserTest
{

    /**
     * @param schemaUrl
     *            String
     * @param expectedNames
     *            List&lt;String&gt;
     * @throws MalformedURLException
     *             if any errors occurred getting the {@link SchemaBrowser}
     * @throws SAXException
     *             if any errors occurred reading the list of feature classes
     */
    protected void testFeatureClassNames(String schemaUrl, List<String> expectedNames)
            throws MalformedURLException, SAXException
    {
        SchemaBrowser browser = getBrowser(schemaUrl);

        List<XSElementDecl> actual = browser.getFeatureClassNames();

        assertNotNull(actual);
        assertThat(actual.size(), is(expectedNames.size()));

        List<String> names = new ArrayList<String>();
        for (XSElementDecl element : actual)
        {
            names.add(element.getName());
        }

        assertThat(names, is(expectedNames));
    }

    /**
     * @param schemaUrl
     *            String
     * @return {@link SchemaBrowser}
     * @throws MalformedURLException
     *             if any errors occurred reading the URL of the schema resource
     */
    protected abstract SchemaBrowser getBrowser(String schemaUrl) throws MalformedURLException;
}
