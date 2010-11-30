/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.digest.UrlToAlignmentDigester;

import eu.esdihumboldt.goml.align.Alignment;

/**
 * Tests that exercise the {@link UrlToAlignmentDigester} component.
 * 
 * @author simonp
 */
public class TestUrlToAlignmentDigester
{
    private UrlToAlignmentDigester digester;

    /**
     * Test-level initialisation.
     */
    @Before
    public void setUp()
    {
        digester = new UrlToAlignmentDigester();
    }

    /**
     * Tests that a translation is possible using the example 1 transportation
     * source dataset.
     * 
     * @throws TranslationException
     *             if any errors occurred during the translation
     */
    @Test
    public void testTranslateExample1TNAlignment() throws TranslationException
    {
        // use this to save duplication of mapping files
        URL url = getClass().getClassLoader().getResource(
                "com/onespatial/jrc/tnstg/proto/oml_to_rif/alignments/example1_tn.goml");
        Alignment result = digester.translate(url);
        assertNotNull(result);
    }

}
